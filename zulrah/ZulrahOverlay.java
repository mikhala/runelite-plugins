package net.runelite.client.plugins.zulrah;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;


import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Slf4j
public class ZulrahOverlay extends Overlay
{
	private final Client client;
	private Zulrah zulrah;
	private int rotation;
	private int zulrahTicker = 0;
	private boolean incrementTicker = true;
	private Zulrah.Style prevStyle = null;

	// Contains all the images for the various phases
	private BufferedImage restart;
	private ArrayList<BufferedImage> init_imgs;
	private ArrayList<BufferedImage> rotation1_imgs;
	private ArrayList<BufferedImage> rotation2_imgs;
	private ArrayList<BufferedImage> rotation3_imgs;
	private ArrayList<BufferedImage> rotation4_imgs;

	@Inject
	public ZulrahOverlay(Client client)
	{
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		rotation = 0;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (zulrah != null)
		{
			graphics.translate(0, 40);

			Font current = FontManager.getRunescapeFont();
			current = current.deriveFont(Font.PLAIN, 20);
			graphics.setFont(current);
			graphics.setColor(new Color(1f, 0.9607843f, 0.0f));

			graphics.drawString("Current", 20, 73);
			graphics.drawString("Next", 20, 173);
		}

		// If the rotation has not yet been determined, then draw the initial rotation
		if (rotation == 0 && zulrah != null)
		{
			// If Zulrah changes styles, then we can increment the ticker again
			if (zulrah.getStyle() != prevStyle || zulrahTicker == 0)
			{
				incrementTicker = true;
			}

			// Increment the ticker, and get the current style of Zulrah to store for later
			if (incrementTicker)
			{
				incrementTicker = false;
				prevStyle = zulrah.getStyle();
				zulrahTicker++;
			}

			if (init_imgs.size() == 7 && rotation1_imgs.size() == 6 && rotation2_imgs.size() == 6)
			{
				drawInit(graphics);
			}
		}

		else if (rotation == 1)
		{
			// If Zulrah changes styles, then we can increment the ticker again
			if (zulrah.getStyle() != prevStyle  || zulrahTicker == 4)
			{
				incrementTicker = true;
			}

			// Increment the ticker, and get the current style of Zulrah to store for later
			if (incrementTicker)
			{
				incrementTicker = false;
				prevStyle = zulrah.getStyle();
				zulrahTicker++;
			}

			if (rotation1_imgs.size() == 6)
			{
				drawRotation1(graphics);
			}

		}

		else if (rotation == 2)
		{
			// If Zulrah changes styles, then we can increment the ticker again
			if (zulrah.getStyle() != prevStyle || zulrahTicker == 4)
			{
				incrementTicker = true;
			}

			// Increment the ticker, and get the current style of Zulrah to store for later
			if (incrementTicker)
			{
				incrementTicker = false;
				prevStyle = zulrah.getStyle();
				zulrahTicker++;
			}

			if (rotation2_imgs.size() == 6)
			{
				drawRotation2(graphics);
			}
		}

		else if (rotation == 3)
		{
			// If Zulrah changes styles, then we can increment the ticker again
			if (zulrah.getStyle() != prevStyle || zulrahTicker == 6) // zulrahTicker == 6, so it will show the double rangers -> just draw them together
			{
				incrementTicker = true;
			}

			// Increment the ticker, and get the current style of Zulrah to store for later
			if (incrementTicker)
			{
				incrementTicker = false;
				prevStyle = zulrah.getStyle();
				zulrahTicker++;
			}

			if (rotation3_imgs.size() == 10)
			{
				drawRotation3(graphics);
			}
		}
		else if (rotation == 4)
		{
			if (zulrah.getStyle() != prevStyle || zulrahTicker == 6) // zulrahTicker == 6, so it will show the double rangers -> just draw them together
			{
				incrementTicker = true;
			}

			// Increment the ticker, and get the current style of Zulrah to store for later
			if (incrementTicker)
			{
				incrementTicker = false;
				prevStyle = zulrah.getStyle();
				zulrahTicker++;
			}

			if (rotation4_imgs.size() == 11)
			{
				drawRotation4(graphics);
			}
		}


		return new Dimension();
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}

	public void setZulrah(Zulrah zulrah)
	{
		this.zulrah = zulrah;
	}

	public void setZulrahTicker(int zulrahTicker)
	{
		this.zulrahTicker = zulrahTicker;
	}

	public int getZulrahTicker()
	{
		return zulrahTicker;
	}

	public void setPrevStyle(Zulrah.Style style)
	{
		this.prevStyle = style;
	}

