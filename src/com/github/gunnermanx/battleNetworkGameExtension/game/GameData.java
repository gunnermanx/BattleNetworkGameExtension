package com.github.gunnermanx.battleNetworkGameExtension.game;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;

import net.sf.json.JSONObject;

public class GameData {
	
	private BattleNetworkZoneExtension ext;
	private JSONObject chips;
	private JSONObject projectiles;
	
	public GameData(BattleNetworkZoneExtension ext) {
		this.ext = ext;
		try {				
			URL url = new URL("https://s3-us-west-1.amazonaws.com/battlenetwork.gamedata/chips.json");			
			Scanner sc = new Scanner(url.openStream());
 
			StringBuffer sb = new StringBuffer();
			while(sc.hasNext()) {
				sb.append(sc.next());		         
			}
			//Retrieving the String from the String Buffer object
			String chipJsonStr = sb.toString();
						
			//String chipJsonStr = new String(readAllBytes(get(baseDir + "./chips.json")));
			this.chips = JSONObject.fromObject(chipJsonStr);
			
			//this.ext.trace("LOADED GAME DATA FROM URL");
			
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
		public static final String DEPTH = "depth";
		public static final String WIDTH = "width";
		public static final String DURATION = "duration";
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
