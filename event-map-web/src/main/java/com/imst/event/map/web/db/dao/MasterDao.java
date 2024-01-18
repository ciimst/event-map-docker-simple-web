package com.imst.event.map.web.db.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.vo.UserItem;

import lombok.NonNull;

@Repository
@Transactional(readOnly = true, transactionManager = "masterTransactionManager")
@Primary // MultitenantDatabaseE içinde masterdao çağrılıyor. conflict olmasın diye konulmuştur
public class MasterDao{
	
	private final EntityManager em;
	
	public MasterDao(@Qualifier("masterEntityManagerFactory") EntityManager entityManager) {
		
		this.em = entityManager;
	}
	
	
	public <P> List<P> executeHql(String hql, Class<P> projectionClass) {
		
		String projectionClassName = projectionClass.getName();
		
		String mappings = StringUtils.substringBetween(hql, "SELECT", "FROM");
		boolean isDistinctSelect = mappings.trim().startsWith("DISTINCT");
		String replacedMapping = "";
		if (isDistinctSelect) {
			String distinctMappings = mappings.replaceFirst("DISTINCT", "");
			replacedMapping = String.format("SELECT DISTINCT NEW %s (%s) FROM", projectionClassName, distinctMappings);
		} else {
			replacedMapping = String.format("SELECT NEW %s (%s) FROM", projectionClassName, mappings);
		}
		String result = RegExUtils.replaceAll(hql, "SELECT.*?FROM", replacedMapping);
		TypedQuery<P> namedQuery = em.createQuery(result, projectionClass);
		return namedQuery.getResultList();
	}
	
