package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.factory.annotation.Autowired;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

public class LayerSpecification {
	@Autowired
	static UserLayerPermissionService userLayerPermissionService;
	@SuppressWarnings("serial")
	public static CustomSpecification<UserLayerPermission, UserLayerPermissionItem> layerSpecification(User user){
		
		return new CustomSpecificationAbs<UserLayerPermission, UserLayerPermissionItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<UserLayerPermission> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<UserLayerPermission> root, CriteriaQuery<UserLayerPermissionItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();	
				
				predicates.add(criteriaBuilder.equal(root.get("user").get("id"),user.getId()));
				predicates.add(criteriaBuilder.equal(root.get("layer").get("state"), true));
			
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
