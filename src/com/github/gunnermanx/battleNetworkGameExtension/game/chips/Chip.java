package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;

import net.sf.json.JSONObject;

public abstract class Chip {
	public Player player;
	public JSONObject chipJSON;
	public int playerX;
	public int playerY;
	protected BattleNetworkGame game;
	protected int progress = 0;
	
	public Chip(BattleNetworkGame game, Player player, JSONObject chipJSON, int playerX, int playerY) {
		this.player = player;
		this.chipJSON = chipJSON;
		this.progress = 0;
		this.playerX = playerX;
		this.playerY = playerY;
		this.game = game;
	}
	
	public abstract void Init();
	
	public abstract void Advance();
	
	public abstract boolean IsComplete();
	
}
