package com.github.gunnermanx.battleNetworkGameExtension.game.chips;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;

import net.sf.json.JSONObject;

public class ChipFactory {
	
	public static Chip getChip(BattleNetworkGame game, short cid, Player player, JSONObject data, int pUnitX, int pUnitY) {
		
		switch (cid) {
			case (short) 10:
				return new Cannon(game, player, data, pUnitX, pUnitY);
			case (short) 11:
				return new Vulcan(game, player, data, pUnitX, pUnitY);
			case (short) 20:
				return new Missile(game, player, data, pUnitX, pUnitY);
			case (short) 50:
				return new Sword(game, player, data, pUnitX, pUnitY);
			case (short) 80:
				return new Poison(game, player, data, pUnitX, pUnitY);
			case (short) 100:
				return new AreaGrab(game, player, data, pUnitX, pUnitY);
		} 
		
		
		return null;		
	}
}
