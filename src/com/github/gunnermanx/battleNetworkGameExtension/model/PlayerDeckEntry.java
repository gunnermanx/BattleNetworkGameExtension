package com.github.gunnermanx.battleNetworkGameExtension.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@Basic
	@Column(name = "deck_id", columnDefinition = "TINYINT UNSIGNED NOT NULL")
	private int deckId;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private PlayerAccount account;
	
	@ManyToOne
	@JoinColumn(name = "chip_id")
	private PlayerChip chip;
	
	@Basic
	@Column(name = "copies", columnDefinition = "TINYINT UNSIGNED NOT NULL")
	private byte copies;
}
