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
    this.eventWrapperMap = {}; // javascriptte bulunan bütün olaylar
	this.eventWrapperIndexList = []; // haritada görünen bütün olaylar. event gruplar filtrelenmiş
    this.eventWrapperMapDeleted = {}; // heatmap
    this.lastChoosenId = 0; // modal açılması için son seçilen item
  	this.lastHoverChoosenId = 0; // markerlar üzerinde gezinirken hover olduğunda en öne gelen marker üzerinde durdurken pırpır yapmaması için

	this.alertListMap = {};
	

	this.selectedClusterMarker = [];
	
    this.tileLayerMap = [];
    this.alertLayers;
    this.heatmapLayer;
	this.urlPathName = window.location.pathname;
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
			
					
	    
		var timeDimensionDate = ""
		var timeDimensionPeriod = ""
		
		if($.cookie("timeDimensionDate") != undefined && TimeDimension.timeDimensionMode && $.cookie("periodType") != undefined && $.cookie("periodTime") != undefined){
			
			TimeDimension.TimeDimensionCookieControl();
			
			const dates = $.cookie("timeDimensionDate").split("/")
			var startDateStr = dates[0]
			var endDateStr = dates[1]				
			
			startDateStr = moment(startDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TIME_DIMENSION)
			endDateStr = moment(endDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TIME_DIMENSION)
			timeDimensionDate = startDateStr+"/"+endDateStr;
			
			var periodType = $.cookie("periodType")
			var periodTime = $.cookie("periodTime")
			
			switch(periodType){
				
				case "minute":
					timeDimensionPeriod = "PT" + periodTime + "M"
				break;
				
				case "hour":
					timeDimensionPeriod = "PT" + periodTime + "H"
				break;
			}
			
		}
	
		var coordinateInfo = Common.MapSetDefaultInfo();
    	this.MyMap = L.map('map', {
			center: [coordinateInfo.latitude, coordinateInfo.longitude],
			zoom: coordinateInfo.zoom,
			minZoom: 2,
			maxZoom: 21,
			maxBounds: this.bounds,
			useCache: false,
			zoomControl: false,
			preferCanvas: true,
			
		    timeDimensionControl: TimeDimension.timeDimensionMode == true ? true : false,
		    timeDimensionControlOptions: {
		        timeSliderDragUpdate: true,
		        loopButton: false,
		        autoPlay: false,
				playReverseButton:false,
				
				timeZones: ["local"], //utc	//local olunca +3 oluyor.
		        playerOptions: {
		            transitionTime: 1000, //geçiş süresi
		            loop: false,
					startOver: true
		        }
		    },
		    timeDimensionOptions:{	
				timeInterval: timeDimensionDate,
		        //timeInterval: "2021-03-28/2021-04-18",
		        period: timeDimensionPeriod,//"PT1H",
				//validTimeRange: "06:00/09:00", saat filtreleme
		    },
		    timeDimension: true,	
			
		});				
		
		
		this.MyMap.createPane("eventMarker");
		this.MyMap.getPane("eventMarker").style.zIndex = 999;
		
	
        var searchbox = L.control.searchbox({
            position: 'topleft',
            expand: 'right',
            iconPath: '../image/search_icon.png'
        });
		
		this.MyMap.addControl(searchbox);
		var markersLayer = new L.LayerGroup();	//layer contain searched elements
		this.MyMap.addLayer(markersLayer);
		// Close and clear searchbox 600ms after pressing ENTER in the search box
        searchbox.onInput("keyup", function (e) {
            if (e.keyCode == 13) {
			
                SearchHelper.Search(searchbox, markersLayer);
            }
        });
        // Close and clear searchbox 600ms after clicking the search button
        searchbox.onButton("click", function () {
			
			SearchHelper.Search(searchbox, markersLayer);
			
            setTimeout(function () {
                searchbox.hide();
                searchbox.clear();
            }, 300);
        });
        
        
		
		//Map üzerinde buton olusturmak icin easyButton, introjs
        var InfoButton = L.easyButton('fa-info-circle', Page.IntroToMap, 'Info');
		InfoButton.addTo(this.MyMap); //easyButton to map
			
//		L.Control.geocoder({
//      		query: "",
//  			placeholder: "Adres girin"
////	  		defaultMarkGeocode: false
//		}).addTo(this.MyMap);
		
							
        // zoom control options
        var zoomOptions = {
        	zoomInTitle: lang.props["label.map.zoom.in"],
        	zoomOutTitle: lang.props["label.map.zoom.out"],
        };
        var zoom = L.control.zoom(zoomOptions);   // Creating zoom control
        zoom.addTo(this.MyMap);   // Adding zoom control to the map
    	

