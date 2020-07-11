package battleNetwork;

import java.util.concurrent.TimeUnit;

public class GameTicker implements Runnable {	
	private static final int INITIAL_DELAY_MS = 0; 
	private static final int INTERVAL_MS = 100;
	
	private static GameTicker ticker = null;	
	private BattleNetworkExtension ext;
	
	public int current = 0;
	
	public static GameTicker Start(BattleNetworkExtension ext) {
		if (ticker == null) {
			ticker = new GameTicker(ext);
			ext.getApi().getSystemScheduler().scheduleAtFixedRate(
				ticker, 
				INITIAL_DELAY_MS, 
				INTERVAL_MS, 
				TimeUnit.MILLISECONDS
			);			
		}
		return ticker;
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