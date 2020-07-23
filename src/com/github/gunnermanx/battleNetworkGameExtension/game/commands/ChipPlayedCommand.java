package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class ChipPlayedCommand extends Command {	 
	public int playerId;
	public short cid;
	
	public ChipPlayedCommand(int playerId, short cid) {
		this.playerId = playerId;
		this.cid = cid;		
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.CHIP_PLAYED_COMMAND_ID);
		payload.addInt(playerId);
		payload.addShort(this.cid);
		return payload;
	}
}
