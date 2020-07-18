package battleNetwork.entities;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import battleNetwork.BattleNetworkExtension;
import battleNetwork.entities.Unit.UnitDamagedListener;
import battleNetwork.entities.Projectiles.Projectile;
import battleNetwork.entities.Projectiles.ProjectileFactory;
import net.sf.json.JSONObject;

public class Arena implements UnitDamagedListener {
	private static final int ARENA_WIDTH = 3;
	private static final int ARENA_LENGTH = 6;
	
	private static final int P1_PLAYERUNIT_ID = 1;
	private static final int P2_PLAYERUNIT_ID = 2;
	
	// Incremented for every new unit made
	private int currentUnitId = P2_PLAYERUNIT_ID + 1;
	
	private Tile[][] arena;
	private Unit[][] units;	

	private CopyOnWriteArrayList<Projectile> projectiles;
	
	
	private Unit p1PlayerUnit;
	private Unit p2PlayerUnit;
	
	
	private BattleNetworkExtension ext;
	
	
	
	
	public Arena(BattleNetworkExtension ext) {
		arena = new Tile[ARENA_LENGTH][ARENA_WIDTH];
		units = new Unit[ARENA_LENGTH][ARENA_WIDTH];
		
		
		
		
//		p1Units = new CopyOnWriteArrayList<Unit>();
//		p2Units = new CopyOnWriteArrayList<Unit>();
		projectiles = new CopyOnWriteArrayList<Projectile>();
		
		this.ext = ext;
	}
	
	// load the data from somewhere later
	public void LoadArenaData() {
		for (int x = 0; x < ARENA_LENGTH; x++) {
			Tile[] arenaCol = arena[x];
			for (int y = 0; y < ARENA_WIDTH; y++) {				
				// TEMP
				if (x < 3) {
					arenaCol[y] = new Tile(Arena.Ownership.PLAYER1, Tile.State.EMPTY);
				} else {
					arenaCol[y] = new Tile(Arena.Ownership.PLAYER2, Tile.State.EMPTY);
				}
			}
		}		
	}
	
	public void LoadPlayerUnitData() {
		p1PlayerUnit = SpawnPlayerUnit(Arena.Ownership.PLAYER1, "pu1", 0, 1);
		p1PlayerUnit.Register(this);
		p2PlayerUnit = SpawnPlayerUnit(Arena.Ownership.PLAYER2, "pu2", 5, 1);
		p2PlayerUnit.Register(this);
	}
	
	@Override
	public void OnUnitDamaged(int unitId, int damage, int currentHitpoints) {
		if (unitId == P1_PLAYERUNIT_ID && currentHitpoints <= 0) {
			this.ext.Player2Victory();
		} else if (unitId == P2_PLAYERUNIT_ID && currentHitpoints <= 0) {
			this.ext.Player1Victory();
		}		
	}
	
	
	
	
	
