package com.github.gunnermanx.battleNetworkGameExtension.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;
import com.github.gunnermanx.battleNetworkGameExtension.game.chips.Chip;
import com.github.gunnermanx.battleNetworkGameExtension.game.chips.ChipFactory;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit.UnitDamagedListener;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.projectiles.Projectile;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles.BaseTileEffect;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles.Tile;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;

import net.sf.json.JSONObject;


// Responsible for the game and all entities
// The main game logic resides here
// Logic pertaining to each element of the game, 
// eg arena, can be found in their own classes.

// Player is a concept within the scope of the game,
// Other classes deal with User and their PlayerId
// Arena, Unit, Projectiles are also 'unknown' to things outside of
// BattleNetworkGame and they should be modified through this class
public class BattleNetworkGame implements UnitDamagedListener {
	
	// 50ms tick time => 20hz game ticker
	public static final int TICKS_PER_SECOND = 20;	
	public static final int INTERVAL_MS = 1000 / TICKS_PER_SECOND;
	// Energy is gained at a rate of 1 second
	private static final int MILLISECONDS_PER_ENERGY = 2000;
	// ticks per energy is naturally = MILLISECONDS_PER_ENERGY / INTERVAL_MS
	public static final int TICKS_PER_ENERGY = MILLISECONDS_PER_ENERGY / INTERVAL_MS;
	// Max energy
	private static final int MAX_ENERGY = 6;
	
	// Milliseconds before the game starts after the tick starts
	private static final int STARTING_TIME_MILLISECONDS = 5000;
	private static final int ROUND_DURATION_MILLISECONDS = 99000;
	
	public static final int ROUND_START_TICK = STARTING_TIME_MILLISECONDS / INTERVAL_MS;
	public static final int ROUND_END_TICK = ROUND_START_TICK + ROUND_DURATION_MILLISECONDS / INTERVAL_MS;
	
	// Arena dimensions
	public static final int ARENA_WIDTH = 3;
	public static final int ARENA_LENGTH = 6;
	
	// Static unit ids for player units
	private static final int P1_PLAYERUNIT_ID = 1;
	private static final int P2_PLAYERUNIT_ID = 2;
	// Incremented for every new unit made
	private int currentUnitId = P2_PLAYERUNIT_ID + 1;
	
	// Game entities and arena state
	private Tile[][] arena;
	private Unit[][] units;	
	private CopyOnWriteArrayList<Projectile> projectiles;
		
	// Players, encapsulates the user and unit
	public Player player1;
	public Player player2;	
		
	// The active chip that is running for the player, only one can be active at a time.
	private Chip p1ActiveChip;
	private Chip p2ActiveChip;
	
	// Handy cached ref to the room extension
	private BattleNetworkExtension ext;
	
	
	public Object p1PlayerLock = new Object();
	public Object p2PlayerLock = new Object();
	
	public AtomicInteger p1Ready = new AtomicInteger(0);
	public AtomicInteger p2Ready = new AtomicInteger(0);
	
	//================================================================================
    // Initialize
    //================================================================================
	public BattleNetworkGame(BattleNetworkExtension ext) {
		this.ext = ext;
		
		arena = new Tile[ARENA_LENGTH][ARENA_WIDTH];
		units = new Unit[ARENA_LENGTH][ARENA_WIDTH];
		
		projectiles = new CopyOnWriteArrayList<Projectile>();
		
		setupArena();	
	}
	
	// load the data from somewhere later
	private void setupArena() {
		for (int x = 0; x < ARENA_LENGTH; x++) {
			Tile[] arenaCol = arena[x];
			for (int y = 0; y < ARENA_WIDTH; y++) {				
				// TEMP
				if (x < 3) {
					arenaCol[y] = new Tile(Owner.PLAYER1, Tile.State.EMPTY);
				} else {
					arenaCol[y] = new Tile(Owner.PLAYER2, Tile.State.EMPTY);
				}
			}
		}		
	}
	
	
	
	//================================================================================
    // Handle game tick
    //================================================================================
	public void handleTick(int currentTick) {
		// Dont do anything unless the game is started
		if (!this.ext.IsGameStarted()) {
			return;
		}
		
		if (player1 == null || player2 == null) {
			this.ext.Error("Player1 or player2 object is null!");
			return;
		}
		
		// Add energy if needed
		tickEnergy(currentTick);
		// Advance any active tile effects
		tickTiles(currentTick);
		// Advance any active chips
		tickActiveChip(currentTick);		
		// Ask the Arena to advance projectiles
		tickProjectiles(currentTick);
	}
	
