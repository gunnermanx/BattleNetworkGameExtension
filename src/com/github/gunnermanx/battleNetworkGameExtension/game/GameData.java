package com.github.gunnermanx.battleNetworkGameExtension.game;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;

import net.sf.json.JSONObject;

public class GameData {
	
	private BattleNetworkExtension ext;
	private JSONObject chips;
	private JSONObject projectiles;
	
	public GameData(BattleNetworkExtension ext) {
		this.ext = ext;
		try {
			String baseDir = this.ext.getCurrentFolder();
						
			String chipJsonStr = new String(readAllBytes(get(baseDir + "./chips.json")));
			this.chips = JSONObject.fromObject(chipJsonStr);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	public boolean IsValidChipId(short chipId) {
		String chipIdStr = Integer.toString(chipId);
		return chips.has(chipIdStr);
	}
	
	public JSONObject GetChipData(short chipId) {
		String chipIdStr = Integer.toString(chipId);
		return chips.getJSONObject(chipIdStr);	
	}
	
	
	public JSONObject GetProjectileData(int projectileId) {
		String projectileIdStr = Integer.toString(projectileId);
		return projectiles.getJSONObject(projectileIdStr);	
	}
	
	public class ChipDataKeys {
		public static final String COST = "cost";
		public static final String DATA = "data";
		public static final String PROJECTILE_SPEED = "projectileSpeed";
		public static final String DAMAGE = "damage";
	}
	
	
	// cannons
	// shots/guns
	//     element spreads
	// bombs
	// swords 
	// shock waves
	// towers
	// quakes
	// punches
	// dash
}
