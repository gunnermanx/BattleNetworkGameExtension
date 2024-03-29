package com.github.gunnermanx.battleNetworkGameExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame;
import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.BasicAttackCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.ChipDrawnCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.ChipPlayedCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.Command;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.DamageDealtCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.EnergyChangedCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.MoveCommand;
import com.github.gunnermanx.battleNetworkGameExtension.game.commands.TileOwnershipChangeCommand;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.BasicAttackHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.ChipPlayedHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.MovementHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.PlayerReadyToReceiveHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserJoinRoomHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserLeaveRoomHandler;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

// The game extension
// responsible for:
//    sfs event/request setup
//    the game ticker
//    broadcasting commands
//
// TODO: a command to replay commands for a certain player
public class BattleNetworkExtension extends SFSExtension {
	
	private class GameTicker implements Runnable {
		
		private BattleNetworkExtension ext;
				
		public GameTicker(BattleNetworkExtension ext) {
			this.ext = ext;			
		}
			
		@Override
		public void run() {
			ext.OnGameTick();			
		}
	}
	
	private static final String CMD_UPDATE = "tick";
	private static final String HAND_INIT = "hand";
	private static final String PLAYER_VICTORY = "pv";

	
	public ArrayList<CopyOnWriteArrayList<Command>> commands;

	private int lastUpdatedTick = 0;
	private int currentTick = 0;
	private static final int TICK_BUFFER_SIZE = 1;
	
	
	private BattleNetworkGame game;
	
	private GameData gameData;
	
	private ScheduledFuture<?> tickerTaskHandle;
	
	private boolean tickerStarted;
	
	private boolean gameStarted;
	
	@Override
	public void init() {
		gameData = ((BattleNetworkZoneExtension) this.getParentZone().getExtension()).GetGameData();		
		
		// TODO Send data about each player, maybe basic unit data? payload		
		game = new BattleNetworkGame(this);
		
		commands = new ArrayList<CopyOnWriteArrayList<Command>>();
		
		addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinRoomHandler.class);
		// TODO handle disconnects and leaves
		addEventHandler(SFSEventType.USER_DISCONNECT, UserLeaveRoomHandler.class);
		
