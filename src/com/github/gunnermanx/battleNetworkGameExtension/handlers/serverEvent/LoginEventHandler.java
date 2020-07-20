package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import javax.persistence.EntityManager;

import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAccount;
import com.github.gunnermanx.battleNetworkGameExtension.model.PlayerAuth;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class LoginEventHandler extends BaseServerEventHandler
{
    private final EntityManager em;
 
    public LoginEventHandler(EntityManager em)
    {
        this.em = em;
    }
 
    @Override
    public void handleServerEvent(ISFSEvent event) throws SFSException
    {
    	SFSErrorData errData = null;
    	
    	ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
    	String username = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
    	String password = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
    	
    	// Query the db
    	try {
    		PlayerAuth pa = em.createQuery("SELECT pa FROM PlayerAuth pa WHERE pa.username = ?1", PlayerAuth.class)
		    	.setParameter(1, username)
		    	.getSingleResult();
    		
    		// Successful login
    		if (getApi().checkSecurePassword(session, pa.GetSecret(), password)) {
    			
    			try {
    				// Get account details for the user that is logging in
    				PlayerAccount acc = em.createQuery("SELECT acc FROM PlayerAccount acc WHERE acc.id = ?1", PlayerAccount.class)
	    				.setParameter(1, pa.GetAccount().GetId())
	    				.getSingleResult();
    				
    				// Set account properties into session, put them on user after
        			session.setProperty("account_id", acc.GetId());
        			session.setProperty("level", acc.GetLevel());
        			session.setProperty("xp", acc.GetXP());
        			session.setProperty("points", acc.GetPoints());
        			
        			return;
        			
    			} catch (Exception e) {
    				// TODO error codes
    				errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
    	            errData.addParameter(username);
    			}
    			
    			
    			
    		}
    		
    		// Create the error code that will be sent to the client and raise the exception
            errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
            errData.addParameter(username);
                    		
    	} catch (Exception e) {    		
            errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
            errData.addParameter(username);
    	}
    	
    	
    	throw new SFSLoginException("LoginError", errData);
    }
}