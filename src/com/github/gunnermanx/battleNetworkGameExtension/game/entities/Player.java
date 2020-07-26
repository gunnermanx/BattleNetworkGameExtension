package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerDeckEntry;
import com.smartfoxserver.v2.entities.User;

public class Player {
	public int id;
	public User user;
	public int energy = 0;
	public Owner owner;
	public Unit unit;
	public Deck deck;
	
	public Player(int id, User user, Owner owner) {
		this.id = id;
		this.user = user;
		this.energy = 0;
		this.owner = owner;
		
		initializeDeck();
	}
	
	private void initializeDeck() {
		List<PlayerDeckEntry> playerDeckEntries = (List<PlayerDeckEntry>) this.user.getProperty("deckEntries");
		
		// assume for now active deck is 1;
		short activeDeckId = 1;
		
		List<PlayerDeckEntry> activeDeck = new ArrayList<PlayerDeckEntry>();
		for (int i = 0; i < playerDeckEntries.size(); i++) {
			if (playerDeckEntries.get(i).GetDeckId() == activeDeckId) {
				activeDeck.add(playerDeckEntries.get(i));
			}
		}
		
		this.deck = new Deck(activeDeck);		
		
		//this.deck.DebugPrint();
	}
	
	
	public boolean hasChipInHand(short chipId) {
		return this.deck.isChipInHand(chipId); 
	}
	
	public short[] playChipAndGetNext(short chipId) {
		return this.deck.playChipAndReturnNext(chipId);
	}
}
