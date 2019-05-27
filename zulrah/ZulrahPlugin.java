package net.runelite.client.plugins.zulrah;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
		name = "Zulrah"
)

@Slf4j
public class ZulrahPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ZulrahOverlay zulrahOverlay;

	private Zulrah zulrah;
	private NPC npc;
	private ArrayList<Zulrah.Style> zulrahStyles = new ArrayList<>(); // keep track before rotation figured out
	private Zulrah.Style[] rotation1 = {Zulrah.Style.RANGED, Zulrah.Style.MELEE, Zulrah.Style.MAGIC, Zulrah.Style.RANGED, Zulrah.Style.MELEE};
	private Zulrah.Style[] rotation2 = {Zulrah.Style.RANGED, Zulrah.Style.MELEE, Zulrah.Style.MAGIC, Zulrah.Style.RANGED, Zulrah.Style.MAGIC};
	private Zulrah.Style[] rotation4 = {Zulrah.Style.RANGED, Zulrah.Style.MAGIC}; // rotation 3 is determined with a tick counter
	private int rotation = 0; // 0, 1, 2, 3, 4 -> 0 is not figured out
	private int tickCounter = 0;
	private boolean rotationDetermined = false;
	private boolean zulrahHasSpawned = false;
	private boolean zulrahExists = false;
	private boolean reset = false;

	// Contains all the images for the various phases
	private ArrayList<BufferedImage> init_imgs = new ArrayList<>();
	private ArrayList<BufferedImage> rotation1_imgs = new ArrayList<>();
	private ArrayList<BufferedImage> rotation2_imgs = new ArrayList<>();
	private ArrayList<BufferedImage> rotation3_imgs = new ArrayList<>();
	private ArrayList<BufferedImage> rotation4_imgs = new ArrayList<>();
	private BufferedImage restart;

	@Override
	protected void startUp()
	{
		overlayManager.add(zulrahOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(zulrahOverlay);
	}

	// Set the Zulrah object
	@Subscribe
	public void onNpcSpawned(final NpcSpawned event)
	{
		npc = event.getNpc();
		if (isNpcZulrah(npc.getId()))
		{
			zulrah = new Zulrah(npc);
			zulrahHasSpawned = true;
			zulrahExists = true;
		}
	}

	// When Zulrah despawns
	@Subscribe
	public void onNpcDespawned(final NpcDespawned event)
	{
		npc = event.getNpc();
		if (isNpcZulrah(npc.getId()))
		{
			zulrahCleanUp();
		}
	}

	// Called if Zulrah dies or if Zulrah no longer exists
	public void zulrahCleanUp()
	{
		rotation = 0;
		zulrahHasSpawned = false;
		rotationDetermined = false;
		tickCounter = 0;
		zulrah = null;
		npc = null;
		zulrahStyles = new ArrayList<>();
		init_imgs = new ArrayList<>();
		restart = null;
		reset = false;
		rotation1_imgs = new ArrayList<>();
		rotation2_imgs = new ArrayList<>();
		rotation3_imgs = new ArrayList<>();
		rotation4_imgs = new ArrayList<>();
		zulrahOverlay.zulrahCleanup();
	}

	private BufferedImage getImage(String path)
	{
		BufferedImage image = null;
		try
		{
			synchronized (ImageIO.class)
			{
				image = ImageIO.read(ZulrahOverlay.class.getResourceAsStream(path));
			}
		}
		catch (IOException e)
		{
			log.warn("Error fetching image" , e);
		}
		image = resize(image);
		return image;
	}

	private static BufferedImage resize(BufferedImage img)
	{
		int width = 70;
		int height = 70;
		Image temp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = newImg.createGraphics();
		g2d.drawImage(temp, 0, 0, null);
		g2d.dispose();

		return newImg;
	}

	// Check if the npc is zulrah based on the id of NpcSpawned event
	public static boolean isNpcZulrah(int npcId)
	{
		return npcId == NpcID.ZULRAH ||
				npcId == NpcID.ZULRAH_2043 ||
				npcId == NpcID.ZULRAH_2044;
	}

	// Keep checking Zulrah to determine the rotation / style change
	@Subscribe
	public void onGameTick (final GameTick onTick)
	{
		if (zulrahHasSpawned && zulrahExists) // Make sure that Zulrah exists in the first place
		{
			// START INITIAL CHECKS

			// If Zulrah does exist and the image arrays are empty, then fill the image arrays and send them to the overlay
			if (init_imgs.isEmpty() && rotation1_imgs.isEmpty() &&  rotation2_imgs.isEmpty() && rotation3_imgs.isEmpty() && rotation4_imgs.isEmpty())
			{
				populateRotationArrays();
				zulrahOverlay.setRotationArrays(init_imgs, rotation1_imgs, rotation2_imgs, rotation3_imgs, rotation4_imgs);
				zulrahOverlay.setRestartImage(restart);
			}

			// If Zulrah dies then perform clean up
			if (zulrah.getZulrah().isDead())
			{
				zulrahCleanUp();
			}

			// Check that Zulrah actaully exists in the first place (probably redundant, but a just in case)
			else if (!doesZulrahExist())
			{
				zulrahCleanUp();
			}

			// END INITIAL CHECKS

			else // Zulrah isn't dead and does exist
			{
				// Update the style of the Zulrah object -> this is the Zulrah that can be currently seen by the player
				// It checks all the NPCs in the room to find the Zulrah object and based on the ID will set the style
				// Ignores snakelings
				updateZulrahStyle();

				// If the rotation is not determined, then get the
				// current Zulrah attack style and keep track of it
				// until we can determine a rotation
				if (!rotationDetermined)
				{
					if (zulrah.getStyle() != null) // Make sure it isn't null in the first place (probably redundant)
					{
						addStyle(zulrah.getStyle()); // Add the style to the zulrahStyles ArrayList to keep track of the style order
						zulrahOverlay.setZulrah(zulrah); // Set the Zulrah object in the overlay so that overlay can keep track of Zulrah
						determineRotation(onTick); // Determines the rotation based on zulrahStyles, if it is rotation 3 this is determined by ticks instead
					}
				}
				else // Once the rotation is determined, set the rotation for that kill, if rotation over then reset EVERYTHING
				{
					if (rotation == 1 && zulrahOverlay.getZulrahTicker() == 11) // Rotation 1 has reset
					{
						zulrahStyles = new ArrayList<>();
						zulrahOverlay.setZulrahTicker(0);
						zulrahOverlay.setRotation(0);
						zulrahOverlay.setPrevStyle(null);
						rotationDetermined = false;
						reset = true;
					}
					else if (rotation == 2 && zulrahOverlay.getZulrahTicker() == 11) // Rotation 2 has reset
					{
						zulrahStyles = new ArrayList<>();
						zulrahOverlay.setZulrahTicker(0);
						zulrahOverlay.setRotation(0);
						zulrahOverlay.setPrevStyle(null);
						rotationDetermined = false;
						reset = true;
					}
					else if (rotation == 3 && zulrahOverlay.getZulrahTicker() == 11) // Rotation 3 has reset
					{
						zulrahStyles = new ArrayList<>();
						zulrahOverlay.setZulrahTicker(0);
						zulrahOverlay.setRotation(0);
						zulrahOverlay.setPrevStyle(null);
						rotationDetermined = false;
						reset = true;
					}
					else if (rotation == 4 && zulrahOverlay.getZulrahTicker() == 13) // Rotation 4 has reset
					{
						zulrahStyles = new ArrayList<>();
						zulrahOverlay.setZulrahTicker(0);
						zulrahOverlay.setRotation(0);
						zulrahOverlay.setPrevStyle(null);
						rotationDetermined = false;
						reset = true;
					}
					else  // The rotation has been determined, so set the rotation for zulrahOverlay to draw the correct rotation
					{
						zulrahOverlay.setRotation(rotation);
					}
				}

			}
		}
	}

	// Check if Zulrah still exists
	private boolean doesZulrahExist()
	{
		List<NPC> zulrahNpcs; // List of all the NPCs in Zulrah room at a given game tick
		boolean checker = false;

		zulrahNpcs = client.getNpcs(); // Continuously get the NPCs every game tick
		if (zulrahNpcs.size() >= 1) // If there are actually NPCs in the ArrayList
		{
			// Go through the NPC list and find the Zulrah NPC
			for (NPC npcs : zulrahNpcs)
			{
				// Check if we have found the Zulrah NPC in the ArrayList
				if (isNpcZulrah(npcs.getId()))
				{
					zulrahExists = true;
					checker = true;
				}
			}
		}

		return checker;
	}

	// Fill the rotation arrays when needed
	public void populateRotationArrays()
	{
		String restart_path = "/zulrah/restart.png";
		String init_path = "/zulrah/init/";
		String rotation1_path = "/zulrah/rotation_1/";
		String rotation2_path = "/zulrah/rotation_2/";
		String rotation3_path = "/zulrah/rotation_3/";
		String rotation4_path = "/zulrah/rotation_4/";

		restart = getImage(restart_path);

		// Populate the init array
		init_imgs.add(getImage(init_path + "init_1.png"));
		init_imgs.add(getImage(init_path + "init_2.png"));
		init_imgs.add(getImage(init_path + "init_2_alt_r3.png"));
		init_imgs.add(getImage(init_path + "init_2_alt_r4.png"));
		init_imgs.add(getImage(init_path + "init_3.png"));
		init_imgs.add(getImage(init_path + "init_4.png"));
		init_imgs.add(getImage(init_path + "init_4_alt.png"));

		// Populate the rotation 1 array
		for (int i = 0; i < 6; i++)
		{
			rotation1_imgs.add(getImage(rotation1_path + "ro_" + Integer.toString(i + 5) + ".png"));
		}

		// Populate the rotation 2 array
		for (int i = 0; i < 6; i++)
		{
			rotation2_imgs.add(getImage(rotation2_path + "ro_" + Integer.toString(i + 5) + ".png"));
		}

		// Populate the rotation 3 array
		for (int i = 0; i < 10; i++)
		{
			rotation3_imgs.add(getImage(rotation3_path + "ro_" + Integer.toString(i + 3) + ".png"));
		}

		// Populate the rotation 4 array
		for (int i = 0; i < 11; i++)
		{
			rotation4_imgs.add(getImage(rotation4_path + "ro_" + Integer.toString(i + 2) + ".png"));
		}
	}

	// Check all the NPCs in the room and set the style of the Zulrah object
	public void updateZulrahStyle()
	{
		List<NPC> zulrahNpcs; // List of all the NPCs in Zulrah room at a given game tick

		zulrahNpcs = client.getNpcs(); // Continuously get the NPCs every game tick
		if (zulrahNpcs.size() >= 1) // If there are actually NPCs in the ArrayList
		{
			// Go through the NPC list and find the Zulrah NPC
			for (NPC npcs : zulrahNpcs)
			{
				// Check if we have found the Zulrah NPC in the ArrayList
				if (isNpcZulrah(npcs.getId()))
				{
					// Update the Zulrah object to the current style
					zulrah.setStyle(zulrah.determineZulrahType(npcs.getId()));
				}
			}
		}
	}

	// Keep track of the Zulrah styles at beginning of fight until the rotation is figured out
	// Only add a style if it doesn't exist yet (we only need this to determine rotation)
	public void addStyle(Zulrah.Style style)
	{
		Zulrah.Style prevStyle;

		if (zulrahStyles.isEmpty())
		{
			zulrahStyles.add(style);
		}

		// Get the last style listed in the ArrayList
		prevStyle = zulrahStyles.get(zulrahStyles.size() - 1);

		// If the last style is not the current style, add it to array list
		if (!prevStyle.equals(style))
		{
			zulrahStyles.add(style);
		}
	}

	// Determine which is the rotation
	public void determineRotation(GameTick onTick)
	{
		if (zulrah.getStyle() == Zulrah.Style.RANGED)
		{
			// To make sure it doesn't confuse rotation 1 or 2 with 3
			if (zulrahStyles.size() < 2)
			{
				tickCounter++;
			}
		}

		// It is rotation 1 or 2
		if (zulrahStyles.size() == 5)
		{
			if (zulrahStyles.get(4).equals(rotation1[4]))
			{
				rotation = 1;
				rotationDetermined = true;
				System.out.println("ROTATION 1");
			}

			if (zulrahStyles.get(4).equals(rotation2[4]))
			{
				rotation = 2;
				rotationDetermined = true;
				System.out.println("ROTATION 2");
			}
		}

		// It is rotation 3
		if (tickCounter == 31 && !reset)
		{
			rotation = 3;
			rotationDetermined = true;
			System.out.println("ROTATION 3");
		}

		// It is rotation 3 but after reset wave
		if (tickCounter == 43 && reset)
		{
			rotation = 3;
			rotationDetermined = true;
		}

		// It is rotation 4
		else if (zulrahStyles.size() == 2)
		{
			if (zulrahStyles.get(1).equals(rotation4[1]))
			{
				rotation = 4;
				rotationDetermined = true;
				System.out.println("ROTATION 4");
			}
		}

		if (rotationDetermined)
		{
			tickCounter = 0;
		}
	}
}