// ZulrahPlugin - Plugin for the OSRS RuneLite client to display Zulrah rotation.
// Copyright (C) 2019  mikhala

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