	public <P, T> List<P> criteriaTest(UserItem userItem, Class<T> domain, Class<P> projectionClass) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<P> query = criteriaBuilder.createQuery(projectionClass);
		Root<T> root = query.from(domain);
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(userItem.getName())){
			predicates.add(ilike(criteriaBuilder, root.get("name"), userItem.getName()));
		}
		
		query.where(predicates.toArray(new Predicate[0]));
		query.select(criteriaBuilder.construct(projectionClass, root.get("id"), root.get("username")));
		
		TypedQuery<P> namedQuery = em.createQuery(query);
		return namedQuery.getResultList();
	}
	
	
	
	public <D, T> List<T> findAll(CustomSpecification<D, T> spec, @NonNull Sort sort) {
		
		TypedQuery<T> namedQuery = getQuery(spec, sort);
		
		return namedQuery.getResultList();
	}
	
	public <D, T> Page<T> findAll(CustomSpecification<D, T> spec, @NonNull Pageable pageable) {
		
		TypedQuery<T> namedQuery = getQuery(spec, pageable.getSort());
		
		namedQuery.setFirstResult((int)pageable.getOffset());
		namedQuery.setMaxResults(pageable.getPageSize());
		
		
		
		return readPageWithProjection(namedQuery.getResultList(), spec, pageable);
	}
	
	public <D, T> Slice<T> sliceAll(CustomSpecification<D, T> spec, @NonNull Pageable pageable) {
		
		TypedQuery<T> namedQuery = getQuery(spec, pageable.getSort());
		
		return readSliceWithProjection(namedQuery, pageable);
	}
	
	public <D, T> TypedQuery<T> getQuery(CustomSpecification<D, T> spec, Sort sort) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = criteriaBuilder.createQuery(spec.getTarget());
		Root<D> root = query.from(spec.getDomain());
		
		Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);
		if (predicate != null) {
			query.where(predicate);
		}
		
		query.select(criteriaBuilder.construct(spec.getTarget(), spec.getSelections(root)));
		
		if (sort != null) {
			query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
		}
		
		TypedQuery<T> namedQuery = em.createQuery(query);
		return namedQuery;
	}
	
	public <D, T> TypedQuery<Long> getCountQuery(CustomSpecification<D, T> spec) {
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<D> root = query.from(spec.getDomain());
		
		Predicate predicate = spec.toPredicateCount(root, query, criteriaBuilder);
		if (predicate != null) {
			query.where(predicate);
		}
		
		if (query.isDistinct()) {
			query.select(criteriaBuilder.countDistinct(root));
		} else {
			query.select(criteriaBuilder.count(root));
		}
		
		return em.createQuery(query);
		
	}
	
	public Long executeCountQueryAndGetResult(CustomSpecification<Event, Long> spec) {
		
		TypedQuery<Long> query = getCountQuery(spec);
		
		List<Long> totals = query.getResultList();
		return totals.get(0);	
	}
	
	public Long executeCountQueryAndGetResultBlackList(CustomSpecification<BlackList, Long> spec) {
		
		TypedQuery<Long> query = getCountQuery(spec);
		
		List<Long> totals = query.getResultList();
		return totals.get(0);	
	}
	
	
	public static Long executeCountQuery(@NonNull TypedQuery<Long> query) {
		
		List<Long> totals = query.getResultList();
		long total = 0L;
		
		Long element;
		for(Iterator<Long> var3 = totals.iterator(); var3.hasNext(); total = total + (element == null ? 0L : element)) {
			element = var3.next();
		}
		
		return total;
	}
	
	
	private <D, T> Page<T> readPageWithProjection(List<T> projectedResults, CustomSpecification<D, T> spec, @NonNull Pageable pageable) {
		
		Page<T> page = PageableExecutionUtils.getPage(projectedResults, pageable, () -> executeCountQuery(getCountQuery(spec)));
		return page;
	}
	
	private <T> Slice<T> readSliceWithProjection(TypedQuery<T> namedQuery, @NonNull Pageable pageable) {
		
		namedQuery.setFirstResult((int)pageable.getOffset());
		
		int pageSize = 0;
		if (pageable.isPaged()) {
			
			pageSize = pageable.getPageSize();
			namedQuery.setMaxResults(pageSize + 1);
		}
		
		List<T> resultList = namedQuery.getResultList();
		
		boolean hasNext = pageable.isPaged() && resultList.size() > pageSize;
		
		SliceImpl<T> slice = new SliceImpl<>(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
		return slice;
	}
	
	public Predicate ilike(CriteriaBuilder criteriaBuilder, Path<String> name, String search) {
		
		return criteriaBuilder.like(
				criteriaBuilder.upper(criteriaBuilder.lower(name)),
				"%" + search.toLowerCase(new Locale("tr")).toUpperCase() + "%");
	}
	
	
	
	
	private <T> JpaEntityInformation<T, ?> getEntityInfo(Class<T> domainClass) {
		JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
		return entityInformation;
	}
	
	@SuppressWarnings("unchecked")
	private <T> JpaEntityInformation<T, ?> getEntityInfo(T entity) {
		JpaEntityInformation<T, ?> entityInformation = getEntityInfo((Class<T>) entity.getClass());
		return entityInformation;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
	 */
	@Transactional
	public <T> T save(T entity) {
		
		JpaEntityInformation<T, ?> entityInformation = getEntityInfo(entity);
		
		if (entityInformation.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}
	
	@Transactional
	public <T> List<T> saveAll(Iterable<T> entities) {
		
		Assert.notNull(entities, "Entities must not be null!");
		List<T> result = new ArrayList<T>();
		
		for (T entity : entities) {
			
			result.add(this.save(entity));
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public  <T> void delete(T entity) {
		
		Assert.notNull(entity, "Entity must not be null!");
		
		JpaEntityInformation<T, ?> entityInformation = getEntityInfo(entity);
		
		if (entityInformation.isNew(entity)) {
			return;
		}
		
		Class<?> type = ProxyUtils.getUserClass(entity);
		
		T existing = (T) em.find(type, entityInformation.getId(entity));
		
		// if the entity to be deleted doesn't exist, delete is a NOOP
		if (existing == null) {
			return;
		}
		
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Transactional
	public  <T> void deleteAll(Iterable<T> entities) {
		
		Assert.notNull(entities, "Entities must not be null!");
		
		for (T entity : entities) {
			delete(entity);
		}
	}
}
