package com.github.gunnermanx.battleNetworkGameExtension.game.entities.tiles;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Unit;

public abstract class BaseTileEffect{
	
	protected BattleNetworkGame game;
	protected Owner owner;
	
	public BaseTileEffect(BattleNetworkGame game, Owner owner) {
		this.game = game;
		this.owner = owner;
	}
	
	public abstract void EnteredTile(Unit unit);
	
	public abstract void ExitedTile(Unit unit);
	
	public abstract void Advance();
	
	public abstract boolean IsComplete();
}
