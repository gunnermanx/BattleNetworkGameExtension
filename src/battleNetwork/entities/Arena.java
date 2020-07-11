package battleNetwork.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.smartfoxserver.v2.entities.User;

import battleNetwork.BattleNetworkExtension;

public class Arena {
	private static final int ARENA_WIDTH = 3;
	private static final int ARENA_LENGTH = 6;
	
	private static final int P1_PLAYERUNIT_ID = 1;
	private static final int P2_PLAYERUNIT_ID = 2;
	
	// Incremented for every new unit made
	private int currentUnitId = P2_PLAYERUNIT_ID + 1;
	
	private Tile[][] arena;
	
	// units should be split into two LinkedLists
	// easier to track projectile hits
	private LinkedList<Unit> p1Units;
	private LinkedList<Unit> p2Units;
	
	private LinkedList<Projectile> projectiles;
	
	
	private Unit p1PlayerUnit;
	private Unit p2PlayerUnit;
	
	
	private BattleNetworkExtension ext;
	
	public Arena(BattleNetworkExtension ext) {
		arena = new Tile[ARENA_LENGTH][ARENA_WIDTH];
		p1Units = new LinkedList<Unit>();
		p2Units = new LinkedList<Unit>();
		projectiles = new LinkedList<Projectile>();
		
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
		p2PlayerUnit = SpawnPlayerUnit(Arena.Ownership.PLAYER2, "pu2", 5, 1);
	}
	
	
	public Unit SpawnPlayerUnit(Arena.Ownership owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding player unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));		
		Unit u = null;
		if (owner == Arena.Ownership.PLAYER1) {
			u = new Unit(P1_PLAYERUNIT_ID, owner, type, posX, posY, hp);
			p1Units.add(u);
		} else if (owner == Arena.Ownership.PLAYER2) {
			u = new Unit(P2_PLAYERUNIT_ID, owner, type, posX, posY, hp);
			p2Units.add(u);
		}
		return u;
	}
	
	public Unit SpawnUnit(Arena.Ownership owner, String type, int posX, int posY) {		
		// TEMPORARY
		int hp = 100;
		
		ext.trace(String.format("Adding unit. owner: %s, type: %s, id: %d at [%d,%d]", owner, type, currentUnitId, posX, posY));
		
		Unit u = new Unit(currentUnitId, owner, type, posX, posY, hp);
		if (owner == Arena.Ownership.PLAYER1) {
			p1Units.add(u);
		} else if (owner == Arena.Ownership.PLAYER2) {
			p2Units.add(u);
		}
		return u;
	}
	
	public void SpawnProjectile(Arena.Ownership owner, int cid) {
		// TEMP Treat everything as a straight projectile for test
		int x = 0; 
		int y = 0;
		if (owner == Arena.Ownership.PLAYER1) {
			x = p1PlayerUnit.posX;
			y = p1PlayerUnit.posY;
		} else if (owner == Arena.Ownership.PLAYER2) {
			x = p2PlayerUnit.posX;
			y = p2PlayerUnit.posY;
		}
		
		StraightProjectile p = new StraightProjectile(owner, cid, x, y);
		projectiles.add(p);
		
		// send message to client that straight projectile has spawned		
	}
	
	public void TickProjectiles() {
		// Advance each projectile
		Iterator<Projectile> projectileIter = projectiles.iterator();
		while (projectileIter.hasNext()) {
			// Advance the projectile
			Projectile p = projectileIter.next();
			p.Advance();
			
			// this.ext.trace(String.format("Advancing projectile [%d, %d]", p.posX,  p.posY));
			
			// check if the projectile hit anything
			// not going to be that costly, since there wont be that many projectiles / units
			
			LinkedList<Unit> units;
			
			if (p.owner == Arena.Ownership.PLAYER1) {
				units = p2Units;
			} else {
				units = p1Units;
			}
			
			Iterator<Unit> unitIter = p2Units.iterator();
			while (unitIter.hasNext()) {
				Unit u = unitIter.next();
				
				//this.ext.trace(String.format("Unit at [%d, %d]", u.posX,  u.posY));
				
				// HIT!
				if (u.posX == p.posX && u.posY == p.posY) {
					
					this.ext.trace(String.format("  hit!"));
					
					int damage = 10;						
					u.hitpoints -= damage;						
					ext.QueueDamageDealt(u.id, damage);
					if (u.hitpoints <= 0) {
						unitIter.remove();
						
						// TODO, if this is a player unit we need to trigger some game ending logic
					}
					
					// remove projectile
					projectileIter.remove();
					break;
				}
				else if (p.posX >= ARENA_LENGTH || p.posX < 0 || p.posY >= ARENA_WIDTH || p.posY < 0) {
					projectileIter.remove();
					break;
				}
			}
		}		
	}
	
	public BasicAttackResult BasicAttackFromPlayerUnit(int playerId) {
		// later data about the attack should have been loaded first
		int basicAttackDmg = 1;
		
		Unit target = null;
		
		if (playerId == 1) {								
			for (int i = 0; i < p2Units.size(); i++) {
				Unit candidate = p2Units.get(i);
				if (candidate.posY == p1PlayerUnit.posY) {
					if (target == null) {
						target = candidate;
					} else if (target.posX > candidate.posY) {
						target = candidate;
					}
				}
			}			
		} else if (playerId == 2){
			for (int i = 0; i < p1Units.size(); i++) {
				Unit candidate = p1Units.get(i);
				if (candidate.posY == p2PlayerUnit.posY) {
					if (target == null) {
						target = candidate;
					} else if (target.posX < candidate.posY) {
						target = candidate;
					}
				}
			}
		}
		
		if (target != null) {
			target.hitpoints = target.hitpoints - basicAttackDmg;
		}
				
		return new BasicAttackResult(target, basicAttackDmg);
	}
	
	public TryMovePlayerUnitResult TryMovePlayerUnit(int playerId, byte dir) {
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
						u.posY++;
						return new TryMovePlayerUnitResult(true, u.posX, u.posY);
					}
					break;
				case (byte)'d':
					if (IsPathable(u.owner, u.posX, u.posY-1)) {
						u.posY--;
						return new TryMovePlayerUnitResult(true, u.posX, u.posY);
					}
					break;
				case (byte)'l':
					if (IsPathable(u.owner, u.posX-1, u.posY)) {
						u.posX--;
						return new TryMovePlayerUnitResult(true, u.posX, u.posY);
					}
					break;
				case (byte)'r':
					if (IsPathable(u.owner, u.posX+1, u.posY)) {
						u.posX++;
						return new TryMovePlayerUnitResult(true, u.posX, u.posY);
					}
					break;
				default:
					break;
			}
		}
		
		return new TryMovePlayerUnitResult(false, 0, 0);
	}
	
	private boolean IsPathable(Arena.Ownership owner, int x, int y) {
		if (x >= ARENA_LENGTH || x < 0 || y >= ARENA_WIDTH || y < 0) {
			return false;
		}
		
		return arena[x][y].owner == owner && 
				arena[x][y].state == Tile.State.EMPTY;
	}
	
		
	
	
	//public class Player
	
	
	public enum Ownership {
		NEUTRAL(0),
		PLAYER1(1),
		PLAYER2(2);
		
		private int val;
		
		private Ownership(int val) {
			this.val = val;
		}
		
		private int getVal() {
			return val;
		}
	}
	
	public class TryMovePlayerUnitResult {
		public boolean valid = false;
		public int x, y;
		
		public TryMovePlayerUnitResult(boolean valid, int x, int y) {
			this.valid = valid;
			this.x = x;
			this.y = y;
		}
	}
	
	public class BasicAttackResult {
		public Unit target = null;
		public int damage = 0;
		
		public BasicAttackResult(Unit target, int damage) {
			this.target = target;
			this.damage = damage;
		}
	}
}
