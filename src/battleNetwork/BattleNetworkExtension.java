package battleNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import battleNetwork.clientRequestHandlers.BasicAttackHandler;
import battleNetwork.clientRequestHandlers.ChipPlayedHandler;
import battleNetwork.clientRequestHandlers.MovementHandler;
import battleNetwork.commands.Command;
import battleNetwork.commands.DamageDealtCommand;
import battleNetwork.commands.EnergyChangedCommand;
import battleNetwork.commands.MoveCommand;
import battleNetwork.commands.SpawnProjectileCommand;
import battleNetwork.eventHandlers.UserJoinRoomHandler;

// The game extension
// responsible for:
//    sfs event/request setup
//    the game ticker
//    broadcasting commands
//
// TODO: a command to replay commands for a certain player
public class BattleNetworkExtension extends SFSExtension {
	
	private static final String CMD_UPDATE = "tick";
	private static final String PLAYER_VICTORY = "pv";

	
	public ArrayList<LinkedList<Command>> commands;

	private int lastTick = 0;
	private int currentTick = 0;
	private static final int TICK_BUFFER_SIZE = 2;
	
	
	private boolean gameStarted;	
	private BattleNetworkGame game;
	
	private GameData gameData;
	
	private GameTicker ticker;
	
	@Override
	public void init() {
		trace("BattleNetworkExtension started");
				
		gameData = new GameData(this);		
		
		// TODO Send data about each player, maybe basic unit data? payload		
		game = new BattleNetworkGame(this);
		
		commands = new ArrayList<LinkedList<Command>>();
		
		addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinRoomHandler.class);
		
		addRequestHandler("m", MovementHandler.class);
		addRequestHandler("ba", BasicAttackHandler.class);
		addRequestHandler("ch", ChipPlayedHandler.class);
		
	}
	
	public void PlayersPresent() {
		this.getApi().getSystemScheduler().schedule(new Runnable() {
			@Override
			public void run() {
				StartGame();
			}
		}, 2, TimeUnit.SECONDS);
	}
	
	public void StartGame() {
		if (!gameStarted && ticker == null) {
			// start the game ticker
			ticker = GameTicker.Start(this);
			gameStarted = true;
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
	
	public void OnGameTick(int current) {
		//trace("tick ", current);
		currentTick = current;
		
		// Add a new command LinkedList for this tick
		commands.add(current, new LinkedList<Command>());
		
		// Let the game know a new tick occurred
		game.HandleTick(current);
				
		// send out queued commands
		if (currentTick == lastTick + TICK_BUFFER_SIZE) {
			SFSObject payload = CreateUpdatePayload(lastTick, currentTick);
			this.send(CMD_UPDATE, payload, this.getParentRoom().getPlayersList());
			lastTick = currentTick;
		}
	}

	private SFSObject CreateUpdatePayload(int startingTick, int endingTick) {
		SFSObject payload = new SFSObject();
		payload.putInt("t", endingTick);

		// encapsulate all commands into payload
		SFSArray sfsArr = new SFSArray();
		for (int i = startingTick; i < endingTick; i++) {
			LinkedList<Command> cList = commands.get(i);
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
		ticker.Stop();
		SFSObject payload = new SFSObject();
		payload.putInt("pid", playerId);
		this.send(PLAYER_VICTORY, payload, this.getParentRoom().getPlayersList());
	}
	
	public void QueueEnergyChanged(int playerId, int delta) {
		Command e = new EnergyChangedCommand(playerId, delta);
		commands.get(currentTick).add(e);
	}
	
	public void QueueMoveStateChange(int id, int x, int y) {
		Command m = new MoveCommand(id, x, y);
		commands.get(currentTick).add(m);
	}
	
	public void QueueDamageDealt(int id, int damage) {
		Command dd = new DamageDealtCommand(id, damage);
		commands.get(currentTick).add(dd);	
	}
	
	public void QueueSpawnProjectile(int playerId, int cid) {
		Command sp = new SpawnProjectileCommand(playerId, cid);
		commands.get(currentTick).add(sp);
	}
	
}
