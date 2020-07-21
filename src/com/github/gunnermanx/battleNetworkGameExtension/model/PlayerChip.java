package com.github.gunnermanx.battleNetworkGameExtension.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="player_chips")
public class PlayerChip {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id")
	private PlayerAccount account;
	
	@Basic
	@Column(name = "chip_data_id", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
	private short chipDataId;
	
	@Basic
	@Column(name = "level", columnDefinition = "TINYINT UNSIGNED NOT NULL DEFAULT 1")
	private byte level;
	
	@Basic
	@Column(name = "xp", columnDefinition = "SMALLINT UNSIGNED NOT NULL DEFAULT 0")
	private short xp;
	
	public void SetAccount(PlayerAccount account) {
		this.account = account;
	}
	
	public void SetChipDataId(short id) {
		this.chipDataId = id;
	}
	
	public short GetChipData() {
		return this.chipDataId;
	}
	
	public void SetLevel(byte level) {
		this.level = level;		
	}
	
	public byte GetLevel() {
		return this.level;
	}
	
	public void SetXp(short xp) {
		this.xp = xp;		
	}
}
