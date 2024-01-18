package com.imst.event.map.web.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.jscookie.javacookie.Cookies;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.hibernate.entity.ProfilePermission;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;
import com.imst.event.map.hibernate.entity.UserSettings;
import com.imst.event.map.hibernate.entity.UserUserId;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.db.repositories.PermissionRepository;
import com.imst.event.map.web.db.repositories.ProfilePermissionRepository;
import com.imst.event.map.web.db.repositories.ProfileRepository;
import com.imst.event.map.web.db.repositories.UserRepository;
import com.imst.event.map.web.db.repositories.UserSettingsRepository;
import com.imst.event.map.web.services.UserEventGroupPermissionService;
import com.imst.event.map.web.services.UserGroupIdService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.services.UserUserIdService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;


@Service
public class UserDetailsServiceImpl implements UserDetailsService  {
	
	@Autowired private PermissionRepository permissionRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private UserLayerPermissionService userLayerPermissionService;
	@Autowired private UserEventGroupPermissionService userEventGroupPermissionService;
	
	@Autowired private UserUserIdService userUserIdService;
	@Autowired private UserGroupIdService userGroupIdService;
	@Autowired private ProfilePermissionRepository profilePermissionRepository;
	@Autowired private LayerRepository layerRepository;
	@Autowired private EventGroupRepository eventGroupRepository;
	@Autowired private UserSettingsRepository userSettingsRepository;
	@Autowired private ProfileRepository profileRepository;
	
	@Autowired private  HttpServletRequest request;
	@Autowired private HttpServletResponse response;
	
	@Transactional(transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsernameKeycloak(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, false, true);
		if (user == null) {
			
			List<Profile> defaultProfileList = profileRepository.findByIsDefault(true);
			
			if(defaultProfileList.size() == 0) {
				return null;
//				throw new UsernameNotFoundException("Profile not found authenticationFailed");
			}
			
			user = new User();
			user.setCreateDate(DateUtils.nowT());
			user.setIsDbUser(false);
			user.setName(username);
			user.setPassword(UUID.randomUUID().toString());
			user.setProfile(defaultProfileList.get(0));
			user.setState(true);
			user.setUsername(username);
			
			userRepository.save(user);
		}
				
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.webUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}
	
	@Override
	@Transactional(readOnly = true, transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, true, true);
		
