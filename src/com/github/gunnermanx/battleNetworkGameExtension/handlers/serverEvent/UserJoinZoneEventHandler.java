package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerChip;
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

	private final String ACCOUNT = "account";
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
		PlayerAccount acc = (PlayerAccount) session.getProperty(ACCOUNT);
		
		try {
			user.setProperty(ACCOUNT, acc);
			
			UserVariable levelVariable = new SFSUserVariable(LEVEL, acc.GetLevel());
			UserVariable xpVariable = new SFSUserVariable(XP, acc.GetXP());
			UserVariable pointsVariable = new SFSUserVariable(POINTS, acc.GetPoints());
						
			List<PlayerChip> chips = acc.GetPlayerChips();
			for (int i = 0; i < chips.size(); i++ ) {
				this.trace(String.format("chip id: %d, level: %d", 
						chips.get(i).GetChipData(),
						chips.get(i).GetLevel()
				));
			}
			
			this.getApi().setUserVariables(user, Arrays.asList(
				levelVariable,
				xpVariable,
				pointsVariable
			));
			
			ext.trace(String.format("id:%d, level:%d, xp:%d, points:%d", 
					acc.GetId(), 
					(byte) levelVariable.getValue(), 
					xpVariable.getIntValue(), 
					(short) pointsVariable.getValue()
			));
			
			
			
		} catch(Exception e) {
			// TODO
			this.trace(e.toString());
		}		
	}

}
