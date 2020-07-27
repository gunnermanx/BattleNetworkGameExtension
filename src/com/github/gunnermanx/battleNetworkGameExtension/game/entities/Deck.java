package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class Deck {
	
	private List<DeckEntry> hand = new ArrayList<DeckEntry>();
	private List<DeckEntry> deck = new ArrayList<DeckEntry>();
	
	public Deck(ISFSArray deckData) {		
		// Go through all deckEntries to create a deck
		for (int i = 0; i < deckData.size(); i++) {
			// For every copy create a PrivateDeckEntry
			ISFSObject deckEntry = deckData.getSFSObject(i);
			
			for (short j = 0; j < deckEntry.getInt("copies"); j++) {
				this.deck.add(new DeckEntry(deckEntry.getInt("chip_data_id").shortValue(), deckEntry.getInt("level")));				
			}			
		}
		
		// shuffle the deck
		int i = this.deck.size();
		while (i > 1) {
			i--;
			int j = (int) Math.floor(Math.random() * i+1);
			DeckEntry tmp = this.deck.get(j); 
			this.deck.set(j, this.deck.get(i)); 
			this.deck.set(i, tmp);
		}
		
		// draw cards
		int handSize = 4;
		for (int j = 0; j < handSize; j++) {
			draw();
		}
	}		
	
	public boolean isChipInHand(short chipId) {
		for (int i = 0; i < this.hand.size(); i++) {
			if (hand.get(i).cid == chipId) {
				return true;
			}
		}
		return false;
	}
	
	public short[] playChipAndReturnNext(short chipId) {
		returnChip(chipId);
		short[] res = new short[2];
		res[0] = draw();
		res[1] = getTopCidInDeck();
		return res;
	}
	
	public short draw() {
		DeckEntry drawnChip = this.deck.get(0);
		this.deck.remove(0);
		hand.add(drawnChip);
		return drawnChip.cid;
	}
	
	public void returnChip(short chipId) {
		// find the first chip in hand that has the chip id and return it to the deck
		int idxToRemove = -1;
		for (int i = 0; i < this.hand.size(); i++) {
			if (hand.get(i).cid == chipId) {
				idxToRemove = i;
				break;
			}
		}
		DeckEntry returnedChip = this.hand.get(idxToRemove);
		this.hand.remove(idxToRemove);
		this.deck.add(returnedChip);
	}
	
	public short[] getChipIdsInHand() {
		short[] cids = new short[4];
		for (int i = 0; i < this.hand.size(); i++ ) {
			cids[i] = this.hand.get(i).cid;
		}
		return cids;
	}
	
	public short getTopCidInDeck() {
		return this.deck.get(0).cid;
	}
	
	
	
	
	private class DeckEntry {
		public short cid;
		public int level;
		
		public DeckEntry(short cid, int level) {
			this.cid = cid;
			this.level = level;
		}
	}
	
	
	
	public void debugPrint() {
		SFSExtension ext = (SFSExtension) SmartFoxServer.getInstance().getZoneManager().getZoneByName("BattleNetwork").getExtension();
		
		for (int i = 0; i < this.deck.size(); i++) {
			ext.trace(String.format("Deck: Pos: %d => chipId: %d", i, this.deck.get(i).cid));
		}
		
		for (int i = 0; i < this.hand.size(); i++) {
			ext.trace(String.format("Hand: Pos: %d => chipId: %d", i, this.hand.get(i).cid));
		}
	}
	
	
}
