package battleNetwork;

import com.smartfoxserver.v2.entities.User;

import battleNetwork.entities.Arena;
import battleNetwork.entities.Player;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


// Responsible for the game and all entities
// The main game logic resides here
// Logic pertaining to each element of the game, 
// eg arena, can be found in their own classes.

// Player is a concept within the scope of the game,
// Other classes deal with User and their PlayerId
// Arena, Unit, Projectiles are also 'unknown' to things outside of
// BattleNetworkGame and they should be modified through this class
public class BattleNetworkGame {
	
	private static final int TICKS_PER_ENERGY = 10;
	
	public Player player1;
	public Player player2;	
	
	public Arena arena;
	
	private BattleNetworkExtension ext;
	
	
	public BattleNetworkGame(BattleNetworkExtension ext) {
		this.ext = ext;
		this.arena = new Arena(ext);
		this.arena.LoadArenaData();
		this.arena.LoadPlayerUnitData();
	}
	
	public void HandleTick(int currentTick) {		
		// i dont think this would actually work quite as expected 
		// if you are at 10, you are capped, and may spend at any tick that is not % TICKS_PER_ENERGY...
		// but maybe thats too complex
		if (currentTick > 0 && (currentTick % TICKS_PER_ENERGY == 0)) {
			
			//this.ext.trace("tick up for energy!");
			
			if (player1 != null && player1.energy < 10) {
				player1.energy ++;
				this.ext.QueueEnergyChanged(1, 1);
			}
			if (player2 != null && player2.energy < 10) {
				player2.energy ++;
				this.ext.QueueEnergyChanged(2, 1);
			}			
		}
		
		arena.TickProjectiles();
	}
	
	public void CreatePlayer(User user) {
		// TODO see if there are reconnection issues
		int id = user.getPlayerId();
		if (id == 1) {
			if (player1 == null) {
				player1 = new Player(user, Arena.Ownership.PLAYER1);
			}
		} else if (id == 2) {
			if (player2 == null) {
				player2 = new Player(user, Arena.Ownership.PLAYER2);
			}
		}
	}	
	
	
	public void PlayerBasicAttack(int playerId) {
		Arena.BasicAttackResult result = arena.BasicAttackFromPlayerUnit(playerId);				
		if (result.target != null) {
			ext.QueueDamageDealt(result.target.id, result.damage);
		} 
	}
	
	public void MovePlayer(int playerId, byte dir) {
		Arena.TryMovePlayerUnitResult result = arena.TryMovePlayerUnit(playerId, dir);				
		if (result.valid) {
			ext.QueueMoveStateChange(playerId, result.x, result.y);
		}	
	}
	
	public void PlayChip(int playerId, int cid) {		
		// we know cid is valid, already validated in the handler
		JSONObject chipJson = ext.GameData().GetChipData(cid);
		
		this.ext.trace("play chip");	
		
		// Get the cost, validate and deduct
		int chipCost = chipJson.getInt(GameData.ChipDataKeys.COST);
		Player player = GetPlayer(playerId);
		if (player == null || player.energy < chipCost) {
			// logs?
			// error?
			this.ext.trace("ERROR is null or player has less energy than the cost");
			
			return;
		}
		player.energy -= chipCost;
		this.ext.QueueEnergyChanged(playerId, -chipCost);
		
		// For each projectile listed, we want to spawn it
		JSONArray projectiles = chipJson.getJSONArray(GameData.ChipDataKeys.PROJECTILES);
		for (int i = 0; i < projectiles.size(); i++) {
			JSONObject projectileJson = projectiles.getJSONObject(i);
			int pid = projectileJson.getInt(GameData.ProjectileDataKeys.ID);
			
			// validate projectile id, in case of errors
			if (ext.GameData().IsValidProjectileId(pid)) {			
				this.arena.SpawnProjectile(player.owner, pid);
				this.ext.QueueSpawnProjectile(playerId, pid);
			}
		}	
	}
	
	
	
	private Player GetPlayer(int id) {
		if (id == 1) {
			return player1;
		} else if (id == 2) {
			return player2;
		} else {
			return null;
		}
	}
	
}
