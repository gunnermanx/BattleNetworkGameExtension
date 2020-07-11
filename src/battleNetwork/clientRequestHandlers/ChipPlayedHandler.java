package battleNetwork.clientRequestHandlers;

import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import battleNetwork.BattleNetworkExtension;

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
		
		// assumption: all chips have some kind of target, or originates from player unit, or set locations
		
		// projectile is on a tile for a certain number of ticks
		// ie 
		//  tick  [n, n+10] on tile A
		//	tick  [n+11, n+20] on tile B
		
		// Validation checks on concepts OUTSIDE of BattleNetworkGame
		// eg: is the chip actually in this player's deck?
		//     is this a valid chip id?
		//     has the game started?
		
		// do some validation of whether the card is in that players deck...
		boolean inDeck = true;
		if (!inDeck) {
			// logs?
			// error?
			return;
		}		
				
		// the chip id
		int cid = 0;
		ext.Game().PlayChip(user.getPlayerId(), cid);
	}

}
