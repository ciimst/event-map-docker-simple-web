package com.imst.event.map.web.db.multitenant;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.imst.event.map.web.db.multitenant.conf.TenantIdentifierResolver;
import com.imst.event.map.web.db.multitenant.provider.DataSourceBasedMultiTenantConnectionProviderMssqlImpl;


@ConditionalOnBean(DataSourceBasedMultiTenantConnectionProviderMssqlImpl.class)
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.imst.event.map.web.db.repositories.mssql", "com.imst.event.map.hibernate.entity" })
@EnableJpaRepositories(basePackages = { "com.imst.event.map.web.db.repositories.mssql" },
		entityManagerFactoryRef = "mssqlTenantEntityManagerFactory",
		transactionManagerRef = "mssqlTenantTransactionManager")
public class MssqlTenantDatabaseConfig {

	@Bean(name = "mssqlTenantJpaVendorAdapter")
	public JpaVendorAdapter tenantJpaVendorAdapter() {

		return new HibernateJpaVendorAdapter();
	}
	

	@Bean(name = "mssqlTenantTransactionManager")
	@ConditionalOnBean(name = "mssqlTenantEntityManagerFactory")
	public JpaTransactionManager transactionManager(
			@Qualifier("mssqlTenantEntityManagerFactory") EntityManagerFactory tenantEntityManager) {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(tenantEntityManager);

		return transactionManager;
	}

	/**
	 * 
	 * The multi tenant connection provider
	 * @return
	 */
	@Bean(name = "mssqlDatasourceBasedMultitenantConnectionProvider")
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {

		// Autowires the multi connection provider
		return new DataSourceBasedMultiTenantConnectionProviderMssqlImpl();
	}

	/**
	 * 
	 * The current tenant identifier resolver
	 * @return
	 */
	@Bean(name = "mssqlCurrentTenantIdentifierResolver")
	public CurrentTenantIdentifierResolver tenantIdentifierResolver() {

		return new TenantIdentifierResolver();
	}

	/**
	 * 
	 * Creates the entity manager factory bean which is required to access the
	 * JPA functionalities provided by the JPA persistence provider, i.e.
	 * Hibernate in this case.
	 * 
	 * @param connectionProvider
	 * @param tenantResolver
	 * @return
	 */
	@Bean(name = "mssqlTenantEntityManagerFactory")
	@ConditionalOnBean(name = "mssqlDatasourceBasedMultitenantConnectionProvider")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("mssqlDatasourceBasedMultitenantConnectionProvider") MultiTenantConnectionProvider multiTenantConnectionProvider,
			@Qualifier("mssqlCurrentTenantIdentifierResolver") CurrentTenantIdentifierResolver tenantIdentifierResolver,
			@Qualifier("mssqlTenantJpaVendorAdapter") JpaVendorAdapter jpaVendorAdapter) {
		
	
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		// All tenant related entities, repositories and service classes must be scanned

		emfBean.setPackagesToScan( "com.imst.event.map.web.db.repositories.mssql", "com.imst.event.map.hibernate.entity" );
		emfBean.setJpaVendorAdapter(jpaVendorAdapter);
		emfBean.setPersistenceUnitName("mssql-tenantdb-persistence-unit");

		Map<String, Object> properties = new HashMap<>();
		properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
		properties.put(Environment.DIALECT, "org.hibernate.dialect.SQLServer2012Dialect");
		properties.put(Environment.SHOW_SQL, false);
//		properties.put(Environment.FORMAT_SQL, true);
		properties.put(Environment.HBM2DDL_AUTO, "none");
		emfBean.setJpaPropertyMap(properties);

		return emfBean;
	}

}
