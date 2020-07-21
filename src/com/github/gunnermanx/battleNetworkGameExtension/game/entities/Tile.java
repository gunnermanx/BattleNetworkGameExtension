package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;

public class Tile {
	
	public Owner owner;
	public State state;
	
	public Tile(Owner owner, State state) {
		this.owner = owner;
		this.state = state;
	}
	
	public enum State {
		EMPTY(0),
		BROKEN(1),
		OCCUPIED(2);
		
		private State(int id) {}		
	}
}
