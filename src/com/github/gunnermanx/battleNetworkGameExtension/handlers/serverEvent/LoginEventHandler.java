package com.github.gunnermanx.battleNetworkGameExtension.handlers.serverEvent;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class LoginEventHandler extends BaseServerEventHandler
{
 
	@Override
    public void handleServerEvent(ISFSEvent event) throws SFSException
    {
    	SFSErrorData errData = null;
    	
    	ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
    	String username = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
    	String password = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
    	
    	// Query the db
    	try {
    		IDBManager dbManager = this.getParentExtension().getParentZone().getDBManager();
    		String query = "SELECT * FROM player_auth pa WHERE pa.username = ?";
    		
    		ISFSArray resArr = dbManager.executeQuery(query, new Object[] {username});
    		if (resArr.size() > 0) {
    			ISFSObject res = resArr.getSFSObject(0); 
    			// Successful login    			
    			String secret = res.getUtfString("secret");
        		if (getApi().checkSecurePassword(session, secret, password)) {
        			session.setProperty("account", res.getInt("account_id"));
        			return;
        		} 
    		}
    		
			// Create the error code that will be sent to the client and raise the exception
            errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
            errData.addParameter(username);    			
                    		
    	} catch (Exception e) {    		
            errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
            errData.addParameter(username);
    	}
    	
    	if (errData != null) {
    		throw new SFSLoginException("LoginError", errData);
    	}    	
    }
}