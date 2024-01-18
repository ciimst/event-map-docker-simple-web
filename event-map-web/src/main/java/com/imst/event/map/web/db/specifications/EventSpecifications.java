package com.imst.event.map.web.db.specifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.State;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.support.CustomSpecification;
import com.imst.event.map.web.db.support.CustomSpecificationAbs;
import com.imst.event.map.web.db.support.CustomSpecificationImpl;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.AlertCriteriaEventItem;
import com.imst.event.map.web.vo.EventItem;
import com.imst.event.map.web.vo.EventItemForEventTypeSelectBox;
import com.imst.event.map.web.vo.EventTableViewItem;
import com.imst.event.map.web.vo.SidebarEventItem;
import com.imst.event.map.web.vo.SidebarEventItemForHeatMap;

public class EventSpecifications  {
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, SidebarEventItem> sidebarSpecification(Date startDate, Date endDate, Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, Date lastScrollDate, Integer alertLastId, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, Double southWestLng, Double southWestLat,
			Double northEastLng, Double northEastLat, List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, boolean isAlertEvent){
		
		return new CustomSpecificationAbs<Event, SidebarEventItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<SidebarEventItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if (lastScrollDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), lastScrollDate));
				}
				
				if (firstScrollDate != null) {// page refresh esnasında sonradan eklenmiş eski tarihli bir olayın idsinin büyük olmasından dolayı sayfa açıldıktan sonra refresh ile görünmesini önlemek için eklenmiştir
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), firstScrollDate));
				}
				
				
				if (lastId != null && lastId != 0) {					
					predicates.add(criteriaBuilder.gt(root.get("id"), lastId));
				}
				
				if (alertLastId != null && alertLastId != 0) {					
					predicates.add(criteriaBuilder.le(root.get("id"), alertLastId));
				}
				
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}	
				
				if(eventGroupIdList != null && ! eventGroupIdList.isEmpty()) {
					
					predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));
				}		  
				
				
				if(southWestLng != null) {
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("longitude"), southWestLng));
				}
				if(southWestLat != null) {
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("latitude"), southWestLat));
				}
				
				if(northEastLng != null) {
					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("longitude"), northEastLng));
				}
				
				if(northEastLat != null) {
					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("latitude"), northEastLat));
				}
				
				if(!StringUtils.isBlank(eventSearch)){
					
					Predicate spotPredicate = ilike(criteriaBuilder, root.get("spot"), eventSearch);
					Predicate descriptionPredicate = ilike(criteriaBuilder, root.get("description"), eventSearch);
					Predicate titlePredicate = ilike(criteriaBuilder, root.get("title"), eventSearch);
					
					Predicate spotOrTitleOrDescriptionSearch = criteriaBuilder.or(spotPredicate, descriptionPredicate, titlePredicate);
					
					predicates.add(criteriaBuilder.and(spotOrTitleOrDescriptionSearch));				
				
				}
				
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.in(root.get("eventType").get("id").in(eventTypeIdSearch)));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
								
				
				if(isAlertEvent == true) {
					
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();
					
					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
				}
				
				
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, Long> totalCountEvents(List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, 
			String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList, Boolean isAlertEvent, Date startDate, Date endDate){
		
		return new CustomSpecificationImpl<Event, Long>() {

			
			@Override
			public Predicate toPredicateCount(Root<Event> root, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				if(!StringUtils.isBlank(eventSearch)){
					
					Predicate spotPredicate = ilike(criteriaBuilder, root.get("spot"), eventSearch);
					Predicate descriptionPredicate = ilike(criteriaBuilder, root.get("description"), eventSearch);
					Predicate titlePredicate = ilike(criteriaBuilder, root.get("title"), eventSearch);
					
					Predicate spotOrTitleOrDescriptionSearch = criteriaBuilder.or(spotPredicate, descriptionPredicate, titlePredicate);
					
					predicates.add(criteriaBuilder.and(spotOrTitleOrDescriptionSearch));				
				
				}
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}						  
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				
				if(eventGroupIdList != null && ! eventGroupIdList.isEmpty()) {
					
					predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));
				}	
				
				
				if(isAlertEvent == true) {
					
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");			
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();

					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
				}
				
				
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, AlertCriteriaEventItem> alertSpecification(Integer lastId){
		
		return new CustomSpecificationAbs<Event, AlertCriteriaEventItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<AlertCriteriaEventItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				predicates.add(criteriaBuilder.gt(root.get("id"), lastId));
				//state olmayacak çünkü kullanan yer eskiye yönelik işlem yapmadığından event pasifte gelse işleme almalıdır.
								
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, EventItem> lastEventSpecification(){
		
		return new CustomSpecificationAbs<Event, EventItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
	}
	
	
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, SidebarEventItem> getAllEventSpecification(List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, Date startDate, Date endDate, Integer lastId, Integer alertLastId, String eventSearch){
		
		return new CustomSpecificationAbs<Event, SidebarEventItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<SidebarEventItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
								
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}		
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if (lastId != null && lastId != 0) {					
					predicates.add(criteriaBuilder.gt(root.get("id"), lastId));
				}
				
				if (alertLastId != null && alertLastId != 0) {					
					predicates.add(criteriaBuilder.le(root.get("id"), alertLastId));
				}
				
				if(!StringUtils.isBlank(eventSearch)){
					
					Predicate spotPredicate = ilike(criteriaBuilder, root.get("spot"), eventSearch);
					Predicate descriptionPredicate = ilike(criteriaBuilder, root.get("description"), eventSearch);
					Predicate titlePredicate = ilike(criteriaBuilder, root.get("title"), eventSearch);
					
					Predicate spotOrTitleOrDescriptionSearch = criteriaBuilder.or(spotPredicate, descriptionPredicate, titlePredicate);
					
					predicates.add(criteriaBuilder.and(spotOrTitleOrDescriptionSearch));				
				
				}

				
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
								
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, SidebarEventItemForHeatMap> sidebarSpecificationForHeatMap(Date startDate, Date endDate, Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, Date lastScrollDate, Integer alertLastId, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList){
		
		return new CustomSpecificationAbs<Event, SidebarEventItemForHeatMap>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<SidebarEventItemForHeatMap> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if (lastScrollDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), lastScrollDate));
				}
				
				if (firstScrollDate != null) {// page refresh esnasında sonradan eklenmiş eski tarihli bir olayın idsinin büyük olmasından dolayı sayfa açıldıktan sonra refresh ile görünmesini önlemek için eklenmiştir
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), firstScrollDate));
				}
				
				
				if (lastId != null && lastId != 0) {					
					predicates.add(criteriaBuilder.gt(root.get("id"), lastId));
				}
				
				if (alertLastId != null && alertLastId != 0) {					
					predicates.add(criteriaBuilder.le(root.get("id"), alertLastId));
				}
				
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}		
				
				if(!StringUtils.isBlank(eventSearch)){
					
					Predicate spotPredicate = ilike(criteriaBuilder, root.get("spot"), eventSearch);
					Predicate descriptionPredicate = ilike(criteriaBuilder, root.get("description"), eventSearch);
					Predicate titlePredicate = ilike(criteriaBuilder, root.get("title"), eventSearch);
					
					Predicate spotOrTitleOrDescriptionSearch = criteriaBuilder.or(spotPredicate, descriptionPredicate, titlePredicate);
					
					predicates.add(criteriaBuilder.and(spotOrTitleOrDescriptionSearch));				
				
				}				
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				if(eventGroupIdList != null && ! eventGroupIdList.isEmpty()) {
					
					predicates.add(root.get("eventGroup").get("id").in(eventGroupIdList));
				}
								
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		
		
		
	}
		
	
