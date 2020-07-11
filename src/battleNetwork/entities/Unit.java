package battleNetwork.entities;

public class Unit {
	public int id;
	public Arena.Ownership owner;
	public String type;
	public int posX, posY;
	public int hitpoints;
	
	public Unit(int id, Arena.Ownership owner, String type, int posX, int posY, int hitpoints) {
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		this.hitpoints = hitpoints;
	}
}
