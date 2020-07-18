package chips;

import battleNetwork.BattleNetworkGame;
import battleNetwork.GameData;
import battleNetwork.entities.Arena;
import battleNetwork.entities.Player;
import net.sf.json.JSONObject;

public class ChipFactory {
	
	public static Chip getChip(BattleNetworkGame game, int cid, Player player, JSONObject data, int pUnitX, int pUnitY) {
		
		if (cid == 0) {
			return new Missile(game, player, data, pUnitX, pUnitY);			
		} 
		
		
		return null;		
	}
}
