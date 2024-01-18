//package com.imst.event.map.web.db.dao;
//
//import javax.persistence.EntityManager;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.imst.event.map.web.db.multitenant.cond.OnDatabaseOracleCondition;
//
//@Conditional(OnDatabaseOracleCondition.class)
//@Repository
//@Transactional(readOnly = true, transactionManager = "oracleTenantTransactionManager")
//public class MasterDaoOracle extends MasterDao{
//
//	public MasterDaoOracle(@Qualifier("oracleTenantEntityManagerFactory") EntityManager entityManager) {
//		super(entityManager);
//	}
//}
