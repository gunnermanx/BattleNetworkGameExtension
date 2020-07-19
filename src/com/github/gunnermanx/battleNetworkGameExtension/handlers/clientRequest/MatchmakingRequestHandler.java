package com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest;

import java.util.Date;
import java.util.List;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkZoneExtension;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.CreateRoomSettings.RoomExtensionSettings;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.api.ISFSGameApi;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.match.MatchExpression;
import com.smartfoxserver.v2.entities.match.NumberMatch;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.game.CreateSFSGameSettings;

public class MatchmakingRequestHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		BattleNetworkZoneExtension ext = (BattleNetworkZoneExtension) this.getParentExtension();
		
		if (ext.matchmakingUsers.contains(user)) {
			ext.trace(String.format("user %s is already matchmaking", user.getName()));
			return;
		}
		
		Zone zone = ext.getParentZone();
		
		ISFSGameApi gameApi = SmartFoxServer.getInstance().getAPIManager().getGameApi();
		ISFSApi sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
						
		// user asks to matchmake, check first to see if any waiting users are able to match with this user
		MatchExpression expr = new MatchExpression("rank", NumberMatch.GREATER_THAN, 0)
				.and("rank", NumberMatch.LESS_THAN, 10);
		List<User> matchedUser = sfsApi.findUsers(ext.matchmakingUsers, expr, 1);		
		if (matchedUser.size() > 0) {
			ext.trace(String.format("Found a match for user %s, matched with %s", user.getName(), matchedUser.get(0).getName()));
			
			// we found a user, lets remove the user from the matchmaking list
			ext.matchmakingUsers.remove(matchedUser.get(0));
			// create a room for both users
			CreateSFSGameSettings settings = new CreateSFSGameSettings();
			settings.setName((new Date()).getTime() + "_" + ext.matchmakingUsers.size());
			settings.setGamePublic(true);
			settings.setMinPlayersToStartGame(2);
			settings.setLeaveLastJoinedRoom(true);
			settings.setMaxUsers(2);			
			settings.setMaxSpectators(0);
			settings.setGroupId("games");
			settings.setDynamic(true);
			settings.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
			settings.setExtension(new RoomExtensionSettings( "BattleNetwork", "com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension"));
			
			try {
				ext.matchmakingUsers.remove(matchedUser.get(0));
				
				Room room = gameApi.createGame(zone, settings, user);					
				//sfsApi.joinRoom(user, room);
				sfsApi.joinRoom(matchedUser.get(0), room);
				
			} catch (SFSCreateRoomException | SFSJoinRoomException e2) {
				// TODO, actually handle 
				e2.printStackTrace();
			}
		} else {
			// couldnt find a match so add user to matchmakingUsers
			ext.trace(String.format("Couldn't find a match for user %s", user.getName()));
			ext.matchmakingUsers.add(user);
			
			for (int i = 0; i < ext.matchmakingUsers.size(); i++) {
				ext.trace(String.format("    user: %s", ext.matchmakingUsers.get(i).getName()));
			}
		}
			
	}

}
 