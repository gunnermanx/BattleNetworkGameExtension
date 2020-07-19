package com.github.gunnermanx.battleNetworkGameExtension.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="player_accounts")
public class PlayerAccount {
	@Id
	@Column(name = "accountId")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

}
