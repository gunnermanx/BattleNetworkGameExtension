package battleNetwork.eventHandlers;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import battleNetwork.BattleNetworkExtension;

public class UserLeaveRoomHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();		
		User user = (User) event.getParameter(SFSEventParam.USER);
		
		Room room = (Room) event.getParameter(SFSEventParam.ROOM);
		
		int userCount = ext.getParentRoom().getPlayersList().size();
		
		ext.trace("player leaving room, %d users remain", userCount);		
		// TODO
		
		
		//ext.getApi().leaveRoom(user, user.getLastJoinedRoom());
		
//		if (userCount == 0) {			
//			//ext.PlayersPresent();
//			//room.destroy();
//			
//			
//		}		
	}

}
