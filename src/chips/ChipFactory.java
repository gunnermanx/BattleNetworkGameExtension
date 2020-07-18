package chips;

import battleNetwork.BattleNetworkGame;
import battleNetwork.entities.Player;
import net.sf.json.JSONObject;

public class ChipFactory {
	
	public static Chip getChip(BattleNetworkGame game, int cid, Player player, JSONObject data, int pUnitX, int pUnitY) {
		
		switch (cid) {
			case 0:
				return new Missile(game, player, data, pUnitX, pUnitY);
			case 1:
				return new Cannon(game, player, data, pUnitX, pUnitY);
		} 
		
		
		return null;		
	}
}
