package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerDeckEntry;
import com.smartfoxserver.v2.entities.User;

public class Player {
	public int id;
	public User user;
	public int energy = 0;
	public Arena.Ownership owner;
	public Unit unit;
	public Deck deck;
	
	public Player(int id, User user, Arena.Ownership owner) {
		this.id = id;
		this.user = user;
		this.energy = 0;
		this.owner = owner;
		
		InitializeDeck();
	}
	
	private void InitializeDeck() {
		PlayerAccount acc = (PlayerAccount) this.user.getProperty("account");
		
		// assume for now active deck is 1;
		short activeDeckId = 1;
		
		List<PlayerDeckEntry> playerDeckEntries = acc.GetPlayerDeckEntries();
		List<PlayerDeckEntry> activeDeck = new ArrayList<PlayerDeckEntry>();
		for (int i = 0; i < playerDeckEntries.size(); i++) {
			if (playerDeckEntries.get(i).GetDeckId() == activeDeckId) {
				activeDeck.add(playerDeckEntries.get(i));
			}
		}
		
		this.deck = new Deck(activeDeck);		
		
		//this.deck.DebugPrint();
	}
	
	
	public boolean HasChipInHand(short chipId) {
		return this.deck.IsChipInHand(chipId); 
	}
	
	public short PlayChipAndGetNext(short chipId) {
		return this.deck.PlayChipAndReturnNext(chipId);
	}
}
