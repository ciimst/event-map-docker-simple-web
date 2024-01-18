var HeatMap = {
	heatmapStartDate: null,
	heatmapEndDate: null,
	heatmapMagnitude: 50,
	isHeatmap: false,
	lastChoosenId: 0, // modal açılması için son seçilen item
	sidebarEventWrapperList: {},
	heatMapEventTypeId: 0,
	Init: function() {

		
		
		//Detaylı arama kısmının sayfa yenilendiğinde de gelmesi için.
		HeatMap.HeatMapForSearchDivOpen();
		
		HeatMap.HeatMapShowHtmlElement();
		
		var cookieVal = $.cookie('isSidebarEventShowForHeatMap');
		if(cookieVal != undefined && cookieVal == 'true'){
			$.cookie('isSidebarEventShowForHeatMap', false, { expires: Page.cookieExpires, path: '/' });								
		}
		
		HeatMap.ShowHeatmap();
		



	},

	InitializeEvents: function() {


	},
	
	HeatMapForSearchDivOpen(){
				

		$("#detailedSearchDivNewsLive").css("display", "none");
		$("#defaultSearchResultAndButtonDiv").css("display", "block");
		
	},
	
	HeatMapShowHtmlElement(){


		$("#heatmapSearchButton").show();
		$("#eventFound").show();
		$("#heatMapLoadSidebarEvent").show();

		$("#timeDimensionSpinner").css("display", "block")
		

	},

	ShowHeatmap(){
		
		var cookieModel = Common.ReadCookies();
		
		$("#sidebarContainer > .sidebarItem").remove();
		var startDateStr = cookieModel.startDate;
		var endDateStr = cookieModel.endDate;

		startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)

		var eventSearch = cookieModel.eventTextSearch;

		
		var eventTypeIdSearch = null;
		if(cookieModel.eventTypeIdSearch != undefined){
			eventTypeIdSearch = cookieModel.eventTypeIdSearch;
			eventTypeIdSearch = eventTypeIdSearch.split(',');
			if (eventTypeIdSearch == 0) {
					eventTypeIdSearch = [];
			}
		}else{
			eventTypeIdSearch = [];
		}

		var eventSearchCity = cookieModel.eventSearchCity;


		var eventSearchCountry = cookieModel.eventSearchCountry;

		var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
		
		var url = contextPath + 'event/heatmapForFirstLoading?layerId=' + Page.layerId + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr + "&eventSearch=" + eventSearch + "&eventTypeIdSearch=" + eventTypeIdSearch 
		+ "&eventSearchCity=" + eventSearchCity + "&eventSearchCountry=" + eventSearchCountry + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;

		XhrProgress(url, function () {
			let eventAllWrapper = JSON.parse(this.responseText);

			Page.lastScrollDate = eventAllWrapper.lastScrollDate;

			eventWrapperList = eventAllWrapper.eventWrapperList;

			HeatMap.UseEvents(eventAllWrapper);

			for (let value of Page.removeEventGroupList) {
				HeatMap.heatmapRemoveEventGroup(value.eventGroupKey);
			}

			document.querySelector("#heatmapEventCount").textContent = Object.keys(Page.mapHelper.eventWrapperMap).length - Object.keys(Page.mapHelper.eventWrapperMapDeleted).length;
		});
	},
	
	OnClickShowSidebarEvent(){		
		
		$("#eventNotFound").remove();
		$("#heatMapSpinner").css("visibility", "visible");
		$.cookie('isSidebarEventShowForHeatMap', true, { expires: Page.cookieExpires, path: '/' });
		$("#sidebarContainer").addClass("sidebarContainerDivForHeatMapDefaultSearch");
		HeatMap.ShowHeatmapWithSidebarEvent();
	},
	
	ShowHeatmapWithSidebarEvent() {
		
		var cookieModel = Common.ReadCookies();
		$("#sidebarContainer > .sidebarItem").remove();

		var coordinates = Page.mapHelper.MyMap.getBounds();
		
		var southWestLng = coordinates._southWest.lng;
		var southWestLat = coordinates._southWest.lat;
		var northEastLng = coordinates._northEast.lng;
		var northEastLat = coordinates._northEast.lat;

		

		var startDateStr = cookieModel.startDate;
		var endDateStr = cookieModel.endDate;

		startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)

		var eventSearch = cookieModel.eventTextSearch;
		
		var eventTypeIdSearch = null;
		if(cookieModel.eventTypeIdSearch != undefined){
			eventTypeIdSearch = cookieModel.eventTypeIdSearch != 0 ? cookieModel.eventTypeIdSearch : [];
		}else{
			eventTypeIdSearch = [];
		}


		var eventSearchCity = cookieModel.eventSearchCity;

		var eventSearchCountry = cookieModel.eventSearchCountry;


		var eventGroupdbNameIdList =  cookieModel.selectedEventGroupList;
		
	
		var url = '/event/heatmap?layerId=' + Page.layerId + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr + "&eventSearch=" + eventSearch + "&eventTypeIdSearch=" + eventTypeIdSearch +
		'&southWestLng=' + southWestLng + '&southWestLat=' + southWestLat + '&northEastLng=' + northEastLng + '&northEastLat=' + northEastLat + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList  + "&eventSearchCity=" + eventSearchCity + "&eventSearchCountry=" + eventSearchCountry;
		
		$.ajax({
			type: "GET",
			url: url,
		 	contentType: "application/json; charset=utf-8",  
    		async: true, 
			start_time: new Date().getTime(),
			 complete: function() {
	            console.log('olay yükleme '+(new Date().getTime() - this.start_time)+' ms');
	        },
			success: function(response) {

				var eventAllWrapper = response;

				Page.lastScrollDate = eventAllWrapper.lastScrollDate;
				eventWrapperList = eventAllWrapper.eventWrapperList;
				
				if(eventWrapperList.length == 0){
					$("#sidebarContainer").append('<div id="eventNotFound"  class="veritacalAndHorizantalDiv"><i class="fa fa-exclamation-circle fa-1x"></i><span class="eventNotFoundText">'+lang.props['label.event.not.found.displayed.area']+'</span></div>');
				}

				HeatMap.UseEventsWithSidebar(eventAllWrapper);				

				$("#heatMapSpinner").css("visibility", "hidden")
			},
			done: function() {

			
				
			}
		});
		
		
		
	},
	


	UseEvents: function(eventAllWrapper, isPrepend = false) {

		Page.isALertEventClick = Page.GetCookieIsAlertEvent();

		eventWrapperList = eventAllWrapper.eventWrapperList;

		$.each(eventWrapperList, function(key, eventWrapper) {

			if (Page.isALertEventClick == "true") {

				if (eventWrapper.alertList == null) {
					return;
				}
			}

			eventWrapper.eventGroupKey = eventWrapper.dbName + "_" + eventWrapper.event.groupId
			Page.mapHelper.addSidebarEventWrapper(eventWrapper);
			HeatMap.addEventToHeatmap(eventWrapper);

			if (Page.lastEventId < eventWrapper.event.id) {
				Page.lastEventId = eventWrapper.event.id
			}

		});


		if (Page.lastEventIdMap == null) {
			Page.lastEventIdMap = eventAllWrapper.lastEventIdMap;
		}

		$.each(eventAllWrapper.lastEventIdMap, function(dbName, lastId) {

			if (Page.lastEventIdMap[dbName] < lastId) {
				Page.lastEventIdMap[dbName] = lastId;
			}
		})


		Page.UpdateDate();
	},
	
	UseEventsWithSidebar: function(eventAllWrapper, isPrepend = false) {
		
		Page.mapHelper.eventWrapperIndexList = [];
		Page.isALertEventClick = Page.GetCookieIsAlertEvent();

		var eventWrapperListResult =[]
		eventWrapperList = eventAllWrapper.eventWrapperList;
		
		$.each(eventWrapperList, function(key, eventWrapper) {

			if (Page.isALertEventClick == "true") {

				if (eventWrapper.alertList == null) {
					return;
				}
			}
			
			sidebarEventW = eventWrapper.event;
			eventWrapper.eventGroupKey = eventWrapper.dbName + "_" + eventWrapper.event.groupId
			Page.mapHelper.addSidebarEventWrapper(eventWrapper);	
			
			eventWrapperListResult.push(eventWrapper)

			if (Page.lastEventId < eventWrapper.event.id) {
				Page.lastEventId = eventWrapper.event.id
			}

		});
		
			
		//sidebara ekleme.
		HeatMap.addEventToSidebar(eventWrapperListResult)
		



		if (Page.lastEventIdMap == null) {
			Page.lastEventIdMap = eventAllWrapper.lastEventIdMap;
		}

		$.each(eventAllWrapper.lastEventIdMap, function(dbName, lastId) {

			if (Page.lastEventIdMap[dbName] < lastId) {
				Page.lastEventIdMap[dbName] = lastId;
			}
		})


		Page.UpdateDate();
		
	},


	resetHeatmap: function() {
		Page.mapHelper.MyMap.removeLayer(Page.mapHelper.heatmapLayer);
		Page.mapHelper.heatmapLayer = L.heatLayer([]);
		Page.mapHelper.MyMap.addLayer(Page.mapHelper.heatmapLayer);
		for (let [key, value] of Object.entries(Page.mapHelper.eventWrapperMap)) {
			if (!(Page.mapHelper.eventWrapperMapDeleted.hasOwnProperty(key) && Page.mapHelper.eventWrapperMapDeleted[key] === value)) {
				var dataPoint = [value.event.latitude, value.event.longitude, HeatMap.heatmapMagnitude];
				Page.mapHelper.heatmapLayer.addLatLng(dataPoint);
			}
		}

		$("#heatmapEventCount").text(Object.keys(Page.mapHelper.eventWrapperMap).length - Object.keys(Page.mapHelper.eventWrapperMapDeleted).length);
	},

	heatmapRemoveEventGroup: function(layerGroupKey) {
		for (let [key, value] of Object.entries(Page.mapHelper.eventWrapperMap)) {
			if (value.eventGroupKey == layerGroupKey) {
				Page.mapHelper.eventWrapperMapDeleted[key] = value;
			}
		}
		HeatMap.resetHeatmap();
	},

	heatmapAddEventGroup: function(layerGroupKey) {
		for (let [key, value] of Object.entries(Page.mapHelper.eventWrapperMapDeleted)) {
			if (value.eventGroupKey == layerGroupKey) {
				delete Page.mapHelper.eventWrapperMapDeleted[key];
			}
		}
		HeatMap.resetHeatmap();
	},
	

	
	addEventToSidebar: function(eventWrapperList, isPrepend = false){
		
		var sidebarHtmlItemList = "";
		$.each( eventWrapperList, function( key, eventWrapper ){
			
			sidebarHtmlItemList = sidebarHtmlItemList + HeatMap.addEventToSidebarHTML(eventWrapper)
			
		});
		
		$("#sidebarContainer").append(sidebarHtmlItemList);

	},
	
	addEventToHeatmap: function (sidebarEventW){
		
		var definedSidebarEventW = Page.mapHelper.eventWrapperMap[Page.mapHelper.getDbNameEventId(sidebarEventW)];
    	if(definedSidebarEventW != null){
    		sidebarEventW = definedSidebarEventW;
    	}
    	
    	var sidebarEvent = sidebarEventW.event;
		
		var dataPoint = [sidebarEvent.latitude, sidebarEvent.longitude, HeatMap.heatmapMagnitude];
		
		
		Page.mapHelper.heatmapLayer.addLatLng(dataPoint);
		
	},
	
	 ChooseEvent : function (sidebarId){
    	HeatMap.eventOnClick(sidebarId);
    },

    eventOnClick: function (id) {
    	
    	if(HeatMap.lastChoosenId == id){
    		Page.OpenDetailModal(Page.mapHelper.eventWrapperMap[id]);
    		return;
    	}
    	
    	HeatMap.showInMap(id);    	
    }, 

    showInMap: function (id) {
    	
    	let markerId = id;
    	
		HeatMap.selectIcon(markerId);
		HeatMap.selectSidebarItem(markerId);
    	
    	Page.mapHelper.setLastInfoToCookie(id);

    	HeatMap.lastChoosenId = markerId;
    },

    selectIcon: function (id){
    	
    	var event = Page.mapHelper.eventWrapperMap[id].event

		var cookieVal = JSON.parse($.cookie('coordinateInfo'));	
		if(cookieVal != undefined){
			
			Page.mapHelper.MyMap.setView(new L.LatLng(event.latitude, event.longitude), cookieVal.zoom);
		} else{
			Page.mapHelper.MyMap.setView(new L.LatLng(event.latitude, event.longitude), 6);
		}  		
    },

    selectSidebarItem: function (id){
	
		var sidebarId = "#" + Page.mapHelper.getSidebarId(id);
		$(".selected").removeClass("selected");
		
		$(sidebarId).addClass("selected");
		$("#sidebarContainer").scrollTo($(sidebarId), 500, {offsettop:100});

    },  

    addEventToSidebarHTML: function(sidebarEventW){
		
//		var definedSidebarEventW = Page.mapHelper.eventWrapperMap[Page.mapHelper.getDbNameEventId(sidebarEventW)];
//    	if(definedSidebarEventW != null){
//    		sidebarEventW = definedSidebarEventW;
//    	}
    	
    	var sidebarEvent = sidebarEventW.event;
    	
    	sidebarEvent.sidebarId = Page.mapHelper.getSidebarId(Page.mapHelper.getDbNameEventId(sidebarEventW));	
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
		
		//Page.mapHelper.eventWrapperMap[Page.mapHelper.getDbNameEventId(sidebarEventW)].event.sidebarSrc = sidebarEvent.sidebarSrc;
		
		var sidebarHtmlItem =
			"<div class='col-md-12 mb-2 mt-2 sidebarItem news-card' id='"+sidebarEventW.eventGroupKey+"' data-event-id='"+sidebarEventW.eventIdDbName+"'>"
			    +"<div class='card course-card hover-effect noborder' onclick=HeatMap.ChooseEvent('"+sidebarEventW.dbName+sidebarEventW.event.id+"') id='"+sidebarEventW.event.sidebarId+"' data-id='"+sidebarEventW.event.id+"'  data-color='"+sidebarEventW.event.color+"'  style='box-shadow: 0 0 20px rgba(0,0,0,.2); --bcolor: "+sidebarEventW.event.color+"'>"
					  +"<div class='card-body py-2'>"

			            +"<img class='i-circled i-custom' src='"+sidebarSrc+"' />"
			            +"<p class='t500 mb-3 mt-2'><a href='#' class='d-block time-ago' >"+CustomFormatter.GetDatePrettyFormatted(sidebarEventW.event.eventDate)+ " - " +CustomFormatter.GetDateFormatted(sidebarEventW.event.eventDate)+"</a></p>"
			            sidebarHtmlItem += "<div class='news-top-right'>" ;
	
							if(sidebarEventW.alertList){
								
								$.each(sidebarEventW.alertList, function(key,value){
									
									if(!value.readState && key == 0){
										
										sidebarHtmlItem += "<i class='fa fa-envelope-open fa-1x readStateIcon'  id='readStateIcon' style='visibility:hidden;' aria-hidden='true'></i>"
			                            sidebarHtmlItem += "<i class='fa fa-envelope fa-1x readStateIconClose alertEventIcon' style='color:red;' aria-hidden='true'></i>"
									}
									
									if(value.readState && key == 0){
										
										sidebarHtmlItem += "<i class='fa fa-envelope-open fa-1x alertEventIcon'  style='color:green;' aria-hidden='true'></i>"
									}
								})	
							}
							
							
							if(sidebarEventW.alertList){
								sidebarHtmlItem +="<i class='fa fa-bell fa-1x alarmIcon' aria-hidden='true'></i>"
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
	
//	GotoTop : function(){
//
//		$("#news-live").scrollTo(0, 500, {offsettop:100});
//	},
	

}

$(document).ready(function () {    
	
//	$("#news-live").scroll(function(e){ 
//		
//		if($("#news-live").scrollTop() > 20){
//			
//			$("#gotoTop").css("display", "block")
//		}else{
//			$("#gotoTop").css("display", "none")
//		}
//	});

});