package battleNetwork;

import com.smartfoxserver.v2.entities.User;

import battleNetwork.entities.Arena;
import battleNetwork.entities.Player;
import battleNetwork.entities.Unit;
import chips.Chip;
import chips.ChipFactory;
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
	
	// 50ms tick time => 20hz game ticker
	public static final int INTERVAL_MS = 50;
	// Energy is gained at a rate of 1 second
	private static final int MILLISECONDS_PER_ENERGY = 2000;
	// ticks per energy is naturally = MILLISECONDS_PER_ENERGY / INTERVAL_MS
	public static final int TICKS_PER_ENERGY = MILLISECONDS_PER_ENERGY / INTERVAL_MS;
		
	public Player player1;
	public Player player2;	
	
	public Arena arena;
	
	private BattleNetworkExtension ext;
	
	private Chip activeChip;
	
	//private Chip
	
	
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
			
			if (player1 == null) {
				this.ext.Error("Player1 object is null!");
				return;
			}
			if (player2 == null) {
				this.ext.Error("Player2 object is null!");
				return;
			}
			
			
			
			
			if (player1.energy < 10) {
				player1.energy ++;
				this.ext.QueueEnergyChanged(currentTick, 1, 1);
			}
			
			if (player2.energy < 10) {
				player2.energy ++;
				this.ext.QueueEnergyChanged(currentTick, 2, 1);
			}
		}
		
		// Advance any active chips
		if (activeChip != null) {
			activeChip.Advance();
			if (activeChip.IsComplete()) {
				activeChip = null;
			}
		}		
		
		// Ask the Arena to advance projectiles
		arena.TickProjectiles();
	}
	
	
	public void CreatePlayer(User user) {
		// TODO see if there are reconnection issues
		int id = user.getPlayerId();
		
		this.ext.trace("CreatePlayer name:%s, id:%d", user.getName(), id);
		
		if (id == 1) {
			if (player1 == null) {
				player1 = new Player(id, user, Arena.Ownership.PLAYER1);
			}
		} else if (id == 2) {
			if (player2 == null) {
				player2 = new Player(id, user, Arena.Ownership.PLAYER2);
			}
		}
	}	
	
	
	public void PlayerBasicAttack(int playerId) {
		arena.BasicAttackFromPlayerUnit(playerId);
		
	}
	
	public void MovePlayer(int playerId, byte dir) {
		arena.MovePlayerUnit(playerId, dir);		
	}
	
	public void PlayChip(int playerId, int cid) {		
		// we know cid is valid, already validated in the handler
		JSONObject chipJson = ext.GameData().GetChipData(cid);
		
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
		this.ext.QueueEnergyChanged(0, playerId, -chipCost);
		
		Unit playerUnit = arena.GetPlayerUnit(player.owner);
		Chip chip = ChipFactory.getChip(this, cid, player, chipJson, playerUnit.posX, playerUnit.posY);
		if (chip != null) {
			activeChip = chip;
			activeChip.Init();
		}
	}
	
	public void SpawnProjectile(Player player, int projectileID) {
		this.arena.SpawnProjectile(player.owner, projectileID);
		this.ext.QueueSpawnProjectile(player.id, projectileID);
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
