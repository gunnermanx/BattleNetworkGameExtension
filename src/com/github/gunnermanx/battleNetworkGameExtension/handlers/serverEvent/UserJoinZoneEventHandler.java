package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import java.util.Arrays;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinZoneEventHandler extends BaseServerEventHandler {

	private final String ACCOUNT_ID = "account_id";
	private final String LEVEL = "level";
	private final String XP = "xp";
	private final String POINTS = "points";
	
	@Override
	public void handleServerEvent(ISFSEvent evt) throws SFSException {
		
		User user = (User) evt.getParameter(SFSEventParam.USER);
		
		BattleNetworkZoneExtension ext = (BattleNetworkZoneExtension) this.getParentExtension();		
		ISFSApi sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		
		SFSUserVariable rankVar = new SFSUserVariable("rank", 1);		
		sfsApi.setUserVariables(user, Arrays.asList(rankVar));
		
		// Get account properties from session
		ISession session = user.getSession();
		int accountId = (int) session.getProperty(ACCOUNT_ID);
		session.removeProperty(ACCOUNT_ID);
		byte level = (byte) session.getProperty(LEVEL);
		session.removeProperty(LEVEL);
		int xp = (int) session.getProperty(XP);
		session.removeProperty(XP);
		short points = (short) session.getProperty(POINTS);
		session.removeProperty(POINTS);		
		
		// Store data from session into user properties
		UserVariable accountIdVariable = new SFSUserVariable(ACCOUNT_ID, accountId);
		UserVariable levelVariable = new SFSUserVariable(LEVEL, level);
		UserVariable xpVariable = new SFSUserVariable(XP, xp);
		UserVariable pointsVariable = new SFSUserVariable(POINTS, points);
		
		this.getApi().setUserVariables(user, Arrays.asList(
			accountIdVariable,
			levelVariable,
			xpVariable,
			pointsVariable
		));
		
		ext.trace(String.format("id:%d, level:%d, xp:%d, points:%d", accountId, level, xp, points));
	}

}