	// Draw initial rotation of Zulrah (until rotation figured out)
	public void drawInit(Graphics2D graphics)
	{
		// Draw the first Zulrah phase
		if (zulrahTicker == 1)
		{
			graphics.translate(15, 75);
			graphics.drawImage(init_imgs.get(0), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(init_imgs.get(1), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(init_imgs.get(2), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(init_imgs.get(3), null, 0, 0);

			graphics.translate(-160, -100);
			graphics.translate(-15, -75);

		}

		// Draw second
		if (zulrahTicker == 2)
		{
			graphics.translate(15, 75);
			graphics.drawImage(init_imgs.get(1), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(init_imgs.get(4), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		// Draw third
		if (zulrahTicker == 3)
		{
			graphics.translate(15, 75);
			graphics.drawImage(init_imgs.get(4), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(init_imgs.get(5), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(init_imgs.get(6), null, 0, 0);

			graphics.translate(-80, -100);
			graphics.translate(-15, -75);
		}

		// Draw fourth
		if (zulrahTicker == 4)
		{
			graphics.translate(15, 75);
			graphics.drawImage(init_imgs.get(5), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(init_imgs.get(6), null, 0, 0);

			graphics.translate(-80, 100);
			graphics.drawImage(rotation1_imgs.get(0), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(rotation2_imgs.get(0), null, 0, 0);

			graphics.translate(-80, -100);
			graphics.translate(-15, -75);
		}
	}

	// Draw rotation 1
	public void drawRotation1(Graphics2D graphics)
	{
		if (zulrahTicker == 5)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(0), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation1_imgs.get(1), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 6)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(1), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation1_imgs.get(2), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 7)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(2), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation1_imgs.get(3), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 8)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(3), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation1_imgs.get(4), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 9)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(4), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation1_imgs.get(5), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 10)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation1_imgs.get(5), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(restart, null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}
	}

	// Draw rotation 2
	public void drawRotation2(Graphics2D graphics)
	{
		if (zulrahTicker == 5)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(0), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation2_imgs.get(1), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 6)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(1), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation2_imgs.get(2), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 7)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(2), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation2_imgs.get(3), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 8)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(3), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation2_imgs.get(4), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 9)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(4), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation2_imgs.get(5), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 10)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation2_imgs.get(5), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(restart, null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}
	}

	// Draw rotation 3
	public void drawRotation3(Graphics2D graphics)
	{
		if (zulrahTicker == 1) // ticker 1 because it starts on ranger -> ranger (double ranger issue)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(0), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(1), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 2)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(1), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(2), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 3)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(2), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(3), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 4)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(3), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(4), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 5)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(4), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(5), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 6) // DOUBLE RANGERS HERE
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(5), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(6), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 7) // Current: ranger, Next: mage <- 'double ranger issue' so draw both rangers as a 'work around'
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(5), null, 0, 0); // DRAW PREVIOUS RANGER

			graphics.translate(80, 0);
			graphics.drawImage(rotation3_imgs.get(6), null, 0, 0); // DRAW CURRENT RANGER

			graphics.translate(-80, 100);
			graphics.drawImage(rotation3_imgs.get(7), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 8)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(7), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(8), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 9)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(8), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation3_imgs.get(9), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 10)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation3_imgs.get(9), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(restart, null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}
	}

	// Draw rotation 4
	public void drawRotation4(Graphics2D graphics)
	{
		if (zulrahTicker == 2)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(0), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(1), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 3)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(1), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(2), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 4)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(2), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(3), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 5)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(3), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(4), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 6) // CURRENT: Ranger, NEXT: Ranger
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(4), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(5), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 7) // CURRENT: Ranger, NEXT: Mage -> DRAW BOTH TO DEMONSTRATE DOUBLE RANGER PROBLEM
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(4), null, 0, 0);

			graphics.translate(80, 0);
			graphics.drawImage(rotation4_imgs.get(5), null, 0, 0);

			graphics.translate(-80, 100);
			graphics.drawImage(rotation4_imgs.get(6), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 8)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(6), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(7), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 9)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(7), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(8), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 10)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(8), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(9), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 11)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(9), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(rotation4_imgs.get(10), null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}

		if (zulrahTicker == 12)
		{
			graphics.translate(15, 75);
			graphics.drawImage(rotation4_imgs.get(10), null, 0, 0);

			graphics.translate(0, 100);
			graphics.drawImage(restart, null, 0, 0);

			graphics.translate(0, -100);
			graphics.translate(-15, -75);
		}
	}

	// Sets all the rotation arrays
	public void setRotationArrays(ArrayList<BufferedImage> init_imgs, ArrayList<BufferedImage> rotation1_imgs, ArrayList<BufferedImage> rotation2_imgs, ArrayList<BufferedImage> rotation3_imgs, ArrayList<BufferedImage> rotation4_imgs)
	{
		this.init_imgs = init_imgs;
		this.rotation1_imgs = rotation1_imgs;
		this.rotation2_imgs = rotation2_imgs;
		this.rotation3_imgs = rotation3_imgs;
		this.rotation4_imgs = rotation4_imgs;
	}

	// Set reset image
	public void setRestartImage(BufferedImage restart)
	{
		this.restart = restart;
	}

	// Restart everything when Zulrah dies or despawns
	public void zulrahCleanup()
	{
		rotation = 0;
		zulrah = null;
		prevStyle = null;
		zulrahTicker = 0;
		incrementTicker = true;
		init_imgs = new ArrayList<>();
		rotation1_imgs = new ArrayList<>();
		rotation2_imgs = new ArrayList<>();
		rotation3_imgs = new ArrayList<>();
		rotation4_imgs = new ArrayList<>();
	}
}
