package com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;
import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class ChipPlayedHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();
		if (!ext.IsGameStarted()) {
			return;
		}
		
		if (!params.containsKey("cid")) {
			throw new SFSRuntimeException("Invalid request, missing x or y");
		}
		
		short cid = params.getShort("cid");	// direction
		
		// assumption: all chips have some kind of target, or originates from player unit, or set locations

		
		// Validation checks on concepts OUTSIDE of BattleNetworkGame
		// eg: is the chip actually in this player's deck?
		//     is this a valid chip id?
		//     has the game started?
		if (!ext.GameData().IsValidChipId(cid)) {
			// logs?
			// error?
			return;
		}		
		
		// the chip id
		ext.Game().playChip(user.getPlayerId(), cid);
	}

}
