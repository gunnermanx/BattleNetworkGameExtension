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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="player_auth", indexes = { @Index(columnList = "username") })
public class PlayerAuth {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
		
	@Basic
	@Column(name = "username", nullable = false)
	private String username;
	
	@Basic
	@Column(name = "secret", nullable = false)
	private String secret;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id", referencedColumnName = "id", columnDefinition = "INT UNSIGNED NOT NULL")
	private PlayerAccount account;
	
	public PlayerAccount GetAccount() {
		return this.account;
	}
	
	public void SetUsername(String username) {
		this.username = username;
	}
	public String GetUsername() {
		return this.username;
	}
	
	public void SetSecret(String secret) {
		this.secret = secret;
	}	
	public String GetSecret() {
		return this.secret;
	}
	
}
