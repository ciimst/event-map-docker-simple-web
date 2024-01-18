package com.imst.event.map.web.constant;

import java.util.HashMap;	
	public enum LogTypeE {
		NONE(0, "unknown", "unknown"),
		LOGIN(1, "login", "user"),
		LOGOUT(2, "logout", "user"),
		USER_ADD(3, "user_add", "user"),
		USER_EDIT(4, "user_edit", "user"),
		USER_DELETE(5, "user_delete", "user"),
		PROFILE_ADD(6, "profile_add", "profile"),
		PROFILE_EDIT(7, "profile_edit", "profile"),
		PROFILE_DELETE(8, "profile_delete", "profile"),
		EVENT_ADD(9, "event_add", "event"),
		EVENT_EDIT(10, "event_edit", "event"),
		EVENT_DELETE(11, "event_delete", "event"),
		EVENT_TAG_ADD(12, "event_tag_add", "event_tag"),
		EVENT_TAG_EDIT(13, "event_tag_edit", "event_tag"),
		EVENT_TAG_DELETE(14, "event_tag_delete", "event_tag"),
		EVENT_TYPE_ADD(15, "event_type_add", "event_type"),
		EVENT_TYPE_EDIT(16, "event_type_edit", "event_type"),
		EVENT_TYPE_DELETE(17, "event_type_delete", "event_type"),
		LAYER_ADD(18, "layer_add", "layer"),
		LAYER_EDIT(19, "layer_edit", "layer"),
		LAYER_DELETE(20, "layer_delete", "layer"),
		MAP_AREA_ADD(21, "map_area_add", "map_area"),
		MAP_AREA_EDIT(22, "map_area_edit", "map_area"),
		MAP_AREA_DELETE(23, "map_area_delete", "map_area"),
		SETTINGS_EDIT(24, "settings_edit", "settings"),
		TILE_SERVER_ADD(25, "tile_server_add", "tile_server"),
		TILE_SERVER_EDIT(26, "tile_server_edit", "tile_server"),
		TILE_SERVER_DELETE(27, "tile_server_delete", "tile_server"),
		GEO_LAYER_ADD(28, "geo_layer_add", "geo_layer"),
		GEO_LAYER_EDIT(29, "geo_layer_edit", "geo_layer"),
		GEO_LAYER_DELETE(30, "geo_layer_delete", "geo_layer"),
		MAP_AREA_GROUP_ADD(31, "map_area_add", "map_area_group"),
		MAP_AREA_GROUP_EDIT(32, "map_area_edit", "map_area_group"),
		MAP_AREA_GROUP_DELETE(33, "map_area_delete", "map_area_group"),
		USER_EVENT_PERMISSION_ADD(34, "user_event_permission_add", "user_event_permission"),
		USER_EVENT_PERMISSION_EDIT(35, "user_event_permission_edit", "user_event_permission"),
		USER_EVENT_PERMISSION_DELETE(36, "user_event_permission_delete", "user_event_permission"),
		EVENT_GROUP_ADD(37, "event_group_add", "event_group"),
		EVENT_GROUP_EDIT(38, "event_group_edit", "event_group"),
		EVENT_GROUP_DELETE(39, "event_group_delete", "event_group"),
		TAG_ADD(40, "tag_add", "tag"),
		TAG_EDIT(41, "tag_edit", "tag"),
		TAG_DELETE(42, "tag_delete", "tag"),
		USER_GROUP_ID_ADD(43,"user_group_id_add","user_group_id"),
		USER_GROUP_ID_EDIT(44,"user_group_id_edit","user_group_id"),
		USER_GROUP_ID_DELETE(45,"user_group_id_delete","user_group_id"),
		USER_LAYER_PERMISSION_ADD(46,"user_layer_permission_add","user_layer_permission"),
		USER_LAYER_PERMISSION_EDIT(47,"user_layer_permission_edit","user_layer_permission"),
		USER_LAYER_PERMISSION_DELETE(48,"user_layer_permission_delete","user_layer_permission"),
		USER_USER_ID_ADD(49,"user_user_id_add","user_user_id"),
		USER_USER_ID_EDIT(50,"user_user_id_edit","user_user_id"),
		USER_USER_ID_DELETE(51,"user_user_id_delete","user_user_id"),
		EVENT_MEDIA_ADD(52,"event_media_add","event_media"),
		EVENT_MEDIA_EDIT(53,"event_media_edit","event_media"),
		EVENT_MEDIA_DELETE(54,"event_media_delete","event_media"),
		ALERT_ADD(55,"alert_add","alert"),
		ALERT_EDIT(56,"alert_edit","alert"),
		ALERT_DELETE(57,"alert_delete","alert"),
		USER_SETTINGS_ADD(61,"user_settings_add", "user_settings"),
		USER_SETTINGS_EDIT(62,"user_settings_edit", "user_settings"),
		USER_SETTINGS_DELETE(63,"user_settings_delete", "user_settings"),
		BLACK_LIST_ADD(64, "black_list_add", "black_list"),
		BLACK_LIST_EDIT(65, "black_list_edit", "black_list"),
		BLACK_LIST_DELETE(66, "black_list_delete", "black_list"),
		EVENT_LINK_ADD(67,"event_link_add", "event_link"),
		EVENT_LINK_EDIT(68,"event_link_edit", "event_link"),
		EVENT_LINK_DELETE(69,"event_link_delete", "event_link");
						
		private int id;
		private String name;
		private String relatedTable;
		private static HashMap<Integer, LogTypeE> map = new HashMap<>();
		
		LogTypeE(int id, String name, String relatedTable){
			this.id = id;
			this.name = name;
			this.relatedTable = relatedTable;
		}
		
		
		public int getId() {
			
			return id;
		}
		
		public String getName() {
			
			return name;
		}
		
		public String getRelatedTable() {
			return relatedTable;
		}
		
		public static LogTypeE getSetting(Integer key) {
			return map.get(key) == null ? NONE : map.get(key);
		}
		static {
			
			for (LogTypeE logTypeE : LogTypeE.values()) {
				map.put(logTypeE.id, logTypeE);
			}
		}
	}

