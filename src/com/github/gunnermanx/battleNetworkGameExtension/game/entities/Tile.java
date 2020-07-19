package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

public class Tile {
	
	public Arena.Ownership owner;
	public State state;
	
	public Tile(Arena.Ownership owner, State state) {
		this.owner = owner;
		this.state = state;
	}
	
	public enum State {
		EMPTY(0),
		BROKEN(1),
		OCCUPIED(2);
		
		private int val;
		
		private State(int id) {
			this.val = id;
		}		
	}
}
