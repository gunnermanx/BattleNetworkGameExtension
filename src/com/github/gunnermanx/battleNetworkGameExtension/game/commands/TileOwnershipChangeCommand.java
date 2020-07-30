package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class TileOwnershipChangeCommand extends Command {	 
	public int playerId;
	public int x, y;
	
	public TileOwnershipChangeCommand(int playerId, int x, int y) {
		this.playerId = playerId;
		this.x = x;
		this.y = y;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.TILE_OWNERSHIP_CHANGE_COMMAND_ID);
		payload.addInt(playerId);
		payload.addInt(x);
		payload.addInt(y);
		return payload;
	}
}
