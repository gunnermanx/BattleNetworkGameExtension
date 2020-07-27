package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import java.util.Arrays;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinZoneEventHandler extends BaseServerEventHandler {

	private final String ACCOUNT = "account";
	private final String LEVEL = "level";
	private final String XP = "xp";
	private final String POINTS = "points";
	private final String DECK_ENTRIES = "deckEntries";
	private final String CHIPS = "chips";
	 
	@Override
	public void handleServerEvent(ISFSEvent evt) throws SFSException {
		
		User user = (User) evt.getParameter(SFSEventParam.USER);
		
		BattleNetworkZoneExtension ext = (BattleNetworkZoneExtension) this.getParentExtension();		
		ISFSApi sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		
		SFSUserVariable rankVar = new SFSUserVariable("rank", 1);		
		sfsApi.setUserVariables(user, Arrays.asList(rankVar));
		
		// Get account properties from session
		ISession session = user.getSession();
		int accId = (int) session.getProperty(ACCOUNT);
		
		// Account id
		user.setProperty(ACCOUNT, accId);
		
		// DBManager
		IDBManager dbManager = this.getParentExtension().getParentZone().getDBManager();
		try {
			// Get player account information
    		String accountQuery = "SELECT * FROM player_accounts pa WHERE pa.id = ?";			
    		ISFSArray accountResArr = dbManager.executeQuery(accountQuery, new Object[] {accId});
    		if (accountResArr.size() > 0) {    			
    			ISFSObject res = accountResArr.getSFSObject(0);
    			
    			UserVariable levelVariable = new SFSUserVariable(LEVEL, res.getInt("level"));
    			UserVariable xpVariable = new SFSUserVariable(XP, res.getInt("xp"));
    			UserVariable pointsVariable = new SFSUserVariable(POINTS, res.getInt("points"));
    			
    			this.getApi().setUserVariables(user, Arrays.asList(
    				levelVariable,
    				xpVariable,
    				pointsVariable
    			));
    			
    			ext.trace(String.format("id:%d, level:%d, xp:%d, points:%d", 
    					accId, 
    					(byte) levelVariable.getValue(), 
    					xpVariable.getIntValue(), 
    					(short) pointsVariable.getValue()
    			));		
    		}    
    		    		
    		
		} catch(Exception e) {
			// TODO
			this.trace(e.toString());
		}		
	}

}
