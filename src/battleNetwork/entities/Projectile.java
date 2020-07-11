package battleNetwork.entities;

import com.smartfoxserver.v2.entities.data.SFSArray;

public abstract class Projectile {
	public Arena.Ownership owner;
	public int cid;
	public int posX, posY;
	public int speed;
	public int progress;
	
	public Projectile(Arena.Ownership owner, int cid, int posX, int posY) {
		this.owner = owner;
		this.cid = cid;
		this.posX = posX;
		this.posY = posY;
		
		// speed is 1grid per X ticks, or X ticks/1grid
		this.speed = 10;
		this.progress = 0;
	}
	
	public abstract void Advance();
	
}
