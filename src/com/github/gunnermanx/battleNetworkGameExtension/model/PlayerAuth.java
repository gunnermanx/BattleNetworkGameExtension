package com.github.gunnermanx.battleNetworkGameExtension.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="player_auth")
public class PlayerAuth {
	@Id
	@Column(name = "authId")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Basic
	@Column(name = "username", nullable = false)
	private String username;
	
	@Basic
	@Column(name = "secret", nullable = false)
	private String secret;
	
	@Id
	@Column(name = "accountId")
	private Long accountId;
	
	
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
	
	public void SetAccountId(Long accountId) {
		this.accountId = accountId;
	}	
	public Long GetAccountId() {
		return this.accountId;
	}
}
