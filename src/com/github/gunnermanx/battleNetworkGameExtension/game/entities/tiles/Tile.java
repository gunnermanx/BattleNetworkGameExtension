package com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

public class Tile {
	
	public Owner owner;
	public State state;
	public BaseTileEffect effect;
	
	public Tile(Owner owner, State state) {
		this.owner = owner;
		this.state = state;
	}
	
	
	public void Advance() {
		// check for tile effect
		if (effect != null) {
			effect.Advance();
			if (effect.IsComplete()) {
				effect = null;
			}
		}
	}
	
	
	public void UnitEnteredTile(Unit unit) {
		if (effect != null) {
			effect.EnteredTile(unit);
		}
	}
	
	public void UnitExitedTile(Unit unit) {
		if (effect != null) {
			effect.ExitedTile(unit);
		}
	}
	
	public enum State {
		EMPTY(0),
		BROKEN(1),
		OCCUPIED(2);
		
		private State(int id) {}		
	}
}
