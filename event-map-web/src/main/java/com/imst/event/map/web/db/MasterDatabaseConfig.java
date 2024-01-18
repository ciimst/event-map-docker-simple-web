package com.imst.event.map.web.db;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.imst.event.map.hibernate.entity", "com.imst.event.map.web.db.repositories" },
		entityManagerFactoryRef = "masterEntityManagerFactory",
		transactionManagerRef = "masterTransactionManager")
public class MasterDatabaseConfig {
	

	@Bean("masterDatasourceHikariConfig")
	@ConfigurationProperties(prefix = "master.datasource")
	public HikariConfig DatasoruceProperties() {
		return new HikariConfig();
	}

	// Create Master Data Source using master properties and also configure HikariCP
	@Bean(name = "masterDataSource")
	@ConditionalOnBean(name = "masterDatasourceHikariConfig")
	@Primary
	public DataSource masterDataSource(HikariConfig hikariConfig) {

		HikariDataSource hikariDataSource = new HikariDataSource();
		
		hikariDataSource.setUsername(hikariConfig.getUsername());
		hikariDataSource.setPassword(hikariConfig.getPassword());
		hikariDataSource.setJdbcUrl(hikariConfig.getJdbcUrl());
		hikariDataSource.setDriverClassName(hikariConfig.getDriverClassName());
        hikariDataSource.setPoolName("master-datasource-connection-pool");

        // HikariCP settings
		hikariDataSource.setMaximumPoolSize(hikariConfig.getMaximumPoolSize());
		hikariDataSource.setMinimumIdle(hikariConfig.getMinimumIdle());
		hikariDataSource.setConnectionTimeout(30000);
		hikariDataSource.setIdleTimeout(60000);

		log.info("Setup of masterDataSource succeeded.");

		return hikariDataSource;
	}

	@Primary
	@Bean(name = "masterEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(@Qualifier("masterDataSource") DataSource masterDataSource) {

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		// Set the master data source
		em.setDataSource(masterDataSource);
		

		// The master tenant entity and repository need to be scanned
		em.setPackagesToScan("com.imst.event.map.hibernate.entity", "com.imst.event.map.web.db.repositories");

		// Setting a name for the persistence unit as Spring sets it as
		// 'default' if not defined
		em.setPersistenceUnitName("masterdb-persistence-unit");

		// Setting Hibernate as the JPA provider
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		em.setJpaVendorAdapter(vendorAdapter);

		// Set the hibernate properties
		em.setJpaProperties(hibernateProperties());

		log.info("Setup of masterEntityManagerFactory succeeded.");

		return em;
	}

	@Bean(name = "masterTransactionManager")
	@Primary
	public JpaTransactionManager masterTransactionManager(@Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	@Primary
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {

		return new PersistenceExceptionTranslationPostProcessor();
	}


	private Properties hibernateProperties() {

		Properties properties = new Properties();
		properties.put(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.spatial.dialect.postgis.PostgisDialect");
		properties.put(org.hibernate.cfg.Environment.SHOW_SQL, false);
//		properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, true);
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "none");

		return properties;
	}
}
