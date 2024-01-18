package com.imst.event.map.web.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.imst.event.map.web.vo.LatLongItem;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpatialUtil {
	
	private final static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());

	/**
	 * 
	 * @param wellKnownText : POLYGON((0 0, 0 5, 5 5, 5 0, 0 0))  -  POINT (31.745 43.583)
	 * @return
	 * @throws ParseException
	 */
	public static Geometry wktToGeometry(String wellKnownText) {

		try {
			
			Geometry geometry = new WKTReader().read(wellKnownText);
			
			return geometry;
		} catch (ParseException e) {
			log.debug(e);
		}
		
		return null;
	}
	
	
	public static Point getPoint(Coordinate coordinate) {
		
		Point point = geometryFactory.createPoint(coordinate);
		return point;
	}
	
	public static Point getPoint(double latitude, double longitude) {
		
		return getPoint(new Coordinate(latitude, longitude));
	}
	
	public static Polygon getCircle(LatLongItem[] latLongItemArr) {
	    
		if(latLongItemArr == null || latLongItemArr.length == 0) {
			return null;
		}
		
		List<Coordinate> coordinateList = Arrays.asList(latLongItemArr).stream().map(item -> new Coordinate(item.getLat(), item.getLng())).collect(Collectors.toList());
		
//		System.out.println(coordinateList.get(0));
//		System.out.println(coordinateList.get(1));
//		System.out.println(coordinateList.get(2));
//		System.out.println("r 2 : " + radius * 2);
//		System.out.println("rY 2 : " + radiusY * 2);
		
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
	    shapeFactory.setNumPoints(32);
	    shapeFactory.setCentre(coordinateList.get(0));

	    
	    double radiusLat = coordinateList.get(1).x - coordinateList.get(2).x;
	    radiusLat = Math.abs(radiusLat);
	    double radiusLon = coordinateList.get(1).y - coordinateList.get(2).y;
	    radiusLon = Math.abs(radiusLon);
	    
//	    System.out.println("R lat : " + radiusLat);
//	    System.out.println("R lon : " + radiusLon);
	    
	    
	    shapeFactory.setWidth(radiusLat);
	    shapeFactory.setHeight(radiusLon);

	    
	    return shapeFactory.createEllipse();
	}
	
	
	public static Polygon getPolygon(Coordinate[] coordinates) {
		
		if(coordinates == null || coordinates.length == 0) {
			return null;
		}
		
		// Polygonlarda ilk nokta ile son nokta aynı olması gerekmektedir. bunu sağlamak amacıyla yapılmış bir kontroldür
		if(coordinates[0] != coordinates[coordinates.length - 1]) {
			List<Coordinate> tempList = Arrays.asList(coordinates);
			tempList.add(coordinates[0]);
			
			coordinates = new Coordinate[tempList.size()];
			coordinates = tempList.toArray(coordinates);
		}
		
		Polygon polygon = geometryFactory.createPolygon(coordinates);
		return polygon;
	}
	
	public static Polygon getPolygon(LatLongItem[] latLongItemArr) {
		
		if(latLongItemArr == null || latLongItemArr.length == 0) {
			return null;
		}
		
		List<Coordinate> coordinateList = Arrays.asList(latLongItemArr).stream().map(item -> new Coordinate(item.getLat(), item.getLng())).collect(Collectors.toList());

		return getPolygon(coordinateList);
	}

	public static Polygon getPolygon(List<Coordinate> coordinateList) {

		if(coordinateList == null || coordinateList.size() == 0) {
			return null;
		}
		
		// Polygonlarda ilk nokta ile son nokta aynı olması gerekmektedir. bunu sağlamak amacıyla yapılmış bir kontroldür
		if(coordinateList.get(0) != coordinateList.get(coordinateList.size() - 1)) {
			coordinateList.add(coordinateList.get(0));
		}
		
		Coordinate[] coordinates = new Coordinate[coordinateList.size()];
		coordinates = coordinateList.toArray(coordinates);
		
		return getPolygon(coordinates);
	}
	
	public static List<LatLongItem> convertToLatLongItemList(Polygon polygon) {
	
		Coordinate[] coordinateArr = polygon.getCoordinates();
		List<Coordinate> coordinateList = Arrays.asList(coordinateArr);
		
		List<LatLongItem> latLongItemList = coordinateList.stream().map(item -> new LatLongItem(item.x, item.y)).collect(Collectors.toList());
		
		return latLongItemList;
	}
	
}
