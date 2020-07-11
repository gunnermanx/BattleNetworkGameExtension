package battleNetwork;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;

import net.sf.json.JSONObject;

public class GameData {
	
	private JSONObject chips;
		
	public void Load() {
		try {
			String chipJsonStr = new String(readAllBytes(get("chips.json")));
			JSONObject chips = JSONObject.fromObject(chipJsonStr);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
