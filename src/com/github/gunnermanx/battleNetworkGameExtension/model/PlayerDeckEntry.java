package com.github.gunnermanx.battleNetworkGameExtension.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="player_deck_entries", indexes = { @Index(columnList = "account_id, deck_id") })
public class PlayerDeckEntry {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Basic
	@Column(name = "deck_id", columnDefinition = "TINYINT UNSIGNED NOT NULL")
	private short deckId;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private PlayerAccount account;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "chip_id")
	private PlayerChip chip;
	
	@Basic
	@Column(name = "copies", columnDefinition = "TINYINT UNSIGNED NOT NULL")
	private byte copies;
	
	public void SetAccount(PlayerAccount account) {
		this.account = account;
	}
	
	public void SetChipId(PlayerChip chip) {
		this.chip = chip;
	}
	
	public void SetDeckId(short id) {
		this.deckId = id;
	}
	
	public short GetDeckId() {
		return this.deckId;
	}
	
	public void SetCopies(byte copies) {
		this.copies = copies;		
	}
	
	public short GetCopies() {
		return this.copies;
	}
	
	public PlayerChip GetChip() {
		return this.chip;
	}
}
