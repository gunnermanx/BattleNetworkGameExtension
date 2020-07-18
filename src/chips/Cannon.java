package chips;

import battleNetwork.BattleNetworkGame;
import battleNetwork.GameData;
import battleNetwork.entities.Arena.Ownership;
import battleNetwork.entities.Arena;
import battleNetwork.entities.Player;
import battleNetwork.entities.Unit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Cannon extends Chip {

	public Cannon(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
		super(game, player, chipJSON, x, y);	
	}

	@Override
	public void Init() {
		
		int damage = 30; // get from json
		
		Unit target = game.arena.GetFirstEnemyUnitInRow(player.owner, this.playerX, this.playerY);
		this.game.arena.DamageUnit(target, damage);		
	}
	
	@Override
	public void Advance() {
		// no-op
	}

	@Override
	public boolean IsComplete() {
		return true;
	}

}
