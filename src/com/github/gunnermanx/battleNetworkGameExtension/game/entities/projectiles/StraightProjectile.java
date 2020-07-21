package com.github.gunnermanx.battleNetworkGameExtension.game.entities.projectiles;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;

public class StraightProjectile extends Projectile {
	
	public StraightProjectile(Owner owner, int speed, int damage, int posX, int posY) {
		super(owner, speed, damage, posX, posY);
	}
	
	public void Advance() {
		progress++;
		if (progress % speed == 0) {
			if (owner == Owner.PLAYER1) {
				this.posX++;
			} else if (owner == Owner.PLAYER2) {
				this.posX--;
			}
			progress = 0;			
		}
	}
}
