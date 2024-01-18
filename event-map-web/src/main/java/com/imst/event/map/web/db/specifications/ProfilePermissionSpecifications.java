package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.imst.event.map.hibernate.entity.ProfilePermission;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.vo.ProfilePermissionItem;

public class ProfilePermissionSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<ProfilePermission, ProfilePermissionItem> permissionNameSpecification(Integer profileId){
		
		return new CustomSpecificationImpl<ProfilePermission, ProfilePermissionItem>() {
			@Override
			public Predicate toPredicate(Root<ProfilePermission> root, CriteriaQuery<ProfilePermissionItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.equal(root.get("profile").get("id"), profileId));
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
}
