package com.imst.event.map.web.db.multitenant.conf;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    static final String DEFAULT_TENANT = "default";

    @Override
    public String resolveCurrentTenantIdentifier() {
    	
    	String currentTenant = (String) TenantContext.getCurrentTenant();
    	
    	if(currentTenant == null) {
    		return DEFAULT_TENANT;
    	}
    	
    	return currentTenant;
    }
    
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}