package com.imst.event.map.hibernate.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tile_export", schema = "public")
public class TileExport implements java.io.Serializable{

	private Integer id;
	private String name;
	private Integer minZ;
	private Integer maxZ;
	private Timestamp createDate;
	private Double maxLat;
	private Double minLat;
	private Double maxLong;
	private Double minLong;
	private TileServer tileServer;
	
	public TileExport() {
		
	}
	
	public TileExport(Integer id, String name, Integer minZ, Integer maxZ, Timestamp createDate, double maxLat, double minLat, double maxLong, double minLong, TileServer tileServer) {
		this.id = id;
		this.name = name;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.createDate = createDate;
		this.maxLat = maxLat;
		this.minLat = minLat;
		this.maxLong = maxLong;
		this.minLong = minLong;
		this.tileServer = tileServer;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "name", length = 256)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "min_z", nullable = false)
	public Integer getMinZ() {
		return minZ;
	}

	public void setMinZ(Integer minZ) {
		this.minZ = minZ;
	}

	@Column(name = "max_z", nullable = false)
	public Integer getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(Integer maxZ) {
		this.maxZ = maxZ;
	}
	

	@Column(name = "create_date", nullable = false, length = 29)
	@org.hibernate.annotations.ColumnDefault("now()")
	public Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@Column(name = "max_lat")
	public Double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(Double maxLat) {
		this.maxLat = maxLat;
	}

	@Column(name = "min_lat")
	public Double getMinLat() {
		return minLat;
	}

	public void setMinLat(Double minLat) {
		this.minLat = minLat;
	}

	@Column(name = "max_long")
	public Double getMaxLong() {
		return maxLong;
	}

	public void setMaxLong(Double maxLong) {
		this.maxLong = maxLong;
	}

	@Column(name = "min_long")
	public Double getMinLong() {
		return minLong;
	}

	public void setMinLong(Double minLong) {
		this.minLong = minLong;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_tile_server_id")
	public TileServer getTileServer() {
		return tileServer;
	}

	public void setTileServer(TileServer tileServer) {
		this.tileServer = tileServer;
	}
	

	
}
