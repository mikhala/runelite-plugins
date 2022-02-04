package net.runelite.client.plugins.idlealerter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import static net.runelite.api.AnimationID.*;

@PluginDescriptor(
	name = "Idle Alerter",
	description = "Draw coloured overlay when idle",
	tags = {"fishing"}
)
public class IdleAlerterOverlay extends Overlay
{
  private final Client client;
	private final IdleAlerterPlugin plugin;
	private final IdleAlerterConfig config;
	private Graphics2D box;

  @Inject
  private IdleAlerterOverlay(final Client client, final IdleAlerterPlugin plugin, final IdleAlerterConfig config)
  {
    super(plugin);
    this.client = client;
    this.plugin = plugin;
    this.config = config;
  }

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Player local = client.getLocalPlayer();
		if (config.fishingIdle())
		{
			drawIdleBox(local, graphics);
		}

		return null;
	}

	private void drawIdleBox(Player local, Graphics2D graphics)
	{
		if (isSupportedPreviousAction() && isPlayerIdle(local))
		{
			graphics.setColor(config.fishingIdleColor());
			graphics.fillRect(0, 0, client.getCanvas().getWidth(), client.getCanvas().getHeight());
		}
	}

	private boolean isSupportedPreviousAction()
	{
		if (plugin.getLastPlayerAction() == IdleAlerterPlugin.LastPlayerAction.FISHING)
		{
			return true;
		}
		return false;
	}

	private boolean isPlayerIdle(Player local)
	{
		final int animation = local.getAnimation();
		if (animation == IDLE)
		{
			return true;
		}
		return false;
	}

}