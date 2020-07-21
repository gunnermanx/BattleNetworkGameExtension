package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;

import net.sf.json.JSONObject;

public class ChipFactory {
	
	public static Chip getChip(BattleNetworkGame game, short cid, Player player, JSONObject data, int pUnitX, int pUnitY) {
		
		switch (cid) {
			case (short) 0:
				return new Missile(game, player, data, pUnitX, pUnitY);
			case (short) 1:
				return new Cannon(game, player, data, pUnitX, pUnitY);
			case (short) 2:
				return new Sword(game, player, data, pUnitX, pUnitY);
		} 
		
		
		return null;		
	}
}
