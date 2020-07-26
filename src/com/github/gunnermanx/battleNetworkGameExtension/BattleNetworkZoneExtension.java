package com.github.gunnermanx.battleNetworkGameExtension;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.github.gunnermanx.battleNetworkGameExtension.game.GameData;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.MatchmakingRequestHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.LoginEventHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserDisconnectHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserJoinZoneEventHandler;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAuth;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerChip;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerDeckEntry;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class BattleNetworkZoneExtension extends SFSExtension {

	public static final String PERSISTENCE_NAME = "BattleNetwork";
	
    private EntityManagerFactory emf;
    private EntityManager em;
    
    private GameData gameData;
    
	public CopyOnWriteArrayList<User> matchmakingUsers;
	
	@Override
	public void init() {
		gameData = new GameData(this);
		
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
		em = emf.createEntityManager();
		
		// Activates the login routing to the Extension without manual configuration
        getParentZone().setCustomLogin(true);
        	
		matchmakingUsers = new CopyOnWriteArrayList<User>();
		
		addRequestHandler("matchmaking", MatchmakingRequestHandler.class);
				
        addEventHandler(SFSEventType.USER_LOGIN, new LoginEventHandler(emf));
        addEventHandler(SFSEventType.USER_JOIN_ZONE, new UserJoinZoneEventHandler(emf));
		addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectHandler.class);
		
		initDatabase();
		
		em.close();
	}
	
	public GameData GetGameData() {
		return this.gameData;
	}
	
	private void initDatabase()
    {
        Query query = em.createQuery("SELECT pa FROM PlayerAuth pa");
        List<PlayerAuth> res = (List<PlayerAuth>) query.getResultList();
 
        // Table is empty, populate 1 record
        if (res.size() == 0)
        {
        	// Test account 1
        	PlayerAccount pacc = new PlayerAccount();
        	pacc.SetLevel((byte)1);
        	pacc.SetPoints((short)101);
        	pacc.SetXP(0);
        	
        	PlayerAuth pa = new PlayerAuth();
        	pa.SetUsername("test1");
        	pa.SetSecret("098f6bcd4621d373cade4e832627b4f6");
        	pa.SetAccount(pacc);
        	
        	PlayerChip c11 = new PlayerChip();
        	c11.SetAccount(pacc);
        	c11.SetChipDataId((short)0);
        	c11.SetLevel((byte)1);
        	c11.SetXp((short)0);
        	
        	PlayerChip c12 = new PlayerChip();
        	c12.SetAccount(pacc);
        	c12.SetChipDataId((short)1);
        	c12.SetLevel((byte)1);
        	c12.SetXp((short)0);
        	
        	PlayerChip c13 = new PlayerChip();
        	c13.SetAccount(pacc);
        	c13.SetChipDataId((short)2);
        	c13.SetLevel((byte)1);
        	c13.SetXp((short)0);
        	
        	pacc.GetPlayerChips().add(c11);
        	pacc.GetPlayerChips().add(c12);
        	pacc.GetPlayerChips().add(c13);
        	
        	        	
        	
        	// Test account 2      	
        	PlayerAccount pacc2 = new PlayerAccount();
        	pacc2.SetLevel((byte)1);
        	pacc2.SetPoints((short)102);
        	pacc2.SetXP(0);
        	
        	PlayerAuth pa2 = new PlayerAuth();
        	pa2.SetUsername("test2");
        	pa2.SetSecret("098f6bcd4621d373cade4e832627b4f6");
        	pa2.SetAccount(pacc2);
        	        	
        	PlayerChip c21 = new PlayerChip();
        	c21.SetAccount(pacc2);
        	c21.SetChipDataId((short)0);
        	c21.SetLevel((byte)1);
        	c21.SetXp((short)0);
        	
        	PlayerChip c22 = new PlayerChip();
        	c22.SetAccount(pacc2);
        	c22.SetChipDataId((short)1);
        	c22.SetLevel((byte)1);
        	c22.SetXp((short)0);
        	
        	PlayerChip c23 = new PlayerChip();
        	c23.SetAccount(pacc2);
        	c23.SetChipDataId((short)2);
        	c23.SetLevel((byte)1);
        	c23.SetXp((short)0);
        	
        	pacc2.GetPlayerChips().add(c21);
        	pacc2.GetPlayerChips().add(c22);
        	pacc2.GetPlayerChips().add(c23);
        	
        	
        	
        	PlayerDeckEntry pd11 = new PlayerDeckEntry();
        	pd11.SetAccount(pacc);
        	pd11.SetChipId(c11);
        	pd11.SetCopies((byte)2);
        	pd11.SetDeckId((short)1);
        	
        	PlayerDeckEntry pd12 = new PlayerDeckEntry();
        	pd12.SetAccount(pacc);
        	pd12.SetChipId(c12);
        	pd12.SetCopies((byte)2);
        	pd12.SetDeckId((short)1);
        	
        	PlayerDeckEntry pd13 = new PlayerDeckEntry();
        	pd13.SetAccount(pacc);
        	pd13.SetChipId(c13);
        	pd13.SetCopies((byte)2);
        	pd13.SetDeckId((short)1);
        	
        	
        	PlayerDeckEntry pd21 = new PlayerDeckEntry();
        	pd21.SetAccount(pacc2);
        	pd21.SetChipId(c21);
        	pd21.SetCopies((byte)2);
        	pd21.SetDeckId((short)1);
        	
        	PlayerDeckEntry pd22 = new PlayerDeckEntry();
        	pd22.SetAccount(pacc2);
        	pd22.SetChipId(c22);
        	pd22.SetCopies((byte)2);
        	pd22.SetDeckId((short)1);
        	
        	PlayerDeckEntry pd23 = new PlayerDeckEntry();
        	pd23.SetAccount(pacc2);
        	pd23.SetChipId(c23);
        	pd23.SetCopies((byte)2);
        	pd23.SetDeckId((short)1);
        	
            // Store
            em.getTransaction().begin();
            em.persist(pacc);
            em.persist(pacc2);
            em.persist(pa);
            em.persist(pa2);
            
            em.persist(c11);
            em.persist(c12);
            em.persist(c13);
            em.persist(pd11);
            em.persist(pd12);
            em.persist(pd13);
            
            em.persist(c21);
            em.persist(c22);
            em.persist(c23);
            em.persist(pd21);
            em.persist(pd22);
            em.persist(pd23);
            
            
            em.getTransaction().commit();
        }		
    }
	
}
