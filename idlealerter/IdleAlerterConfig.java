package net.runelite.client.plugins.idlealerter;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("idlealerter")
public interface IdleAlerterConfig extends Config
{
  @ConfigItem(
		keyName = "fishingIdle",
		name = "Idle Fishing Alert",
		description = "Configures if alerting on idle fishing animations are enabled",
		position = 1
	)
	default boolean fishingIdle()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "fishingIdleColor",
		name = "Idle Fishing Color",
		description = "Configures the color overlay for fishing idle alerts",
		position = 2
	)
	default Color fishingIdleColor()
	{
		return new Color(255, 123, 45, 67);
	}
}