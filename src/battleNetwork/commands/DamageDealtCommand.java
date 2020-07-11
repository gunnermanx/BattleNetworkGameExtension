package battleNetwork.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class DamageDealtCommand extends Command {	 
	public int id;
	public int damage;
	
	public DamageDealtCommand(int id, int damage) {
		this.id = id;
		this.damage = damage;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.DAMAGE_DEALT_COMMAND_ID);
		payload.addInt(id);
		payload.addInt(damage);
		return payload;
	}
}
