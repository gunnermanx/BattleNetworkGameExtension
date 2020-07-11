package battleNetwork.clientRequestHandlers;

import com.smartfoxserver.v2.annotations.Instantiation;

import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import battleNetwork.BattleNetworkExtension;
import battleNetwork.entities.Arena;
import battleNetwork.entities.Unit;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class BasicAttackHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();
		if (!ext.IsGameStarted()) {
			return;
		}
		
		trace("basic attack received from client");
		
		ext.Game().PlayerBasicAttack(user.getPlayerId());		
	}

}
