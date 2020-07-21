package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

import net.sf.json.JSONObject;

public class Sword extends Chip {

	public Sword(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
		super(game, player, chipJSON, x, y);	
	}

	@Override
	public void Init() {
		
		int damage = 40; // get from json		
		List<Unit> targets = game.getUnitsInRange(player.owner, this.playerX, this.playerY, 1, 1);
		
		for (int i = 0; i < targets.size(); i++) {
			this.game.damageUnit(targets.get(i), damage);
		}				
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
