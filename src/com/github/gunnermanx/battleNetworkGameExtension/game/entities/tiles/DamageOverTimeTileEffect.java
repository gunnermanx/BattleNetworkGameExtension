package com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class DamageOverTimeTileEffect extends BaseTileEffect {

	private int damagePerSecond;
	private int progress = 0;
	private Unit unit;
	private int duration;

	public DamageOverTimeTileEffect(BattleNetworkGame game, Owner owner, int damagePerSecond, int duration) {
		super(game, owner);
		this.damagePerSecond = damagePerSecond;
		this.duration = duration;
	}

	@Override
	public void EnteredTile(Unit unit) {
		this.unit = unit;
		game.damageUnit(unit, this.damagePerSecond);
	}

	@Override
	public void ExitedTile(Unit unit) {
		this.unit = null;
	}

	@Override
	public void Advance() {
		progress++;		
		if (this.unit != null && unit.owner != owner) {
			if (progress % BattleNetworkGame.TICKS_PER_SECOND == 0) {				
				game.damageUnit(unit, this.damagePerSecond);				
			}
		}
	}

	@Override
	public boolean IsComplete() {
		return progress >= duration * BattleNetworkGame.TICKS_PER_SECOND;
	}

}
