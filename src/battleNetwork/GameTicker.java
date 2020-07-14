package battleNetwork;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameTicker implements Runnable {
	
	private static GameTicker ticker = null;	
	private BattleNetworkExtension ext;
	
    private ScheduledFuture<?> taskHandle;
	
	public int current = 0;
	
	public static GameTicker Start(BattleNetworkExtension ext) {
		if (ticker == null) {
			ticker = new GameTicker(ext);
			ticker.taskHandle = ext.getApi().getSystemScheduler().scheduleAtFixedRate(
				ticker, 
				0, 
				BattleNetworkGame.INTERVAL_MS, 
				TimeUnit.MILLISECONDS
			);			
		}
		return ticker;
	}
	
	public void Stop() {
		this.taskHandle.cancel(true);
	}
	
	private GameTicker(BattleNetworkExtension ext) {
		this.ext = ext;
		current = 0;
	}
	
	public int Current() {
		return current;
	}
		
	@Override
	public void run() {				
		ext.OnGameTick(current);
		++current;
	}
}