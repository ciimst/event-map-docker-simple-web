package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.hibernate.entity.MapAreaGroup;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.vo.MapAreaGroupItem;

public class MapAreaGroupSpecifications  {
		
	@SuppressWarnings("serial")
	public static CustomSpecification<MapAreaGroup, MapAreaGroupItem> mapAreaGroupSpecification(Integer currentLayerId){
		
		return new CustomSpecificationAbs<MapAreaGroup, MapAreaGroupItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<MapAreaGroup> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<MapAreaGroup> root, CriteriaQuery<MapAreaGroupItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				predicates.add(criteriaBuilder.equal(root.get("layer").get("id"), currentLayerId));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
