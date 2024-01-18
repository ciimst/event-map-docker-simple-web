package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.web.db.support.CustomSpecificationAbs;

public class ProjectionSpecifications<T, D>  {
	
	@SuppressWarnings("serial")
	public CustomSpecificationAbs<T, D> eventIdListInSpecification(List<Integer> eventIdList){
		
		return new CustomSpecificationAbs<T, D>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<D> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				if(eventIdList != null && !eventIdList.isEmpty()){
					predicates.add( root.get("event").get("id").in(eventIdList));
				}
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}

			@Override
			public Selection<?>[] getConstructorParams(Root<T> root) {
				return null;
			}
		};
		
	}
}
