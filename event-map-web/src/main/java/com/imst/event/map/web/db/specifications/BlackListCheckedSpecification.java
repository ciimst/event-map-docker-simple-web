package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.imst.event.map.hibernate.entity.BlackList;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.vo.BlackListItem;


public class BlackListCheckedSpecification extends CustomSpecificationAbs<BlackList, BlackListItem> {
	
	   private static final long serialVersionUID = -1680943579044626506L;
	
	   private BlackListItem blackListItem;
	   private List<Integer> permissionEventGroupIdList;
	
       public BlackListCheckedSpecification(BlackListItem blackListItem, List<Integer> permissionEventGroupIdList) {
		
			this.blackListItem = blackListItem;
			this.permissionEventGroupIdList = permissionEventGroupIdList;
		}

        @Override
        public Predicate toPredicate(Root<BlackList> root, CriteriaQuery<BlackListItem> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		
        	List<Predicate> predicatesList = new ArrayList<>();
        	
			List<Predicate> predicates1 = new ArrayList<>();
			List<Predicate> predicates2 = new ArrayList<>();
			List<Predicate> predicates3 = new ArrayList<>();
			List<Predicate> predicates4 = new ArrayList<>();
			
			predicates1.add(criteriaBuilder.equal(root.get("tag"), blackListItem.getTag()));
			predicates1.add(criteriaBuilder.equal(root.get("layer").get("id"), blackListItem.getLayerId()));
			predicates1.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
			predicates1.add(criteriaBuilder.isNull(root.get("eventType").get("id")));
			predicates1.add(criteriaBuilder.isNull(root.get("eventGroup").get("id"))); 
		
			predicatesList.add(criteriaBuilder.and(predicates1.toArray(new Predicate[0])));
			
			predicates2.add(criteriaBuilder.equal(root.get("tag"), blackListItem.getTag()));
			predicates2.add(criteriaBuilder.equal(root.get("layer").get("id"), blackListItem.getLayerId())); 
			predicates2.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
			predicates2.add(criteriaBuilder.equal(root.get("eventType").get("id"), blackListItem.getEventTypeId()));
			predicates2.add(criteriaBuilder.isNull(root.get("eventGroup").get("id"))); 
		
			predicatesList.add(criteriaBuilder.and(predicates2.toArray(new Predicate[0])));
			
			predicates3.add(criteriaBuilder.equal(root.get("tag"), blackListItem.getTag()));
			predicates3.add(criteriaBuilder.equal(root.get("layer").get("id"), blackListItem.getLayerId()));
			predicates3.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
			predicates3.add(criteriaBuilder.isNull(root.get("eventType").get("id")));
			//predicates3.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), blackListItem.getEventGroupId()));
			predicates3.add( root.get("eventGroup").get("id").in(permissionEventGroupIdList));
		
			predicatesList.add(criteriaBuilder.and(predicates3.toArray(new Predicate[0])));
			
			predicates4.add(criteriaBuilder.equal(root.get("tag"), blackListItem.getTag()));
			predicates4.add(criteriaBuilder.equal(root.get("layer").get("id"), blackListItem.getLayerId()));
			predicates4.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
			predicates4.add(criteriaBuilder.equal(root.get("eventType").get("id"), blackListItem.getEventTypeId()));
//			predicates4.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), blackListItem.getEventGroupId()));
			predicates4.add( root.get("eventGroup").get("id").in(permissionEventGroupIdList));
			
			predicatesList.add(criteriaBuilder.and(predicates4.toArray(new Predicate[0])));
				
			
		    return criteriaBuilder.or(predicatesList.toArray(new Predicate[0]));
		}
		@Override
		public Selection<?>[] getConstructorParams(Root<BlackList> root) {
			
			return new Selection[] {root.get("id"),
					root.get("name"),
					root.get("tag"),
					root.get("createUser"),
					root.get("createDate"),
					root.get("updateDate"),
					root.get("state").get("id"),
					root.get("layer").get("id"),
					root.get("layer").get("name"),
					root.get("eventGroup").get("id"),
					root.get("eventType").get("id"),
					root.get("actionState").get("id"),
					root.get("actionState").get("stateType")
			};
		}
	
	}
