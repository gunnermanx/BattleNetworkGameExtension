package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;


import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserDisconnectHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent evt) throws SFSException {
		
		User user = (User) evt.getParameter(SFSEventParam.USER);
		
		BattleNetworkZoneExtension ext = (BattleNetworkZoneExtension) this.getParentExtension();		
		ISFSApi sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		
//		ext.matchmakingUsers.remove(user);
//		
//		for (int i = 0; i < ext.matchmakingUsers.size(); i++) {
//			ext.trace(String.format("    user: %s", ext.matchmakingUsers.get(i).getName()));
//		}
		
	}

}
