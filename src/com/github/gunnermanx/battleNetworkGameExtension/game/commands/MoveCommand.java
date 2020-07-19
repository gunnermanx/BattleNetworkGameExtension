package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class MoveCommand extends Command {	 
	public int id;
	public int x, y;
	
	public MoveCommand(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.MOVE_COMMAND_ID);
		payload.addInt(id);
		payload.addInt(x);
		payload.addInt(y);
		return payload;
	}
}
