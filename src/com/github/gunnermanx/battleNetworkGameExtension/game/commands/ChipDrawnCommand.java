package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class ChipDrawnCommand extends Command {	 
	public short cid;
	public short cidNext;
	public int playerId;
	
	public ChipDrawnCommand(int playerId, short cid, short cidNext) {
		this.cid = cid;
		this.cidNext = cidNext;
		this.playerId = playerId;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.CHIP_DRAWN_COMMAND_ID);
		payload.addInt(this.playerId);
		payload.addShort(this.cid);
		payload.addShort(this.cidNext);
		return payload;
	}
}
