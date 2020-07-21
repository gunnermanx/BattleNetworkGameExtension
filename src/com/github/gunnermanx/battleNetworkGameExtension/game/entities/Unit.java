package com.github.gunnermanx.battleNetworkGameExtension.game.entities;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;

import com.github.gunnermanx.battleNetworkGameExtension.game.BattleNetworkGame.Owner;

public class Unit {
	public int id;
	public Owner owner;
	public String type;
	public int posX, posY;
	private int hitpoints;
	
	private Collection<UnitDamagedListener> listeners = new HashSet<>();
	
	public interface UnitDamagedListener extends EventListener {
		void onUnitDamaged(int unitId, int damage, int currentHitpoints);
	}
	
	public Unit(int id, Owner owner, String type, int posX, int posY, int hitpoints) {
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		this.hitpoints = hitpoints;
	}
	
	public void damage(int damage) {
		this.hitpoints -= damage;
		UnitDamaged(this.id, damage, this.hitpoints);
	}
	
	public int currentHP() {
		return this.hitpoints;
	}
	
	public void registerUnitDamagedListener(UnitDamagedListener listener) {
		listeners.add(listener);
	}	
	
	private void UnitDamaged(int unitId, int damage, int currentHitpoints) {
		for (UnitDamagedListener listener:listeners) {
			listener.onUnitDamaged(unitId, damage, currentHitpoints);
		}
	}
	
}
