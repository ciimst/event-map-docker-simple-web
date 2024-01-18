package com.imst.event.map.web.db.multitenant.provider;
import java.util.Collection;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.multitenant.cond.OnDatabaseMssqlCondition;
import com.imst.event.map.web.vo.DataSourceInfo;

@Configuration
@Conditional(OnDatabaseMssqlCondition.class)
public class DataSourceBasedMultiTenantConnectionProviderMssqlImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 5552529236723780608L;
	
    public DataSourceBasedMultiTenantConnectionProviderMssqlImpl() {
    }

    @Override
    protected DataSource selectAnyDataSource() {
    	
    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
    	for (DataSourceInfo dataSourceInfo : values) {
			if(dataSourceInfo.getMultitenantDatabaseE() == MultitenantDatabaseE.MSSQL) {
				return dataSourceInfo.getDatasource();
			}
		}
    	
    	return null;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
    	
    	return Statics.tenantDataSourceInfoMap.get(tenantIdentifier).getDatasource(); 
    }

}