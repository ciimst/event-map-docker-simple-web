package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.TileServer;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.TileServerItem;

public class TileServerSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<TileServer, TileServerItem> sidebarSpecification(){
		
		return new CustomSpecificationImpl<TileServer, TileServerItem>() {
			@Override
			public Predicate toPredicate(Root<TileServer> root, CriteriaQuery<TileServerItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.equal(root.get("state"), true) );
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
