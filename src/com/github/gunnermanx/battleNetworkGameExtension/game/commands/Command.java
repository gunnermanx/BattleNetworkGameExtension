package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public abstract class Command {
	
	public static final byte MOVE_COMMAND_ID = (byte) 0;
	public static final byte DAMAGE_DEALT_COMMAND_ID = (byte) 1;
	public static final byte ENERGY_CHANGED_COMMAND_ID = (byte) 2;
	public static final byte SPAWN_PROJECTILE_COMMAND_ID = (byte) 3;
	public static final byte CHIP_DRAWN_COMMAND_ID = (byte) 4;
	
	public abstract SFSArray Serialize();
}


