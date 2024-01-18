package com.imst.event.map.web.db.support;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * @param <D> domain class (entity oluyor yani)
 * @param <T> target class
 */
public interface CustomSpecification<D, T> extends Serializable {
	
	
	Predicate toPredicate(Root<D> root, CriteriaQuery<T> criteriaQuery, CriteriaBuilder criteriaBuilder);
	Predicate toPredicateCount(Root<D> root, CriteriaQuery<Long> query, CriteriaBuilder criteriaBuilder);
	
	Class<D> getDomain();
	Class<T> getTarget();
	public Selection<?>[] getSelections(Root<D> root);
}
