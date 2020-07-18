package battleNetwork;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;

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
			
			String projectileJsonStr = new String(readAllBytes(get(baseDir + "./projectiles.json")));
			this.projectiles = JSONObject.fromObject(projectileJsonStr);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	public boolean IsValidChipId(int chipId) {
		String chipIdStr = Integer.toString(chipId);
		return chips.has(chipIdStr);
	}
	
	public JSONObject GetChipData(int chipId) {
		String chipIdStr = Integer.toString(chipId);
		return chips.getJSONObject(chipIdStr);	
	}
	
	
	public boolean IsValidProjectileId(int projectileId) {
		String projectileIdStr = Integer.toString(projectileId);
		return projectiles.has(projectileIdStr);
	}
	
	public JSONObject GetProjectileData(int projectileId) {
		String projectileIdStr = Integer.toString(projectileId);
		return projectiles.getJSONObject(projectileIdStr);	
	}
	
	public class ChipDataKeys {
		public static final String COST = "cost";
		public static final String PROJECTILES = "projectiles";
	}
	
	public class ProjectileDataKeys {
		public static final String ID = "id";
		public static final String TYPE = "type";
		public static final String SPEED = "speed";
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
