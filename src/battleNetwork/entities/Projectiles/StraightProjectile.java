package battleNetwork.entities.Projectiles;

import battleNetwork.entities.Arena;

public class StraightProjectile extends Projectile {
	
	public StraightProjectile(Arena.Ownership owner, int speed, int damage, int posX, int posY) {
		super(owner, speed, damage, posX, posY);
	}
	
	public void Advance() {
		progress++;
		if (progress % speed == 0) {
			if (owner == Arena.Ownership.PLAYER1) {
				this.posX++;
			} else if (owner == Arena.Ownership.PLAYER2) {
				this.posX--;
			}
			progress = 0;			
		}
	}
}
