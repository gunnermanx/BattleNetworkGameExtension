package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Sword extends Chip {

	public Sword(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
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
		int depth = dataForLevel.getInt(GameData.ChipDataKeys.DEPTH);
		int width = dataForLevel.getInt(GameData.ChipDataKeys.WIDTH);
				
		List<Unit> targets = game.getUnitsInRange(player.owner, this.playerX, this.playerY, width, depth);
		
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
