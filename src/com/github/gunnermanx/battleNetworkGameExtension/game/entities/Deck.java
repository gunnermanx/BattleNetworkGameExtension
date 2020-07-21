package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerChip;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerDeckEntry;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class Deck {
	
	private List<PrivateDeckEntry> hand = new ArrayList<PrivateDeckEntry>();
	private List<PrivateDeckEntry> deck = new ArrayList<PrivateDeckEntry>();
	
	public Deck(List<PlayerDeckEntry> deckEntries) {		
		Initialize(deckEntries);
	}
	
	public void DebugPrint() {
		SFSExtension ext = (SFSExtension) SmartFoxServer.getInstance().getZoneManager().getZoneByName("BattleNetwork").getExtension();
		
		for (int i = 0; i < this.deck.size(); i++) {
			ext.trace(String.format("Deck: Pos: %d => chipId: %d", i, this.deck.get(i).chip.GetChipData()));
		}
		
		for (int i = 0; i < this.hand.size(); i++) {
			ext.trace(String.format("Hand: Pos: %d => chipId: %d", i, this.hand.get(i).chip.GetChipData()));
		}
	}
	
	
	private void Initialize(List<PlayerDeckEntry> deckEntries) {
		// Go through all deckEntries to create a deck
		for (int i = 0; i < deckEntries.size(); i++) {
			// For every copy create a PrivateDeckEntry
			for (short j = 0; j < deckEntries.get(i).GetCopies(); j++) {
				this.deck.add(new PrivateDeckEntry(deckEntries.get(i).GetChip()));				
			}			
		}
		
		// shuffle the deck
		int i = this.deck.size();
		while (i > 1) {
			i--;
			int j = (int) Math.floor(Math.random() * i+1);
			PrivateDeckEntry tmp = this.deck.get(j); 
			this.deck.set(j, this.deck.get(i)); 
			this.deck.set(i, tmp);
		}
		
		// draw cards
		int handSize = 4;
		for (int j = 0; j < handSize; j++) {
			Draw();
		}
	}
	
	public boolean IsChipInHand(short chipId) {
		for (int i = 0; i < this.hand.size(); i++) {
			if (hand.get(i).chip.GetChipData() == chipId) {
				return true;
			}
		}
		return false;
	}
	
	public short PlayChipAndReturnNext(short chipId) {
		ReturnChip(chipId);
		return Draw();
	}
	
	public short Draw() {
		PrivateDeckEntry drawnChip = this.deck.get(0);
		this.deck.remove(0);
		hand.add(drawnChip);
		return drawnChip.chip.GetChipData();
	}
	
	public void ReturnChip(short chipId) {
		// find the first chip in hand that has the chip id and return it to the deck
		int idxToRemove = -1;
		for (int i = 0; i < this.hand.size(); i++) {
			if (hand.get(i).chip.GetChipData() == chipId) {
				idxToRemove = i;
				break;
			}
		}
		PrivateDeckEntry returnedChip = this.hand.get(idxToRemove);
		this.hand.remove(idxToRemove);
		this.deck.add(returnedChip);
	}
	
	public short[] GetChipIdsInHand() {
		short[] cids = new short[4];
		for (int i = 0; i < this.hand.size(); i++ ) {
			cids[i] = this.hand.get(i).chip.GetChipData();
		}
		return cids;
	}
	
	private class PrivateDeckEntry {
		public PlayerChip chip;
		
		public PrivateDeckEntry(PlayerChip chip) {
			this.chip = chip;
		}
	}
}
