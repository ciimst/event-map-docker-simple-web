package com.imst.event.map.web.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

public class UserItemDetails implements UserDetails {

	private static final long serialVersionUID = -2655557918963171129L;
	
	private String password;
    private String displayName;
    private String username;
    private Integer userId;
    private Integer providerUserId;
    private Collection<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean loggedOut;
    private boolean connectionTimedOut;
    private boolean connectionExpired;
    private List<UserLayerPermissionItem> userLayerPermissionList;
    private List<UserEventGroupPermissionItem> userEventGroupPermissionList;
    private List<Integer> userIdList;
    private List<Integer> groupIdList;
    private String excelStateInformation;
    private boolean writeUserSettingsToCookieAfterLogin;
    
    private String currentLayerGuid;
    private String currentUserSettingsGroupName;

    public UserItemDetails(Integer userId, String username, String displayName, String password, Collection<GrantedAuthority> authorities,
						   boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired,
						   boolean enabled,
						   List<UserLayerPermissionItem> userLayerPermissionList, String currentLayerGuid, String currentUserSettingsGroupName, List<Integer> groupIdList, List<Integer> userIdList, Integer providerUserId,
						   List<UserEventGroupPermissionItem> userEventGroupPermissionList, String excelStateInformation, boolean writeUserSettingsToCookieAfterLogin){
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.displayName = displayName;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.userLayerPermissionList=userLayerPermissionList;
        this.currentLayerGuid = currentLayerGuid;
        this.currentUserSettingsGroupName = currentUserSettingsGroupName;
        this.groupIdList = groupIdList;
        this.userIdList = userIdList;
        this.providerUserId = providerUserId;
        this.userEventGroupPermissionList = userEventGroupPermissionList;
        this.excelStateInformation = excelStateInformation;
        this.writeUserSettingsToCookieAfterLogin = writeUserSettingsToCookieAfterLogin;
    }
    
	public String getCurrentLayerGuid() {
		return currentLayerGuid;
	}

	public void setCurrentLayerGuid(String currentLayerGuid) {
		this.currentLayerGuid = currentLayerGuid;
	}
	
	public String getCurrentUserSettingsGroupName() {
		return currentUserSettingsGroupName;
	}
	
	public void setCurrentUserSettingsGroupName(String currentUserSettingsGroupName) {
		this.currentUserSettingsGroupName = currentUserSettingsGroupName;
	}
	
	public List<UserLayerPermissionItem> getUserLayerPermissionList(){
    	return userLayerPermissionList;
    }    
	public void setUserLayerPermissionList(List<UserLayerPermissionItem> userLayerPermissionIdList) {
		this.userLayerPermissionList = userLayerPermissionIdList;
	}
	
	public List<UserEventGroupPermissionItem> getUserEventGroupPermissionList(){
		return userEventGroupPermissionList;
	}
	
	public void setUserEventGroupPermissionList(List<UserEventGroupPermissionItem> userEventGroupPermissionList) {
		this.userEventGroupPermissionList = userEventGroupPermissionList;
	}
	
	public String getExcelStateInformation() {
		return excelStateInformation;
	}

	public void setExcelStateInformation(String excelStateInformation) {
		this.excelStateInformation = excelStateInformation;
	}
    

	public boolean getWriteUserSettingsToCookieAfterLogin() {
		return writeUserSettingsToCookieAfterLogin;
	}

	public void setWriteUserSettingsToCookieAfterLogin(boolean writeUserSettingsToCookieAfterLogin) {
		this.writeUserSettingsToCookieAfterLogin = writeUserSettingsToCookieAfterLogin;
	}

	public List<Integer> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<Integer> userIdList) {
		this.userIdList = userIdList;
	}

	public List<Integer> getGroupIdList() {
		return groupIdList;
	}

	public void setGroupIdList(List<Integer> groupIdList) {
		this.groupIdList = groupIdList;
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    

    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    public Integer getUserId() {
        
        return userId;
    }
    
    public Integer getProviderUserId() {
        
        return providerUserId;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLoggedOut() {

        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {

        this.loggedOut = loggedOut;
    }

    public boolean isConnectionTimedOut() {

        return connectionTimedOut;
    }

    public void setConnectionTimedOut(boolean connectionTimedOut) {

        this.connectionTimedOut = connectionTimedOut;
    }

    public boolean isConnectionExpired() {

        return connectionExpired;
    }

    public void setConnectionExpired(boolean connectionExpired) {

        this.connectionExpired = connectionExpired;
    }

    @Override
    public boolean equals(Object obj) {

        return this.getUsername().equals(((UserItemDetails) obj).getUsername());
    }

    @Override
    public int hashCode() {

        return this.getUsername().hashCode();
    }

}
