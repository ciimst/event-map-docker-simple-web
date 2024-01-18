MapHelper = function (OpenDetailModalCallbackFunc, eventGroupList) {

	this.MyMap;
	this.MiniMap;
	this.OpenDetailModalCallbackFunc = OpenDetailModalCallbackFunc;
	let southWest = L.latLng(-90, -180),
		northEast = L.latLng(90, 180);
	this.bounds = new L.LatLngBounds(southWest, northEast);

	
	this.eventGroupList = eventGroupList;
	this.eventLayerGroupList = [];

    this.mapAreaList = [];
    this.eventWrapperMap = {};
    this.lastChoosenId = 0;

	this.lastChoosenAlertId = 0;
	this.alertListMap = {};
	
    this.tileLayerMap = [];
    this.alertLayers;
    this.EventIcon = L.Icon.extend({
		options: {
			iconSize:     [39, 39], // size of the icon
			iconAnchor:   [19.5, 19.5], // point of the icon which will correspond to marker's location
			popupAnchor:  [0, -19.5], // point from which the popup should open relative to the iconAnchor
			id: 0
		}
	});
    this.EventExtraIcon = L.Icon.extend({
		options: {
			iconSize:     [20, 20], // size of the icon
			iconAnchor:   [10, 10], // point of the icon which will correspond to marker's location
			popupAnchor:  [0, -10] // point from which the popup should open relative to the iconAnchor
		}
	});
	
	if (this.init) {
		this.init.apply(this, arguments);
	}
	
	this.initializeEvents();			
};

