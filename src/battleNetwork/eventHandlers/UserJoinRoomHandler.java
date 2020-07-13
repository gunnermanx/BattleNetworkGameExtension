package battleNetwork.eventHandlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import battleNetwork.BattleNetworkExtension;

public class UserJoinRoomHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();		
		User user = (User) event.getParameter(SFSEventParam.USER);
		
		if (user.isPlayer() && !ext.IsGameStarted()) {
			
			ext.Game().CreatePlayer(user);
			
			if (ext.getParentRoom().getSize().getUserCount() == 2) {
				ext.PlayersPresent();
			}
		}	
	}

}
