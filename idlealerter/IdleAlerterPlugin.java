package net.runelite.client.plugins.idlealerter;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import static net.runelite.api.AnimationID.*;

@PluginDescriptor(
	name = "Idle Alerter",
	description = "Draw coloured overlay when idle",
	tags = {"fishing"}
)
public class IdleAlerterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private IdleAlerterConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private IdleAlerterOverlay idleAlerterOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(idleAlerterOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.add(idleAlerterOverlay);
	}

	@Provides
	IdleAlerterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IdleAlerterConfig.class);
	}

	public enum LastPlayerAction {
    FISHING,
    UNSUPPORTED,
  }

	@Getter
	private LastPlayerAction lastPlayerAction = LastPlayerAction.UNSUPPORTED;

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer != event.getActor())
		{
			return;
		}

		int animation = localPlayer.getAnimation();
		switch (animation)
		{
			case IDLE:
				// Ignore the idle case
				break;
			/* Fishing */
			case FISHING_BARBTAIL_HARPOON:
			case FISHING_BARBARIAN_ROD:
			case FISHING_BAREHAND:
			case FISHING_BIG_NET:
			case FISHING_CAGE:
			case FISHING_CRUSHING_INFERNAL_EELS:
			case FISHING_CRYSTAL_HARPOON:
			case FISHING_CUTTING_SACRED_EELS:
			case FISHING_DRAGON_HARPOON_OR:
			case FISHING_DRAGON_HARPOON:
			case FISHING_HARPOON:
			case FISHING_INFERNAL_HARPOON:
			case FISHING_KARAMBWAN:
			case FISHING_NET:
			case FISHING_OILY_ROD:
			case FISHING_PEARL_BARBARIAN_ROD_2:
			case FISHING_PEARL_BARBARIAN_ROD:
			case FISHING_PEARL_FLY_ROD_2:
			case FISHING_PEARL_FLY_ROD:
			case FISHING_PEARL_OILY_ROD:
			case FISHING_PEARL_ROD_2:
			case FISHING_PEARL_ROD:
			case FISHING_POLE_CAST:
			case FISHING_TRAILBLAZER_HARPOON:
				lastPlayerAction = LastPlayerAction.FISHING;
				break;
			default: // Unsupported actions
				lastPlayerAction = LastPlayerAction.UNSUPPORTED;
		}
	}
}