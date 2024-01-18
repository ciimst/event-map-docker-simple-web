MapHelper = function (OpenDetailModalCallbackFunc, eventGroupList) {

	this.MyMap;
	this.IsMapHelperTable = true;

	if (this.init) {
		this.init.apply(this, arguments);
	}
	
	this.initializeEvents();			
};

MapHelper.prototype = {
		
    init: function () {
			
					
	    var coordinateInfo = {
		    	latitude : 28.594169,
		    	longitude :18.51882,
		    	zoom : 6
		}	
    	this.MyMap = L.map('map', {
			center: [coordinateInfo.latitude, coordinateInfo.longitude],
			zoom: coordinateInfo.zoom,
			minZoom: 2,
			maxZoom: 19,
			maxBounds: this.bounds,
			useCache: false,
			zoomControl: false,
			preferCanvas: true,
		});				
		
		
		this.MyMap.createPane("eventMarker");
		this.MyMap.getPane("eventMarker").style.zIndex = 999;
						

        // zoom control options
        var zoomOptions = {
        	zoomInTitle: lang.props["label.map.zoom.in"],
        	zoomOutTitle: lang.props["label.map.zoom.out"],
        };
        var zoom = L.control.zoom(zoomOptions);   // Creating zoom control
        zoom.addTo(this.MyMap);   // Adding zoom control to the map
    	
		
    },
   
    initializeEvents: function () {
    	
    	let self = this;

		this.MyMap.on('baselayerchange', function(tileServer) {
		  	var tileServerName = tileServer.name
		  	$.each(Page.tileServers, function(key, value){
				
			if(tileServerName == value.name){											
				$(".leaflet-control-minimap").remove()
				self.AppMini(value.url)
					
				}
			});			
			
		});		
				
    }, 


    getDbNameEventId: function (sidebarEventW){
    	return sidebarEventW.dbName + sidebarEventW.id;
    },
  
    addEventToMap: function (sidebarEventW){  
    	
  
  		var lastMarker = this.LastMarker;
  		if (lastMarker != null){
			lastMarker.remove(this.MyMap);
			this.LastMarker = null;
		}
        
        var marker = this.getOrGenerateMarker(sidebarEventW, false);

		this.LastMarker = marker;

		marker.addTo(this.MyMap);
		this.MyMap.setView(marker.getLatLng());

    },
    
    addMapControlBox: function (tileLayerList, overlaysMap){
    	
    	L.control.layers(tileLayerList, overlaysMap, {"collapsed" : false}).addTo(this.MyMap);

    },

    addTileServers : function (tileServers) {
    	
    	this.tileLayerMap = []; 
    	for (let i = 0; i < tileServers.length; i++) {
    		
    		var tileLayer = L.tileLayer(tileServers[i].url, {
    			attribution: '<a href="https://www.openstreetmap.org/">OpenStreetMap</a>',
    			maxZoom: 19,
    		});
    		
    		this.tileLayerMap[tileServers[i].name] = tileLayer;
    	}
    	
    	this.tileLayerMap[tileServers[0].name].addTo(this.MyMap);
    	
    	
		
    	return this.tileLayerMap;
    },

    getOrGenerateMarker : function (sidebarEventW, selectedIconControl){
    	
    	var sidebarEvent = sidebarEventW;
		var iconSrc = contextPath + "image/markerImg?eventTypeId=" + sidebarEvent.eventTypeId + "&color="+encodeURIComponent(sidebarEvent.color) + "&selectedIconControl=" + selectedIconControl;
		
		var latitude = sidebarEvent.latitude;
		var longitude = sidebarEvent.longitude;
				
		latitude > 90 ? latitude = 90 : (-90 > latitude ? latitude = -90 : latitude);			
		longitude > 180 ? longitude = 180 : (-180 > longitude ? longitude = -180 : longitude);		
		
		marker = L.canvasMarker([latitude, longitude], {
			riseOnHover: true,
			id:this.getDbNameEventId(sidebarEventW),
			selected: selectedIconControl,
	        radius: 15,
	        img: {
	          url: iconSrc,
	          size: [39,39],
	        },
	        weight: 0,
			pane:"eventMarker"
			
	    })
	    .on('click', function (e){
		})

        return marker;
    },

	
}