		if (user == null) {
			throw new UsernameNotFoundException("authenticationFailed");
		}
				
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.webUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}
	
	@Transactional(readOnly = true, transactionManager = "masterTransactionManager")
	public UserItemDetails loadUserByUsernameLdap(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsernameAndIsDbUserAndState(username, false, true);
		
		if (user == null) {
			throw new UsernameNotFoundException("authenticationFailed");
		}
		
		List<ProfilePermission> profilePermissionList = profilePermissionRepository.findAllByProfileIdAndPermissionId(user.getProfile().getId(), Statics.webUserLoginPermissionId);
		
		if(profilePermissionList == null || profilePermissionList.isEmpty()) {
			throw new UsernameNotFoundException("authenticationFailed");
			
		}
		
		return getUserItemDetails(user);
	}
	
	private UserItemDetails getUserItemDetails(User user) {
		
		String username = user.getUsername();
		String displayName = user.getName();
		String password = user.getPassword();
		String excelStateInformation = "";
		boolean writeUserSettingsToCookieAfterLogin = false;
		
		Set<ProfilePermission> profilePermissions = user.getProfile().getProfilePermissions();
		List<GrantedAuthority> auths = profilePermissions.stream()
				.map(profilePermission -> new SimpleGrantedAuthority(profilePermission.getPermission().getName()))
				.collect(Collectors.toList());		
		
		String defaultLayerGuid = null;
		List<UserLayerPermissionItem> userLayerPermissionList = new ArrayList<>();
		
		Optional<GrantedAuthority> fullLayerPermissionAuth = auths.stream().filter(f -> f.toString().equals("ROLE_FULL_LAYER_PERMISSION")).findAny();
		
  		if(fullLayerPermissionAuth.isPresent()) {
  			
  			List<Layer> layerList = layerRepository.findAll(Sort.by(Direction.ASC, "id"));
  			
			for(Layer layer : layerList) {
				
				UserLayerPermissionItem userLayerPermissionItem=new UserLayerPermissionItem();
				userLayerPermissionItem.setLayerId(layer.getId());
				userLayerPermissionItem.setLayerName(layer.getName());
				userLayerPermissionItem.setUserId(user.getId());
				userLayerPermissionItem.setIsTemp(layer.getIsTemp());
				userLayerPermissionItem.setHasFullPermission(true);
				userLayerPermissionItem.setLayerGuid(layer.getGuid());
				userLayerPermissionList.add(userLayerPermissionItem);
			}
  		}
  		else {
			
			userLayerPermissionList = userLayerPermissionService.findAllByUser(Sort.by(Direction.ASC, "id"),user);
		}
		
		if(userLayerPermissionList.size()>0) {
			defaultLayerGuid = userLayerPermissionList.get(0).getLayerGuid();
		}
		
		String defaultUserSettingsGroupName = null;
		List<UserSettings> userSettingsList = userSettingsRepository.findAllByUser(user);
		for(UserSettings userSettings : userSettingsList) {
			
			if(!userSettings.getUserSettingsType().getGroupName().equals("Katman")) {
				
				defaultUserSettingsGroupName = userSettings.getUserSettingsType().getGroupName();
				break;
			}
		}
		
		List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = userEventGroupPermissionService.findAllByUser(Sort.by(Direction.ASC, "id"), user);
		
		//Katmana izni var ise tüm olay gruplarına izni var kabul edilir ve olay grubu iznine bakılmaz.
		//Katmana izni yoksa ve olay grubuna izni var ise izinli olduğu olay grubu ve alt gruplarına izni var kabul edilir.
		
		
		// İzinli olunan layerların altındaki bütün grupların grup izinlerine eklenmesi
		List<Integer> layerIdListFromLayerPermission = userLayerPermissionList.stream().map(UserLayerPermissionItem::getLayerId).distinct().collect(Collectors.toList());
		List<EventGroup> allEventGroupsUnderLayersList = eventGroupRepository.findAllByLayerIdIn(layerIdListFromLayerPermission);

		
		// izinli olunan grupların ait oldukları layerlara izin verilmesi
		List<Integer> distinctLayerIdListFromGroupPermission = userEventGroupPermissionItemList.stream()
				.map(UserEventGroupPermissionItem::getEventGroupLayerId)
				.distinct().collect(Collectors.toList());
		List<Layer> layerList = layerRepository.findAllByIdIn(distinctLayerIdListFromGroupPermission);
		for (UserEventGroupPermissionItem userEventGroupPermissionItem : userEventGroupPermissionItemList) {
			
			List<Layer> list = layerList.stream().filter(item -> item.getId().equals(userEventGroupPermissionItem.getEventGroupLayerId())).collect(Collectors.toList());

			if(list.size() == 0) {
				continue;
			}
			Layer layer = list.get(0);
			
			boolean anyMatch = userLayerPermissionList.stream().anyMatch(item -> item.getLayerId().equals(layer.getId()));
			if(!anyMatch) {
				UserLayerPermissionItem userLayerPartialPermissionItem = new UserLayerPermissionItem(0, layer.getName(), layer.getId(), userEventGroupPermissionItem.getUserId(), layer.getIsTemp(), layer.getGuid());
				userLayerPartialPermissionItem.setHasFullPermission(false);
				userLayerPermissionList.add(userLayerPartialPermissionItem);
			}
		}
		
		
		for (EventGroup eventGroup : allEventGroupsUnderLayersList) {
			UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0, eventGroup.getName(), eventGroup.getId(), eventGroup.getLayer().getId(), null, user.getId(), null);
			userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);
		}
		
		
		/************/
		//Parent Gruba izin varsa child gruplarına izin ekleme.
		//izinli olunan layer altındaki tüm olay grupları getirildi. Child grupları bulmak için kullanıldı.
		List<Integer> allPermLayerIdList = userLayerPermissionList.stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
		List<EventGroup> allEventGroupItemsUnderLayersList = eventGroupRepository.findAllByLayerIdIn(allPermLayerIdList);
		
		List<EventGroupItem> allEventGroupPermList = new ArrayList<>();
		
		allEventGroupItemsUnderLayersList.forEach(item -> {
			EventGroupItem eventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getDescription(), item.getParentId(), item.getLayer().getId(),"");
			allEventGroupPermList.add(eventGroupItem);
		});

		
		EventGroupTree eventGroupTree = new EventGroupTree(null, allEventGroupPermList);
		
		List<Integer> eventGroupPermissionIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		List<Integer> childEventGroupIds = eventGroupTree.getPermissionEventGroup(eventGroupPermissionIdList);
		
		
		childEventGroupIds.forEach(item -> {

			Optional<EventGroup> childEventGroup = allEventGroupItemsUnderLayersList.stream().filter(f -> f.getId().equals(item)).findAny();
		
			if(childEventGroup.isPresent()) {
				
				EventGroup eventGroup = childEventGroup.get();
				UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0, eventGroup.getName(), eventGroup.getId(), eventGroup.getLayer().getId(), null, user.getId(), null);
				userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);
			}
			
		});
		
		/**************/
		
		List<UserGroupId> userGroupIdList = userGroupIdService.findAllByUser(user);
		List<Integer> groupIdList = userGroupIdList.stream().map(UserGroupId::getGroupId).collect(Collectors.toList());
		groupIdList.add(0);
		
		
		List<UserUserId> userUserIdList = userUserIdService.findAllByUser(user);
		List <Integer> userIdList = userUserIdList.stream().map(UserUserId::getUserId).collect(Collectors.toList());
		userIdList.add(0);
		

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		
		if (defaultLayerGuid != null) {
			Cookies cookies = Cookies.initFromServlet( request, response );
			cookies.set( "currentLayerGuid", defaultLayerGuid ); 
		}	
		
		UserItemDetails userItemDetails = new UserItemDetails(user.getId(), username, displayName, password, auths, accountNonExpired,accountNonLocked, credentialsNonExpired, enabled, 
				userLayerPermissionList, defaultLayerGuid, defaultUserSettingsGroupName, groupIdList, userIdList, user.getProviderUserId(), userEventGroupPermissionItemList, excelStateInformation, writeUserSettingsToCookieAfterLogin);
		
		return userItemDetails;
	}
}
