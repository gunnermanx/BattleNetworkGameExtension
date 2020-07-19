package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import java.util.Arrays;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinZoneEventHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent evt) throws SFSException {
		
		User user = (User) evt.getParameter(SFSEventParam.USER);
		
		BattleNetworkZoneExtension ext = (BattleNetworkZoneExtension) this.getParentExtension();		
		ISFSApi sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		
		SFSUserVariable rankVar = new SFSUserVariable("rank", 1);		
		sfsApi.setUserVariables(user, Arrays.asList(rankVar));
		
		ext.trace(String.format("Setting user var rank for %s", user.getName()));
		
		String test = (String) user.getSession().getProperty("test");
		
		user.getSession().setProperty("test2", "okokok");
		String test2 = (String) user.getSession().getProperty("test2");
		
		ext.trace(String.format("id:%d, name:%s, playerid:%d, test:%s, test2:%s", user.getId(), user.getName(), user.getPlayerId(), test, test2));
	}

}
