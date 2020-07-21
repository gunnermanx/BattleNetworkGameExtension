package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

import net.sf.json.JSONObject;

public class Cannon extends Chip {

	public Cannon(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
		super(game, player, chipJSON, x, y);	
	}

	@Override
	public void Init() {
		
		int damage = 30; // get from json
		
		Unit target = game.getFirstEnemyUnitInRow(player.owner, this.playerX, this.playerY);
		this.game.damageUnit(target, damage);		
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
