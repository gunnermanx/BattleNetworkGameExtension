package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class BasicAttackCommand extends Command {	 
	public int playerId;
	
	public BasicAttackCommand(int playerId) {
		this.playerId = playerId;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.BASIC_ATTACK_COMMAND_ID);
		payload.addInt(this.playerId);
		return payload;
	}
}
