package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.projectiles.Projectile;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.projectiles.StraightProjectile;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Missile extends Chip {

	public Missile(BattleNetworkGame game, Player player, JSONObject chipJSON, int x, int y) {
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
		int speed = dataForLevel.getInt(GameData.ChipDataKeys.PROJECTILE_SPEED);
		int damage = dataForLevel.getInt(GameData.ChipDataKeys.DAMAGE);
		
		// start one tile ahead
		//int x = (this.player.id == 1) ? this.playerX + 1 : this.playerX - 1;	
		Projectile p = new StraightProjectile(this.player.owner, speed, damage, this.playerX, this.playerY);
		
		this.game.spawnProjectile(player, p);
		
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