		addRequestHandler("pr", PlayerReadyToReceiveHandler.class);
		addRequestHandler("m", MovementHandler.class);
		addRequestHandler("ba", BasicAttackHandler.class);
		addRequestHandler("ch", ChipPlayedHandler.class);
	}
	
	@Override
    public void destroy() {
        super.destroy(); 
        if (tickerTaskHandle != null) {
        	tickerTaskHandle.cancel(true);
        }
    }
	
	public void PlayersPresent() {
//		this.getApi().getSystemScheduler().schedule(new Runnable() {
//			@Override
//			public void run() {
//				StartGameTicker();
//			}
//		}, 2, TimeUnit.SECONDS);	
		
		StartGameTicker();
	}
	
	public void StartGameTicker() {
		synchronized(this) {
			if (!tickerStarted && tickerTaskHandle == null) {
				// start the game ticker
				
				// initializing the commands list
				commands.add(currentTick, new CopyOnWriteArrayList<Command>());
				
				tickerTaskHandle = this.getApi().getSystemScheduler().scheduleAtFixedRate(
					new GameTicker(this), 
					0, 
					BattleNetworkGame.INTERVAL_MS, 
					TimeUnit.MILLISECONDS
				);
							
				tickerStarted = true;
			}
		}
	}
	
	public boolean IsGameStarted() {
		return gameStarted;
	}
	
	public BattleNetworkGame Game() {
		return game;
	}
	
	public GameData GameData() {
		return gameData;
	}
	
	public void Player1Victory() {
		gameStarted = false;
		QueuePlayerVictory(1);
	}
	
	public void Player2Victory() {
		gameStarted = false;
		QueuePlayerVictory(2);
	}
	
		
	public void Error(String err) {
		this.trace(String.format("Error happend: %s", err));
		// stop the game?
	}
	
	
	public void OnGameTick() {
		synchronized(this) {
			// check if the game is "started"
			if (!gameStarted && currentTick >= BattleNetworkGame.ROUND_START_TICK) {
				gameStarted = true;
			}
			
			// Let the game sim the current tick
			game.handleTick(currentTick);
					
			// We want to buffer some commands
			if (currentTick == lastUpdatedTick + TICK_BUFFER_SIZE) {
				// create the payload for the tick update command that we send to the clients
				SFSObject payload = CreateUpdatePayload(lastUpdatedTick, currentTick);
				lastUpdatedTick = currentTick; // This needs to happen after we create the payload				
				//this.trace(String.format("sending tick: %d => p1 %s, p2: %s", currentTick, game.player1.user.getName(), game.player2.user.getName()));
				this.send(CMD_UPDATE, payload, Arrays.asList(game.player1.user, game.player2.user));				
			}
			
			// After sending out the commands, update the tick and create the command list
			currentTick++;
			commands.add(currentTick, new CopyOnWriteArrayList<Command>());
			
			// Check to see if the game is ended 
			if (gameStarted && currentTick >= BattleNetworkGame.ROUND_END_TICK) {
				gameStarted = false;
				// trigger game ended
				// TODO: need a tieing case
				if (game.player1.unit.currentHP() >= game.player2.unit.currentHP()) {
					QueuePlayerVictory(1);
				} else {
					QueuePlayerVictory(2);
				}
			}
		}		
	}

	private SFSObject CreateUpdatePayload(int startingTick, int endingTick) {
		SFSObject payload = new SFSObject();
		payload.putInt("t", endingTick);
		
		// encapsulate all commands into payload
		SFSArray sfsArr = new SFSArray();
		for (int i = startingTick; i < endingTick; i++) {		
			CopyOnWriteArrayList<Command> cList = commands.get(i);
			Iterator<Command> it = cList.iterator();
			while (it.hasNext()) {
				Command c = it.next();
				// serialize
				sfsArr.addSFSArray(c.Serialize());
			}
		}		
		payload.putSFSArray("c", sfsArr);
		return payload;
	}
	
	public void QueuePlayerVictory(int playerId) {
		// TODO
		tickerTaskHandle.cancel(true);
		SFSObject payload = new SFSObject();
		payload.putInt("pid", playerId);		
		this.send(PLAYER_VICTORY, payload, Arrays.asList(game.player1.user, game.player2.user));
	}
	
	public void SendChipHandInit(User user, short[] cids, short cidNext) {
		
		this.trace(String.format("SENDING HAND TO PLAYER %d: [%d,%d,%d,%d]", user.getPlayerId(), cids[0], cids[1], cids[2], cids[3] ));
		
		SFSObject payload = new SFSObject();
		SFSArray chips = new SFSArray();
		chips.addShort(cids[0]);
		chips.addShort(cids[1]);
		chips.addShort(cids[2]);
		chips.addShort(cids[3]);
		payload.putSFSArray("chips", chips);
		payload.putShort("next", cidNext);
		this.send(HAND_INIT, payload, user);		
	}
	
		
	public void QueueEnergyChanged(int currentTick, int playerId, int delta) {		
		Command e = new EnergyChangedCommand(playerId, delta);		
		QueueCommand(e);
	}
	
	public void QueueMoveStateChange(int id, int x, int y) {
		Command m = new MoveCommand(id, x, y);
		QueueCommand(m);
	}
	
	public void QueueDamageDealt(int id, int damage) {
		Command dd = new DamageDealtCommand(id, damage);
		QueueCommand(dd);
	}
	
	public void QueueChipPlayed(int playerId, short cid) {
		Command cp = new ChipPlayedCommand(playerId, cid);
		QueueCommand(cp);
	}
	
	public void QueueChipDrawn(int playerId, short cid, short cidNext) {
		Command cd = new ChipDrawnCommand(playerId, cid, cidNext);
		QueueCommand(cd);
	}
	
	public void QueueBasicAttack(int playerId) {
		Command ba = new BasicAttackCommand(playerId);
		QueueCommand(ba);
	}
	
	public void QueueTileOwnershipChange(int playerId, int x, int y) {
		Command to = new TileOwnershipChangeCommand(playerId, x, y);
		this.trace(String.format("queue tile ownership change [%d,%d] to player: %d", x,y, playerId));
		QueueCommand(to);
	}
	
	
	
	private void QueueCommand(Command c) {		
		CopyOnWriteArrayList<Command> tickCommandList = commands.get(currentTick);
		if (tickCommandList != null) {
			tickCommandList.add(c);
		} else {
			this.trace(String.format("Failed to queue command because tick command list is null!"));
		}
	}
	
}
