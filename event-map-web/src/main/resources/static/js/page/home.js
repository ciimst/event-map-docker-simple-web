var Page = {    
    PageText: "Ana Sayfa",
    PageUrl: "/home",
    PageKod: "home",
    PageNo: 0,    
    eventGroupList: [],
    hasNextPage: true,
    tileServers: null,
    tileLayerList: null,
    time: null,
    lastScrollDate: null,
    firstScrollDate: null,
    mapHelper: null,    
    lastEventId: 0,
	lastEventIdDB1: 0,
	lastEventIdList: null,
	lastEventIdMap: null,
    loadEventRunning: false,
    timeLineStartDate: "01.01.2020",
    pageAutoRefreshTimeInterval: 10000,
    layerId: 0,
	isALertEventClick : false,
	defaultDbName: "default",
	loadAlertEventSize: 0,
	eventCount: 0,
	eventCountPrev: 0,
	eventTextSearch: null,
	notNullEventGroupCount: 0,
	infiniteScrollContainer: null,
	cookieExpires: 7,
	sidebarLastIndex: null,
	sidebarPrevIndex: 0,
	lastSidebarLastOrPrevIndex: null,
	liveScrollDestroyControl: false,
	liveDownNormalScroll: true,
	thirtyEventChecks: false,
	removeEventGroupList: [],
	sidebarMaxEventCount: 30,
	totalCountEvents: 0,
	countLoadedEvents:0,
	eventGroupCookieSaveList: {"removedEventGroupUnSelected": [], "removedEventGroupSelected": []},
	goToLastEvent: false,
	isEventTable: false,
    Init: function () {
    	
    	Page.timeLineStartDate = paramTimeLineStartDate;
    	Page.layerId = paramLayerId;
    	
    	HeatMap.isHeatmap = paramHeatmap;
		TimeDimension.timeDimensionMode = paramTimeDimensionMode;

    	Page.time = navtime;    	
   
		Common.LoadEventGroups(Page.layerId); // sync
		
		DetailedSearch.Init();	
		Page.GetRemoveEventGroupsCookieValue("removedEventGroups");
		
		//Maphelperdan önce olması gerekiyor.
		//Timedimension için tarih setleme yapılıyor.
		if(TimeDimension.timeDimensionMode == true){	
			TimeDimension.SetDateAndTime();	
		}
		
		Common.SetLayerId();
		Common.SetParamValue(); 
		Common.UserSettingsUnderLayer();
		
    	Page.mapHelper = new MapHelper(Page.OpenDetailModal, Common.eventGroupList);   
			
		Common.Init(Page.mapHelper); 
		Common.UserSettingsUnderLayer();
    	
    	Page.LoadTileServers();
    	Page.LoadMapAreas();
    	
		
		DetailedSearch.AdvancedSearchModalClose();
		Page.GetEventGroupTree();
		
		if (HeatMap.isHeatmap){
			
			HeatMap.Init();
			
		} else if(TimeDimension.timeDimensionMode == true){	
		
			TimeDimension.Init();
			
		}else{
			
			Page.InitScroll()
			
			$('head').prepend('<meta http-equiv="refresh" content="'+pageRefreshTime+'">')

			setTimeout(Page.RefreshPage, Page.pageAutoRefreshTimeInterval);	
			
			$(".leaflet-bar-timecontrol").css("display", "none")	
						
			
			$("#eventCountInfo").css("display", "block");
			
			var lastEventValue = $.cookie('lastEvent');
			if(lastEventValue !== undefined && lastEventValue !== ""){
				$("#goToLastEvent").css("display", "block");
			}else{
				$("#goToLastEvent").css("display", "none");
			}

			
			
			
			
			if (Page.time != null) {
			    
				$("#live-sidebar-header").text(lang.props["label.timeline"]);	
				$("#allEventLoadButton").remove();
				$(".generalFloat").css("margin-top", "-20px");			
			}
			
			Page.isALertEventClick = Page.GetCookieIsAlertEvent();
			if(Page.isALertEventClick == "true" ){
				
				$("#allEventLoadButton").css("display", "none");	
			   	$("#eventCountInfo").css("display", "none");	
												
			}else{
				Page.TotalCountEvents();
			}
			
		}
		
		DetailedSearch.ShowDetailedSearchBadge();
		Common.LoadKey();
    	Common.LoadLayers();
    	Common.LoadTimeLine();
    	Common.SetTimeLine();

		Page.LogOut();


		$("#eventSearch").val($.cookie('eventSearchText'));
		
		
						
		Page.LogoSetUrl();	
		
		
		
    },
    InitializeEvents: function () {

    	$('#state-toggle-button').bootstrapToggle({
			size: "normall",
			offstyle: "danger eventDetailModalStateToggleButton",
			onstyle: "success eventDetailModalStateToggleButton eventDetailModalStateChange",
			on: lang.get("label.active"),
		  	off: lang.get("label.passive"),
 		});
    },

	
	
	
	TotalCountEvents: function(){
		var  url= "/event/totalCountEvents?layerId=" + (Page.layerId == null ? 0 : Page.layerId);
		
		var cookieModel = Common.ReadCookies();
		
		var eventTextSearch = cookieModel.eventTextSearch;
		url = url + "&eventSearch=" + eventTextSearch;										
		
		var eventTypeIdSearch = cookieModel.eventTypeIdSearch;
		if(eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "null"){	
			var eventTypeIdSearch = eventTypeIdSearch.split(',');	
			if (eventTypeIdSearch == 0) {
				eventTypeIdSearch = [];
			}		
			url = url + "&eventTypeIdSearch=" + eventTypeIdSearch;
		}
		
		var eventSearchCountry = cookieModel.eventSearchCountry;
		url = url + "&eventSearchCountry=" + eventSearchCountry;
		
		var eventSearchCity = cookieModel.eventSearchCity;
		url = url + "&eventSearchCity=" + eventSearchCity;
		
		var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
		url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;
		
		var startDateStr = cookieModel.startDate;
		var endDateStr = cookieModel.endDate;

		startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		
		url = url + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr;	
					
		$.ajax({ type: "GET",   
		     url: url,   
		     async: true,
		     contentType: "application/json",
		     success : function(totalCountEvents)
		     {
				
				
				//Toplam olay sayısı
				Page.totalCountEvents = totalCountEvents;
				$("#totalCountEvents").text(Page.totalCountEvents);
				
				$("#totalCountEvents").css("display", "block");
				$("#seperatorCountEvents").css("display", "block");

				
		     }
		});
	},
	
	LoadAllEvents: function(){	
		
		var  url= contextPathWithoutSlash + "/event/loadAllEvents?layerId=" + (Page.layerId == null ? 0 : Page.layerId);
		
		var cookieModel = Common.ReadCookies();
		
		var eventTextSearch = cookieModel.eventTextSearch;
		url = url + "&eventSearch=" + eventTextSearch;									
		
		var eventTypeIdSearch = cookieModel.eventTypeIdSearch;
		if(eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "null"){	
			var eventTypeIdSearch = eventTypeIdSearch.split(',');
			if (eventTypeIdSearch == 0) {
				eventTypeIdSearch = [];
			}				
			url = url + "&eventTypeIdSearch=" + eventTypeIdSearch;
		}
		
		var eventSearchCountry = cookieModel.eventSearchCountry;
		url = url + "&eventSearchCountry=" + eventSearchCountry;
		
		var eventSearchCity = cookieModel.eventSearchCity;
		url = url + "&eventSearchCity=" + eventSearchCity;
		
		if (Page.lastScrollDate != null) {
			url = url + "&lastScrollDate=" + Page.lastScrollDate ;
		}
		
		var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
		url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;
		
		var startDateStr = cookieModel.startDate;
		var endDateStr = cookieModel.endDate;

		startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		
		url = url + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr;	
		XhrProgress(url, function () {
			
			let eventAllWrapper = JSON.parse(this.responseText);
			
			$("#allEventLoadButton").attr("onClick","")
			$("#allEventLoadButton").prop("disabled", true);
			$("#allEventLoadButton").css("color", "#80868c");
			$("#allEventLoadButton").css("cursor", "auto");
				
				eventAlertListSize = 0;
				var eventWrapperListResult = eventAllWrapper.eventWrapperList;
				Page.isALertEventClick = cookieModel.alertChecked;
				
				
				//eventWrapperListResult boş istek atılırken null geliyor. Veritabanından gelebilecek olaylar bittiğinde sıfır olarak geliyor.
				//Alarmlı olaylar için kontrol yapılırken olayların alarmlı olay listesine bakılıp null olmayanlar alınıyor.
				if(eventWrapperListResult != null){
					Page.lastScrollDate = eventAllWrapper.lastScrollDate;
					if(Page.firstScrollDate == null){
						Page.firstScrollDate = eventAllWrapper.firstScrollDate;
					}
					

					
					if(Page.isALertEventClick == "true" ){
					
						eventWrapperListResult = $.grep(eventWrapperListResult, function(e){ 
						     return e.alertList != null; 
						});														
					}
				}
				
				//Tümünü göster butonuna basıldıktan sonra alarmlı olaylar filtresi yoksa ve gelen olay sayısı yüklenmiş olay sayısından fazla ise 
				//zaten listeye eklendiği için yeni gelen olaylar, arka tarafa boş istek atıp var olan listeden ekrana gönderme yapılabilmesi için
				//liveDownNormalScroll değerini false yaptık.
				if(Page.isALertEventClick != "true" && eventWrapperListResult.length > Page.mapHelper.eventWrapperIndexList.length){
					
					Page.liveDownNormalScroll = false;
				}
			
				$.each( eventWrapperListResult, function( key, eventWrapper ){
						
					if(eventWrapper.alertList != null){
						eventAlertListSize++;
						Page.loadAlertEventSize++;
					}
					
					var dbNameEventId = Page.mapHelper.getDbNameEventId(eventWrapper);
								
					eventWrapper.eventGroupKey = eventWrapper.dbName + "_" + eventWrapper.event.groupId;			
					//const eventItemOld = Page.removeEventGroupList.find(element => element.eventGroupKey == eventWrapper.eventGroupKey);
					const eventItem = Page.removeEventGroupList.includes(eventWrapper.eventGroupKey);
				
					if(eventItem == false){
						
						eventWrapper.display = "block";					
						
					}else{
						eventWrapper.display = "none";
					}
					
					
					if(Page.mapHelper.eventWrapperIndexList.indexOf(dbNameEventId) == -1 && eventWrapper.display == "block"){	
										
						Page.mapHelper.eventWrapperIndexList.push(dbNameEventId)	
										
					}

				});		
			
				if(eventAllWrapper != null){
					eventAllWrapper.eventWrapperList = eventWrapperListResult
				}	
				
				//Haritaya gönderme ve object listesini doldurma işlemi yapılıyor.			
				if(eventWrapperListResult != null){
					Page.addEventToMapAndEventWrapperMap(eventAllWrapper);
				}
				
				//Olay Sayıları
				Page.totalCountEvents = eventAllWrapper.totalCountEvents;
				$("#totalCountEvents").text(Page.totalCountEvents);
				
				$("#countLoadedEvents").text(Page.mapHelper.eventWrapperIndexList.length);
				
				$("#totalCountEvents").css("display", "block");
				$("#seperatorCountEvents").css("display", "block");

			
		});
	},
    InitScroll: function (){
		var eventAlertListSize = 0;
		
		var $container = null;
		
		if(Page.liveScrollDestroyControl == true ){//yeniden initialize etmek istenildiğnde buraya gönderiliyor.
			
			$container = $('#sidebarContainer').infiniteScroll({
				
			  // options
			  	path: function() {

//					var url = contextPath + 'event/scroll?page=' + this.pageIndex;
//				  		
//			    	if (Page.time != null) {
//			    		
//			    		url = contextPath + "event/time?time=" + Page.time;							
//					}
//			    	
//			    	url = url + '&layerId='+ (Page.layerId == null ? 0 : Page.layerId);
//			    	
//					url = url + '&liveDownNormalScroll=' + false;	
//					
//					var cookieModel = Common.ReadCookies();
//					var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
//					url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;	
//					
//					
//					


					var url = contextPath + 'event/scroll?page=' + this.pageIndex;
					
					var cookieModel = Common.ReadCookies();
			  		
			    	if (Page.time != null) {
			    		
			    		url = contextPath + "event/time?time=" + Page.time;							
					}
			    	
			    	url = url + '&layerId='+ (Page.layerId == null ? 0 : Page.layerId);
			    	
			    	if (Page.lastScrollDate != null) {
						url = url + "&lastScrollDate=" + Page.lastScrollDate ;
					}
					
					
					var eventTextSearch = cookieModel.eventTextSearch;
					url = url + "&eventSearch=" + eventTextSearch;										
					
					var eventTypeIdSearch = cookieModel.eventTypeIdSearch
					if(eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "null"){	
						var eventTypeIdSearch = eventTypeIdSearch.split(',');
						if (eventTypeIdSearch == 0) {
							eventTypeIdSearch = [];
						}				
						url = url + "&eventTypeIdSearch=" + eventTypeIdSearch;
					}
					
					var eventSearchCountry = cookieModel.eventSearchCountry;
					url = url + "&eventSearchCountry=" + eventSearchCountry;
					
					var eventSearchCity = cookieModel.eventSearchCity;
					url = url + "&eventSearchCity=" + eventSearchCity;;
					
														
					
					var startDateStr = cookieModel.startDate
					var endDateStr = cookieModel.endDate
			
					startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
					endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
					
					url = url + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr;				
					
					url = url + '&liveDownNormalScroll=' + Page.liveDownNormalScroll;
					
					var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
					url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;
										
					
			  		return url;							
				},
			  	scrollThreshold: 300,
			  	elementScroll: true,
			  	checkLastPage: true,
			  	status: '.page-load-status',
			  	
			  	history: false,
			  	//debug: true,
				responseType: 'text',
			});
		
			Page.liveScrollDestroyControl = false;

		}else{// sayfa ilk açıldığında ve veritabanından getirileceğinde buraya giriyor.
			
			
			$container = $('#sidebarContainer').infiniteScroll({
		  // options
		  	path: function() {

			  	if (Page.hasNextPage && this.pageIndex != undefined){
				
					var url = contextPath + 'event/scroll?page=' + this.pageIndex;
					
					var cookieModel = Common.ReadCookies();
			  		
			    	if (Page.time != null) {
			    		
			    		url = contextPath + "event/time?time=" + Page.time;							
					}
			    	
			    	url = url + '&layerId='+ (Page.layerId == null ? 0 : Page.layerId);
			    	
			    	if (Page.lastScrollDate != null) {
						url = url + "&lastScrollDate=" + Page.lastScrollDate ;
					}
					
					
					var eventTextSearch = cookieModel.eventTextSearch;
					url = url + "&eventSearch=" + eventTextSearch;										
					
					var eventTypeIdSearch = cookieModel.eventTypeIdSearch
					if(eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "null"){	
						var eventTypeIdSearch = eventTypeIdSearch.split(',');
						if (eventTypeIdSearch == 0) {
							eventTypeIdSearch = [];
						}				
						url = url + "&eventTypeIdSearch=" + eventTypeIdSearch;
					}
					
					var eventSearchCountry = cookieModel.eventSearchCountry;
					url = url + "&eventSearchCountry=" + eventSearchCountry;
					
					var eventSearchCity = cookieModel.eventSearchCity;
					url = url + "&eventSearchCity=" + eventSearchCity;;
					
														
					
					var startDateStr = cookieModel.startDate
					var endDateStr = cookieModel.endDate
			
					startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
					endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
					
					url = url + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr;				
					
					url = url + '&liveDownNormalScroll=' + Page.liveDownNormalScroll;
					
					var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
					url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;

			  		return url;	
								
				}

			},
		  	scrollThreshold: 300,
		  	elementScroll: true,
		  	checkLastPage: true,
		  	status: '.page-load-status',
		  	
		  	history: false,
		  	//debug: true,
			responseType: 'text',
		});

		
		// ilk olayların ve sonrasında Scroll yapıldıkça yeni olayların yüklenmesini sağlar. 
		$container.on( 'load.infiniteScroll', function( event, response ) {				
			
			eventAlertListSize = 0;
			
			var eventAllWrapper = null;
			var eventWrapperListResult = null;
			if(response != null){
				
				eventAllWrapper = JSON.parse( response );
				eventWrapperListResult = eventAllWrapper.eventWrapperList;
			}	
			
			Page.isALertEventClick = Page.GetCookieIsAlertEvent();
			
			
			//eventWrapperListResult boş istek atılırken null geliyor. Veritabanından gelebilecek olaylar bittiğinde sıfır olarak geliyor.
			//Alarmlı olaylar için kontrol yapılırken olayların alarmlı olay listesine bakılıp null olmayanlar alınıyor.
			if(eventWrapperListResult != null){
				Page.lastScrollDate = eventAllWrapper.lastScrollDate;
				if(Page.firstScrollDate == null){
					Page.firstScrollDate = eventAllWrapper.firstScrollDate;
				}
				
				Page.hasNextPage = (eventWrapperListResult.length > 0);
				this.checkLastPage = Page.hasNextPage;
				
				if(Page.isALertEventClick == "true" ){
				
					eventWrapperListResult = $.grep(eventWrapperListResult, function(e){ 
					     return e.alertList != null; 
					});														
				}
			}
			

			$.each( eventWrapperListResult, function( key, eventWrapper ){
						
				if(eventWrapper.alertList != null){
					eventAlertListSize++;
					Page.loadAlertEventSize++;
				}
				
				var dbNameEventId = Page.mapHelper.getDbNameEventId(eventWrapper);
							
				eventWrapper.eventGroupKey = eventWrapper.dbName + "_" + eventWrapper.event.groupId;			
				//const eventItemeski = Page.removeEventGroupList.find(element => element.eventGroupKey == eventWrapper.eventGroupKey);
				const eventItem = Page.removeEventGroupList.includes(eventWrapper.eventGroupKey);
			
				if(eventItem == false){
					
					eventWrapper.display = "block";					
					
				}else{
					eventWrapper.display = "none";
				}
				
				
				if(Page.mapHelper.eventWrapperIndexList.indexOf(dbNameEventId) == -1 && eventWrapper.display == "block"){	
									
					Page.mapHelper.eventWrapperIndexList.push(dbNameEventId)	
									
				}

			});		
			
			if(eventAllWrapper != null){
				eventAllWrapper.eventWrapperList = eventWrapperListResult
			}		
			
			
				

			var oldLastIndex = Page.sidebarLastIndex; 
			
			Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex + 10, Page.mapHelper.eventWrapperIndexList.length);	
			Page.sidebarPrevIndex = Math.max(Math.min(Page.sidebarLastIndex - Page.sidebarMaxEventCount, Page.mapHelper.eventWrapperIndexList.length), 0);
			
			if(oldLastIndex == Page.sidebarLastIndex && !Page.hasNextPage){
				$container.infiniteScroll('destroy');
				Page.liveScrollDestroyControl = true;//destroy olduktan sonra yeniden listeleri yüklerken bboş istek atılacak kısmın kontrolü için
				return;
			}
			
			//console.log(eskiPrev, oldLastIndex, Page.sidebarPrevIndex, Page.sidebarLastIndex, Page.mapHelper.eventWrapperIndexList.length, eventWrapperListResult != null ? eventWrapperListResult.length:0);
			
			//Haritaya gönderme ve object listesini doldurma işlemi yapılıyor.
			
			if(eventWrapperListResult != null){
				Page.addEventToMapAndEventWrapperMap(eventAllWrapper);
			}
			
			
				

			
			if(Page.sidebarLastIndex < Page.mapHelper.eventWrapperIndexList.length){//Örneğin 50 tane olay yükleniyorsa tek bir istekde, listeden 10'ar tane doldurulacak şekilde yapılıyor ve boş istek atılıyor.
				Page.liveDownNormalScroll = false;
			}
			

			var eventIndexList = Page.mapHelper.eventWrapperIndexList.slice(Page.lastSidebarLastOrPrevIndex, Page.sidebarLastIndex);
			
			var eventList = [];
			$.each(eventIndexList, function(key, id){
				
				var event = Page.mapHelper.eventWrapperMap[id];
				eventList.push(event);
				
			});
			
			
			//sidebara ekleme.
			Page.addEventToSidebar(eventList)			
										
			
			if(!Page.hasNextPage && Page.liveDownNormalScroll){
										
				$container.infiniteScroll('destroy');
				Page.liveScrollDestroyControl = true;//destroy olduktan sonra yeniden listeleri yüklerken bboş istek atılacak kısmın kontrolü için
				return;
			}
			
			
			//Bu kısma listeden yükleneceği arka tarafa boş istek atılacağı zaman giriyor.
			if(!Page.liveDownNormalScroll)
			{
				
				if(Page.sidebarLastIndex == Page.mapHelper.eventWrapperIndexList.length && Page.hasNextPage){
					Page.liveDownNormalScroll = true;
				}
			}
	
			var sidebarItemCount = $("#sidebarContainer").children(".sidebarItem").length;
			//Aşağıya scroll yapıldığında ekrana ekdlendişkten sonra sidebarda 30' dan fazla  olay varsa yukarıdan 10 tanesi silinir.
			if(sidebarItemCount > Page.sidebarMaxEventCount){
				
				var removeCount = sidebarItemCount - Page.sidebarMaxEventCount;
				$('#sidebarContainer').children(".sidebarItem").slice(0, removeCount).remove();					
			}
			
			
								
			var cookieVal = Page.GetCookieIsAlertEvent();
			if(cookieVal == "true" && eventAlertListSize == 0 && !Page.liveScrollDestroyControl){
				
				$container.infiniteScroll('loadNextPage');	
				
			}
			
			if(cookieVal == "true" && Page.loadAlertEventSize < 10 && !Page.liveScrollDestroyControl){
				
				$container.infiniteScroll('loadNextPage');
				
			}
				
				
			
			Page.mapHelper.displayBlockEventCount();

			
			if(Page.eventCount < 10  && cookieVal != "true" && !Page.liveScrollDestroyControl){
				
				if(Page.notNullEventGroupCount != Page.eventGroupList.length){
					$container.infiniteScroll('loadNextPage');				
				}	
			}else{
			
				if(Page.eventCountPrev == Page.eventCount && Page.liveDownNormalScroll && !Page.liveScrollDestroyControl){//30 da sabit kalınca saçmalaya başlıyor.		
					$container.infiniteScroll('loadNextPage');	
				}	
			}
						
			Page.eventCountPrev = Page.eventCount;
			
			Page.lastSidebarLastOrPrevIndex = Page.sidebarLastIndex;
			
			
			// olay sayısı bilgileri
			Page.countLoadedEvents = Page.mapHelper.eventWrapperIndexList.length
			$("#countLoadedEvents").text(Page.countLoadedEvents);
			
			
			if(Page.goToLastEvent){
				
				Page.GoToLastEvent();
			}
		
		});
		
		
		Page.infiniteScrollContainer = $container; 
		Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		}

		
			
			
			
		$(function () {
		    var $sidebarWin = $("#sidebarContainer");
		
		    $sidebarWin.scroll(function () {
				
		        if ($sidebarWin.scrollTop() == 0 && Page.sidebarPrevIndex > 0 && $("#sidebarContainer > .sidebarItem").length > 0){
		        
		        	// birden fazla tetiklenme olması diye kullanılır
		        	$("#sidebarContainer").scrollTop( 10 );
		
					// yukarıya scroll yapıldığında çekilen listede filtreye uygun event yoksa tekrar scroll yapılması gerekiyor.
					
					Page.liveDownNormalScroll = false;//yukarıya scroll yapıp sonrasında aşağıya scroll yapıldığında boş istek atsın diye kontrol amaçlı kullanılıyor.
					var sidebarId = "#" + $("#sidebarContainer div:first-child").children("div").attr("id");
					
					
					Page.lastSidebarLastOrPrevIndex = Page.sidebarPrevIndex;
					
					Page.sidebarPrevIndex = Math.max(Page.sidebarPrevIndex - 10, 0);	
						
					if(Page.sidebarLastIndex - Page.sidebarPrevIndex >= Page.sidebarMaxEventCount){
						
						Page.sidebarLastIndex = Math.min(Page.sidebarPrevIndex + Page.sidebarMaxEventCount, Page.mapHelper.eventWrapperIndexList.length);	
						
					}else{
												
						Page.sidebarLastIndex = Math.max(Page.sidebarLastIndex - 10, 0);	
					}
										
					
					var eventIndexList = Page.mapHelper.eventWrapperIndexList.slice(Page.sidebarPrevIndex, Page.lastSidebarLastOrPrevIndex);

					var eventList = [];
					$.each(eventIndexList, function(key, id){
						
						var event = Page.mapHelper.eventWrapperMap[id];
						eventList.push(event);
					});
					
					eventList = eventList.reverse()
					
					
					Page.addEventToSidebar(eventList, true)
	
					
					var sidebarItemCount = $("#sidebarContainer").children(".sidebarItem").length;
					//Yukarıya scroll yapıldığında ekrana ekdlendikten sonra sidebarda 30' dan fazla  olay varsa aşağıdan 10 tanesi silinir.
					if(sidebarItemCount > Page.sidebarMaxEventCount){
											
						$('#sidebarContainer').children(".sidebarItem").slice(Page.sidebarMaxEventCount, sidebarItemCount).remove();
					}
					
//					//destroydan dolayı
					if(Page.liveScrollDestroyControl == true){
						Page.liveDownNormalScroll = false;
												
						// Destroydan sonra tekrar scroll kullabilmek için initialize etmek gerekiyor.
						//en aşağıya scroll sonra en üste gelip tekrar aşağıya scroll yapınca çalışmıyor.
						
						Page.InitScroll();					
					}
					
					if(Page.sidebarPrevIndex != Page.mapHelper.eventWrapperIndexList.length -1){//listenin sonuna gelindiğinde sürekli olarak en alta scroll yapmasın diye.
						$('#sidebarContainer').scrollTo(sidebarId);
					}
					
					

				}
				
		
		    });
		});
		
			
    },

	addEventToMapAndEventWrapperMap: function(eventAllWrapper){
		
		var eventWrapperList = eventAllWrapper.eventWrapperList
		$.each( eventWrapperList, function( key, eventWrapper ){
			

			var dbNameEventId = Page.mapHelper.getDbNameEventId(eventWrapper);							
			
			if(Page.mapHelper.eventWrapperMap[dbNameEventId] == undefined){//Aynı liste tekrar geliyor. Marker oluşmadan önceki hali setlenmiş oluyor.
				
				Page.mapHelper.addSidebarEventWrapper(eventWrapper);//object listesi dolduruluyor.
			}
			
			if(Page.mapHelper.eventWrapperMap[dbNameEventId].marker == undefined){
				
				Page.mapHelper.addEventToMap(eventWrapper);	//haritaya ekleniyor.
			}
			
			if(Page.lastEventId < eventWrapper.event.id){
	    		Page.lastEventId = eventWrapper.event.id						
	    	}
			
		 });
	
		if(Page.lastEventIdMap == null){
			Page.lastEventIdMap = eventAllWrapper.lastEventIdMap;
		}
		
		$.each( eventAllWrapper.lastEventIdMap, function( dbName, lastId ) {
			
			if(Page.lastEventIdMap[dbName] < lastId){
				Page.lastEventIdMap[dbName] = lastId;
			}				
		})
		
		
	    Page.UpdateDate();
	
	
		
	},
	
	addEventToSidebar: function(eventWrapperList, isPrepend = false){
		
		var sidebarHtmlItemList = "";
		$.each( eventWrapperList, function( key, eventWrapper ){
			
			if(isPrepend == true){
					
				sidebarHtmlItemList = Page.mapHelper.addEventToSidebarHTML(eventWrapper) + sidebarHtmlItemList
				
			}else{
				
				sidebarHtmlItemList = sidebarHtmlItemList + Page.mapHelper.addEventToSidebarHTML(eventWrapper)
				
			}
			
		});
		
        if(isPrepend == true){
			$("#sidebarContainer").prepend(sidebarHtmlItemList);
		}else{
			$("#sidebarContainer").append(sidebarHtmlItemList);
		}
		
	},

	Delay: function(size = 10){ // size:50000 olduğunda 1 sn civarında delay sağlar. test için kullanılabilir
		console.log(new Date());
		var a = 0;
		var b = 1;
		
		var list = [];
		var sortedList = [];
		while(a < size){
		    a++;
		    list.push(Math.random());		    
		}
		
		
		for(var i = 0 ; i < list.length; i++){
			var min = 100;
			var index = 0;
			for(var j = i ; j < list.length; j++){
				
				var item = list[j];
				if(min > item){
					min = item;
					index = j;
				}				
			}
			var itemIth = list[i];
			var itemJth = list[index];
			list[i] = itemJth;
			list[index] = itemIth;
		}
		
		
	},	

	
    AddMapControlBoxWhenReady: function () {
    	
		Page.mapHelper.addMapControlBox(Page.tileLayerList, Page.mapHelper.eventLayerGroupList);
		Page.LoadGeoLayers();
    },
    ChooseEvent : function (sidebarId){
	    $(".modal-dialog").draggable({
		  cursor: "move",
	      "handle":".modal-header"
	    });
    	Page.mapHelper.eventOnClick(sidebarId);
    },
  
 // Sadece refresh anında çalışır, scroll veya initial load esnasında çalışmaz. sidebardaki eventler scrollloader ile dolar
    LoadEvents: function () {

    	if(Page.lastEventIdMap == null){
			return;
		}
		
    	Page.loadEventRunning = true;
		
		var cookieModel = Common.ReadCookies();
		
    	var data = {};
    	data.lastEventIdMap = Page.lastEventIdMap;
    	data.layerId = Page.layerId;
		data.eventSearchText = cookieModel.eventTextSearch;
		data.firstScrollDate = Page.firstScrollDate;
		var eventTypeIdSearch = cookieModel.eventTypeIdSearch;
		if (eventTypeIdSearch == "null" || eventTypeIdSearch == "") {
			eventTypeIdSearch = null;
		}
		if(eventTypeIdSearch != null && eventTypeIdSearch != ""){	
			eventTypeIdSearch = eventTypeIdSearch.split(',');
			if (eventTypeIdSearch == 0) {
				eventTypeIdSearch = [];
			}				
		}
		data.eventTypeIdSearch = eventTypeIdSearch;
		data.eventSearchCity = cookieModel.eventSearchCity;
		data.eventSearchCountry = cookieModel.eventSearchCountry;
		data.eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
		
		var startDateStr = cookieModel.startDate;
		var endDateStr = cookieModel.endDate;

		startDateStr = moment(startDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		endDateStr = moment(endDateStr, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		
		data.startDateStr = startDateStr;
		data.endDateStr = endDateStr;
					

		$.ajax({ type: "POST",   
		     url: "/event/refresh",   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventAllWrapper)
		     {
			
				eventWrapperList = eventAllWrapper.eventWrapperList;			
				eventAlertListSize = 0;
				Page.isALertEventClick = cookieModel.alertChecked;
				
				
				
				//Alarmlı olaylar için kontrol yapılırken olayların alarmlı olay listesine bakılıp null olmayanlar alınıyor.
				if(eventWrapperList != null && Page.isALertEventClick == "true" ){
					
					eventWrapperList = $.grep(eventWrapperList, function(e){ 
					     return e.alertList != null; 
					});														
				}
				
			
			

		    	 if (eventWrapperList != undefined && eventWrapperList.length > 0){
								
					//refresh ile yeni bir olay geldiğinde ekrana direkt olarak gönderilmeyecek. YUkarıya scroll yapıldığında gösterilecek.
		    		var count = 0;	
					eventWrapperList = eventWrapperList.reverse();					
	    		    $.each( eventWrapperList, function( key, eventWrapper ) {
						
						eventWrapper.eventGroupKey = eventWrapper.dbName + "_" + eventWrapper.event.groupId
						//const foundOld = Page.removeEventGroupList.find(element => element.eventGroupKey == eventWrapper.eventGroupKey);
						const found = Page.removeEventGroupList.includes( eventWrapper.eventGroupKey);
						
						if(eventWrapper.alertList != null && found == false){//alarmlı bölgeye olay düştüğünde olay grup filtresine göre toast çıkarmak için kontrol eklendi.
							Page.mapHelper.checkAlert(eventWrapper);
							
							//okunmamış alarmlı olay sayısı
							var str = $(".alert-events").find(".alertReadCount").text();
							var alertEventCount = parseInt(str)+ eventWrapper.alertList.length;
							$(".alert-events").find(".alertReadCount").text(alertEventCount);//+1							
							$("#sidebarAlertReadCount").text(alertEventCount)
						}

						
						if(found == false){//eventWrapper
							eventWrapper.display = "block";	
							count++;						
						}else{
							eventWrapper.display = "none";
						}
						
						eventWrapper.refreshWithNewEvent = "true";
						
						if(Page.mapHelper.eventWrapperIndexList.indexOf(Page.mapHelper.getDbNameEventId(eventWrapper)) == -1 && eventWrapper.display == "block"){

							Page.mapHelper.eventWrapperIndexList.unshift(Page.mapHelper.getDbNameEventId(eventWrapper))//ekranda gösterilecek olanlar sadece bu liste de tutuluyor.											
						}
	    		    });					
					
					//indeks ayarlama ve ekrana gönderme kısmı.
					Page.addEventToMapAndEventWrapperMap(eventAllWrapper);					
					var sidebarScrollTop = $("#sidebarContainer").scrollTop();
					
					if(Page.sidebarPrevIndex != 0){
								
						Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex + count, Page.mapHelper.eventWrapperIndexList.length);
						Page.sidebarPrevIndex = Math.min(Page.sidebarPrevIndex + count, Page.mapHelper.eventWrapperIndexList.length);
						
					}else{
						
						//Yeni gelen olaylar listenin en başına eklenir. Scroll yukarıdaysa eğer sidebara prepend ile ekleme yapılır.
						//Scroll yukarıda değilse liste içinde bir yerlerdeyse listeye yine eklenir fakat sidebara gönderilmez.
						
						Page.sidebarPrevIndex = 0;						
						Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex + count, Page.mapHelper.eventWrapperIndexList.length);
											
						var eventIndexList = Page.mapHelper.eventWrapperIndexList.slice(Page.sidebarPrevIndex, count);			
						var eventList = [];
						$.each(eventIndexList, function(key, id){
							
							var event = Page.mapHelper.eventWrapperMap[id];
							eventList.push(event);
							
						});
						
						eventList = eventList.reverse();
						//sidebara ekleme.
						Page.addEventToSidebar(eventList, true);
						
						var sidebarItemCount = $("#sidebarContainer").children(".sidebarItem").length;
						
						if(sidebarItemCount > Page.sidebarMaxEventCount){	
							
							$("#sidebarContainer").scrollTo(sidebarScrollTop);
							$('#sidebarContainer').children(".sidebarItem").slice(Page.sidebarMaxEventCount, sidebarItemCount).remove();	
														
							var removeCount = sidebarItemCount - Page.sidebarMaxEventCount;
							Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex - removeCount, Page.mapHelper.eventWrapperIndexList.length);			
						}
						
					}
					
					Page.lastSidebarLastOrPrevIndex = Page.sidebarLastIndex;
					
					
					Page.countLoadedEvents = Page.mapHelper.eventWrapperIndexList.length
					$("#countLoadedEvents").text(Page.countLoadedEvents);
					
					Page.totalCountEvents = Page.totalCountEvents + eventWrapperList.length;
					$("#totalCountEvents").text(Page.totalCountEvents);
					
		    	 }
		    	 
			     Page.loadEventRunning = false;

		     }
		});
		
    	
    },    
    RefreshPage: function () {

    	// eski tarihli sorgularda refresh yapılmaz
    	if(Page.time != null ){
    		return;
    	}
    	
    	// Bir sorgu çalışıyor ise yenisi gönderilmez
    	if(Page.loadEventRunning == false){
    		Page.LoadEvents();
    	}
    	
    	setTimeout(function() {
        	Page.RefreshPage();
        }, Page.pageAutoRefreshTimeInterval)
    },
	
	


    LoadGeoLayers: function () {
    	
    	var url = '/component/geolayer';
    	
    	var data = {};
    	data.layerId = Page.layerId;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(geoList)
		     {
		    	 Page.mapHelper.addGeoLayer(geoList);
		    	 
		    	 Page.UpdateDate();
		     }
		});
   	
    },
   
    LoadMapAreas: function () {
    	
    	var url = '/component/mapArea';
    	
    	var data = {};
    	data.layerId = Page.layerId;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(mapAreaList)
		     {
		    	 
			     $.each( mapAreaList, function( key, mapArea ) {

			    	 Page.mapHelper.addMapArea(mapArea);
			     });
			     Alert.Init(Page.mapHelper, Page.mapHelper.addAlertArea);
		     }
		});
   	
    },
    LoadTileServers: function () {
    	
    	var url = '/component/tileServer';

		$.ajax({ type: "GET",   
		     url: url,   
		     async: true,
		     success : function(tileServerList)
		     {
		    	 Page.tileServers = tileServerList;
		    	 Page.tileLayerList = Page.mapHelper.addTileServers(Page.tileServers);

		    	 Page.AddMapControlBoxWhenReady();
		     }
		});
   	
    },

	LogOut: function(){
		$( ".logout" ).click(function() {
  			localStorage.removeItem("layerItemSelected");

			$.removeCookie('isALertEventClick');
			$.removeCookie('coordinateInfo');
			
			$.removeCookie('eventSearchText');
			$.removeCookie('removeEventGroups');
			
		});
	},

    ModalShowInMapClick: function (btn){

		//Olaylara basıldığında açılan modal için çağıralacak method.
		if(HeatMap.isHeatmap){
			HeatMap.showInMap($(btn).data("id"));
		}else{
			Page.mapHelper.showInMap($(btn).data("id"));
		}
    		
    },
	ToggleButtonChangeForState: function(a){
		var eventWrapper  = Page.mapHelper.eventWrapperMap[a];
		var key = eventWrapper.dbName+eventWrapper.event.id;
		Page.mapHelper.eventWrapperMap[key].event.state = Page.mapHelper.eventWrapperMap[key].event.state ? false : true;
		var id = eventWrapper.event.id;
		
		Common.StateChanged(id)
	},
    OpenDetailModal: function (eventWrapper){
		
	
		//#regin - state toggle button
		$(".eventDetailModalStateToggleButton ").removeAttr("onChange")
		if(eventWrapper.event.state){
			$('#state-toggle-button').bootstrapToggle('on')
			$('#state-toggle-button').bootstrapToggle('enable');
		}else{
			$('#state-toggle-button').bootstrapToggle('off')
		}
		
		//default db den gelmeyen olaylar da görünmeyecek.
		if(eventWrapper.dbName !== Page.defaultDbName){
			
			$('.eventDetailModalStateToggleButton ').css("display","none");
		}
		else{
			
			var key = eventWrapper.dbName+eventWrapper.event.id;
			$(".eventDetailModalStateToggleButton").attr("onChange", "Page.ToggleButtonChangeForState('"+key+"')")
		}
		//#endregin - state toggle button

    	
		//Okundu bilgisi icon değiştirme işlemleri
		var sidebarIdIconClose = "#"+eventWrapper.event.sidebarId+" .readStateIconClose";
		var sidebarIdIconOpen = "#"+eventWrapper.event.sidebarId+" .readStateIcon";

		$(sidebarIdIconClose).removeClass();	
		$(sidebarIdIconOpen).addClass("alertEventIcon");	
		$(sidebarIdIconOpen).css("visibility","visible");
		$(sidebarIdIconOpen).css("color","green");
		
		var lastEventId = $.cookie("lastEvent");
		if(lastEventId === eventWrapper.dbName+eventWrapper.event.id){
			$("#lastEventChooseIcon").css("visibility", "visible");
		}else{
			$("#lastEventChooseIcon").css("visibility", "hidden");
		}
		
			
		var alertEventList = eventWrapper.alertList;
		var alertIds = [];
		var alertEventIds = [];
		 $.each( alertEventList, function( key, alertListItem ) {					
			if(alertListItem.alertId != null){
				
				alertIds.push(alertListItem.alertId);
				alertEventIds.push(alertListItem.id);
				
			}	
													
	    });

		//Alarm okunma sayısı
		if(alertEventList !=null && alertEventList[0].readState == false){		
			
			var str = $(".alert-events").find(".alertReadCount").text();
			var count = parseInt(str)-alertEventList.length;
			$(".alert-events").find(".alertReadCount").text(count);//-1
			$("#sidebarAlertReadCount").text(count);
			alertEventList[0].readState = true;			
		}
		
		Common.AlertList(alertIds, alertEventIds)
								
    	var myModal = $('#myModal');

    	var event = eventWrapper.event;
    	var mediaList = eventWrapper.mediaList;
    	var tagList = eventWrapper.tagList;
    	  		
    	myModal.find(".card-title-sub").text(event.location);
    	myModal.find(".card-title").text(event.title);
    	myModal.find(".card-text").text(event.spot);
		myModal.find(".card-text-description").html(event.description);
    	myModal.find(".text-time").text(CustomFormatter.GetDatePrettyFormatted(event.eventDate)+ " - " +CustomFormatter.GetDateFormatted(event.eventDate));
    	myModal.find(".text-location").text(CustomFormatter.ConvertDMS(event.latitude, event.longitude));
    	myModal.find(".show-map").data("id", eventWrapper.dbName + event.id);
    	myModal.find(".modal-text-icon").attr("src", event.sidebarSrc);
		
		
		var eventGroup = Common.eventGroupList.find(f => f.dbName + "_" +f.id == eventWrapper.eventGroupKey);
		
		myModal.find(".modal-text-icon").attr("data-event-group-id", eventGroup.id);
		myModal.find(".modal-text-icon").attr("data-event-db-name", eventGroup.dbName);
			
		if(eventGroup.description != null && eventGroup.description != ""){
			
			myModal.find(".modal-text-icon").attr("data-toggle", "tooltip-modal-marker");			
			myModal.find(".modal-text-icon").attr({
			    title: lang.props['label.view.detail'],
				'data-original-title':lang.props['label.view.detail'],
			});			
			
			$('[data-toggle="tooltip-modal-marker"]').tooltip({
				placement: 'left',
				trigger: 'hover',
				container: 'body',
				boundary: 'window',
			});
			
		}else{
			myModal.find(".modal-text-icon").removeAttr("data-toggle");
			myModal.find(".modal-text-icon").removeAttr("data-original-title");	

		}
    	myModal.find(".source").attr("href", event.reservedLink);
		myModal.find(".eventGroup").text(event.groupName);
		
		$(".modal-text-icon").attr("title",event.eventTypeName);
		myModal.find(".alert-area").hide();		
			
		if(alertEventList != null ){						
			myModal.find(".alarmIconModal").css("visibility","visible");
		}else{
			myModal.find(".alarmIconModal").css("visibility","hidden");
		}
    	
		if(event.reservedLink == "" || event.reservedLink == null){
			$(".source").removeAttr("href");
		}
		
		var mediaListStr = JSON.stringify(mediaList);
		$("#choosePictures").attr("onClick","ImageCompare.ChoosePictures('"+mediaListStr+"')");

    	myModal.find(".media-area").html("");
    	myModal.find(".media-area").append('<div class="eventDetailModalMediaArea"></div>')


    	if(mediaList == null){
    		myModal.find(".media-area").hide();
    	}else{
    		myModal.find(".media-area").show();
    		
    		$.each( mediaList, function( key, media ) {
    			
    			if(media.isVideo){ 
					var videoTag =  '<video class="modal-media" preload="none" controls style="display: block; width: 100%;">' +					
										'<source src="'+contextPath+'video/get/'+media.path+'" type="video/mp4" />' +
										'Your browser does not support the video tag.' +
									'</video>';
					
					myModal.find(".media-area").append(videoTag);
    			}else{
    				
    				let mediaPath = contextPath + 'image/get/' + media.path;
    				if(media.path.startsWith("http")){
    					mediaPath = media.path;
    				}

					var fancyboxHtmlItem = 

					'<div data-responsive="" data-src="'+mediaPath+'" data-sub-html="">'
	                +'<img class="img-responsive" src="'+mediaPath+'">'
	                +'</div>';

					myModal.find(".eventDetailModalMediaArea").append(fancyboxHtmlItem)
					//FancyBox//
    			}
    			
    		});
    	}
    	
    	
    	if(tagList == null){
    		myModal.find(".tags-area").hide();
    	}else{
    		myModal.find(".tags-area").show();
    		myModal.find(".tagcloud").html("");
    		$.each( tagList, function( key, tag ) {
    			myModal.find(".tagcloud").append('<a href="#">'+tag.tagName+'</a>');
    		});	
    	}
    	
    	myModal.modal('show');
    	
    	myModal.on('hidden.bs.modal', function () {
    		$.each($("video"), function (key, video){
    			video.pause();
    		});	  	
		})
		
		$(".eventDetailModalMediaArea").removeAttr("id");	
		Common.EventImageLightBox();
	
    },
	GetCookieIsAlertEvent: function(){
		return $.cookie("isALertEventClick")
	},    

	

	EventGroupMarker: function(btn){
		event.preventDefault();
		
		var eventGroupId = parseInt($(btn).attr("data-event-group-id"));
		var eventGroupDName = $(btn).attr("data-event-db-name");
		Common.KeyDetailModalOpen(eventGroupId, eventGroupDName);
		
	},
		
	onlyEventGroupSearch: function(isDefaultChoose = false){
		var urlList = [contextPathWithoutSlash ,contextPathWithoutSlash + "/heatmap/" + Page.layerId, contextPathWithoutSlash + "/timeDimension/" + Page.layerId, contextPathWithoutSlash + "/time/" + Page.layerId, contextPathWithoutSlash + "/region/" + Page.layerId, contextPathWithoutSlash + "/event-table/" + Page.layerId];
		
		$.each(urlList, function(key, url){
		    $.removeCookie('removedEventGroups', { path: url});//Olay gruplarının değerinin setlenmesi işlemi.Öncesinde cookie temizleniyor.
		});
	    
		
		$.each(Common.eventGroupCookieSaveList.removedEventGroupSelected, function(key, layerGroupKey){
			Common.SetRemovedEventGroupsCookieValue("removedEventGroupSelected", layerGroupKey)
		});
			
			
		$.each(Common.eventGroupCookieSaveList.removedEventGroupUnSelected, function(key, layerGroupKey){
			Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
		});
		
		
		if(!isDefaultChoose){
			
			window.location = "";
		}
		
	},
	
	onlyEventGroupUserSettingsSave: function(){
		
		Page.onlyEventGroupSearch(true);
		var url = "/settings/userSettingsValueSave/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupSave=true&outSideEventGroup=false";
		var eventGroupdbNameIdList = Page.GetShowEventGroupDbNameList();
		
		var list = [];
		$.each(eventGroupdbNameIdList, function(key, layerEventGroup){
			
			
			var eventGroup = Common.eventGroupList.filter(f => f.dbName + "_" +f.id == layerEventGroup);
			if(eventGroup != undefined){
				
				var eventGroup = eventGroup[0];
				var eventGroupModel = {
					"id": eventGroup.id,
					"name": eventGroup.name,
					"dbName": eventGroup.dbName
				}
				
				list.push(eventGroupModel);
			}
			
			
		});
		
		var data = {};
		data.eventGroupList = list;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
			 data: JSON.stringify(data),
		     contentType: "application/json",
		     success : function(result)
		     {				
				window.location = "";
		     }
		});
	},
	
	onlyEventGroupSearchClear: function(){
		
		var urlList = [contextPathWithoutSlash, contextPathWithoutSlash + "/heatmap/" + Page.layerId, contextPathWithoutSlash + "/timeDimension/" + Page.layerId, contextPathWithoutSlash + "/time/" + Page.layerId, contextPathWithoutSlash + "/region/" + Page.layerId, contextPathWithoutSlash + "/event-table/" + Page.layerId];
		
		$.each(urlList, function(key, url){
			$.removeCookie('removedEventGroups', { path: url});
		});
		
		window.location = "";
	},
	
	onlyEventGroupUncheck: function(){
		
		$('.wj-node-check').prop('checked', false);	
		
		function root(selectedChangeItem){
			

			for( var i = 0 ; i < selectedChangeItem.length; i++ ){
				
				var childSelectedChangeItem = selectedChangeItem[i];
								
				childSelectedChangeItem.showCheckboxes = false 
				
				
				if(childSelectedChangeItem.items != null &&childSelectedChangeItem.items.length > 0){
					child(childSelectedChangeItem.items);
				}
			}
					
		}
		
		function child(selectedChangeItem){	
							
			for( var i = 0 ; i < selectedChangeItem.length; i++ ){
				
				var childSelectedChangeItem = selectedChangeItem[i];
								
				childSelectedChangeItem.showCheckboxes = false
				
				if(childSelectedChangeItem.items != null && childSelectedChangeItem.items.length > 0){
					child(childSelectedChangeItem.items);
				}
			}			
						
		}
		
		root(Common.eventGroupTreeViewList)
		
		$.each(Common.eventGroupCookieSaveList.removedEventGroupSelected, function(key, layerGroupKey){		
			Common.eventGroupCookieSaveList.removedEventGroupUnSelected.push(layerGroupKey)
		});
		
		Common.eventGroupCookieSaveList.removedEventGroupSelected = [];
						
		$.each(Common.eventGroupCookieSaveList.removedEventGroupUnSelected, function(key, layerGroupKey){
			Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
		});
		
		var cookieVal = $.cookie("removedEventGroups");
		
		if(cookieVal != undefined){
			
			Page.removeEventGroupList = JSON.parse(cookieVal);
			
		}
		
	},	
	
	UsingDefaultSearch: function(isBtn = false, isOnlyEventGroupBackToDefaultValue = false){//isbtn aramadan gelir. diğeri olay gruplarından.

		
		//Her layer için default ayarlar cookilere setleniyor.
		$.each(Common.userSettingsListUnderLayer, function(layerId, userSettingsTypeColumn){
			
			// modaldaki buttondan gelmişse sadece current layer daki setlenir cookiye;
			if(isBtn && layerId != Page.layerId){
				
				return;
			}
			
			
			/**** */
			
			var cookieUserSettingsLayerList = $.cookie("userSettingsSetLayer");
			
			if(cookieUserSettingsLayerList != undefined && cookieUserSettingsLayerList != ''){
				
				var list = JSON.parse($.cookie("userSettingsSetLayer"));
				
				if(!list.includes(Page.layerId)){
					list.push(layerId);
				} 		
				
				var value = JSON.stringify(list);
				$.cookie("userSettingsSetLayer", value, { expires: Page.cookieExpires, path: "/" });
				
			}else{
				
				var list = [];
				list.push(Page.layerId);
				var value = JSON.stringify(list);
				$.cookie("userSettingsSetLayer", value, { expires: Page.cookieExpires, path: "/" });
			}

			
			/**** */
			var urlList = [ contextPathWithoutSlash + "/heatmap/" + layerId, contextPathWithoutSlash + "/timeDimension/" + layerId, contextPathWithoutSlash + "/time/" + layerId, contextPathWithoutSlash + "/region/" + layerId, contextPathWithoutSlash + "/event-table/" + layerId];
		
				
			
			if(!isOnlyEventGroupBackToDefaultValue){
				
				var eventSearchKey = userSettingsTypeColumn.titleAndSpotAndDescription;
				var eventSearchCity = userSettingsTypeColumn.city;
				var eventSearchCountry = userSettingsTypeColumn.country;
				var eventTypeSelected = userSettingsTypeColumn.eventTypeId;
				var alertEventFilterState = userSettingsTypeColumn.alertEvent;
				var startDate = userSettingsTypeColumn.startDate;
				var endDate = userSettingsTypeColumn.endDate;
				
				eventSearchKey = eventSearchKey != undefined ? eventSearchKey : "";
				Page.eventTextSearch = eventSearchKey;
				eventTypeSelected = eventTypeSelected != undefined ? eventTypeSelected : 0;		
				eventSearchCity = eventSearchCity != undefined ? eventSearchCity : "";
				eventSearchCountry =  eventSearchCountry != undefined ? eventSearchCountry : "";
				alertEventFilterState = alertEventFilterState != undefined ? alertEventFilterState : "false";
				startDate = startDate != undefined ? startDate : "";
				endDate = endDate != undefined ? endDate : "";
				$.each(urlList, function(key, url){
					
					$.cookie('eventSearchText', eventSearchKey, { expires: Page.cookieExpires, path: url }); 
					$.cookie('eventTypeIdSearch', eventTypeSelected, { expires: Page.cookieExpires, path: url });
					$.cookie('eventSearchCity', eventSearchCity, { expires: Page.cookieExpires, path: url }); 
					$.cookie('eventSearchCountry', eventSearchCountry, { expires: Page.cookieExpires, path: url }); 
					$.cookie('isALertEventClick', alertEventFilterState, { expires: Page.cookieExpires, path: url }); 
					$.cookie('startDate', startDate, {expires: Page.cookieExpires, path: url});
					$.cookie('endDate', endDate, {expires: Page.cookieExpires, path: url});
				 	
				});
				

				
			}
			
		});

		
		//Button ile defaul değere dönülmek istenirse sayfa refresh yapılır.
		if(isBtn){
			window.location = "";
		}
		
		
	},
	
	OnlyGetUserSettingsMapInfo: function(isBtn= false){
		
		$.each(Common.userSettingsListUnderLayer, function(layerId, userSettingsTypeColumn){
			
			var mapCoordinates = userSettingsTypeColumn.mapCoordinate;
			var mapZoom = userSettingsTypeColumn.mapZoom;
		
			if(mapCoordinates != undefined && mapZoom != undefined && mapCoordinates != "" && mapZoom != ""){
				
				var coordinates = mapCoordinates.split(",");
				
				var coordinateInfo = {
			    	latitude : coordinates[0],
			    	longitude :coordinates[1],
			    	zoom : mapZoom
				}
				if(coordinates.length != 2){
					 coordinateInfo = Common.MapSetDefaultInfo();
				}
				
				
				$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));
			}else{
				
				 Common.MapSetDefaultInfo();
			}
		})
		
		//Button ile defaul değere dönülmek istenirse sayfa refresh yapılır.
		if(isBtn){
			window.location="";
		}
		
		
	},
	
	onlygetUserSettingsEventGroup: function(isBtn=false){
		

		$.each(Common.userSettingsListUnderLayer, function(layerId, userSettingsTypeColumn){
			
			/**** */
			
			var cookieUserSettingsLayerList = $.cookie("userSettingsSetLayer");
			
			if(cookieUserSettingsLayerList != undefined && cookieUserSettingsLayerList != ''){
				
				var list = JSON.parse($.cookie("userSettingsSetLayer"));
				
				if(!list.includes(Page.layerId)){
					list.push(layerId);
				} 		
				
				var value = JSON.stringify(list);
				$.cookie("userSettingsSetLayer", value, { expires: Page.cookieExpires, path: "/" });
				
			}else{
				
				var list = [];
				list.push(Page.layerId);
				var value = JSON.stringify(list);
				$.cookie("userSettingsSetLayer", value, { expires: Page.cookieExpires, path: "/" });
			}

			
			/**** */
			
			
			var urlList = [ contextPathWithoutSlash + "/heatmap/" + layerId, contextPathWithoutSlash + "/timeDimension/" + layerId, contextPathWithoutSlash + "/time/" + layerId, contextPathWithoutSlash + "/region/" + layerId, contextPathWithoutSlash + "/event-table/" + layerId];
		
			Common.eventGroupCookieSaveList.removedEventGroupSelected = [];
			Common.eventGroupCookieSaveList.removedEventGroupUnSelected = [];
			
			var eventGroupSelected = userSettingsTypeColumn.eventGroupList;
			eventGroupSelected = eventGroupSelected != undefined ? eventGroupSelected : [];
				

			$.each(urlList, function(key, url){		
				$.removeCookie('removedEventGroups', { path: url});		
			});
				
				
			//Olay Gruplarının default değerlerinin setlenmesi işlemi.
			$.each(Common.eventGroupList, function(key, eventGroupItem){
				
				var layerGroupKey = eventGroupItem.dbName + "_" + eventGroupItem.id;
				var control = eventGroupSelected.filter(f => f.dbName + "_" +f.id == layerGroupKey);
						
				if(control.length > 0){
					Common.eventGroupCookieSaveList.removedEventGroupSelected.push(layerGroupKey);
				}else{
					Common.eventGroupCookieSaveList.removedEventGroupUnSelected.push(layerGroupKey);
				}
			});
			
			
			$.each(Common.eventGroupCookieSaveList.removedEventGroupSelected, function(key, layerGroupKey){
				Common.SetRemovedEventGroupsCookieValue("removedEventGroupSelected", layerGroupKey)
			});
				
				
			$.each(Common.eventGroupCookieSaveList.removedEventGroupUnSelected, function(key, layerGroupKey){
				Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
			});
		});
		
		if(isBtn){
			window.location = "";
		}
			
	},

	userSettingsSave: function(){//Olay grubu dışında kalanları kaydedip cookieye setleyecek.
		
		if(!Common.StartAndEndDateGreatherControl()){
			return;
		}
		
		//olay grubu dışındakileri setler cookiye;
		Common.setCookies(false);
		
		var data = {};

		var eventTextSearch = $.cookie("eventSearchText")
		if(eventTextSearch != null && eventTextSearch != ""){					
			data.titleAndSpotAndDescription = eventTextSearch;
		}										
		
		var eventTypeIdSearch = $.cookie("eventTypeIdSearch")
		if(eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "0"){					
			data.eventTypeId = eventTypeIdSearch;
		}
		
		var eventSearchCountry = $.cookie("eventSearchCountry")
		if(eventSearchCountry != null && eventSearchCountry != ""){					
			data.country = eventSearchCountry;
		}	
		
		var eventSearchCity = $.cookie("eventSearchCity")
		if(eventSearchCity != null && eventSearchCity != ""){					
			data.city = eventSearchCity;
		}
		
		var alertEventFilterState = $.cookie('isALertEventClick');
		if(alertEventFilterState != null && alertEventFilterState != ""){
			data.alertEvent = alertEventFilterState;
		}
		
		var startDate = $.cookie('startDate');
		if(startDate != null && startDate != ""){
			data.startDate = startDate;
		}
		
		var endDate = $.cookie('endDate');
		if(endDate != undefined && endDate != ""){
			data.endDate = endDate;
		}
		
		
		
		var url = "/settings/userSettingsValueSave/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupSave=false&outSideEventGroup=true";
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
			 data: JSON.stringify(data),
		     contentType: "application/json",
		     success : function(result)
		     {
				window.location = "";

		     }
		});
		
	},

	
	userSettingsDelete: function(){
				
		var url = "/settings/userSettingsDelete/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupDelete=false&outSideEventGroupDelete=true";
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     success : function(result)
		     {
				window.location = "";

		     }
		});
		
	},
	
	onlyEventGroupUserSettingsDelete: function(){
				
		var url = "/settings/userSettingsDelete/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupDelete=true&outSideEventGroupDelete=false";
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     success : function(result)
		     {
				
				window.location = "";

		     }
		});
		
	},
	
    Set: function () {

    },    
    UpdateDate: function () {

    	
    	setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
  
    	
    	$("#updateDate").text(CustomFormatter.GetDateFormatted(new Date()));
    },
	
	LogoSetUrl: function(){
		
		
		var defaultLayer = $("#navbarUl > li").children("a:first").attr("href");
		$("#logo > a").attr("href", $.trim(defaultLayer))
		
	},	
	
	GetRemoveEventGroupsCookieValue: function(){//sayfa ilk açıldığında Page.removeEventGroupList buradan doldurulacak.
		
		var cookieVal = $.cookie("removedEventGroups");
		
		if(cookieVal != undefined){
			Page.removeEventGroupList = JSON.parse(cookieVal);
		}
	},
	

	GetShowEventGroupDbNameList(){
		
		var eventGroupdbNameIdList = [];
		$.each(Page.mapHelper.eventGroupList, function(key, value){
			
			var item = value.dbName + "_" + value.id
			//const result = Page.removeEventGroupList.find(element => element.eventGroupKey == item);
			const result = Page.removeEventGroupList.includes(item);
			
			if(result == false){
				eventGroupdbNameIdList.push(item);
			}
			
		});		
		
		return eventGroupdbNameIdList;
	},
	
	GetEventGroupTree: function(){
		
		var eventGroupdbNameIdList = Page.GetShowEventGroupDbNameList();
		Common.GetEventGroupTreeData(eventGroupdbNameIdList);
	},	
	
	LastEventSet: function(btn){
		event.stopPropagation();
		
		var sidebarEventIdDbName = $(btn).data("id");
				
		$("#lastEventChooseIcon").css("visibility", "visible");
		$("#goToLastEvent").css("display", "block");
		$.cookie('lastEvent', sidebarEventIdDbName, { expires: Page.cookieExpires, path: window.location.pathname }); 
		
		Swal.fire({
			text: lang.props["label.choosed.last.event"],
			icon: 'success',
			showConfirmButton: false,
			timer: 1500
		});
	},
	
	GoToLastEvent: function(){
		
		var cookieModel = Common.ReadCookies();
		var count = cookieModel.count;

		var eventSearchText = cookieModel.eventTextSearch;
		if (eventSearchText != undefined && eventSearchText != "") {
		    count++;
		}
		
		
		if (count > 0) {
		
		    const swalWithBootstrapButtons = Swal.mixin({
		        customClass: {
		            confirmButton: 'btn btn-success',
		            cancelButton: 'btn btn-danger'
		        },
		        buttonsStyling: {
		            confirmButton: 'margin:10px;'
		        }
		    });
		
		    swalWithBootstrapButtons.fire({
		        text: lang.props["label.last.event.with.detailed.search.clear"],
		        showCancelButton: true,
		        confirmButtonText: lang.props["label.ok"],
		        cancelButtonText: lang.props["label.close"],
		        reverseButtons: true
		    }).then((result) => {
		        if (result.isConfirmed) {
		            DetailedSearch.EventSearchClear();
					Page.onlyEventGroupSearchClear();
		
		        }
		    });
			
			return;
		}
		
		
		Page.goToLastEvent = true;
		var lastEventValue = $.cookie('lastEvent');
		var isListInValue = Page.mapHelper.eventWrapperMap[lastEventValue];
		
		if (isListInValue == undefined) {
		    Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
		} else {
		
		    Page.goToLastEvent = false;
		    Page.mapHelper.showInMap(lastEventValue);
		
		}	        
	},
	
	IntroToMap: function() {
					
		const intro = introJs(); //Introjs ile tour ekleme

		var stepsLive=[
			{element:"#navbarDropdown", /*title:"HG",*/ intro:"Kullanıcıya tanımlanmış katmanlar listelenmektedir. Buradan katmanlar arası geçiş yapılabilir.", name:"LayerDropdown"},
			{element:".leaflet-control-zoom", intro:"Harita üzerinde yakınlaştırma/uzaklaştırma yapabilmeyi sağlayan butonlardır.", name:"MapZoom"},
			{element:$(".leaflet-draw-section")[0], intro:"Harita üzerinde dairesel veya farklı şekillerde alarm alanı oluşturabilmek için kullanılan butonlardır.", name:"MapDrawTools"},
			{element:$(".leaflet-draw-section")[1], intro:"Alarm alanlarının düzenlenmesi/silinmesi için kullanılan butonlardır.", name:"MapDrawEdit"},
			{element:"#alertAddForm", intro:"Harita üzerinde çizilen alarm bölgesine bilgiler girilmektedir. Burada alarm için isim, renk ve ayrılmış alanlarda ekstra bilgi girilebilmektedir."
			+"Alarm bölgesinde oluşacak olayların alarmlı olay olarak oluşması için metin arama, olay grubu ve olay türü alanları kullanılmaktadır. Bu alanlarda belirtilen bilgileri sağlayan olaylar alarm bölgesinde alarmlı olay olarak oluşacaktır.", name:"MapDrawModal"},
			
			{element:".leaflet-top.leaflet-right", intro:"Harita üzerinden alarm bölgeleri kaldırılabilir/eklenebilir. Haritanın altlığı değiştirilebilir. Coğrafik katman var ise yine bu kısımda listelenmektedir."
			+"Harita üzerinden kaldırılıp/eklenebilir. Olay grupları alanında, harita üzerinde gösterilecek olayların olay grubu bilgisine göre filtrelenebilmesi sağlanmaktadır. İşaretli olan olay gruplarına ait olaylar harita üzerinde gösterilmektedir." 
			+"Ara butonu ile filtreleme yapılabilir. Tümünü seç butonu ile tüm olay grupları seçilebilir.", name:"MapLayers"},
			
			{element:".leaflet-control-minimap", intro:"Haritanın kapsadığı alanı gösteren küçük harita.", name:"MapMinimap"},
			{element:".step-bars", intro:"Haritanın sağ tarafında bulunan, olayların listelendiği alanı açıp/kapatmak için kullanılmaktadır.", name:"HeaderBars"},
			{element:".step-live", intro:"Canlı akış butonuna basıldığında şuan görüntülenen sayfa açılmaktadır. Bu sayfada katmanlar, olaylar, olay grupları, harita altlıkları, coğrafik katman, harita alanları, son kalınan olaya gidebilme, olayları "
			+"filtreleme, alarm oluşturma/düzenleme/silme gibi işlevler bulunmaktadır.", name:"HeaderLive"},
			
			{element:".step-live-sidebar", intro:"Sidebar da olaylar günümüzden geçmişe doğru kronolojik sıra ile gösterilmektedir. Yeni bir olay geldiğinde en üstte görünmektedir."
			+"Olayların üst kısmında ki alanda canlı akışın en son güncellenme tarihi , okunmamış alarmlı olay sayısı gösterilmektedir. Kullanıcı olay detayından son kalınan olayı seçmiş ise okunmamış alarmlı olaylar ikonunun yanında "
			+"bookmark ikonu gösterilmektedir. Basıldığında son kalınan olay sidebar ve harita üzerinde seçilip gösterilir. Tüm olaylar butonu ile o anki seçili olan katman, olay grubu ve diğer arama kritelerine uygun olan tüm olaylar (admin panelde belirtilen miktar kadar) "
			+"harita ve sidebarda gösterilmektedir.Tüm olayların altında bulunan olay sayısı alanında ise o an haritada gösterilen olay sayısı ve gösterilebilecek tüm olayların sayı bilgisi verilmektedir.", name:"SidebarLive"},
			
			{element:$("div.sidebarItem")[0], intro:"Kart alanına basıldığında ilgili olay harita üzerinde seçilerek gösterilir. İkinci bir defa aynı olaya harita veya sidebar üzerinden basıldığında detay bilgileri gösterilmektedir."
			+"Kart içerisinde olayın adı, kısa açıklama, tarih olarak ne zaman gerçekleştiği, var ise link bilgisi (kaynak), alarmlı olay ise alarm ikonu ve okunma/okunmama durumunu ifade eden ikon bulunmaktadır. Olayın gerçekleştiği ülke ve şehir bilgisi gösterilmektedir."
			+"Olaya ait medyalar slider ile gösterilmektedir.  ", name:"SidebarLiveItem"},					
			
			{element:".event-step", intro:"Olaya ait detay bilgileri burada gösterilmektedir. Olayın ne zaman geçekleştiği, koordinat bilgisi, marker, olayın adı, kısa açıklaması ve detaylı açıklama bilgisi, alarmlı olay ise hangi alarm bölgesinde yer aldığı ve "
			+"medyaları gösterilmektedir. Detayı görüntülenen olay alarmlı olay ise okunmuş olarak işaretlenir. Resimleri karşılaştır butonuna basılarak olaya ait resimlerden iki tanesi seçilerek resimlerin karşıştırılması yapılmaktadır.", name:"EventModal"},
			
			
			
			{element:".detailedSearchSpan", intro:"Olayları aramak/filtrelemek için kullanılmaktadır. Basıldığında arama modalı açılmaktadır.", name:"DetailedSearchButton"},
			{element:".detailed-search-form-step", intro:"Olayları filtrelemek için kullanılmaktadır. Girilen bilgilere uygun olan olaylar harita ve sidebar üzerinde gösterilmektedir. "			
			+" Filtreye uygun olayları getirmek için ara butonu, filtreyi temizlemek için temizle butonu kullanılmaktadır. Yeni bir olay geldiğinde aranan kriterlere uygun olması durumunda sidebar ve harita üzerinde gösterilmektedir.", name:"DetailedSearchModal"},
			
			{element:"#eventSearchDetailed", intro:"Başlık, kısa açıklama ve açıklama alanına girilen kriter bu üç alandan herhangi birinde uygun olay var ise getirilir.", name:"DetailedSearchModal"},
			{element:"#eventSearchCity", intro:"Şehir alanına girilen bilgi şehir alanında aranmaktadır. Uygun veri var ise getirilir.", name:"DetailedSearchModal"},
			{element:"#eventSearchCountry", intro:"Ülke alanına girilen bilgi ülke alanında aranmaktadır. Uygun veri var ise getirilir.", name:"DetailedSearchModal"},				
			{element:".form-group.m-select2.search", intro:"Olay türü seçilmişse seçilen olay türüne ait olaylar getirilmektedir.", name:"DetailedSearchModal"},			
			{element:".alert-events", intro:"Alarmlı olay butonu seçilmişse belirtilen kriterlere uygun olan alarmlı olaylar getirilmektedir.", name:"DetailedSearchModal"},					
			{element:".leaflet-html-legend", intro:"Olay gruplarına göre filtreleme yapılabilmektedir. Seçili olan olay gruplarına ait olaylar ekranda gösterilmektedir. Olay grubuna göre filtreleme yapabilmek için gösterilmek istenen gruplar seçilir ve "
			+"arama işlemi gerçekleştirilir. Tüm olay grupları gösterilmek istenildiğinde tümünü seç butonu kullanılabilir.", name:"DetailedSearchModalTree"},
			
			{element:".step-event-table", intro:"Tüm olaylar sayfasına gitmek için kullanılır. Olaylar tablo görünümünde detaylı incelenebilir ve excel çıktısı alınabilir. Olaylar üzerinde filtre uygulanabilir.", name:"HeaderEventTable"},			
			{element:".step-timedimension", intro:"Zaman akışı sayfasına gitmek için kullanılır. Zaman akışı sayfasında olaylar seçilen tarihler aralığında belirtilen hız ve süreye göre harita ve sidebar üzerinde izlenebilmektedir.", name:"HeaderTimeDimension"},
			{element:".step-heat", intro:"Isı haritası sayfasına gitmek için kullanılır. Olayların yoğunlaştığı alanları göstermek ve incelemek amacıyla kullanılır.", name:"HeaderHeat"},
			{element:".step-event-table-view", intro:"Olayları tablo formatında gösteren sayfadır.", name:"HeaderEventTableView"},
			
			{element:".step-time", intro:"Zaman sekmesini görüntülemek için kullanılan buton.", name:"HeaderTime"},
			{element:".step-time-sidebar", intro:"Seçilen tarihden sonra gerçekleşen olaylar, geçmişten günümüze doğru kronolojik sıra ile gösterilir.", name:"SidebarTime"},
			
			{element:$('#primaryRightMenuUl .nav-item')[0], intro:"", name:"Other"},
			
			{element:".step-alarm", intro:"Alarmların listelendiği ve detay bilgilerinin gösterildiği sidebar alanına gidilebilir.", name:"HeaderAlarm"},			
			{element:".step-alarm-sidebar", intro:"Katman üzerineki alarmlar listelenmektedir. Alarm ismine göre arama yapılabilir. Kart alanına çift tıklandığında alarmın detaylı bilgileri gösterilmektedir."
			+" Alarmlar silinebilir/güncellenebilir. Başka bir kullanıcı veya kullanıcılar ile paylaşılabilir. Kart alanının alt kısmında alarmın kimin paylaştığı bilgisi gösterilmektedir.", name:"SidebarAlarm"},
			
			{element:".step-language", intro:"Dil tercih sekmesine gidilir.", name:"HeaderLanguage"},
			{element:".step-language-sidebar", intro:"Uygulamanın görüntülenmek istenen dil tercihi yapılabilir.", name:"SidebarLanguage"},

			{element:".step-key", intro:"Anahtar sekmesini açmak için kullanılan buton.", name:"HeaderKey"},
			{element:".step-key-sidebar", intro:"Katmanda bulunan olay grupları, olay türleri ve harita alanları için rehberdir. Olay gruplarının (taraflar) üzerine gelindiğinde detayını görüntüle bilgisi olan olay gruplarında açıklama bulunmaktadır. "
			+"Olay grubu üzerinde basılarak açıklamaya bakılabilir.", name:"SidebarKey"},
	    	];	    
		var stepsTimelapse = [
			{element:".leaflet-bottom.leaflet-left", intro:"Olayları zaman akşına göre izlemek için kullanılan araçtır.", name:"MapTimelapse"},
			{element:".input-group.tleft.timeDimensionDateDiv", intro:"Olaylar, seçilen başlangıç ve bitiş tarihine göre getirilmektedir.", name:"MapTimelapse"},
			{element:".row.timeDimensionGeneralDiv", intro:"Zaman akışının her adımda ilerleyeceği süre.", name:"MapTimelapse"},
			{element:".button.button-small.button-circle.button-leaf.labelTimeDimention", intro:"Tarihler ve süre seçildikten sonra arama yapılır. Arama sonucuna göre olaylar gelir. Harita ve sidebar üzerinde gösterilir.", name:"MapTimelapse"},
			{element:".leaflet-control-timecontrol.timecontrol-backward", intro:"Zaman akışında tarihi geriye sarmak için kullanılır.", name:"MapTimelapse"},
			{element:".leaflet-control-timecontrol.timecontrol-play", intro:"Zaman akışı oynatma butonu. Basıldığında seçilen değerlere göre zaman akışı çalışmaya başlar. Başlangıç tarihinden başlayarak, üzerine seçilen süre eklenerek "
			+" bu iki tarih aralığında bulunan olaylar ekranda gösterilir.", name:"MapTimelapse"},
			{element:".leaflet-control-timecontrol.timecontrol-forward", intro:"Zaman akışında tarihi ileriye sarmak için kullanılır.", name:"MapTimelapse"},
			{element:".leaflet-control-timecontrol.timecontrol-slider.timecontrol-dateslider", intro:"Zaman akışının gösterdiği zaman aralığı. Burada düğmeler ileri/geri hareket ettirilerek tarih aralığı seçilebilir.", name:"MapTimelapse"},
			{element:".leaflet-control-timecontrol.timecontrol-slider.timecontrol-speed", intro:"Zaman akışında sürenin ilerleyeceği hız bilgisi buradan ayarlanmaktadır. Seçilen hıza göre tarihler arası hızlı veya yavaş geçilir.", name:"MapTimelapse"},
			{element:".step-live-sidebar", intro:"Zaman aralığında olan olaylar burada gösterilmektedir. Günümüzden geçmişe doğru kronolojik sıra ile gösterilir.", name:"SidebarTimelapse"},
			];
		var stepsHeatmap = [
			{intro:"Olaylar, ısı haritası üzerinde gösterilmektedir. Olayların bulunguğu alanlarda ki yoğunluğa göre ısı haritasında bölgeler renklendirilir.", name:"HeatMapIntro"},
			{element:".step-live-sidebar", intro:"Olayların gösterildiği sidebar. Burada canlı akışda olduğu gibi detaylı arama kısmı ile olaylar üzerinde filtreleme yapılabilmektedir. Ekstra olarak, gösterilmek istenen olaylar başlangıç ve bitiş tarihine göre filtrelenebilmektedir."
			+"Ekranda gösterilecek toplam olay sayısı gösterilmektedir. Arama filtresine uygun olarak sayı gösterilir.", name:"SidebarHeatmap"},
			{element:"#heatMapLoadSidebarEvent", intro:"Olayları sidebar da göstermek için kullanılır. Butona basıldığında harita üzerinde hangi alan ekranda ise o kısımda bulunan olaylar sidebarda gösterilir. Olayların tamamını veya belirli bir "
			+"bölgedeki olayları görmek için harita üzerinde yakınlaştırma/uzaklaştırma yapılarak olayları göster butonuna basılır. Bölge içindeki olaylar sidebarda gösterilir.", name:"SidebarHeatmapButton"},
			];
				    
  		var detailedSearchModal = $("#detailModalSearch");
  		var alertModal = $("#myModalAlertAdd");
  		var eventModal = $("#myModal");
  		var modals = [detailedSearchModal,alertModal,eventModal];
	  		
//		var stepsVisible = [];
//		for (var i= 0; i<stepsLive.length;i++){
//			if ($(stepsLive[i].element).is(':visible')) {
//				stepsVisible.push(stepsLive[i]);
//			}
//		}
//
//		stepsVisible = stepsLive.filter(function(obj){
//			return $(obj.element).is((':visible'))
//		});
		
		var stepsLiveModified = [];
		for (var i = 0;i<stepsLive.length; i++) {
			if (!stepsLive[i].element) {
				//Tabloda event yoksa event modalı atlamak için
				// Elementler yoksa stepleri iptal edilir
				i++;
				continue;
			}
			stepsLiveModified.push(stepsLive[i])
		}
	  		
  		intro.onchange(function () {
			
			if (intro._introItems[intro._currentStep].name == "MapDrawModal") {
				alertModal.modal('show');
			}
			else {
				if(alertModal.is(':visible')) {
					alertModal.modal('hide');
				}
			}
			
			if (intro._introItems[intro._currentStep].name == "DetailedSearchModal") {
				DetailedSearch.AdvancedSearchModalOpen();
			}
			else {
				if(detailedSearchModal.is(':visible')) {
					detailedSearchModal.modal('hide');
				}
			}
				
			if (intro._introItems[intro._currentStep].name == "EventModal") {
				var eventId = $("div.sidebarItem").first().data('event-id'); // $($("div.sidebarItem")[0]).data('event-id') that also works $("div.sidebarItem").first().data('event-id')
				eventId =eventId.split('_');
				eventId = (eventId[1]+eventId[0]);
				Page.OpenDetailModal(Page.mapHelper.eventWrapperMap[eventId]);
				//;
				$(".introjs-helperLayer.introjs-fixedTooltip").css({position: "relative"});
				//eventModal.modal('show');
			}
			else {
				$(".introjs-helperLayer.introjs-fixedTooltip").css({position: ""})
				if(eventModal.is(':visible')) {
					eventModal.modal('hide');
				}
			}
				
				
			if (intro._introItems[intro._currentStep].name == "HeaderAlarm" || intro._introItems[intro._currentStep].name == "SidebarAlarm") {
				$("div.inside-sidebar").hide();
                $("div#alarm").show();
			}
			else if (intro._introItems[intro._currentStep].name == "HeaderLanguage" || intro._introItems[intro._currentStep].name == "SidebarLanguage") {
				$("div.inside-sidebar").hide();
                $("div#language").show();
			}
			else if (intro._introItems[intro._currentStep].name == "HeaderTime" || intro._introItems[intro._currentStep].name == "SidebarTime") {
				$("div.inside-sidebar").hide();
                $("div#time").show();
			}
			else if (intro._introItems[intro._currentStep].name == "HeaderKey" || intro._introItems[intro._currentStep].name == "SidebarKey") {
				$("div.inside-sidebar").hide();
                $("div#key").show();
			}
			else {
				$("div.inside-sidebar").hide();					
                $("div#news-live").show();
            }

			if(intro._introItems[intro._currentStep].name == "DetailedSearchModalTree" || intro._introItems[intro._currentStep].name == "MapLayers"){				
				$(".legend-elements").show();
			}else{
				$(".legend-elements").hide();
			}
			
			if(intro._introItems[intro._currentStep].name == "Other"
			|| intro._introItems[intro._currentStep].name == "HeaderAlarm"
			|| intro._introItems[intro._currentStep].name == "HeaderLanguage"
			|| intro._introItems[intro._currentStep].name == "HeaderKey"){			
//				$('#primaryRightMenuUl .nav-item')[0].querySelector('a[data-bs-toggle]').classList.add('show');
//				$('#primaryRightMenuUl .nav-item a[data-bs-toggle]')[0].classList.add('show')
				$('#primaryRightMenuUl .nav-item ul').css({display: "block"});
				
			} else {
				$('#primaryRightMenuUl .nav-item ul').css({display: "none"});		
			}
			
			
			promise = new Promise((resolve) => {setInterval(resolve, 150)} )				
			return promise;
		});
			
		intro.oncomplete(function(){
			$("div.inside-sidebar").hide();					
            $("div#news-live").show();
		});
			
		intro.onexit(function() {
			for (var i = 0; i<modals.length; i++){
				if(modals[i].is(':visible')) {
					modals[i].modal('hide');
				}
			}
			$("div.inside-sidebar").hide();					
            $("div#news-live").show();
		});
			
		if (window.location.pathname == contextPathWithoutSlash + "/region/" + Page.layerId) {
			intro.setOptions({
				steps:stepsLiveModified,
				nextLabel:"İleri",
				prevLabel:"Geri",
				doneLabel:"Son",
				showStepNumbers:true
			}).start()
		}
		else if (window.location.pathname == contextPathWithoutSlash + "/heatmap/" + Page.layerId) {
			intro.setOptions({
				steps:stepsHeatmap,
				nextLabel:"İleri",
				prevLabel:"Geri",
				doneLabel:"Son",
				showStepNumbers:true
			}).start()
		}
		else if (window.location.pathname == contextPathWithoutSlash + "/timeDimension/" + Page.layerId) {
			intro.setOptions({
				steps:stepsTimelapse,
				nextLabel:"İleri",
				prevLabel:"Geri",
				doneLabel:"Son",
				showStepNumbers:true
			}).start()
		}
	},
	
	CoordinatesSave: function(){
		
		var currentMapCenterPoint = Page.mapHelper.MyMap.getCenter();
		var currentMapZoom = Page.mapHelper.MyMap.getZoom();
		
		
		var data = {};
		data.mapCoordinate = currentMapCenterPoint.lat+","+currentMapCenterPoint.lng;
		data.mapZoom = currentMapZoom;
		var url = "/settings/userSettingsValueSave/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupSave=false&outSideEventGroup=false&mapInfo=true";
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
			 data: JSON.stringify(data),
		     contentType: "application/json",
		     success : function(result)
		     {
				window.location = "";

		     }
		});
		
		
	},
	
	LatLongToMgrs: function (Lat, Long)
	{ 
		if (Lat < -80) return 'Too far South' ; if (Lat > 84) return 'Too far North' ;
		var c = 1 + Math.floor ((Long+180)/6);
		var e = c*6 - 183 ;
		var k = Lat*Math.PI/180;
		var l = Long*Math.PI/180;
		var m = e*Math.PI/180;
		var n = Math.cos (k);
		var o = 0.006739496819936062*Math.pow (n,2);
		var p = 40680631590769/(6356752.314*Math.sqrt(1 + o));
		var q = Math.tan (k);
		var r = q*q;
		var s = (r*r*r) - Math.pow (q,6);
		var t = l - m;
		var u = 1.0 - r + o;
		var v = 5.0 - r + 9*o + 4.0*(o*o);
		var w = 5.0 - 18.0*r + (r*r) + 14.0*o - 58.0*r*o;
		var x = 61.0 - 58.0*r + (r*r) + 270.0*o - 330.0*r*o;
		var y = 61.0 - 479.0*r + 179.0*(r*r) - (r*r*r);
		var z = 1385.0 - 3111.0*r + 543.0*(r*r) - (r*r*r);
		var aa = p*n*t + (p/6.0*Math.pow (n,3)*u*Math.pow (t,3)) + (p/120.0*Math.pow (n,5)*w*Math.pow (t,5)) + (p/5040.0*Math.pow (n,7)*y*Math.pow (t,7));
		var ab = 6367449.14570093*(k - (0.00251882794504*Math.sin (2*k)) + (0.00000264354112*Math.sin (4*k)) - (0.00000000345262*Math.sin (6*k)) + (0.000000000004892*Math.sin (8*k))) + (q/2.0*p*Math.pow (n,2)*Math.pow (t,2)) + (q/24.0*p*Math.pow (n,4)*v*Math.pow (t,4)) + (q/720.0*p*Math.pow (n,6)*x*Math.pow (t,6)) + (q/40320.0*p*Math.pow (n,8)*z*Math.pow (t,8));
		aa = aa*0.9996 + 500000.0;
		ab = ab*0.9996; if (ab < 0.0) ab += 10000000.0;
		var ad = 'CDEFGHJKLMNPQRSTUVWXX'.charAt(Math.floor (Lat/8 + 10));
		var ae = Math.floor (aa/100000);
		var af = ['ABCDEFGH','JKLMNPQR','STUVWXYZ'][(c-1)%3].charAt(ae-1);
		var ag = Math.floor (ab/100000)%20;
		var ah = ['ABCDEFGHJKLMNPQRSTUV','FGHJKLMNPQRSTUVABCDE'][(c-1)%2].charAt(ag);
		function pad (val) {if (val < 10) {val = '0000' + val} else if (val < 100) {val = '000' + val} else if (val < 1000) {val = '00' + val} else if (val < 10000) {val = '0' + val};return val};
		aa = Math.floor (aa%100000); aa = pad (aa);
		ab = Math.floor (ab%100000); ab = pad (ab);
		return c + ad + ' ' + af + ah + ' ' + aa + ' ' + ab;
	},
	
	MgrsToLatLong: function (a) 
	{
		var b = a.trim();
		b = b.match(/\S+/g);
		if (b == null || b.length != 4) return [false,null,null];
		var c = (b[0].length < 3) ? b[0][0] : b[0].slice(0,2);
		var d = (b[0].length < 3) ? b[0][1] : b[0][2];
		var e = (c*6-183)*Math.PI / 180;
		var f = ["ABCDEFGH","JKLMNPQR","STUVWXYZ"][(c-1) % 3].indexOf(b[1][0]) + 1;
		var g = "CDEFGHJKLMNPQRSTUVWXX".indexOf(d);
		var h = ["ABCDEFGHJKLMNPQRSTUV","FGHJKLMNPQRSTUVABCDE"][(c-1) % 2].indexOf(b[1][1]);
		var i = [1.1,2.0,2.8,3.7,4.6,5.5,6.4,7.3,8.2,9.1,0,0.8,1.7,2.6,3.5,4.4,5.3,6.2,7.0,7.9];
		var j = [0,2,2,2,4,4,6,6,8,8,0,0,0,2,2,4,4,6,6,6];
		var k = i[g];
		var l = Number(j[g]) + h / 10;
		if (l < k) l += 2;
		var m = f*100000.0 + Number(b[2]);
		var n = l*1000000 + Number(b[3]);
		m -= 500000.0;
		if (d < 'N') n -= 10000000.0;
		m /= 0.9996; n /= 0.9996;
		var o = n / 6367449.14570093;
		var p = o + (0.0025188266133249035*Math.sin(2.0*o)) + (0.0000037009491206268*Math.sin(4.0*o)) + (0.0000000074477705265*Math.sin(6.0*o)) + (0.0000000000170359940*Math.sin(8.0*o));
		var q = Math.tan(p);
		var r = q*q;
		var s = r*r;
		var t = Math.cos(p);
		var u = 0.006739496819936062*Math.pow(t,2);
		var v = 40680631590769 / (6356752.314*Math.sqrt(1 + u));
		var w = v;
		var x = 1.0 / (w*t); w *= v;
		var y = q / (2.0*w); w *= v;
		var z = 1.0 / (6.0*w*t); w *= v;
		var aa = q / (24.0*w); w *= v;
		var ab = 1.0 / (120.0*w*t); w *= v;
		var ac = q / (720.0*w); w *= v;
		var ad = 1.0 / (5040.0*w*t); w *= v;
		var ae = q / (40320.0*w);
		var af = -1.0-u;
		var ag = -1.0-2*r-u;
		var ah = 5.0 + 3.0*r + 6.0*u-6.0*r*u-3.0*(u*u)-9.0*r*(u*u);
		var ai = 5.0 + 28.0*r + 24.0*s + 6.0*u + 8.0*r*u;
		var aj = -61.0-90.0*r-45.0*s-107.0*u + 162.0*r*u;
		var ak = -61.0-662.0*r-1320.0*s-720.0*(s*r);
		var al = 1385.0 + 3633.0*r + 4095.0*s + 1575*(s*r);
		var lat = p + y*af*(m*m) + aa*ah*Math.pow(m,4) + ac*aj*Math.pow(m,6) + ae*al*Math.pow(m,8);
		var lng = e + x*m + z*ag*Math.pow(m,3) + ab*ai*Math.pow(m,5) + ad*ak*Math.pow(m,7);
		lat = lat*180 / Math.PI;
		lng = lng*180 / Math.PI;
		return [true,lat,lng];
	}
		

 }

$(document).ready(function () {
    
	Page.InitializeEvents();
	Page.Init();
	

});