	public Unit SpawnPlayerUnit(Arena.Ownership owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding player unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));
		
		Unit u = null;
		if (owner == Arena.Ownership.PLAYER1) {
			u = new Unit(P1_PLAYERUNIT_ID, owner, type, posX, posY, hp);
		} else if (owner == Arena.Ownership.PLAYER2) {
			u = new Unit(P2_PLAYERUNIT_ID, owner, type, posX, posY, hp);			
		}
		
		units[posX][posY] = u;
		return u;
	}
	
	public Unit SpawnUnit(Arena.Ownership owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));
		
		Unit u = new Unit(currentUnitId, owner, type, posX, posY, hp);
		
		units[posX][posY] = u;		
		return u;
	}
	
	public void SpawnProjectile(Arena.Ownership owner, int pid) {
		// based on the pid, we want to spawn a type of Projectile
		JSONObject projectileJson = this.ext.GameData().GetProjectileData(pid);
		
		// Clean this up later... just ugly
		int x = 0; 
		int y = 0;
		if (owner == Arena.Ownership.PLAYER1) {
			x = p1PlayerUnit.posX;
			y = p1PlayerUnit.posY;
		} else if (owner == Arena.Ownership.PLAYER2) {
			x = p2PlayerUnit.posX;
			y = p2PlayerUnit.posY;
		}				
		
		Projectile p = ProjectileFactory.getProjectile(owner, projectileJson, x, y);		
		if (p != null) {
			projectiles.add(p);
		} else {
			this.ext.trace("projectile was null!");
		}
	}
	
	public void TickProjectiles() {
		
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
					
					this.ext.trace("hit!");
					//this.ext.trace(String.format("  Hit with projectile %d!", p.toString()));
											
					u.Damage(p.damage);
					ext.QueueDamageDealt(u.id, p.damage);
					
					if (u.CurrentHP() <= 0) {
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
	
	public void BasicAttackFromPlayerUnit(int playerId) {
		// later data about the attack should have been loaded first
		int basicAttackDmg = 1;
		
		Unit target = null;		
		if (playerId == 1) {								
			target = GetFirstEnemyUnitInRow(Ownership.PLAYER1, p1PlayerUnit.posX, p1PlayerUnit.posY);		
		} else if (playerId == 2){
			target = GetFirstEnemyUnitInRow(Ownership.PLAYER2, p2PlayerUnit.posX, p2PlayerUnit.posY);
		}
		
		DamageUnit(target, basicAttackDmg);		
	}
	
	public void DamageUnit(Unit target, int damage) {
		if (target != null) {
			target.Damage(damage);		
			this.ext.QueueDamageDealt(target.id, damage);
		}
	}
	
	public Unit GetFirstEnemyUnitInRow(Ownership owner, int rowX, int rowY) {
		
		if (owner == Ownership.PLAYER1) {
			this.ext.trace("Checking for player1, starting %d, ending %d", rowX, ARENA_LENGTH);
		    for (int x = rowX; x < ARENA_LENGTH; x++) {
		    	Unit u = units[x][rowY];
		    	if (u != null && u.owner != owner) {
		    		return u;
		    	}
		    }
		} else if (owner == Ownership.PLAYER2) {
			this.ext.trace("Checking for player2, starting %d, ending %d", rowX, ARENA_LENGTH);
			for (int x = rowX; x >= 0; x--) {
				Unit u = units[x][rowY];
				if (u != null && u.owner != owner) {
					return u;
		    	}
		    }
		}
		
		return null;
	}
	
	public void MovePlayerUnit(int playerId, byte dir) {
		Unit u;	
		// TODO: sketchy
		if (playerId == 1) {
			u = p1PlayerUnit;
		} else {
			u = p2PlayerUnit;
		}
		
		if (u != null) {
			switch(dir) {
				case (byte)'u':
					if (IsPathable(u.owner, u.posX, u.posY+1)) {
						units[u.posX][u.posY] = null;
						u.posY++;
						units[u.posX][u.posY] = u;
						this.ext.QueueMoveStateChange(playerId, u.posX, u.posY);
					}
					break;
				case (byte)'d':
					if (IsPathable(u.owner, u.posX, u.posY-1)) {
						units[u.posX][u.posY] = null;
						u.posY--;
						units[u.posX][u.posY] = u;
						this.ext.QueueMoveStateChange(playerId, u.posX, u.posY);
					}
					break;
				case (byte)'l':
					if (IsPathable(u.owner, u.posX-1, u.posY)) {
						units[u.posX][u.posY] = null;
						u.posX--;
						units[u.posX][u.posY] = u;
						this.ext.QueueMoveStateChange(playerId, u.posX, u.posY);
					}
					break;
				case (byte)'r':
					if (IsPathable(u.owner, u.posX+1, u.posY)) {
						units[u.posX][u.posY] = null;
						u.posX++;
						units[u.posX][u.posY] = u;
						this.ext.QueueMoveStateChange(playerId, u.posX, u.posY);
					}
					break;
				default:
					break;
			}
		}
	}
	
	
	public Unit GetPlayerUnit(Arena.Ownership owner) {
		if (owner == Arena.Ownership.PLAYER1) {
			return this.p1PlayerUnit;
		} else if (owner == Arena.Ownership.PLAYER2) {
			return this.p2PlayerUnit;
		}
		this.ext.trace("returning null player unit for owner that is none");
		return null;
	}
	
	private boolean IsPathable(Arena.Ownership owner, int x, int y) {
		if (x >= ARENA_LENGTH || x < 0 || y >= ARENA_WIDTH || y < 0) {
			return false;
		}
		
		return arena[x][y].owner == owner && 
				arena[x][y].state == Tile.State.EMPTY;
	}
	
	
	public enum Ownership {
		NEUTRAL(0),
		PLAYER1(1),
		PLAYER2(2);
		
		private int val;
		
		private Ownership(int val) {
			this.val = val;
		}		
	}
}
