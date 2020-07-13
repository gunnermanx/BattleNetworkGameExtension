package battleNetwork.entities;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;

public class Unit {
	public int id;
	public Arena.Ownership owner;
	public String type;
	public int posX, posY;
	private int hitpoints;
	
	private Collection<UnitDamagedListener> listeners = new HashSet<>();
	
	public Unit(int id, Arena.Ownership owner, String type, int posX, int posY, int hitpoints) {
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		this.hitpoints = hitpoints;
	}
	
	public void Damage(int damage) {
		this.hitpoints -= damage;
		UnitDamaged(this.id, damage, this.hitpoints);
	}
	
	public int CurrentHP() {
		return this.hitpoints;
	}
	
	public void Register(UnitDamagedListener listener) {
		listeners.add(listener);
	}	
	
	private void UnitDamaged(int unitId, int damage, int currentHitpoints) {
		for (UnitDamagedListener listener:listeners) {
			listener.OnUnitDamaged(unitId, damage, currentHitpoints);
		}
	}
	
	public interface UnitDamagedListener extends EventListener {
		void OnUnitDamaged(int unitId, int damage, int currentHitpoints);
	}
}
