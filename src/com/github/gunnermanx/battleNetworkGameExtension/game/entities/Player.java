package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import com.smartfoxserver.v2.entities.User;

public class Player {
	public int id;
	public User user;
	public int energy = 0;
	public Arena.Ownership owner;
	public Unit unit;
	
	public Player(int id, User user, Arena.Ownership owner) {
		this.id = id;
		this.user = user;
		this.energy = 0;
		this.owner = owner;
	}
	
	
}
