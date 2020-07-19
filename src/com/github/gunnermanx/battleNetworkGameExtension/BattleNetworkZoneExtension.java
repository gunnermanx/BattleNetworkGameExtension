package com.github.gunnermanx.battleNetworkGameExtension;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.github.gunnermanx.battleNetworkGameExtension.handlers.clientRequest.MatchmakingRequestHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.LoginEventHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserDisconnectHandler;
import com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent.UserJoinZoneEventHandler;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAuth;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class BattleNetworkZoneExtension extends SFSExtension {

	private static final String PERSISTENCE_NAME = "BattleNetwork";
	
    private EntityManagerFactory emf;
    private EntityManager em;
    
	public CopyOnWriteArrayList<User> matchmakingUsers;
	
	@Override
	public void init() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_NAME);
		em = emf.createEntityManager();
		
		// Activates the login routing to the Extension without manual configuration
        getParentZone().setCustomLogin(true);
 
        // Add event handler for login events
        addEventHandler(SFSEventType.USER_LOGIN, new LoginEventHandler(em));
 
		
		matchmakingUsers = new CopyOnWriteArrayList<User>();
		addRequestHandler("matchmaking", MatchmakingRequestHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ZONE, UserJoinZoneEventHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectHandler.class);
		
		initDatabase();
	}
	
	private void initDatabase()
    {
        Query query = em.createQuery("SELECT pa FROM PlayerAuth pa");
        List<PlayerAuth> res = (List<PlayerAuth>) query.getResultList();
 
        // Table is empty, populate 1 record
        if (res.size() == 0)
        {
        	PlayerAuth pa = new PlayerAuth();
        	pa.SetUsername("test1");
        	pa.SetSecret("098f6bcd4621d373cade4e832627b4f6");
 
        	PlayerAuth pa2 = new PlayerAuth();
        	pa2.SetUsername("test2");
        	pa2.SetSecret("098f6bcd4621d373cade4e832627b4f6");
        	
            // Store
            em.getTransaction().begin();
            em.persist(pa);
            em.persist(pa2);
            em.getTransaction().commit();
        }		
    }
	
}
