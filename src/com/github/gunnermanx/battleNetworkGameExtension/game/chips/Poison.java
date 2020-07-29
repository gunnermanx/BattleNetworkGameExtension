package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles.DamageOverTimeTileEffect;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Poison extends Chip {

	public Poison(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
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
		int duration = dataForLevel.getInt(GameData.ChipDataKeys.DURATION);
		
		//Unit target = game.getFirstEnemyUnitInRow(player.owner, this.playerX, this.playerY);
		//this.game.damageUnit(target, damage);
		
		// place an effect in each positon that we are targetting
		if (player.owner == Owner.PLAYER1) {
			for (int x = 4; x < BattleNetworkGame.ARENA_LENGTH; x++) {
				for (int y = 0; y < BattleNetworkGame.ARENA_WIDTH; y++) {
					DamageOverTimeTileEffect tileEffect = new DamageOverTimeTileEffect(game, Owner.PLAYER1, damage, duration);		
					game.spawnTileEffect(tileEffect, x, y);
				}
			}
		} else {
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < BattleNetworkGame.ARENA_WIDTH; y++) {
					DamageOverTimeTileEffect tileEffect = new DamageOverTimeTileEffect(game, Owner.PLAYER2, damage, duration);		
					game.spawnTileEffect(tileEffect, x, y);
				}
			}
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
