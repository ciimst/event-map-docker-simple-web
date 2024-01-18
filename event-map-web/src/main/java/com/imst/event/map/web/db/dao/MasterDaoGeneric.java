package com.imst.event.map.web.db.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.web.db.multitenant.cond.OnDatabaseGenericCondition;

@Conditional(OnDatabaseGenericCondition.class)
@Repository
@Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
public class MasterDaoGeneric extends MasterDao{

	public MasterDaoGeneric(@Qualifier("tenantEntityManagerFactory") EntityManager entityManager) {
		super(entityManager);
	}
}
