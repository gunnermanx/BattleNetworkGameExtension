package battleNetwork.entities.Projectiles;

import battleNetwork.GameData;
import battleNetwork.entities.Arena;
import net.sf.json.JSONObject;

public class ProjectileFactory {
	
	public static Projectile getProjectile(Arena.Ownership owner, JSONObject data, int pUnitX, int pUnitY) {
		
		String type = data.getString(GameData.ProjectileDataKeys.TYPE);
		int speed = data.getInt(GameData.ProjectileDataKeys.SPEED);
		int damage = data.getInt(GameData.ProjectileDataKeys.DAMAGE);
		
		if (type.equals("StraightProjectile")) {
			return new StraightProjectile(owner, speed, damage, pUnitX, pUnitY);
		} 
		
		
		return null;		
	}
}
