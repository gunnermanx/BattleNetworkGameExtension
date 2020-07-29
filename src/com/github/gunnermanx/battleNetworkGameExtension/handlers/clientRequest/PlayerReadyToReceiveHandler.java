package com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest;

import com.github.gunnermanx.battleNetworkGameExtension.BattleNetworkExtension;
import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;
import com.github.gunnermanx.battleNetworkGameExtension.game.entities.Player;
import com.smartfoxserver.v2.annotations.Instantiation;

import com.smartfoxserver.v2.annotations.Instantiation.InstantiationMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

@Instantiation(InstantiationMode.SINGLE_INSTANCE)
public class PlayerReadyToReceiveHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		BattleNetworkExtension ext = (BattleNetworkExtension) getParentExtension();
		
		if (user.getPlayerId() == 1) {
			synchronized(ext.Game().p1PlayerLock) {
				if (ext.Game().player1 != null) {
					ext.SendChipHandInit(user, ext.Game().player1.deck.getChipIdsInHand(), ext.Game().player1.deck.getTopCidInDeck());
				} else {
					ext.trace("wtf player1 is null");
					ext.Game().p1Ready.set(1);
				}
			}
		} else if (user.getPlayerId() == 2) {
			synchronized(ext.Game().p2PlayerLock) {
				if (ext.Game().player2 != null) {
					ext.SendChipHandInit(user, ext.Game().player2.deck.getChipIdsInHand(), ext.Game().player2.deck.getTopCidInDeck());
				} else {
					ext.trace("wtf player2 is null");
					ext.Game().p2Ready.set(1);
				}
			}
		} else {
			ext.trace("wtf player id is neither 1 or 2");
		}
	}

}