	private void tickTiles(int currentTick) {
		for (int x = 0; x < ARENA_LENGTH; x++) {
			Tile[] arenaCol = arena[x];
			for (int y = 0; y < ARENA_WIDTH; y++) {				
				arenaCol[y].Advance();
			}
		}
	}
	
	private void tickEnergy(int currentTick) {
		// i dont think this would actually work quite as expected 
		// if you are at 10, you are capped, and may spend at any tick that is not % TICKS_PER_ENERGY...
		// but maybe thats too complex
		if (currentTick > 0 && (currentTick % TICKS_PER_ENERGY == 0)) {	
			if (player1.energy < MAX_ENERGY) {
				player1.energy ++;
				this.ext.QueueEnergyChanged(currentTick, 1, 1);
			}			
			if (player2.energy < MAX_ENERGY) {
				player2.energy ++;
				this.ext.QueueEnergyChanged(currentTick, 2, 1);
			}
		}
	}
	
	private void tickActiveChip(int currentTick) {
		if (p1ActiveChip != null) {
			p1ActiveChip.Advance();
			if (p1ActiveChip.IsComplete()) {
				p1ActiveChip = null;
			}
		}
		if (p2ActiveChip != null) {
			p2ActiveChip.Advance();
			if (p2ActiveChip.IsComplete()) {
				p2ActiveChip = null;
			}
		}
	}
		
