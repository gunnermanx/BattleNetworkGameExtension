package battleNetwork.entities.Projectiles;

import battleNetwork.entities.Arena;

public abstract class Projectile {
	public Arena.Ownership owner;
	public int posX, posY;
	public int speed;
	public int progress;
	public int damage;
	
	public Projectile(Arena.Ownership owner, int speed, int damage, int posX, int posY) {
		this.owner = owner;
		this.speed = speed; 		// speed is 1grid per X ticks, or X ticks/1grid
		this.posX = posX;
		this.posY = posY;
		this.damage = damage;
	
		this.progress = 0;
	}
	
	public abstract void Advance();
	
}
