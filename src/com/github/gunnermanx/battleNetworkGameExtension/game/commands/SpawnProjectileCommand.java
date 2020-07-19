package com.github.gunnermanx.battleNetworkGameExtension.game.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class SpawnProjectileCommand extends Command {	 
	public int playerId;
	public int cid;
	
	public SpawnProjectileCommand(int playerId, int cid) {
		this.playerId = playerId;
		this.cid = cid;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.SPAWN_PROJECTILE_COMMAND_ID);
		payload.addInt(playerId);
		payload.addInt(cid);
		return payload;
	}
}
