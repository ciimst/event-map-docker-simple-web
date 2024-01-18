package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.SidebarAlertItem;

public class AlertSpecifications {

	@SuppressWarnings("serial")
	public static CustomSpecification<Alert, SidebarAlertItem> alertSpecification(Integer alertId){		
		
		return new CustomSpecificationImpl<Alert, SidebarAlertItem>() {

			@Override
			public Predicate toPredicate(Root<Alert> root, CriteriaQuery<SidebarAlertItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				if(alertId != null){
					predicates.add(criteriaBuilder.equal(root.get("id"), alertId));
				}
												
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
