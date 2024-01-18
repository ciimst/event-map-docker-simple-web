package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.MapArea;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.MapAreaItem;

public class MapAreaSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<MapArea, MapAreaItem> sidebarSpecification(Integer currentLayerId){
		
		return new CustomSpecificationImpl<MapArea, MapAreaItem>() {
			@Override
			public Predicate toPredicate(Root<MapArea> root, CriteriaQuery<MapAreaItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.equal(root.get("mapAreaGroup").get("layer").get("id"), currentLayerId) );
				predicates.add(criteriaBuilder.equal(root.get("state"), true) );
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