MapHelper.prototype = {
		
    init: function () {
			
					
	    try{
	    	var cookieVal = JSON.parse($.cookie('coordinateInfo'));	      
	    	if(cookieVal.zoom != null && cookieVal.longitude != null && cookieVal.latitude != null){
		    	var coordinateInfo = {
		    	longitude : cookieVal.longitude,
		    	latitude : cookieVal.latitude,
		    	zoom : cookieVal.zoom
		    	}		    		    	
	    	}
	    }catch(e){	   	 
		    var coordinateInfo = {
			    	latitude : 28.594169,
			    	longitude :18.51882,
			    	zoom : 6
			}
			$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));
	    }	
	    
    	
    	this.MyMap = L.map('map', {
			center: [coordinateInfo.latitude, coordinateInfo.longitude],
			zoom: coordinateInfo.zoom,
			minZoom: 2,
			maxBounds: this.bounds,
			useCache: false,
			zoomControl: false
			
		});
    	
        // zoom control options
        var zoomOptions = {
        	zoomInTitle: lang.props["label.map.zoom.in"],
        	zoomOutTitle: lang.props["label.map.zoom.out"],
        };
        var zoom = L.control.zoom(zoomOptions);   // Creating zoom control
        zoom.addTo(this.MyMap);   // Adding zoom control to the map
    	

		var removeEventGroups = $.cookie("removeEventGroups");
		if(removeEventGroups != undefined && removeEventGroups != null){
			removeEventGroups = JSON.parse(removeEventGroups)
		}

		for (let eventGroup of this.eventGroupList) {
	        	var layerGroup = this.eventLayerGroupList[eventGroup.name];
	        	if(layerGroup == undefined){
	        		layerGroup = L.layerGroup();
	        		this.eventLayerGroupList[eventGroup.name] = layerGroup;
					this.eventLayerGroupList[eventGroup.name].options.eventGroupId = eventGroup.id
					this.eventLayerGroupList[eventGroup.name].options.eventGroupDbName = eventGroup.dbName
					
					if(removeEventGroups != undefined && removeEventGroups != null){
						
						var layerEventGroupKey = eventGroup.dbName + "_" + eventGroup.id;
						var count = 0;
						$.each(removeEventGroups, function(key, value){

							if(layerEventGroupKey ==  removeEventGroups[key].eventGroupKey){
								count++;
							}
						});
							
						if(count == 0){
							layerGroup.addTo(this.MyMap);
						}
						
					}else
					{
						layerGroup.addTo(this.MyMap);
					}
	        	}
		}	
		

    	// FeatureGroup is to store editable layers
    	 
        this.alertLayers = new L.FeatureGroup();
        this.MyMap.addLayer(this.alertLayers);
        let drawOptions = this.getDrawOptions(this.alertLayers);
        
        

        
        var drawControl = new L.Control.Draw(drawOptions);
        this.MyMap.addControl(drawControl);

    	var customOverlayMaps = {};
        customOverlayMaps["Alarm Bölgeleri"] = this.alertLayers;
    	
    	this.addMapControlBox(null, customOverlayMaps);
        
      
		
    	
    	this.AppMini = function(osmUrl){
    	var osmAttrib='Map data &copy; OpenStreetMap contributors';		
		
        var osm2 = new L.TileLayer(osmUrl, {minZoom: 2, maxZoom: 13, attribution: osmAttrib });
		var miniMap = new L.Control.MiniMap(osm2, { toggleDisplay: true }).addTo(this.MyMap);
		
		
		
    	}
		
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
		

        this.MyMap.on(L.Draw.Event.CREATED, function (e) {
        	var layer = e.layer;
        	self.alertLayers.addLayer(layer);
        	
			var myModal = $("#myModalAlertAdd");
			myModal.modal('show');
			
			Alert.OpenDetailAlertAddModal();
		
			
			$("#alertAdd").on("click", function(){
				var alertName = document.forms["alertAddForm"]["name"].value;
				
				$(".alertName").removeClass("has-error");
					$(".with-errors span").remove();
					
				if($.trim(alertName) == ''){
					$(".alertName").addClass("has-error");
					$(".with-errors").append('<span>' + lang.props["label.alert.name.not.null"] + '</span>');
					return;
				}
							
				Alert.Add(layer);
				myModal.modal('hide');
				
				
			});  	
			
			
        	
        });
        
        this.MyMap.on(L.Draw.Event.EDITED, function (e) {
        	
        	$.each(e.layers.getLayers(), function (k, layer){
        		Alert.Edit(layer);	
        	})
        });
        
        
        
        this.MyMap.on(L.Draw.Event.DELETED, function (e) {
        	        	

			const swalWithBootstrapButtons = Swal.mixin({
			  customClass: {
			    confirmButton: 'btn',
			    cancelButton: 'btn'
			  },
			  buttonsStyling: {
				confirmButton: 'margin:10px;'
			  }
			})
			
			swalWithBootstrapButtons.fire({
			  text: lang.props["label.map.confirm.area.delete"],
			  showCancelButton: true,
			  confirmButtonText: lang.props["label.delete"],
			  cancelButtonText: lang.props["label.close"],
			  reverseButtons: false,
			}).then((result) => {
			  if (result.isConfirmed) {
					$.each(e.layers.getLayers(), function (k, layer){
	            		Alert.Delete(layer);	
	            	})			
			  }else{
				
				window.location.href = "";
			  }
			})
        	
        });
    	
		
		this.DragendAndZoomend =  function(e){
			var latLng = e.target.getCenter();
    		var lng = latLng.lng;
		   	var lat = latLng.lat;
				    		    
		    var coordinateInfo = {
		    	longitude: lng,
		    	latitude: lat,
		    	zoom: e.target.getZoom()
		    	}
		    			    	
		 	$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));
		   
		}
		this.MyMap.on("dragend", this.DragendAndZoomend); 
		this.MyMap.on("zoomend", this.DragendAndZoomend); 
		
		
		this.MyMap.on({
			overlayadd: function(e) {
				
				var layerGroupKey = e.layer.options.eventGroupDbName + "_" + e.layer.options.eventGroupId;
				$("#sidebarContainer > #" + layerGroupKey).css("display", "block")
				Page.mapHelper.overlaysAddEventGroup(layerGroupKey)	
			},
			overlayremove: function(e) {
				
				var layerGroupKey = e.layer.options.eventGroupDbName + "_" + e.layer.options.eventGroupId;
				$("#sidebarContainer > #" + layerGroupKey).css("display", "none")	
				Page.mapHelper.overlaysRemoveEventGroup(layerGroupKey)	
			}
			
		});
				
    }, 


	overlaysRemoveEventGroup: function(layerGroupKey){
		
		var cookieRemoveEventGroups = $.cookie("removeEventGroups")
		if(cookieRemoveEventGroups != undefined && cookieRemoveEventGroups != null && cookieRemoveEventGroups.length != 0){
			
			cookieRemoveEventGroups = JSON.parse($.cookie("removeEventGroups"))
			var count = 0;
			
			$.each(cookieRemoveEventGroups, function(key, eventGroupKey){
				
				if(cookieRemoveEventGroups[key].eventGroupKey == layerGroupKey){
					count++;
				}

			});	
			
			if(count == 0){
				cookieRemoveEventGroups.push({eventGroupKey: layerGroupKey})
			}
			
		} else{
			cookieRemoveEventGroups=[];
			cookieRemoveEventGroups.push({eventGroupKey: layerGroupKey})
		}	
		
		$.cookie("removeEventGroups", JSON.stringify(cookieRemoveEventGroups), { expires: 7, path: '/' });
		
		Page.mapHelper.displayBlockEventCount();
		if(Page.notNullEventGroupCount != Page.eventGroupList.length &&  Page.eventCount < 10){

			Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		}
		
		setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
	},
	
	overlaysAddEventGroup: function(layerGroupKey){

		var cookieRemoveEventGroups = JSON.parse($.cookie("removeEventGroups"))
		var popList = JSON.parse($.cookie("removeEventGroups"))
		
		$.each(cookieRemoveEventGroups, function(key, value){
				
			if(value.eventGroupKey === layerGroupKey){
				
				popList.splice(key, 1); 
			}
		});
		
		$.cookie("removeEventGroups", JSON.stringify(popList), { expires: 7, path: '/' });

		Page.mapHelper.displayBlockEventCount();
		if(Page.notNullEventGroupCount != Page.eventGroupList.length &&  Page.eventCount < 10){

			Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		}
		
		setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
	},
	
	displayBlockEventCount: function() {
		var count = 0;
		var sidebarItemList = $("#sidebarContainer .sidebarItem")
		$.each(sidebarItemList, function(key, value){
					
			if($(this).css("display") != "none"){
				count++;
			}
		})
		
		Page.eventCount = count;
		Page.notNullEventGroupCount = 0;
		var cookieRemoveEventGroups = $.cookie("removeEventGroups")
		
		if(cookieRemoveEventGroups != undefined && cookieRemoveEventGroups != null && cookieRemoveEventGroups.length != 0){
			
			cookieRemoveEventGroups = JSON.parse($.cookie("removeEventGroups"))
			
			$.each(Page.eventGroupList, function(k, value){
				$.each(cookieRemoveEventGroups, function(key, eventGroupKey){
					
					var groupKey = value.dbName + "_" + value.id;
					if(groupKey == cookieRemoveEventGroups[key].eventGroupKey){
						Page.notNullEventGroupCount++;
					}
				});	
			})
		}
	},
	
    addSidebarEventWrapper : function (sidebarEventW){
    	this.eventWrapperMap[this.getDbNameEventId(sidebarEventW)] = sidebarEventW;
    },
    getDbNameEventId: function (sidebarEventW){
    	return sidebarEventW.dbName + sidebarEventW.event.id;
    },
    addAlertArea: function (alertItem){
    	
    	//var data = JSON.parse(alertItem.coordinateInfo);
//    	var polygon = L.polygon(data).setStyle({fillColor: mapArea.color , color: mapArea.color, fillOpacity: '0.25', opacity: '0.80', weight: '2.00'});
    	var polygon = L.polygon(alertItem.coordinateInfo).setStyle({fillColor: alertItem.color != null ? alertItem.color : "rgb(51, 136, 255)"  , color: alertItem.color != null ? alertItem.color : "rgb(51, 136, 255)", fillOpacity: '0.25', opacity: '0.80', weight: '2.00'}).addTo(this.MyMap);
    	polygon.id = alertItem.id;
		
		polygon.on('click', function (e){
			mapHelperThis.alertOnClick(polygon.id);
		});

    	this.alertLayers.addLayer(polygon);
    	
    	
//    	var customOverlayMaps = {};
//        customOverlayMaps["deneme"] = this.alertLayers;
//    	
//    	this.addMapControlBox(null, customOverlayMaps);
    },    
    addEventToMap: function (sidebarEventW){  
    	
    	var definedSidebarEventW = this.eventWrapperMap[this.getDbNameEventId(sidebarEventW)];
    	if(definedSidebarEventW != null){
    		sidebarEventW = definedSidebarEventW;
    	}
    	
    	var sidebarEvent = sidebarEventW.event;
	
        var base64Icon = this.getOrGenerateBase64Icon(sidebarEventW);
        sidebarEventW.base64Icon = base64Icon;
        
        var marker = this.getOrGenerateMarker(sidebarEventW, base64Icon);
        this.addMarkerToLayerGroup(marker, sidebarEvent.groupName);
        sidebarEventW.marker = marker;
    },
    addEventToSidebar: function (sidebarEventW, isPrepend){
    	
    	var definedSidebarEventW = this.eventWrapperMap[this.getDbNameEventId(sidebarEventW)];
    	if(definedSidebarEventW != null){
    		sidebarEventW = definedSidebarEventW;
    	}
    	
    	var sidebarEvent = sidebarEventW.event;
    	
    	sidebarEvent.sidebarId = this.getSidebarId(this.getDbNameEventId(sidebarEventW));	
    	sidebarEvent.sidebarSrc = this.getOrGenerateBase64Icon(sidebarEventW);
    	sidebarEvent.location = sidebarEvent.city + " / " + sidebarEvent.country;
    	
    	sidebarEvent.location = "";
    	if(sidebarEvent.city != null){
    		sidebarEvent.location = sidebarEvent.city;
    	}
    	
    	if(sidebarEvent.country != null){
    		if(sidebarEvent.location != ""){
    			sidebarEvent.location += " / "
    		}
    		sidebarEvent.location += sidebarEvent.country;
    	}
    	
//    	$.get('/template/sidebar-item.html?v=' + Main.version, function(template) {
//    		var html = $.tmpl(template, sidebarEventW);
//    		if (isPrepend == true){
//    			$("#sidebarContainer").prepend(html);
//    		}else{
//    			$("#sidebarContainer").append(html);
//    		}
//    	});

		sidebarEventW.eventGroupKey = sidebarEventW.dbName + "_" + sidebarEventW.event.groupId
    	
    	$.ajax({
             type: "GET",
             url: '/template/sidebar-item.html?v=' + Main.version,
		     success : function(template)
		     {
		    	 var html = $.tmpl(template, sidebarEventW);
		    		if (isPrepend == true){
		    			$("#sidebarContainer").prepend(html);
		    		}else{
		    			$("#sidebarContainer").append(html);
		    		}
		     },
		     cache: false,
		     async: false
        })

		var sidebarItem = $("#sidebarContainer").children("#" + sidebarEventW.eventGroupKey);
		var cookieEventGroup = $.cookie("removeEventGroups")
		if(cookieEventGroup != undefined && cookieEventGroup != null){
			cookieEventGroup = JSON.parse(cookieEventGroup);
			
			$.each(cookieEventGroup, function(key, value){
				
				if(sidebarEventW.eventGroupKey === cookieEventGroup[key].eventGroupKey){
					$(sidebarItem).css("display", "none")
				}
			});
		}	
    },
    addExtraMarkers: function (){
    	
//    	L.marker([28.83921, 22.1162], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Abu-Attifel Oil Field").addTo(this.MyMap);
//    	L.marker([26.43123, 11.85322], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjMDAwMEZGIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiMwMDAwRkYiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjMDAwMEZGIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("El Sharara oil field").addTo(this.MyMap);
//    	L.marker([26.5767, 12.21805], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Sharara Oil Field").addTo(this.MyMap);
//    	L.marker([26.03905, 11.97699], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Al Fil oil field").addTo(this.MyMap);
    },
    addGeoLayer: function (geoList){
    	
    	if(geoList.length == 0){
    		return;
    	}
    	
    	let MyMap = this.MyMap;
    	var customOverlayMaps = {};
        $.each( geoList, function( key, geolayer ) {
        	var myLayer = L.geoJSON();
        	var myLines = jQuery.parseJSON(geolayer.data);
        	
        	myLayer.addData(myLines);
        	
        	var layerGroup = L.layerGroup([myLayer]);
        	layerGroup.addTo(MyMap);
        	
        	customOverlayMaps[geolayer.name] = layerGroup;
        });

    	this.addMapControlBox(null, customOverlayMaps);
    },
    addMapArea: function (mapArea){
    	
    	var data = JSON.parse(mapArea.coordinateInfo);
    	var polygon = L.polygon(data).setStyle({fillColor: mapArea.color , color: mapArea.color, fillOpacity: '0.25', opacity: '0.80', weight: '2.00'}).addTo(this.MyMap);
    	this.mapAreaList[mapArea.id] = polygon;
    },

    addMapControlBox: function (tileLayerList, overlaysMap){
    	
    	L.control.layers(tileLayerList, overlaysMap, {"collapsed" : false}).addTo(this.MyMap);

    },
    addMarkerToLayerGroup: function (marker, groupName){
    	
    	var layerGroup = this.eventLayerGroupList[groupName];
    	if(layerGroup == undefined){
    		layerGroup = L.layerGroup();
    		this.eventLayerGroupList[groupName] = layerGroup;
    		layerGroup.addTo(this.MyMap);
    	}
    	
    	marker.addTo(layerGroup);
    },
    addEventGroups: function (eventGroupList){
    	
        $.each( eventGroupList, function( key, eventGroup ) {

        	var layerGroup = this.eventLayerGroupList[eventGroup.name];
        	if(layerGroup == undefined){
        		layerGroup = L.layerGroup();
        		this.eventLayerGroupList[groupName] = layerGroup;
        		layerGroup.addTo(this.MyMap);
        	}
        });
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
    	this.AppMini(tileServers[0].url);
    	
    	
    	
		
    	return this.tileLayerMap;
    },
    eventOnClick: function (id) {
    	
    	if(this.lastChoosenId == id){
    		this.openDetailModal(this.lastChoosenId);
    		return;
    	}
    	
    	this.showInMap(id);    	
    },    
	getDrawOptions (featureGroup) {
		//let self = this;
    	
        L.drawLocal = {
        		draw: {
        			toolbar: {
        				// #TODO: this should be reorganized where actions are nested in actions
        				// ex: actions.undo  or actions.cancel
        				actions: {
        					text: lang.props["label.map.cancel"],
        				},
        				finish: {
        					text: lang.props["label.map.finish"]
        				},
        				undo: {        					
        					text: lang.props["label.map.delete.last.point"]
        				},
        				buttons: {
        					polygon: lang.props["label.map.draw.polygon"],
        					circle: lang.props["label.map.draw.polygon"],
        				}
        			},
        			handlers: {
          				polygon: {
        					tooltip: {
        						start: lang.props["label.map.click.to.start"],
        						cont: lang.props["label.map.click.to.continue"],
        						end: lang.props["label.map.click.to.finish"],
        					}
        				},
        				circle: {
        					tooltip: {
        						start: 'Click and drag to draw circle.'
        					},
        					radius: 'Radius'
        				},
        				circlemarker: {
        					tooltip: {
        						start: 'Click map to place circle marker.'
        					}
        				},
        				marker: {
        					tooltip: {
        						start: 'Click map to place marker.'
        					}
        				},
  
        				polyline: {
        					error: '<strong>Error:</strong> shape edges cannot cross!',
        					tooltip: {
        						start: 'Click to start drawing line.',
        						cont: 'Click to continue drawing line.',
        						end: 'Click last point to finish line.'
        					}
        				},
        				rectangle: {
        					tooltip: {
        						start: 'Click and drag to draw rectangle.'
        					}
        				},
        				simpleshape: {
        					tooltip: {
        						end: 'Release mouse to finish drawing.'
        					}
        				}
        			}
        		},
        		edit: {
        			toolbar: {
        				actions: {
        					save: {
        						text: lang.props["label.map.save"],
        					},
        					cancel: {        						
        						text: lang.props["label.map.cancel"],
        					},
        					clearAll: {
        					
        						text: lang.props["label.map.clear.all"],
        					}
        				},
        				buttons: {
        					edit: lang.props["label.map.edit.layer"],
        					editDisabled: lang.props["label.map.no.layers.to.edit"],
        					remove: lang.props["label.map.delete.layer"],
        					removeDisabled: lang.props["label.map.no.layers.to.delete"],
        				}
        			},
        			handlers: {
        				edit: {
        					tooltip: {
        						text: lang.props["label.map.click.to.edit"],
        						subtext: lang.props["label.map.click.to.cancel"],  
        					}
        				},
        				remove: {
        					tooltip: {
        						text: lang.props["label.map.click.to.delete"],
        					}
        				}
        			}
        		}
        	};
        
		return {
			position: 'topleft',
			draw: {
				
				polygon: true,
				// disable toolbar item by setting it to false
				polyline: false,
				circle: true, // Turns off this drawing tool
				rectangle: false,
				circlemarker:false,
				marker: false,
				
			},
			edit: {
				featureGroup: featureGroup, //REQUIRED!!
				remove: true
			}
		};
		
		
	},
    getOrGenerateBase64Icon : function (sidebarEventW){
    	
        var eventWrapper = sidebarEventW;
        var sidebarEvent = sidebarEventW.event;
        if(eventWrapper != null && eventWrapper.base64Icon != null){
        	
        	return eventWrapper.base64Icon;
        }
    	
        var base64Icon = SvgIcon.GenerateBase64Icon(sidebarEvent.eventTypeImage, sidebarEvent.color);
        return base64Icon;
    },
    getOrGenerateMarker : function (sidebarEventW, base64Icon){
    	
    	var sidebarEvent = sidebarEventW.event;
    	
        var marker  = this.getMarker(this.getDbNameEventId(sidebarEventW));
        if(marker != null){
        	return marker;
        }
    	
        
        mapHelperThis = this;
        marker = L.marker([sidebarEvent.latitude, sidebarEvent.longitude], {icon:  new this.EventIcon({iconUrl: base64Icon}), id:this.getDbNameEventId(sidebarEventW) , riseOnHover: true})
			.bindPopup(sidebarEvent.title)
			.on('click', function (e){
				mapHelperThis.eventOnClick(e.target.options.id);
			});
			
        return marker;
    },
    getMarker : function (id){
    	
        var eventWrapper = this.eventWrapperMap[id];
        if(eventWrapper != null && eventWrapper.marker != null){
        	
        	return eventWrapper.marker;
        }
    	
        return null;
    },
    getSidebarId : function (id){
    	return "sidebarId" + id;
    },
    checkAlert : function (sidebarEventW){
    	
		let self = this;
    	/*   	
    	var event = sidebarEventW.event; 		

	    let alertAreas = Page.mapHelper.alertLayers.getLayers();
	    $.each(alertAreas, function (k, poly){
	    	
	    	var contains = poly.contains({lat: event.latitude, lng: event.latitude});
	    	if(contains == true){

	    		let title = event.title == null ? "" : event.title;
	    		let spot = event.spot == null ? "" : event.spot;

	    		ToastrHelper.Event(title, spot, self.getDbNameEventId(sidebarEventW));
	    	}
	    })*/
		
		var event = sidebarEventW.event;
		let title = event.title == null ? "" : event.title;
		let spot = event.spot == null ? "" : event.spot;
		var alertEventList = sidebarEventW.alertList;

		if(alertEventList != null){
					
			ToastrHelper.Event(title, spot, self.getDbNameEventId(sidebarEventW));		
	    } 
    },
    openDetailModal: function (id){
    	
    	this.OpenDetailModalCallbackFunc(this.eventWrapperMap[id]);
    },
    selectSidebarItem: function (id){

		var sidebarId = "#" + this.getSidebarId(id);
		$(".selected").removeClass("selected");
		$(sidebarId).addClass("selected");
		$("#sidebarContainer").scrollTo($(sidebarId), 500, {offsettop:100});	
		
    },    
    selectIcon: function (id){
    	
    	var newMarker = this.eventWrapperMap[id].marker
    	
    	var oldMarker = this.getMarker(this.lastChoosenId);
    	if (oldMarker != null){
    		$(oldMarker._icon).removeClass('i-circled i-custom-map');
    		oldMarker._zIndex -= 1000;
    	}

    	this.MyMap.setView(newMarker.getLatLng());
    	$(newMarker._icon).addClass('i-circled i-custom-map');
    	newMarker._zIndex += 1000;
    	
    },  
    setLastInfoToCookie: function (id){
    	
    	let eventWrapper = this.eventWrapperMap[id];
		var zoomLevel = this.MyMap.getZoom();	
		
		var coordinateInfo = {
			longitude: eventWrapper.event.longitude,
			latitude: eventWrapper.event.latitude,
			zoom: zoomLevel
		}				
		$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));	
    	
    },
    showInMap : function (id) {
    	
    	let markerId = id;
    	
    	this.selectIcon(markerId);
    	this.selectSidebarItem(markerId);
    	this.setLastInfoToCookie(id);

    	this.lastChoosenId = markerId;
    },

	showInAlertMap: function(sidebarAlarmId){
		
		$.each(Page.sidebarAlertList, function(key, alertItem){
			if(sidebarAlarmId == alertItem.id){
												
				Page.mapHelper.addAlertToMap(alertItem)
				Page.selectSidebarItem(sidebarAlarmId)								
				Page.mapHelper.lastChoosenAlertId = sidebarAlarmId;
				
			}
			
		});
		
	},
	addAlertToMap: function (alertItem){
    	
    	var polygon = L.polygon(alertItem.coordinateInfo);
    	polygon.id = alertItem.id;
    	
		if(this.lastChoosenAlertId != 0){
			polygon._zIndex -= 1000;
		}
		
    	polygon._zIndex += 1000;

		this.MyMap.setView(polygon._latlngs[0][0]);
			
		this.MyMap.fitBounds(polygon._bounds);
		
		var lat = polygon._latlngs[0][0].lat;
		var lng = polygon._latlngs[0][0].lng;
						
		Page.mapHelper.setLastAlertInfoToCookie(alertItem.id, lat, lng )

    },

	alertOnClick: function (id) {

    	if(Page.mapHelper.lastChoosenAlertId == id){
			Page.AlarmOnClick(id)			
    		return;
    	}
    	
		//this.showInAlertMap(id); 
		this.alarmClickMap(id);   	
    }, 


	alarmClickMap: function(sidebarAlarmId){
		
		$.each(Page.sidebarAlertList, function(key, alertItem){
			if(sidebarAlarmId == alertItem.id){
												
			//	Page.mapHelper.addAlertToMap(alertItem)
				Page.selectSidebarItem(sidebarAlarmId)								
				Page.mapHelper.lastChoosenAlertId = sidebarAlarmId;
				
			}
			
		});
		
	},
	

	setLastAlertInfoToCookie: function (id, lat, lng){
    	
    	let alertItem = this.alertListMap[id];	
		var zoomLevel = this.MyMap.getZoom();	
		
		var coordinateInfo = {
			longitude: lng,
			latitude: lat,
			zoom: zoomLevel
		}				
		$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));
    	
		
    },
	
}
