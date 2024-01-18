package com.imst.event.map.web.vo;

import java.util.List;

import javax.persistence.Column;

public class EventGroupTreeItem {
	
	private Integer id;
	private Integer parentId;
	private String name;
	private List<EventGroupTreeItem> items;
	@Column(name = "name")
	private String dbName;
	private boolean showCheckboxes;

	public EventGroupTreeItem(Integer id, Integer parentId, String name, String dbName, boolean showCheckboxes) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.dbName = dbName;
		this.showCheckboxes = showCheckboxes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EventGroupTreeItem> getItems() {
		return items;
	}

	public void setItems(List<EventGroupTreeItem> items) {
		this.items = items;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public boolean isShowCheckboxes() {
		return showCheckboxes;
	}

	public void setShowCheckboxes(boolean showCheckboxes) {
		this.showCheckboxes = showCheckboxes;
	}

	


}