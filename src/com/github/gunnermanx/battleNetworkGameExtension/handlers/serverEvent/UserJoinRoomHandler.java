package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import java.sql.SQLException;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserJoinRoomHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();		
		User user = (User) event.getParameter(SFSEventParam.USER);
		
		if (user.isPlayer() && !ext.IsGameStarted()) {
			int accId = (int) user.getProperty("account");
			
			// TODO: this has to be set and grabbed from somewhere
			int activeDeckId = 1;
			
			
    		// Get the player decks info
			IDBManager dbManager = this.getParentExtension().getParentZone().getDBManager();

			// Get the player decks info
    		String deckQuery = "SELECT pd.copies, pc.chip_data_id, pc.level " + 
    							"FROM player_deck_entries pd " + 
    							"LEFT JOIN player_chips pc " + 
    							"ON pd.chip_id = pc.id " + 
    							"WHERE pd.account_id = ? AND pd.deck_id = ?";    		    	
    		try {
				ISFSArray deckResArr = dbManager.executeQuery(deckQuery, new Object[] {accId, activeDeckId});
				ext.Game().createPlayer(user, deckResArr);
				
				if (ext.getParentRoom().getPlayersList().size() == 2) {	
					//ext.trace("Calling PlayersPresent");
					ext.PlayersPresent();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}	
	}

}
