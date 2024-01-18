package com.imst.event.map.web.db.multitenant.provider;
import java.util.Collection;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.context.annotation.Configuration;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.vo.DataSourceInfo;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderGenericImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 8030610714000531226L;

    public DataSourceBasedMultiTenantConnectionProviderGenericImpl() {
    }

    @Override
    protected DataSource selectAnyDataSource() {
    	
    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
    	for (DataSourceInfo dataSourceInfo : values) {
			if(dataSourceInfo.getMultitenantDatabaseE() == MultitenantDatabaseE.GENERIC) {
				return dataSourceInfo.getDatasource();
			}
		}
    	
    	return null;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
    	
    	if(tenantIdentifier.equals(Statics.DEFAULT_DB_NAME)) {
    		return null;
    	}
    	
        return Statics.tenantDataSourceInfoMap.get(tenantIdentifier).getDatasource();
    }

}