package battleNetwork.clientRequestHandlers;

import com.smartfoxserver.v2.annotations.Instantiation;
import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import battleNetwork.BattleNetworkExtension;
import battleNetwork.entities.Arena;
import battleNetwork.entities.Arena.TryMovePlayerUnitResult;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class MovementHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		if (!params.containsKey("d")) {
			throw new SFSRuntimeException("Invalid request, missing i or d");
		}
		
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();
		if (!ext.IsGameStarted()) {
			return;
		}
		
		byte dir 	= params.getByte("d");	// direction
			
		//trace(String.format("ClientRequest 'move', id: %d, dir: %s", id, dir));
		
		ext.Game().MovePlayer(user.getPlayerId(), dir);				
	}

	
	
}
