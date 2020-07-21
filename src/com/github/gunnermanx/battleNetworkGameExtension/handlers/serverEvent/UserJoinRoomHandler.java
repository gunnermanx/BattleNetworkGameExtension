package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinRoomHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();		
		User user = (User) event.getParameter(SFSEventParam.USER);
		
		String test = (String) user.getSession().getProperty("test");
		ext.trace(String.format("id:%d, name:%s, property:%s", user.getId(), user.getName(), user.getPlayerId(), test));
		
		if (user.isPlayer() && !ext.IsGameStarted()) {
			ext.Game().CreatePlayer(user);
			
			if (ext.getParentRoom().getPlayersList().size() == 2) {	
				ext.trace("Calling PlayersPresent");
				ext.PlayersPresent();
			}
		}	
	}

}
