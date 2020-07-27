package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;

public class Player {
	public int id;
	public User user;
	public int energy = 0;
	public Owner owner;
	public Unit unit;
	public Deck deck;
	
	public Player(int id, User user, Owner owner, ISFSArray deckData) {
		this.id = id;
		this.user = user;
		this.energy = 0;
		this.owner = owner;				
		this.deck = new Deck(deckData);
	}
	
	
	
	public boolean hasChipInHand(short chipId) {
		return this.deck.isChipInHand(chipId); 
	}
	
	public short[] playChipAndGetNext(short chipId) {
		return this.deck.playChipAndReturnNext(chipId);
	}
}
