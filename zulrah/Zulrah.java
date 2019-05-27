package net.runelite.client.plugins.zulrah;

import net.runelite.api.NPC;
import net.runelite.api.NpcID;

public class Zulrah
{
	private NPC zulrah;
	private Style style = null;

	private final int zulrah_ranged = NpcID.ZULRAH; // RANGED
	private final int zulrah_melee = NpcID.ZULRAH_2043; // MELEE
	private final int zulrah_magic = NpcID.ZULRAH_2044; // MAGIC

	enum Style
	{
		MAGIC,
		RANGED,
		MELEE
	}

	public Zulrah(NPC npc)
	{
		this.zulrah = npc;
		this.style = determineZulrahType(npc.getId());
	}

	// Determine the Zulrah type currently active
	public Style determineZulrahType(int zulrahId)
	{
		if (zulrahId == zulrah_ranged)
		{
			style = Style.RANGED;
		}
		if (zulrahId == zulrah_magic)
		{
			style = Style.MAGIC;
		}
		if (zulrahId == zulrah_melee)
		{
			style = Style.MELEE;
		}
		return style;
	}

	public void setStyle(Style style)
	{
		this.style = style;
	}

	public Style getStyle()
	{
		return style;
	}

	public NPC getZulrah()
	{
		return this.zulrah;
	}
}