	private void tickProjectiles(int currentTick) {		
		// Advance each projectile
		ArrayList<Projectile> projectilesToRemove = new ArrayList<Projectile>();		
		Iterator<Projectile> projectileIter = projectiles.iterator();
		while (projectileIter.hasNext()) {
			Projectile p = projectileIter.next();
			
			// Sanity check
			if (p == null) {
				this.ext.trace("projectile in array was null?!");
				projectilesToRemove.add(p);
				continue;
			}
			
			p.Advance();
						
			//this.ext.trace(String.format("Advancing projectile [%d, %d]", p.posX,  p.posY));
			
			// Check if the projectile is out of bounds
			if (p.posX >= ARENA_LENGTH || p.posX < 0 || p.posY >= ARENA_WIDTH || p.posY < 0) {
				projectilesToRemove.add(p);
				break;
			} else {
				// If there is a unit where the projectile is and its on a different team
				Unit u = units[p.posX][p.posY];
				if (u != null && u.owner != p.owner) {	
					
					//this.ext.trace("hit!");
					//this.ext.trace(String.format("  Hit with projectile %d!", p.toString()));
											
					u.damage(p.damage);
					ext.QueueDamageDealt(u.id, p.damage);
					
					if (u.currentHP() <= 0) {
						// TODO need to trigger a unit death command
						units[p.posX][p.posY] = null;
					}
					
					// remove projectile since it hit a unit
					projectilesToRemove.add(p);
					break;
				}
			}			
		}
		
		// Remove all projectiles that were marked for removal
		projectiles.removeAll(projectilesToRemove);
	}
	
	
	//================================================================================
    // Spawn entities
    //================================================================================	
	public void createPlayer(User user, ISFSArray deck) {
		// TODO see if there are reconnection issues
		int id = user.getPlayerId();
		
		//this.ext.trace("CreatePlayer name:%s, id:%d", user.getName(), id);
		
		if (id == 1) {
			if (player1 == null) {
				synchronized(ext.Game().p1PlayerLock) {
					player1 = new Player(id, user, Owner.PLAYER1, deck);
					player1.unit = spawnPlayerUnit(Owner.PLAYER1, "pu1", 0, 1);
					player1.unit.registerUnitDamagedListener(this);					
					if (p1Ready.intValue() == 1) {
						this.ext.SendChipHandInit(user, player1.deck.getChipIdsInHand(), player1.deck.getTopCidInDeck());
					}
				}
			}
		} else if (id == 2) {
			if (player2 == null) {
				synchronized(ext.Game().p2PlayerLock) {
					player2 = new Player(id, user, Owner.PLAYER2, deck);
					player2.unit = spawnPlayerUnit(Owner.PLAYER2, "pu2", 5, 1);
					player2.unit.registerUnitDamagedListener(this);
					if (p2Ready.intValue() == 1) {
						this.ext.SendChipHandInit(user, player2.deck.getChipIdsInHand(), player2.deck.getTopCidInDeck());
					}
				}
			}
		}
	}
	
	
	public Unit spawnPlayerUnit(Owner owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding player unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));
		
		Unit u = null;
		if (owner == Owner.PLAYER1) {
			u = new Unit(P1_PLAYERUNIT_ID, owner, type, posX, posY, hp);
		} else if (owner == Owner.PLAYER2) {
			u = new Unit(P2_PLAYERUNIT_ID, owner, type, posX, posY, hp);			
		}
		
		units[posX][posY] = u;
		return u;
	}
	
	public Unit spawnUnit(Owner owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));
		
		Unit u = new Unit(currentUnitId, owner, type, posX, posY, hp);
		
		units[posX][posY] = u;		
		return u;
	}		
	
	public void spawnProjectile(Player player, Projectile p) {				
		projectiles.add(p);
	}
	
	
	public void spawnTileEffect(BaseTileEffect tileEffect, int posX, int posY) {
		if (posX >= ARENA_LENGTH || posX < 0 || posY >= ARENA_WIDTH || posY < 0) {
			return;
		}
		Tile t = arena[posX][posY];
		t.effect = tileEffect; 
	}
	
	
	//================================================================================
    // Public game interface, that acts as input to the game
    //================================================================================
	public void playChip(int playerId, short cid) {
		// TODO, check for an active chip before allowing one to be played
		
		Player player = getPlayer(playerId);
		// TODO casting now, fix later
		if (player.hasChipInHand(cid)) {
			// we know cid is valid, already validated in the handler
			JSONObject chipJson = ext.GameData().GetChipData(cid);
			
			// Get the cost, validate and deduct
			int chipCost = chipJson.getInt(GameData.ChipDataKeys.COST);		
			if (player == null || player.energy < chipCost) {
				// logs?
				// error?
				this.ext.trace("ERROR is null or player has less energy than the cost");
				
				return;
			}
			
			this.ext.QueueChipPlayed(playerId, cid);
			
			short[] res = player.playChipAndGetNext(cid);
			this.ext.QueueChipDrawn(playerId, res[0], res[1]);
			
			player.energy -= chipCost;
			this.ext.QueueEnergyChanged(0, playerId, -chipCost);
			
			Unit playerUnit = getPlayer(playerId).unit;
			Chip chip = ChipFactory.getChip(this, cid, player, chipJson, playerUnit.posX, playerUnit.posY);
			if (chip != null) {
				if (playerId == 1) {
					p1ActiveChip = chip;
					p1ActiveChip.Init();
				} else {
					p2ActiveChip = chip;
					p2ActiveChip.Init();
				}
				
			}
		}		
	}
	
	public void movePlayer(int playerId, byte dir) {
		Player player = getPlayer(playerId);
		if (player.unit != null) {
			Unit u = player.unit;
			switch(dir) {
				case (byte)'u':
					if (isPathable(u.owner, u.posX, u.posY+1)) {
						units[u.posX][u.posY] = null;
						arena[u.posX][u.posY].UnitExitedTile(u);
						u.posY++;
						units[u.posX][u.posY] = u;
						arena[u.posX][u.posY].UnitEnteredTile(u);
						this.ext.QueueMoveStateChange(player.id, u.posX, u.posY);
					}
					break;
				case (byte)'d':
					if (isPathable(u.owner, u.posX, u.posY-1)) {
						units[u.posX][u.posY] = null;
						arena[u.posX][u.posY].UnitExitedTile(u);
						u.posY--;
						units[u.posX][u.posY] = u;
						arena[u.posX][u.posY].UnitEnteredTile(u);
						this.ext.QueueMoveStateChange(player.id, u.posX, u.posY);
					}
					break;
				case (byte)'l':
					if (isPathable(u.owner, u.posX-1, u.posY)) {
						units[u.posX][u.posY] = null;
						arena[u.posX][u.posY].UnitExitedTile(u);
						u.posX--;
						units[u.posX][u.posY] = u;
						arena[u.posX][u.posY].UnitEnteredTile(u);
						this.ext.QueueMoveStateChange(player.id, u.posX, u.posY);
					}
					break;
				case (byte)'r':
					if (isPathable(u.owner, u.posX+1, u.posY)) {
						units[u.posX][u.posY] = null;
						arena[u.posX][u.posY].UnitExitedTile(u);
						u.posX++;
						units[u.posX][u.posY] = u;
						arena[u.posX][u.posY].UnitEnteredTile(u);
						this.ext.QueueMoveStateChange(player.id, u.posX, u.posY);
					}
					break;
				default:
					break;
			}
		}
	}
	
	public void playerBasicAttack(int playerId) {
		Player player = getPlayer(playerId);
		// TODO: fix this
		int basicAttackDmg = 1;
		
		Unit target = getFirstEnemyUnitInRow(player.owner, player.unit.posX, player.unit.posY);
		damageUnit(target, basicAttackDmg);
		
		this.ext.QueueBasicAttack(playerId);
	}
	
	
	public void damageUnit(Unit target, int damage) {
		if (target != null) {
			target.damage(damage);		
			this.ext.QueueDamageDealt(target.id, damage);
		}
	}
	
	public Player getPlayer(int id) {
		if (id == 1) {
			return player1;
		} else if (id == 2) {
			return player2;
		} else {
			return null;
		}
	}
		
	
	
	
	@Override
	public void onUnitDamaged(int unitId, int damage, int currentHitpoints) {
		if (unitId == P1_PLAYERUNIT_ID && currentHitpoints <= 0) {
			this.ext.Player2Victory();
		} else if (unitId == P2_PLAYERUNIT_ID && currentHitpoints <= 0) {
			this.ext.Player1Victory();
		}		
	}
	
	
	
	
	//================================================================================
    // Arena State Related Checks
    //================================================================================
	public List<Unit> getUnitsInRange(Owner owner, int rowX, int rowY, int width, int depth) {
		List<Unit> targets = new ArrayList<Unit>();
		
		// assumption: width is really only 1 or 3, doesnt make sense to have a skill with 2
		int startY, endY;
		if (width == 1) {
			startY = rowY;
			endY = rowY;
		} else {
			startY = Math.min(rowY - 1, 0);
			endY = Math.max(rowY+1, ARENA_WIDTH-1);
		}
		
//		this.ext.trace(String.format("getUnitsInRange: Checking startingY %d, endingY %d", startY, endY));
		
		for (int y = startY; y <= endY; y++) {
			if (owner == Owner.PLAYER1) {
				int endX = Math.min(rowX + depth, ARENA_LENGTH);
//				this.ext.trace(String.format("getUnitsInRange: Checking startingX %d, endingX %d", rowX, endX));
			    for (int x = rowX; x <= endX; x++) {
//			    	this.ext.trace(String.format("getUnitsInRange: Checking for player1, [%d,%d]", x, y));
			    	Unit u = units[x][y];
			    	if (u != null && u.owner != owner) {
//			    		this.ext.trace(String.format("found target %d", u.id));
			    		targets.add(u);
			    	}
			    }
			} else if (owner == Owner.PLAYER2) {
				int endX = Math.max(rowX - depth, 0);
//				this.ext.trace(String.format("getUnitsInRange: Checking startingX %d, endingX %d", rowX, endX));
				for (int x = rowX; x >= endX; x--) {
					Unit u = units[x][y];
//					this.ext.trace(String.format("getUnitsInRange: Checking for player2, [%d,%d]", x, y));
					if (u != null && u.owner != owner) {
//						this.ext.trace(String.format("found target %d", u.id));
						targets.add(u);
			    	}
			    }
			}
		}
				
		return targets;
	}
	
	public Unit getFirstEnemyUnitInRow(Owner owner, int rowX, int rowY) {
		
		if (owner == Owner.PLAYER1) {
			//this.ext.trace(String.format("Checking for player1, starting %d, ending %d", rowX, ARENA_LENGTH));
		    for (int x = rowX; x < ARENA_LENGTH; x++) {
		    	Unit u = units[x][rowY];
		    	if (u != null && u.owner != owner) {
		    		return u;
		    	}
		    }
		} else if (owner == Owner.PLAYER2) {
			//this.ext.trace(String.format("Checking for player2, starting %d, ending %d", rowX, ARENA_LENGTH));
			for (int x = rowX; x >= 0; x--) {
				Unit u = units[x][rowY];
				if (u != null && u.owner != owner) {
					return u;
		    	}
		    }
		}
		
		return null;
	}
	
	private boolean isPathable(Owner owner, int x, int y) {
		if (x >= ARENA_LENGTH || x < 0 || y >= ARENA_WIDTH || y < 0) {
			return false;
		}
		
		return arena[x][y].owner == owner && 
				arena[x][y].state == Tile.State.EMPTY;
	}	
	
	
	

	
	
	public enum Owner {
		NEUTRAL(0),
		PLAYER1(1),
		PLAYER2(2);
		
		private Owner(int val) {}
	}
}
