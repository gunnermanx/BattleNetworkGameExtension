package battleNetwork.entities;

public class StraightProjectile extends Projectile {
	
	public StraightProjectile(Arena.Ownership owner, int cid, int posX, int posY) {
		super(owner, cid, posX, posY);
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
