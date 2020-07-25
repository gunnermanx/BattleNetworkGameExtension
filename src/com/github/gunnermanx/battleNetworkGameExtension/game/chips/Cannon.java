package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Cannon extends Chip {

	public Cannon(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
		super(game, player, chipJSON, x, y);	
	}

	@Override
	public void Init() {
		// Get data for chip
		JSONArray data = this.chipJSON.getJSONArray(GameData.ChipDataKeys.DATA);
		
		// TODO, gotta grab the level data from db somewhere before creating the chip
		int level = 1;
		// Get the data for the particular level of the chip
		JSONObject dataForLevel = data.getJSONObject(level);
		int damage = dataForLevel.getInt(GameData.ChipDataKeys.DAMAGE);
		
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
