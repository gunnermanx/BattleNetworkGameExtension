package battleNetwork.entities;

import com.smartfoxserver.v2.entities.User;

public class Player {
	public User user;
	public int energy = 0;
	public Arena.Ownership owner;
	
	public Player(User user, Arena.Ownership owner) {
		this.user = user;
		this.energy = 0;
		this.owner = owner;
	}
}
