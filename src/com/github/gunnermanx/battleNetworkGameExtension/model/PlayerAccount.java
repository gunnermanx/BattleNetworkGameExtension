package com.github.gunnermanx.battleNetworkGameExtension.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="player_accounts")
public class PlayerAccount {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Basic
	@Column(name = "level", columnDefinition = "TINYINT UNSIGNED NOT NULL DEFAULT 0")
	private byte level;
	
	@Basic
	@Column(name = "xp", columnDefinition = "INT UNSIGNED NOT NULL DEFAULT 0")
	private int xp;
	
	@Basic
	@Column(name = "points", columnDefinition = "SMALLINT UNSIGNED NOT NULL DEFAULT 0")
	private short points;
	
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<PlayerDeckEntry> playerDeck;
	
	public List<PlayerDeckEntry> GetPlayerDeckEntries() {
		return this.playerDeck;
	}
		
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<PlayerChip> playerChips = new ArrayList<PlayerChip>();
	
	
	public List<PlayerChip> GetPlayerChips() {
		return this.playerChips;
	}
	
	
	public void SetId(int id) {
		this.id = id;
	}
	public int GetId() {
		return this.id;
	}
	
	public void SetLevel(byte level) {
		this.level = level;
	}
	public byte GetLevel() {
		return this.level;
	}
	
	public void SetXP(int xp) {
		this.xp = xp;
	}
	public int GetXP() {
		return this.xp;
	}
	
	public void SetPoints(short points) {
		this.points = points;
	}
	public int GetPoints() {
		return this.points;
	}
	
	
	
}
