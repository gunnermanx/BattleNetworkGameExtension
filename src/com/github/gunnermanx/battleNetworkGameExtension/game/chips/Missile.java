package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Missile extends Chip {

	public Missile(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
		super(game, player, chipJSON, x, y);
	
	}

	@Override
	public void Init() {
		// For each projectile listed, we want to spawn it
		JSONArray projectiles = this.chipJSON.getJSONArray(GameData.ChipDataKeys.PROJECTILES);
		for (int i = 0; i < projectiles.size(); i++) {
			JSONObject projectileJson = projectiles.getJSONObject(i);
			int pid = projectileJson.getInt(GameData.ProjectileDataKeys.ID);
					
			this.game.SpawnProjectile(player, pid);
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