//		for (let eventGroup of this.eventGroupList) {
//	        	var layerGroup = this.eventLayerGroupList[eventGroup.name];
//	        	if(layerGroup == undefined){
//	        		layerGroup = this.createClusterMarkerGroup(eventGroup.color);
//	        		this.eventLayerGroupList[eventGroup.name] = layerGroup;
//					this.eventLayerGroupList[eventGroup.name].options.eventGroupId = eventGroup.id
//					this.eventLayerGroupList[eventGroup.name].options.eventGroupDbName = eventGroup.dbName
//					
//					var layerEventGroupKey = eventGroup.dbName + "_" + eventGroup.id;
//					
//					if(Page.removeEventGroupList.length > 0){
//						
//						var found = Page.removeEventGroupList.find(element => element.eventGroupKey == layerEventGroupKey);
//						if(found == undefined){
//							
//							layerGroup.addTo(this.MyMap);
//						}
//						
//					}else{
//						
//						layerGroup.addTo(this.MyMap);
//					}
//	        	}
//		}	
		

    	// FeatureGroup is to store editable layers
    	 
        this.alertLayers = new L.FeatureGroup();
        this.MyMap.addLayer(this.alertLayers);
        let drawOptions = this.getDrawOptions(this.alertLayers);
        
        

        
        var drawControl = new L.Control.Draw(drawOptions);
        this.MyMap.addControl(drawControl);

    	var customOverlayMaps = {};
        customOverlayMaps["Alarm Bölgeleri"] = this.alertLayers;
		this.alertLayers.options.id = "alert";
    	
    	this.addMapControlBox(null, customOverlayMaps);
        
      
		
    	
    	this.AppMini = function(osmUrl){
    	var osmAttrib='Map data &copy; OpenStreetMap contributors';		
		
        var osm2 = new L.TileLayer(osmUrl, {minZoom: 2, maxZoom: 13, attribution: osmAttrib });
		var miniMap = new L.Control.MiniMap(osm2, { toggleDisplay: true }).addTo(this.MyMap);
		
		if(TimeDimension.timeDimensionMode){
			miniMap._setDisplay(true)
		}
		
		
		
		
				
		this.heatmapLayer = L.heatLayer([]);
		
				
		this.MyMap.addLayer(this.heatmapLayer);
		 
		 

		
		
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
				
				if(e.layer.options.id == "alert"){
					Page.mapHelper.alertLayers.bringToBack();
				}
				
				
				if(e.layer.options.eventGroupId != undefined){				
					
					var layerGroupKey = e.layer.options.eventGroupDbName + "_" + e.layer.options.eventGroupId;
					Common.SetRemovedEventGroupsCookieValue("removedEventGroupSelected", layerGroupKey);
								
					if(TimeDimension.timeDimensionMode){
						
						TimeDimension.timeDimensionOverlaysAddEventGroup(layerGroupKey);
						
					}else if(HeatMap.isHeatmap){
						
						HeatMap.heatmapAddEventGroup(layerGroupKey);
						
					}else{
						
						if(Page.liveScrollDestroyControl){						
							Page.InitScroll();
						}
					
						Page.mapHelper.overlaysAddEventGroup(layerGroupKey)
					}

				
				}
				
			},
			overlayremove: function(e) {
				
				if(e.layer.options.eventGroupId != undefined){
					
					var layerGroupKey = e.layer.options.eventGroupDbName + "_" + e.layer.options.eventGroupId;					
					Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
					
					if(TimeDimension.timeDimensionMode == true){
										
					//	$("#sidebarContainer").children(".sidebarItem").remove();						
						TimeDimension.timeDimensionOverlaysRemoveEventGroup(layerGroupKey)
						
					}else if(HeatMap.isHeatmap){
						
						HeatMap.heatmapRemoveEventGroup(layerGroupKey);
						
					}else{
						Page.mapHelper.overlaysRemoveEventGroup(layerGroupKey)
					}
					
					
					
				}
				
				
			}
			
		});
				
    }, 


	
	
	eventGroupSelectChange: function() {
		
				
		Page.mapHelper.eventWrapperIndexList = [];
	
		$.each(Page.mapHelper.eventWrapperMap, function(key, sidebarEventWrapper){
						
			const found = Page.removeEventGroupList.find(element => element.eventGroupKey == sidebarEventWrapper.eventGroupKey);
			if(found == undefined){	
				
				if(sidebarEventWrapper.refreshWithNewEvent == "true"){//Refresh ile yeni olay geldiğinde en üste eklemek için yapılıyor.
					
					Page.mapHelper.eventWrapperIndexList.unshift(key)					
					delete sidebarEventWrapper.refreshWithNewEvent;
				}			
				Page.mapHelper.eventWrapperIndexList.push(key)
				sidebarEventWrapper.display = "block";
				
			}else{
				sidebarEventWrapper.display = "none";
			}
		});		
		
		$("#sidebarContainer > .sidebarItem").remove();
		
		
		Page.sidebarPrevIndex = 0;
		Page.sidebarLastIndex = Math.min(10, Page.mapHelper.eventWrapperIndexList.length);
		var eventIndexList = Page.mapHelper.eventWrapperIndexList.slice(Page.sidebarPrevIndex, Page.sidebarLastIndex);

		var eventList = [];
		$.each(eventIndexList, function(key, id){
			var event = Page.mapHelper.eventWrapperMap[id];
			eventList.push(event);
		});
		
		eventList = eventList.reverse();
		
		Page.addEventToSidebar(eventList, true)
		
		Page.liveDownNormalScroll = true;
		if(Page.mapHelper.eventWrapperIndexList[Page.mapHelper.eventWrapperIndexList.length -1] != Page.mapHelper.eventWrapperIndexList[Page.sidebarLastIndex-1]){
			Page.liveDownNormalScroll = false;//Yeni olayları çekip çekmeyeceği durumuna bakılıyor.
		}
		
		Page.thirtyEventChecks = false;//ilk on tanesi yükleneceği için yeniden false yapmak gerekir.		
		
		$("#countLoadedEvents").text(Page.mapHelper.eventWrapperIndexList.length);
	},

	overlaysRemoveEventGroup: function(layerGroupKey){
				
		Page.mapHelper.eventGroupSelectChange();
		
		Page.mapHelper.displayBlockEventCount();
		
		//Sidebarda 10 taneden az olay varsa ve eventgroupların hepsi kaldırılmadıysa buraya girer.	
		if(Page.notNullEventGroupCount != Page.eventGroupList.length &&  Page.eventCount < 10){

			Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		}
		
		setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
	},
	
	overlaysAddEventGroup: function(layerGroupKey){

		Page.mapHelper.eventGroupSelectChange();
		
		Page.mapHelper.displayBlockEventCount();
		
		
		if(Page.notNullEventGroupCount != Page.eventGroupList.length &&  Page.eventCount < 10){

			Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		}
		
		
		setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
	},
	
	displayBlockEventCount: function() {
		
		Page.eventCount = Page.sidebarLastIndex;
		
		Page.notNullEventGroupCount = Page.removeEventGroupList.length;		
	},
	
    addSidebarEventWrapper : function (sidebarEventW){
    	this.eventWrapperMap[this.getDbNameEventId(sidebarEventW)] = sidebarEventW;
    },
    getDbNameEventId: function (sidebarEventW){
    	return sidebarEventW.dbName + sidebarEventW.event.id;
    },
    addAlertArea: function (alertItem){
    	
		if(alertItem.coordinateInfo == null){
			return;
		}
    	//var data = JSON.parse(alertItem.coordinateInfo);
//    	var polygon = L.polygon(data).setStyle({fillColor: mapArea.color , color: mapArea.color, fillOpacity: '0.25', opacity: '0.80', weight: '2.00'});
    	var polygon = L.polygon(alertItem.coordinateInfo).setStyle({fillColor: alertItem.color != null ? alertItem.color : "rgb(51, 136, 255)"  , color: alertItem.color != null ? alertItem.color : "rgb(51, 136, 255)", 
		fillOpacity: '0.25', opacity: '0.80', weight: '2.00'}).addTo(Page.mapHelper.MyMap);
    	polygon.id = alertItem.id;
		
		polygon.on('click', function (e){
			Page.mapHelper.alertOnClick(polygon.id);
		});

    	Page.mapHelper.alertLayers.addLayer(polygon);
		Page.mapHelper.alertLayers.bringToBack();
    	
    	
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
	
       var base64Icon = null;//  this.getOrGenerateBase64Icon(sidebarEventW);
        sidebarEventW.base64Icon = base64Icon;
        
        var marker = this.getOrGenerateMarker(sidebarEventW, false);

		if(!TimeDimension.timeDimensionMode){//İlk seferde marker oluşturuluyor fakat haritaya eklenmiyor.Sonrasında siliniyor tekrar çünkü
			this.addMarkerToLayerGroup(marker, sidebarEvent.groupName, sidebarEvent.color);
		}
       	
        sidebarEventW.marker = marker;
    },
    
    
    addEventToSidebarHTML: function(sidebarEventW){
		
		var definedSidebarEventW = Page.mapHelper.eventWrapperMap[Page.mapHelper.getDbNameEventId(sidebarEventW)];
    	if(definedSidebarEventW != null){
    		sidebarEventW = definedSidebarEventW;
    	}
    	
    	var sidebarEvent = sidebarEventW.event;
    	
    	sidebarEvent.sidebarId = Page.mapHelper.getSidebarId(Page.mapHelper.getDbNameEventId(sidebarEventW));	
    	//sidebarEvent.sidebarSrc = Page.mapHelper.getOrGenerateBase64Icon(sidebarEventW);
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

		sidebarEventW.eventGroupKey = sidebarEventW.dbName + "_" + sidebarEventW.event.groupId
		
		var replaceString = "{providerUserId}";
		var regExp = new RegExp(replaceString, 'g');
		
		if(sidebarEventW.event.reservedLink != null){
			
			sidebarEventW.event.reservedLink = sidebarEventW.event.reservedLink.replace(regExp, providerUserId);	
		}
    	
		
		var sidebarSrc = contextPath + "image/markerImg?eventTypeId=" + sidebarEventW.event.eventTypeId + "&color="+encodeURIComponent(sidebarEventW.event.color) + "&selectedIconControl=" + false
		sidebarEvent.sidebarSrc = sidebarSrc;
		
		var sidebarHtmlItem =//"<div class='col-md-12 mb-2 mt-2 sidebarItem news-card' >"+ sidebarEventW.eventIdDbName +"</div>"
			"<div class='col-md-12 mb-2 mt-2 sidebarItem news-card' id='"+sidebarEventW.eventGroupKey+"' data-event-id='"+sidebarEventW.eventIdDbName+"'>"
			    +"<div class='card course-card hover-effect noborder' onclick=Page.ChooseEvent('"+sidebarEventW.dbName+sidebarEventW.event.id+"') id='"+sidebarEventW.event.sidebarId+"' data-id='"+sidebarEventW.event.id+"'  data-color='"+sidebarEventW.event.color+"'  style='box-shadow: 0 0 20px rgba(0,0,0,.2); --bcolor: "+sidebarEventW.event.color+"'>"
					  +"<div class='card-body py-2'>"
			            //src='/event/test/image="+sidebarEventW.event.sidebarId+"/color="+sidebarEventW.event.color+"'
						//src='/event/test/image="+sidebarEventW.event.eventTypeImage+"/color="+sidebarEventW.event.color+"'

			            +"<img class='i-circled i-custom' src='"+sidebarSrc+"' />"
						
						if($(window).width() > 830 && $(window).width() < 1330 ){
							sidebarHtmlItem +="<p class='t500 mb-3 mt-2'><a href='#' class='d-block time-ago' >"+CustomFormatter.GetDatePrettyFormatted(sidebarEventW.event.eventDate)+"</a></p>"
						}else{
							sidebarHtmlItem +="<p class='t500 mb-3 mt-2'><a href='#' class='d-block time-ago' >"+CustomFormatter.GetDatePrettyFormatted(sidebarEventW.event.eventDate)+ " - " +CustomFormatter.GetDateFormatted(sidebarEventW.event.eventDate)+"</a></p>"
						}
			           
						sidebarHtmlItem += "<div class='news-top-right'>" ;
	
							if(sidebarEventW.alertList){
								
								$.each(sidebarEventW.alertList, function(key,value){
									
									if(!value.readState && key == 0){
										
										sidebarHtmlItem += "<i class='fa fa-envelope-open fa-1x readStateIcon'  id='readStateIcon' title='"+lang.get('label.alert.event.read')+"' style='visibility:hidden;' aria-hidden='true'></i>"
			                            sidebarHtmlItem += "<i class='fa fa-envelope fa-1x readStateIconClose alertEventIcon' style='color:red;' title='"+lang.get('label.alert.event.unread')+"'  aria-hidden='true'></i>"
									}
									
									if(value.readState && key == 0){
										
										sidebarHtmlItem += "<i class='fa fa-envelope-open fa-1x alertEventIcon'  title='"+lang.get('label.alert.event.read')+"'  style='color:green;' aria-hidden='true'></i>"
									}
								})	
							}
							
							
							if(sidebarEventW.alertList){
								sidebarHtmlItem +="<i class='fa fa-bell fa-1x alarmIcon' title='"+lang.get('label.alarm')+"'  aria-hidden='true'></i>"
							}
			                
							var reservedLinkHref = "";
							
							if(sidebarEventW.event.reservedLink){
								reservedLinkHref = "href="+sidebarEventW.event.reservedLink
							}
							sidebarHtmlItem += "<a "+reservedLinkHref+" class='source' target='_blank'><i class='fas fa-external-link-alt'></i> "+lang.props["label.news.source"]+"</a>"  
							                 
			            sidebarHtmlItem +="</div>"
			            if(sidebarEventW.event.title!=null){
			           	 	sidebarHtmlItem +="<h4 class='card-title t500 mb-2 news-title'><a href='#' class='d-block' >"
			            	sidebarHtmlItem +=sidebarEventW.event.title
			            	sidebarHtmlItem +="</a></h4>"
			            }
			            if(sidebarEventW.event.spot!=null){
			            	sidebarHtmlItem +="<h4 class='card-text text-black-50 mb-1 news-title w-100 t500 news-content'>"
			            	sidebarHtmlItem +=sidebarEventW.event.spot
			            	sidebarHtmlItem +="</h4>"
						}
			       sidebarHtmlItem +="</div>"

					if(sidebarEventW.mediaList){
						
						sidebarHtmlItem +="<div class='fslider' data-arrows='false'>"
			            sidebarHtmlItem +="<div class='flexslider'>"
			                sidebarHtmlItem +="<div class='slider-wrap'>"
								$.each(sidebarEventW.mediaList, function(key, media){
									
									sidebarHtmlItem += "<div class='slide'>"
				                        sidebarHtmlItem +="<a href='#'>"
											if(JqueryTmplHelper.startsWithHttp(media.coverImagePath)){
												 sidebarHtmlItem += "img  src='"+media.path+"' alt=''>"
												if(media.isVideo){
													 sidebarHtmlItem += "<i class='a fa-play-circle fa-4x video-play-icon' aria-hidden='true'></i>"
												}
												
											}else{
												sidebarHtmlItem += "<img src='"+contextPath+"image/get/"+media.coverImagePath+"' alt=''>"
												if(media.isVideo){
													 sidebarHtmlItem +="<i class='fa fa-play-circle fa-4x video-play-icon' aria-hidden='true' ></i>"
												}
											}
	  				
				                       sidebarHtmlItem +="</a>"
			                   sidebarHtmlItem +="</div>"
	
								});
			                         
			               sidebarHtmlItem +=" </div>"
			           sidebarHtmlItem +=" </div>"
			        sidebarHtmlItem +="</div>"
	
					}
			        
			        sidebarHtmlItem +="<div class='card-footer py-2 d-flex justify-content-between align-items-center bg-white text-muted news-footer'>"      
			            sidebarHtmlItem +="<a href='#'></a>"
			            sidebarHtmlItem +="<div class='clearfix news-share-div'>"
			               sidebarHtmlItem +=" <p class='mb-0 card-title-sub t500 ls1 news-location'><a href='#' class='d-block'  >"+sidebarEventW.event.location+"</a></p>" 
			           sidebarHtmlItem +=" </div>"
			       sidebarHtmlItem +=" </div>"
		
			    sidebarHtmlItem +="</div>"
			sidebarHtmlItem +="</div>"
		
		
		return sidebarHtmlItem;
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

		sidebarEventW.eventGroupKey = sidebarEventW.dbName + "_" + sidebarEventW.event.groupId;
		var replaceString = "{providerUserId}";
		var regExp = new RegExp(replaceString, 'g');
    	sidebarEventW.event.reservedLink = sidebarEventW.event.reservedLink.replace(regExp, providerUserId);
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

//		var sidebarItem = $("#sidebarContainer").children("#" + sidebarEventW.eventGroupKey);
//		var cookieEventGroup = $.cookie("removeEventGroups")
//		if(cookieEventGroup != undefined && cookieEventGroup != null){
//			cookieEventGroup = JSON.parse(cookieEventGroup);
//			
//			$.each(cookieEventGroup, function(key, value){
//				
//				if(sidebarEventW.eventGroupKey === cookieEventGroup[key].eventGroupKey){
//					$(sidebarItem).css("display", "none")
//				}
//			});
//		}	
    },
    addExtraMarkers: function (){
    	
//    	L.marker([28.83921, 22.1162], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Abu-Attifel Oil Field").addTo(this.MyMap);
//    	L.marker([26.43123, 11.85322], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjMDAwMEZGIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiMwMDAwRkYiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjMDAwMEZGIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("El Sharara oil field").addTo(this.MyMap);
//    	L.marker([26.5767, 12.21805], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Sharara Oil Field").addTo(this.MyMap);
//    	L.marker([26.03905, 11.97699], {icon: new this.EventExtraIcon({iconUrl: "data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMTAwcHgiIGhlaWdodD0iMTAwcHgiIHZpZXdCb3g9IjAgMCAxMDAgMTAwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMDAgMTAwIiB4bWw6c3BhY2U9InByZXNlcnZlIj48Zz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNNy4xLDgzLjVoMTEuM2MwLjksMCwxLjctMC44LDEuNy0xLjd2LTIuNmMwLTAuOS0wLjgtMS43LTEuNy0xLjdzLTEuNywwLjgtMS43LDEuN3YwLjlIOC44di0wLjljMC0wLjktMC44LTEuNy0xLjctMS43ICAgcy0xLjcsMC44LTEuNywxLjd2Mi42QzUuNCw4Mi43LDYuMiw4My41LDcuMSw4My41eiIvPjxwYXRoIGZpbGw9IiNGRjAwMDAiIGQ9Ik04MCw3MS42di0xM2wxLjItMS45YzAuNS0wLjgsMC4zLTEuOC0wLjUtMi4zTDM0LjQsMjMuMmwxLjQtOS45YzAuMS0wLjYtMC4yLTEuMi0wLjctMS42bC00LjctMy4zICAgYy0wLjUtMC40LTEuMS0wLjQtMS43LTAuMUM1LjEsMTkuNCw2LjUsMzcuOCw2LjUsMzguNmMwLjEsMC42LDAuNCwxLjEsMC45LDEuM2wzLjgsMmwwLTFjMC0wLjEsMC0wLjIsMC0wLjJsMS4zLTguOSAgIGMwLjEtMC40LDAuMi0wLjcsMC41LTFsMTkuOS0xOWwwLjMsNC41TDE1LjgsMzIuOUwxNC42LDQxbDAsMC43bDExLjItOS42bDEyLjMsN2MtMC41LDEtMC43LDIuMS0wLjcsMy4zYzAsMC4zLDAsMC42LDAuMSwwLjkgICBsLTkuNiwzNi44aC00LjdjLTAuOSwwLTEuNywwLjgtMS43LDEuN3MwLjgsMS43LDEuNywxLjdoNDQuNGMwLjksMCwxLjctMC44LDEuNy0xLjdzLTAuOC0xLjctMS43LTEuN2gtNC44bC04LjktMzEuNGwyMiwxMy41ICAgYzAuMywwLjIsMC42LDAuMiwwLjksMC4ydjkuMmgtMy4zYy0wLjksMC0xLjcsMC44LTEuNywxLjd2Ny41YzAsMC45LDAuOCwxLjcsMS43LDEuN2gxMGMwLjksMCwxLjctMC44LDEuNy0xLjd2LTcuNSAgIGMwLTAuOS0wLjgtMS43LTEuNy0xLjdIODB6IE00NS4zLDM3LjJjMi44LDAsNS4xLDIuMyw1LjEsNS4xYzAsMi44LTIuMyw1LjEtNS4xLDUuMXMtNS4xLTIuMy01LjEtNS4xICAgQzQwLjIsMzkuNSw0Mi41LDM3LjIsNDUuMywzNy4yeiBNNDkuOSw2NS4xbC00LjYsNC4ybC00LjktNC41TDQ1LDUzLjZMNDkuOSw2NS4xeiBNMzksNjguMWwzLjgsMy41bC04LjIsNy42TDM5LDY4LjF6IE0zOC41LDgwLjEgICBsNi44LTYuM2w2LjgsNi4zSDM4LjV6IE01NS45LDc5bC04LjEtNy40bDMuNS0zLjJMNTUuOSw3OXoiLz48cGF0aCBmaWxsPSIjRkYwMDAwIiBkPSJNMTIuOSw0Mi42Yy0wLjMsMC0wLjUtMC4xLTAuOC0wLjJsLTAuOS0wLjVsLTAuMywzNi40bDMuNCwwbDAuMy0zNi42TDE0LDQyLjJDMTMuNyw0Mi40LDEzLjMsNDIuNiwxMi45LDQyLjZ6Ii8+PC9nPjwvc3ZnPg=="})		}).bindPopup("Al Fil oil field").addTo(this.MyMap);
    },
    addGeoLayer: function (geoList){
    	
    	if(geoList.length == 0){
			Page.mapHelper.eventGroupTreeAddMap();
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
		
		Page.mapHelper.eventGroupTreeAddMap();
    },
    addMapArea: function (mapArea){
    	
    	var data = JSON.parse(mapArea.coordinateInfo);
    	var polygon = L.polygon(data).setStyle({fillColor: mapArea.color , color: mapArea.color, fillOpacity: '0.25', opacity: '0.80', weight: '2.00'}).addTo(this.MyMap);
    	this.mapAreaList[mapArea.id] = polygon;
    },

    addMapControlBox: function (tileLayerList, overlaysMap){
    	

		var collapseState = false;
		if(tileLayerList != null){
			collapseState =  true;
		}

		L.control.layers(tileLayerList, overlaysMap, {"collapsed" : collapseState}).addTo(this.MyMap);

    },
    addMarkerToLayerGroup: function (marker, groupName, groupColor){
    	
    	var layerGroup = this.eventLayerGroupList[groupName];
    	if(layerGroup == undefined){
    		layerGroup = this.createClusterMarkerGroup(groupColor);
    		this.eventLayerGroupList[groupName] = layerGroup;
    		layerGroup.addTo(this.MyMap);
    	}
    	
		layerGroup.addLayer(marker);
		layerGroup.bringToFront();
    },
//    addEventGroups: function (eventGroupList){
//    	
//        $.each( eventGroupList, function( key, eventGroup ) {
//
//        	var layerGroup = this.eventLayerGroupList[eventGroup.name];
//        	if(layerGroup == undefined){
//        		layerGroup = L.layerGroup();
//        		this.eventLayerGroupList[groupName] = layerGroup;
//        		layerGroup.addTo(this.MyMap);
//        	}
//        });
//    },
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

	createClusterMarkerGroup: function(color){
		
		return L.markerClusterGroup({preferCanvas: true, 
				chunkedLoading: true, 
				spiderfyOnMaxZoom: true, 
				clusterPane :"eventMarker", 
				zoomToBoundsOnClick: true, 
        		removeOutsideVisibleBounds: true, 
				showCoverageOnHover: true,
				animate : true,
				ChuckDelay:10,
				ChuckInterval :100,
				iconCreateFunction: function(cluster) {
					var childCount = cluster.getChildCount();		
					return new L.DivIcon({ html: '<div style="background-image:linear-gradient('+color+', #000000b0)"><span>' + childCount + '</span></div>',  
					className: ' marker-cluster'  + " clusterMarkerFragmentColor" ,iconSize: new L.Point(40,40) });				
				}});				
	},
	
    eventOnClick: function (id) {
	
		$("#alarm").css("display", "none");  
    	$("#news-live").css("display", "block"); 
		this.selectSidebarItem(id);
		 
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
    getOrGenerateMarker : function (sidebarEventW, selectedIconControl){
    	
    	var sidebarEvent = sidebarEventW.event;
    	
//        var marker  = this.getMarker(this.getDbNameEventId(sidebarEventW));
//        if(marker != null && selectedIconControl != true){
//        	return marker;
//        }
    	
        
        //mapHelperThis = this;
		var iconSrc = contextPath + "image/markerImg?eventTypeId=" + sidebarEvent.eventTypeId + "&color="+encodeURIComponent(sidebarEvent.color) + "&selectedIconControl=" + selectedIconControl;
   		
//		marker = L.marker([sidebarEvent.latitude, sidebarEvent.longitude], {icon:  new this.EventIcon({iconUrl: iconSrc}), id:this.getDbNameEventId(sidebarEventW) , riseOnHover: true})
//			.bindPopup(sidebarEvent.title)
//			.on('click', function (e){
//				mapHelperThis.eventOnClick(e.target.options.id);
//			});
		
		
		var latitude = sidebarEvent.latitude;
		var longitude = sidebarEvent.longitude;
				
		latitude > 90 ? latitude = 90 : (-90 > latitude ? latitude = -90 : latitude);			
		longitude > 180 ? longitude = 180 : (-180 > longitude ? longitude = -180 : longitude);		
		
		var markerOrder = sidebarEventW.marker != undefined ? sidebarEventW.marker.options.order : Object.keys(Page.mapHelper.eventWrapperMap).length
		
		marker = L.canvasMarker([latitude, longitude], {
			order: markerOrder,//Object.keys(Page.mapHelper.eventWrapperMap).length,
			riseOnHover: true,
			id:this.getDbNameEventId(sidebarEventW),
			selected: selectedIconControl,
	        radius: 15,
	        img: {
	          url: iconSrc,
	          size: [39,39],
	        },
	        weight: 0,
			
	    })
	    .bindPopup(sidebarEvent.title)
	    .on('click', function (e){

			Page.mapHelper.eventOnClick(e.target.options.id)
		})
		/*.on('mouseover',function(event) {
			
			if(Page.mapHelper.lastHoverChoosenId != event.target.options.id || Page.mapHelper.lastHoverChoosenId == 0){
				
				Page.mapHelper.markerHover(event.target.options.id);				
			}
    	});*/

			
        return marker;
    },
	markerHover: function(id){
		
		if(this.lastHoverChoosenId == 0){
			
			this.lastHoverChoosenId = id;
		}
		
		var eventWrapper = this.eventWrapperMap[id];
		
		var layerGroup = this.eventLayerGroupList[eventWrapper.event.groupName];
		
		var smallMarker = eventWrapper.marker;
		smallMarker.remove(layerGroup);

		var newMarker = this.eventWrapperMap[id].marker;

		this.addMarkerToLayerGroup(newMarker ,layerGroup, eventWrapper.event.color);

		this.lastHoverChoosenId = id;
		

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
		var sidebarIdObj = $(sidebarId);
		var timeout = 0;
		
		if(sidebarIdObj != null && sidebarIdObj.length <= 0 /*&& Page.timeDimensionMode*/){

			$("#sidebarContainer > .sidebarItem").remove();
			
			var eventIndex = 0;
			var prevIndex = 0;
			var lastIndex = 0;
			$.each(this.eventWrapperIndexList, function(key, value){
				
				if(value == id){	//Page.mapHelper.getDbNameEventId(value)									
					
					prevIndex = eventIndex - 15 < 0 ? 0 : eventIndex - 15;
					lastIndex = Math.min(Math.max(eventIndex + 15, Page.sidebarMaxEventCount), Page.mapHelper.eventWrapperIndexList.length);// 0-30 arası seçmesi sağlanır				
				}
				
				eventIndex++;
			})
			
			Page.sidebarPrevIndex = prevIndex;
			Page.sidebarLastIndex = lastIndex;
			
			
			if(TimeDimension.timeDimensionMode){
				
				TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, false, true)
					

			}else{
				
				var eventIndexList = Page.mapHelper.eventWrapperIndexList.slice(Page.sidebarPrevIndex, Page.sidebarLastIndex);
				var eventList = [];
				$.each(eventIndexList, function(key, id){
					
					var event = Page.mapHelper.eventWrapperMap[id];
					eventList.push(event);
					
				});
				
				var eventAllWrapper = {
					
					eventWrapperList: eventList
				}
				
				Page.addEventToSidebar(eventList, false)

				Page.liveDownNormalScroll = false;
				if(Page.sidebarLastIndex == Page.mapHelper.eventWrapperIndexList.length){
						
					Page.liveDownNormalScroll = true;
				}
		
			}
			
			//timeout = 500;
		}
		$(".selected").removeClass("selected");
		//setTimeout(function() { $(sidebarId).addClass("selected"); }, timeout);
		
		$(sidebarId).addClass("selected");
		$("#sidebarContainer").scrollTo($(sidebarId), 500, {offsettop:100});
		
		if(Page.liveScrollDestroyControl){
			Page.InitScroll();
		}
    },  


 
    selectIcon: function (id){
    	
    	var newMarker = this.eventWrapperMap[id].marker
    	
    	var oldMarker = this.getMarker(this.lastChoosenId);
    	if (oldMarker != null){
    		//$(oldMarker._icon).removeClass('i-circled i-custom-map');
 //   		oldMarker._zIndex -= 1000;
    	}

    	this.MyMap.setView(newMarker.getLatLng());
  //  	$(newMarker._icon).addClass('i-circled i-custom-map');
 //   	newMarker._zIndex += 1000;

		if(id === this.lastChoosenId){//Kaldığım yere git kısmından geldiğinde yeniden haritaya ekleme yapmaması için eklendi.			
			return;
		}
		var sidebarEventW = this.eventWrapperMap[id];
		var sidebarEvent = sidebarEventW.event;
		
		//#region layer içinden markerı silme işlemi yapılıyor.
		
		var newMarker2 = this.getOrGenerateMarker(sidebarEventW, true);
	
	

		
		var layerGroup = this.eventLayerGroupList[sidebarEvent.groupName];
    	newMarker.removeFrom(layerGroup);
		//#endregion layer içinden markerı silme işlemi yapılıyor.


		this.addMarkerToLayerGroup(newMarker2, sidebarEvent.groupName, sidebarEvent.color);
		this.eventWrapperMap[id].marker = newMarker2;


	//Burada post atıp sarı ile çevrilmiş resim gelecek url alanına o basılacak.
		//#region ikonları canvasa yeniden ekleme işlemi yapılıyor.
		
		
		
		
		//önceki seçileni silip sarısız olanı eklemek gerekecek.
		
		if(this.lastChoosenId != undefined && this.lastChoosenId != 0){
			
			
			var isItemExistInList = true;
			if(TimeDimension.timeDimensionMode == true){// timedimension modda daha önceden seçilmiş bir item artık listede olmayabilir
			
				isItemExistInList = this.eventWrapperIndexList.indexOf(this.lastChoosenId) >= 0;
			}
			
			var sidebarEventWOld = this.eventWrapperMap[this.lastChoosenId]
				
			var marker = this.getOrGenerateMarker(sidebarEventWOld, false);	
			this.eventWrapperMap[this.lastChoosenId].marker = marker;
			
			if(isItemExistInList){
				this.addMarkerToLayerGroup(marker, this.eventWrapperMap[this.lastChoosenId].event.groupName, this.eventWrapperMap[this.lastChoosenId].event.color);
			}
			
			oldLayerGroup = this.eventLayerGroupList[sidebarEventWOld.event.groupName];
			oldMarker.removeFrom(oldLayerGroup);
			
		}

    	
    },
    unSelectIcon: function (id){// kullanılmıyor
    
    
    	var selectedMarker = this.eventWrapperMap[id].marker
    	
		var sidebarEventW = this.eventWrapperMap[id];		
		var unselectedMarker = this.getOrGenerateMarker(sidebarEventW, false);
	
		
		var layerGroup = this.eventLayerGroupList[sidebarEventW.event.groupName];
    	selectedMarker.removeFrom(layerGroup);

		//this.addMarkerToLayerGroup(selectedMarker, sidebarEvent.groupName);
		this.eventWrapperMap[id].marker = selectedMarker;

		return unselectedMarker;    
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
    showInMap: function (id) {
    	
    	let markerId = id;
    	
		if(!TimeDimension.timeDimensionPlayerState){
			
			this.selectIcon(markerId);
//    		this.selectSidebarItem(markerId);
		}
    	
    	this.setLastInfoToCookie(id);

    	this.lastChoosenId = markerId;
    },


	addAlertToMap: function (alertItem){
    	
    	var polygon = L.polygon(alertItem.coordinateInfo);
    	polygon.id = alertItem.id;
    	
		if(Alert.lastChoosenAlertId != 0){
			polygon._zIndex -= 1000;
		}
		
    	polygon._zIndex += 1000;

		this.MyMap.setView(polygon._latlngs[0][0]);
			
		this.MyMap.fitBounds(polygon._bounds);
		
		var lat = polygon._latlngs[0][0].lat;
		var lng = polygon._latlngs[0][0].lng;
						
		this.setLastAlertInfoToCookie(alertItem.id, lat, lng )

    },

	alertOnClick: function (id) {
		$("#news-live").css("display", "none");  
		$("#alarm").css("display", "block");
		
		Alert.selectSidebarItem(id);
    	if(Alert.lastChoosenAlertId == id){
			Alert.AlarmOnClick(id)			
    		return;
    	}
    	
		//this.showInAlertMap(id); 
		this.alarmClickMap(id);   	
    }, 


	alarmClickMap: function(sidebarAlarmId){
		
		$.each(Alert.sidebarAlertList, function(key, alertItem){
			if(sidebarAlarmId == alertItem.id){
												
			//	Page.mapHelper.addAlertToMap(alertItem)
				Alert.selectSidebarItem(sidebarAlarmId)								
				Alert.lastChoosenAlertId = sidebarAlarmId;
				
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

	eventGroupTreeAddMap(){
		var html = "<div id='eventGroupTree'></div>"
		
	    var htmlLegend = L.control.htmllegend({
	        position: 'topright',
	        legends: [{
	            name: 'Olay Grupları',
	            layer: null,
				//collapsedOnInit:false,
	            elements: [{
	                label: '',
	                html: html,
	                style: {
	                    'width': 'auto',
	                    'height': 'auto'
	                }
	            }]
	        }],
	        collapsedOnInit: true,
	
	    })
	    this.MyMap.addControl(htmlLegend);
	
		
	
		var dropDownButtonHtml = 
		'<div class="dropdown dropdown-event-group"> '
		+'<button type="button" id="detailedSearchOnlyEventGroup" onclick="Page.onlyEventGroupSearch()" class="btn btn-primary btn-sm eventGroupTreeMapSearchButton" data-id="">'+lang.get("label.search")+'</button>'
		
			+'<button class="btn btn-sm btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'+lang.props["label.transactions"]+'</button>'
			+'<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">'
			
				+'<a class="dropdown-item dropdown-item-event-group" id="detailedSearchOnlyEventGroupSetDefault" onclick="Page.onlyEventGroupUserSettingsSave()" href="#">'+lang.props["label.user.settings.save"]+'</a>'
				
				if(Object.keys(Common.userSettingsListUnderLayer).length > 0 && Common.userSettingsListUnderLayer[Page.layerId].existEventGroups){
					dropDownButtonHtml += '<a class="dropdown-item dropdown-item-event-group" id="detailedSearchOnlyEventGroupGetDefault" onclick="Page.onlygetUserSettingsEventGroup(true)" href="#">'+lang.props["label.get.from.user.settings"]+'</a>'
					dropDownButtonHtml +='<a class="dropdown-item dropdown-item-event-group" id="detailedSearchOnlyEventGroupGetDefault" onclick="Page.onlyEventGroupUserSettingsDelete()" href="#">'+lang.props["label.delete.user.settings"]+'</a>'
				
				}
				
				
				dropDownButtonHtml +='<a class="dropdown-item dropdown-item-event-group" id="detailedSearchOnlyEventGroupClear" onclick="Page.onlyEventGroupSearchClear()" href="#">'+lang.get("label.select.all")+'</a>'
				dropDownButtonHtml +='<a class="dropdown-item dropdown-item-event-group" id="detailedSearchOnlyEventGroupClear" onclick="Page.onlyEventGroupUncheck()" href="#">'+lang.get("label.deselect.all")+'</a>'
				dropDownButtonHtml +='<a class="dropdown-item  dropdown-item-event-group" id="coordinatesSave" onclick="Page.CoordinatesSave()" href="#">'+lang.props["label.map.coordinates.save"]+'</a>'
				dropDownButtonHtml +='<a class="dropdown-item  dropdown-item-event-group" id="coordinatesGet" onclick="Page.OnlyGetUserSettingsMapInfo(true)" href="#">'+lang.props["label.get.map.info"]+'</a>'
				
			+'</div>'
		+'</div>'
		
		$(".legend-elements").append(dropDownButtonHtml);

		
		$("#theTree").append('<div id="eventGroupTree" ></div>');						
		Common.CreateEventGroupTreeView(Common.eventGroupTreeViewList);
		
	}

	
}