//	@SuppressWarnings("serial")
//	public static CustomSpecification<Event, EventItemForEventTypeSelectBox> eventTypeSpecification(List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId){
//		
//		return new CustomSpecificationAbs<Event, EventItemForEventTypeSelectBox>() {
//
//			@Override
//			public Selection<?>[] getConstructorParams(Root<Event> root) {
//				
//				return new Selection[] {
//						root.get("eventType").get("id"),
//						root.get("eventType").get("name"),
//						root.get("eventType").get("image"),
//						root.get("eventType").get("code")
//				};
//				
//				//return null;
//			}
//			
//			@Override
//			public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventItemForEventTypeSelectBox> criteriaQuery,
//					CriteriaBuilder criteriaBuilder) {
//				
//				List<Predicate> predicates = new ArrayList<>();
//				List<Predicate> userOrGroupIdList = new ArrayList<>();
//				
//				
//				
//				if(groupIdList != null && !groupIdList.isEmpty()){	
//					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
//				}
//				
//				if(userIdList != null && !userIdList.isEmpty()){
//					userOrGroupIdList.add( root.get("userId").in(userIdList));
//				}	
//				
//
////				Root<Event> event = criteriaQuery.from(Event.class);  
////				criteriaQuery.multiselect(event.get("eventType").get("id"),criteriaBuilder.count(event)).groupBy(event.get("eventType").get("id"));  
//				
//				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
//				
//				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
//				predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
//								
////				criteriaQuery.groupBy(root.get("eventType").get("id"));
//				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//			}
//		};
//	}
	
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, EventTableViewItem> eventTableViewSpecification(Date startDate, Date endDate,  List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, 
			Integer eventGroupId, String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Boolean state, List<Integer> permEventGroupIds, Boolean hasEventStateViewRole,
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent){
		
		return new CustomSpecificationAbs<Event, EventTableViewItem>() {

			@Override
			public Selection<?>[] getConstructorParams(Root<Event> root) {
				return null;
			}
			
			@Override
			public Predicate toPredicate(Root<Event> root, CriteriaQuery<EventTableViewItem> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}

				predicates.add( root.get("eventGroup").get("id").in(permEventGroupIds));
				
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}	
				
				if(eventGroupId != null && eventGroupId != 0) {
					predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventGroupId));
				}		  

				
				if(!StringUtils.isBlank(title)){
					predicates.add(ilike(criteriaBuilder, root.get("title"), title));
				}
				
				if(!StringUtils.isBlank(spot)){
					predicates.add(ilike(criteriaBuilder, root.get("spot"), spot));
				}
				
				if(!StringUtils.isBlank(description)){
					predicates.add(ilike(criteriaBuilder, root.get("description"), description));
				}
				
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}
				
				if(!StringUtils.isBlank(eventSearchBlackListTag)){
					predicates.add(ilike(criteriaBuilder, root.get("blackListTag"), eventSearchBlackListTag));
				}
				
				if(!StringUtils.isBlank(reserved1)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved1"), reserved1));
				}
				
				if(!StringUtils.isBlank(reserved2)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved2"), reserved2));
				}
				
				if(!StringUtils.isBlank(reserved3)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved3"), reserved3));
				}
				
				if(!StringUtils.isBlank(reserved4)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved4"), reserved4));
				}
				
				if(!StringUtils.isBlank(reserved5)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved5"), reserved5));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				
				if (hasEventStateViewRole) {
					if(state != null) {//aramadan 
						State objState = StateE.getBooleanState(state);
						predicates.add(criteriaBuilder.equal(root.get("state").get("id"), objState.getId()));
					}else{
						predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
					}
				} else {
					predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				}
				
				if(isAlertEvent == true) {
					
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();
					
					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
				}
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static CustomSpecification<Event, Long> totalCountEventTableView(List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, 
			String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Integer eventGroupId,  Date startDate, Date endDate, Boolean state, List<Integer> permEventGroupIds, Boolean hasEventStateViewRole, 
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent){
		
		return new CustomSpecificationImpl<Event, Long>() {

			
			@Override
			public Predicate toPredicateCount(Root<Event> root, CriteriaQuery<Long> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> userOrGroupIdList = new ArrayList<>();
				
				if(!StringUtils.isBlank(title)){
					predicates.add(ilike(criteriaBuilder, root.get("title"), title));
				}
				
				if(!StringUtils.isBlank(spot)){
					predicates.add(ilike(criteriaBuilder, root.get("spot"), spot));
				}
				
				if(!StringUtils.isBlank(description)){
					predicates.add(ilike(criteriaBuilder, root.get("description"), description));
				}				
				
				if (startDate != null) {
					predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), startDate));
				}
				
				if (endDate != null) {
					predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), endDate));
				}
				
				if(!StringUtils.isBlank(eventSearchCity)){
					predicates.add(ilike(criteriaBuilder, root.get("city"), eventSearchCity));
				}
				
				if(!StringUtils.isBlank(eventSearchCountry)){
					predicates.add(ilike(criteriaBuilder, root.get("country"), eventSearchCountry));
				}

				if(!StringUtils.isBlank(eventSearchBlackListTag)){
					predicates.add(ilike(criteriaBuilder, root.get("blackListTag"), eventSearchBlackListTag));
				}
				
				if(!StringUtils.isBlank(reserved1)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved1"), reserved1));
				}
				
				if(!StringUtils.isBlank(reserved2)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved2"), reserved2));
				}
				
				if(!StringUtils.isBlank(reserved3)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved3"), reserved3));
				}
				
				if(!StringUtils.isBlank(reserved4)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved4"), reserved4));
				}
				
				if(!StringUtils.isBlank(reserved5)){
					predicates.add(ilike(criteriaBuilder, root.get("reserved5"), reserved5));
				}
				
				if(eventTypeIdSearch != null && !eventTypeIdSearch.isEmpty()) {
//					predicates.add(criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeIdSearch));
					predicates.add(root.get("eventType").get("id").in(eventTypeIdSearch));
				}
				
				if(groupIdList != null && !groupIdList.isEmpty()){	
					userOrGroupIdList.add( root.get("groupId").in(groupIdList));
				}
				
				if(userIdList != null && !userIdList.isEmpty()){
					userOrGroupIdList.add( root.get("userId").in(userIdList));
				}	
				
				predicates.add( root.get("eventGroup").get("id").in(permEventGroupIds));
				
				predicates.add(criteriaBuilder.or(userOrGroupIdList.toArray(new Predicate[0])));
				
				predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("layer").get("id"), currentLayerId));
				
				if (hasEventStateViewRole) {
					if(state != null) {//aramadan 
						State objState = StateE.getBooleanState(state);
						predicates.add(criteriaBuilder.equal(root.get("state").get("id"), objState.getId()));
					}else{
						predicates.add( criteriaBuilder.or(criteriaBuilder.equal(root.get("state").get("id"), StateE.FALSE.getValue()), criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue())));
					}
				} else {
					predicates.add(criteriaBuilder.equal(root.get("state").get("id"), StateE.TRUE.getValue()));
				}
				
				if(eventGroupId != null && eventGroupId != 0) {
					predicates.add(criteriaBuilder.equal(root.get("eventGroup").get("id"), eventGroupId));
				}
				
				if(isAlertEvent == true) {
					
					Join<AlertEvent, Event> alertEvent = root.join("alertEvents");
					UserItemDetails sessionUser = ApplicationContextUtils.getUser();
					
					predicates.add(criteriaBuilder.equal(alertEvent.get("user").get("id"), sessionUser.getUserId()));
					predicates.add(criteriaBuilder.equal(alertEvent.get("dbName"), Statics.DEFAULT_DB_NAME));
					
					criteriaQuery.distinct(true);
				}
				
				
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
	}


}
