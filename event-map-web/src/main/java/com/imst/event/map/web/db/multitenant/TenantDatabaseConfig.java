package com.imst.event.map.web.db.multitenant;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.imst.event.map.web.db.multitenant.conf.TenantIdentifierResolver;
import com.imst.event.map.web.db.multitenant.provider.DataSourceBasedMultiTenantConnectionProviderGenericImpl;

@ConditionalOnBean(DataSourceBasedMultiTenantConnectionProviderGenericImpl.class)
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.imst.event.map.hibernate.entity",	"com.imst.event.map.web.db.repositories.generic" })
@EnableJpaRepositories(basePackages = { "com.imst.event.map.web.db.repositories.generic" },
		entityManagerFactoryRef = "tenantEntityManagerFactory",
		transactionManagerRef = "tenantTransactionManager")
public class TenantDatabaseConfig {

	
	@Bean(name = "tenantJpaVendorAdapter")
	@Primary
	public JpaVendorAdapter jpaVendorAdapter() {

		return new HibernateJpaVendorAdapter();
	}

	@Bean(name = "tenantTransactionManager")
	@ConditionalOnBean(name = "tenantEntityManagerFactory")
	public JpaTransactionManager transactionManager( @Qualifier("tenantEntityManagerFactory") EntityManagerFactory tenantEntityManager) {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(tenantEntityManager);

		return transactionManager;
	}

	/**
	 * 
	 * The multi tenant connection provider
	 * @return
	 */
	@Bean(name = "datasourceBasedMultitenantConnectionProvider")
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {

		return new DataSourceBasedMultiTenantConnectionProviderGenericImpl();
	}

	/**
	 * 
	 * The current tenant identifier resolver
	 * @return
	 */
	@Bean(name = "currentTenantIdentifierResolver")
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {

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
	@Bean(name = "tenantEntityManagerFactory")
	@ConditionalOnBean(name = "datasourceBasedMultitenantConnectionProvider")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("datasourceBasedMultitenantConnectionProvider") MultiTenantConnectionProvider multiTenantConnectionProvider,
			@Qualifier("currentTenantIdentifierResolver") CurrentTenantIdentifierResolver tenantIdentifierResolver,
			@Qualifier("tenantJpaVendorAdapter") JpaVendorAdapter jpaVendorAdapter) {
		
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		
		// All tenant related entities, repositories and service classes must be scanned
		emfBean.setPackagesToScan( "com.imst.event.map.web.db.repositories.generic", "com.imst.event.map.hibernate.entity" );
		emfBean.setJpaVendorAdapter(jpaVendorAdapter);
		emfBean.setPersistenceUnitName("tenantdb-persistence-unit");

		Map<String, Object> properties = new HashMap<>();
		properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
		properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
		
		properties.put(Environment.SHOW_SQL, false);
//		properties.put(Environment.FORMAT_SQL, true);
		properties.put(Environment.HBM2DDL_AUTO, "none");

		emfBean.setJpaPropertyMap(properties);

		return emfBean;
	}

}
