package com.imst.event.map.web.db.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.web.db.multitenant.cond.OnDatabaseMssqlCondition;

@Conditional(OnDatabaseMssqlCondition.class)
@Repository
@Transactional(readOnly = true, transactionManager = "mssqlTenantTransactionManager")
public class MasterDaoMssql extends MasterDao{

	public MasterDaoMssql(@Qualifier("mssqlTenantEntityManagerFactory") EntityManager entityManager) {
		super(entityManager);

	}
}
