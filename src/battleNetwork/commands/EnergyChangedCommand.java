package battleNetwork.commands;

import com.smartfoxserver.v2.entities.data.SFSArray;

public class EnergyChangedCommand extends Command {	 
	public int id;
	public int deltaEnergy;
	
	public EnergyChangedCommand(int id, int deltaEnergy) {
		this.id = id;
		this.deltaEnergy = deltaEnergy;
	}
	
	public SFSArray Serialize() {
		SFSArray payload = new SFSArray();
		payload.addByte(Command.ENERGY_CHANGED_COMMAND_ID);
		payload.addInt(id);
		payload.addInt(deltaEnergy);
		return payload;
	}
}
