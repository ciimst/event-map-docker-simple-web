var Page = {    
    PageText: "Ana Sayfa",
    PageUrl: "/home",
    PageKod: "home",
    PageNo: 0,    
    eventGroupList: null,
    hasNextPage: true,
    tileServers: null,
    tileLayerList: null,
    time: null,
    lastScrollDate: null,
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
	sidebarAlertList: null,
	userList: null,
	shareAlertId: 0,
	isAlertSharedClick: false,
	loadAlertEventSize: 0,
	eventCount: 0,
	eventCountPrev: 0,
	eventTextSearch: null,
	notNullEventGroupCount: 0,
	infiniteScrollContainer: null,
    Init: function () {
    	
    	Page.timeLineStartDate = paramTimeLineStartDate;
    	Page.layerId = paramLayerId;
    	
    	
    	
    	Page.time = navtime;
    	
    	Page.LoadEventGroups(); // sync
    	Page.mapHelper = new MapHelper(Page.OpenDetailModal, Page.eventGroupList);    	
    	
    	Page.LoadTileServers();
    	Page.LoadMapAreas();
    	
    	Page.InitScroll();
    	
    	Page.LoadKey();
    	Page.LoadLayers();
    	Page.LoadTimeLine();
    	Page.SetTimeLine();
	
		Page.LogOut();
		
//		Page.LoadAlerts();
		Page.AlertEventLoadControl();
		Page.AlertReadCount();
	
    	setTimeout(Page.RefreshPage, Page.pageAutoRefreshTimeInterval);	

		
//test
    },
    InitializeEvents: function () {

    	
    },
    InitScroll: function (){
		var eventAlertListSize = 0;
		
		var $container = $('#sidebarContainer').infiniteScroll({
			  // options
			  	path: function() {

				  	if (Page.hasNextPage && this.pageIndex != undefined){

						var url = '/event/scroll?page=' + this.pageIndex;
				  		
				    	if (Page.time != null) {
				    		
				    		url = "/event/time?time=" + Page.time;							
						}
				    	
				    	url = url + '&layerId='+ (Page.layerId == null ? 0 : Page.layerId);
				    	
				    	if (Page.lastScrollDate != null) {
							url = url + "&lastScrollDate=" + Page.lastScrollDate ;
						}
						
						
						var eventTextSearch = $.cookie("eventSearchText")
						if(eventTextSearch != null && eventTextSearch != ""){					
							url = url + "&eventSearch=" + $.cookie("eventSearchText");
						}
				    	
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
									
				var eventAllWrapper = JSON.parse( response );
				
				Page.lastScrollDate = eventAllWrapper.lastScrollDate;
				
				eventWrapperList = eventAllWrapper.eventWrapperList;

		    	Page.UseEvents(eventAllWrapper); 

				Page.hasNextPage = (eventWrapperList.length > 0);
				this.checkLastPage = Page.hasNextPage;
				if(!Page.hasNextPage){

					$container.infiniteScroll('destroy');
					return
				}			
																							
		
	    		$.each( eventWrapperList, function( key, eventWrapper ){
						
					if(eventWrapper.alertList != null){
						eventAlertListSize++;
						Page.loadAlertEventSize++;
					}
				});
								
				var cookieVal = Page.GetCookieIsAlertEvent();
				if(cookieVal == "true" && eventAlertListSize == 0){
					
					$container.infiniteScroll('loadNextPage');	
				}
				
				if(cookieVal == "true" && Page.loadAlertEventSize < 10){
					
					$container.infiniteScroll('loadNextPage');
				}
				
				
				//var removeEventGroups = $.cookie("removeEventGroups");
				//if(removeEventGroups != undefined && removeEventGroups != null){
					Page.mapHelper.displayBlockEventCount();
				//}
			
				if(Page.notNullEventGroupCount != Page.eventGroupList.length &&  Page.eventCount < 10){
					
					$container.infiniteScroll('loadNextPage');
				}else{
				
					if(Page.eventCountPrev == Page.eventCount){
						$container.infiniteScroll('loadNextPage');	
					}
				}				
				Page.eventCountPrev = Page.eventCount;
												
			});
			
			Page.infiniteScrollContainer = $container; 
			Page.infiniteScrollContainer.infiniteScroll('loadNextPage');
			
    },
    AddMapControlBoxWhenReady: function () {
    	
		Page.mapHelper.addMapControlBox(Page.tileLayerList, Page.mapHelper.eventLayerGroupList);
		Page.LoadGeoLayers();
    },
    ChooseEvent : function (sidebarId){
    	Page.mapHelper.eventOnClick(sidebarId);
    },
    LoadAlerts: function () {
    	Alert.Get(Page.UseAlerts);
    },
    UseAlerts: function (alertItemList) {
    	
		$.each(alertItemList, function (k, alertItem){
		
			Page.mapHelper.addAlertArea(alertItem);
		})
    },   
 // Sadece refresh anında çalışır, scroll veya initial load esnasında çalışmaz. sidebardaki eventler scrollloader ile dolar
    LoadEvents: function () {

    	if(Page.lastEventIdMap == null){
			return;
		}
		
    	Page.loadEventRunning = true;

    	var data = {};
    	data.lastEventIdMap = Page.lastEventIdMap;
    	data.layerId = Page.layerId;
		data.eventSearchText = $.cookie("eventSearchText")
		
		
		$.ajax({ type: "POST",   
		     url: "/event/refresh",   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventAllWrapper)
		     {
		    	 var lastIdMap = eventAllWrapper.lastIdMap;
				 eventWrapperList = eventAllWrapper.eventWrapperList;
		    	 if (eventWrapperList != undefined && eventWrapperList.length > 0){
									
		    		 Page.UseEvents(eventAllWrapper, true);
		    		 
	    		    $.each( eventWrapperList, function( key, eventWrapper ) {
						
						if(eventWrapper.alertList != null){
							Page.mapHelper.checkAlert(eventWrapper);
							
							//okunmamış alarmlı olay sayısı
							var str = $(".alert-events").find(".alertReadCount").text();
							$(".alert-events").find(".alertReadCount").text(parseInt(str)+ eventWrapper.alertList.length);//+1
						}
 											
	    		    });
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
	
	AlertEventLoadControl: function(){
								
		var cookieVal = Page.GetCookieIsAlertEvent();
		if(cookieVal == "true"){
			
			//$(".alert-events>.alertText").prop("value", lang.props["label.all.events"]);
			$(".alert-events").find(".alertText").text(lang.props["label.all.events"]);
		}
		$(".alert-events").on("click",function(){
				
			var cookieVal = Page.GetCookieIsAlertEvent();
							
			if(cookieVal == "true"){				
				$.cookie("isALertEventClick", false) 				 				
			}			
			else{							
				$.cookie("isALertEventClick", true) 
			}
							
			location.reload();
							
		});
				
	},
    UseEvents: function (eventAllWrapper, isPrepend){
			
		Page.isALertEventClick = Page.GetCookieIsAlertEvent();
		
		eventWrapperList = eventAllWrapper.eventWrapperList;				
		
	    $.each( eventWrapperList, function( key, eventWrapper ) {

			if(Page.isALertEventClick == "true" ){
				
				if( eventWrapper.alertList == null){	
					return;		
				}													
			}

			Page.mapHelper.addSidebarEventWrapper(eventWrapper);	    	 
	    	Page.mapHelper.addEventToMap(eventWrapper);
	    	Page.mapHelper.addEventToSidebar(eventWrapper, isPrepend);
				
									    	
	    	//Test için kullanılabilir
//	    	Page.mapHelper.checkAlert(eventWrapper.event);
			
	    	if(Page.lastEventId < eventWrapper.event.id){
	    		Page.lastEventId = eventWrapper.event.id						
	    	}
						
			//var lastIdList = new Array();
			/*
	    	Page.lastEventIdList = new Array();
			if(Page.lastEventId < eventAllWrapper.lastIdMap.DB_event){
				
				Page.lastEventId = eventAllWrapper.lastIdMap.DB_event			
				Page.lastEventIdList.push(eventAllWrapper.lastIdMap.DB_event) 
				
				console.log(eventAllWrapper.lastIdMap.DB_event)	
			}
			console.log(Page.lastEventIdList)
			
			if(Page.lastEventIdDB1 < eventAllWrapper.lastIdMap.DB1){
				
				Page.lastEventIdDB1 = eventAllWrapper.lastIdMap.DB1				
				Page.lastEventIdList.push(eventAllWrapper.lastIdMap.DB1) 
				
				console.log(eventAllWrapper.lastIdMap.DB1)		
			}				
			*/		
								
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
    LoadEventGroups: function () {
    	
    	var url = '/component/eventGroups';
    	
    	var data = {};
    	data.layerId = Page.layerId;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: false,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventGroupList)
		     {
		    	 Page.eventGroupList = eventGroupList;
		     }
		});

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
    LoadLayers: function () {
    	
    	var url = '/component/layer';
		
		var pageWidth = $("#map").width();
	
	
		$.ajax({ type: "GET",   
		     url: url,   
		     async: true,
		     success : function(layerList)
		     {
				var menuListW = {};
				let layerListResponsive = [];
				layerListResponsive[0]="";
				layerListResponsive[1]="";
				var i = 2;
											
				var list = [];
				
				$.each(layerList, function(key, value){
										
					var layerNameText = "";
					
					if(value.layerName.length >= 25){						
						layerNameText = value.layerName.substr(0, 22) + '...'
					}else{
						layerNameText = value.layerName
					}
					
					list.push({
							id: value.id,
							layerGuid: value.layerGuid,
							titleLayerName: value.layerName,
							layerName: layerNameText,
							layerId: value.layerId,
							userId: value.userId,
							isTemp: value.isTemp,							
					})					
				})					

		    	if(pageWidth  <= 770){//responsive için
		    	
					$.each(list, function(key, value){
		
						layerListResponsive[i] = value;										
						i++;

					});
					
					menuListW = {
			    		menuList : layerListResponsive,
						activeLayerId : Page.layerId,
						dropdownActiveClass : ""
							
					}					
				}else{
					
					menuListW = {
			    		menuList : list,
						activeLayerId : Page.layerId,
						dropdownActiveClass : ""						
				 	}
					
		     	}
				var isPrepend = false;
		    	$.ajax({
		    		type: "GET",
	   			 	url: '/template/navbar.html?v=' + Main.version,
		    		success : function(template)
		    		{
		    			var html = $.tmpl(template, menuListW);
		    			if (isPrepend == true){
		    				$("#navbarUl").prepend(html);
		    			}else{
		    				$("#navbarUl").append(html);
		    			}
		    		},
		    		cache: false,
		    		async: false
		    	});
			}//success end
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
			     Page.LoadAlerts();
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
    LoadKey: function(){
		
    	var url = '/component/key';
		
    	var data = {};
    	data.layerId = Page.layerId;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {
		    	 
		    	 $.get('/template/key.html?v=' + Main.version, function(template) {
		    		 var html = $.tmpl(template, result);    		         
		    		 $("#locales.key").empty().append(html);
		    	 });
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
    LoadTimeLine: function(){
		let dateArray =  (Page.timeLineStartDate.toString() ).split(".");

    	var startDate = new Date(dateArray[2], dateArray[1], dateArray[0]);//Page.timeLineStartDate
    	var currentDate = new Date();
    	
    	var startDay = startDate.getDate();
    	var currentDay = currentDate.getDate();
    	
    	var startMonth = startDate.getMonth()+1;
    	var currentMonth = currentDate.getMonth()+1;
    	
    	var startYear = startDate.getFullYear();
    	var currentYear = currentDate.getFullYear();
    	    	    	
    	var timeLineDiv = $(".timeLine");
    	timeLineDiv.empty();
    	
    	var timeLineSb = "";
    	
    	var sbTotal = "";
    	
    	var tempYear = startYear;
    	
    	while(tempYear <= currentYear){
        	var sb = "<ul class='dd_1'>";
        	
        	sb += "<li class='li_dd1' data-data='year_"+tempYear+"'>";
        	sb += "<a class='y' href='#'>" + tempYear + "</a>";
        	
        	sb += "<ul class='dd_2'>";
        	
        	tempMonth = 1;
        	// Aylar oluşturuluyor
        	for(i = (tempYear == startYear ? startMonth : 1) ; i<=(tempYear == currentYear ? currentMonth : 12); i++){
        		sb += "<li class='li_dd2' data-data='month_"+tempYear+"_"+i+"'>";
        		sb += "<a class='m' href='#'>"+CustomFormatter.GetMonthName(i-1) +"</a>";
        		sb += "<ul class='dd_3'>";

        		var daysInMonth = moment( tempYear + "-" + tempMonth, "YYYY-MM").daysInMonth() // 29
        		
        		// Günler oluşturuluyor
        		for(j = ((tempYear == startYear && tempMonth == startMonth) ? startDay : 1); j<=((tempYear == currentYear && tempMonth == currentMonth) ? currentDay : daysInMonth); j++){
        			var date = j.toString().padStart(2, '0') + "." + tempMonth.toString().padStart(2, '0') + "." + tempYear;
        		  sb += "<li class=''><a class='d' href='/time/" + date + "?layerId=" + Page.layerId + "' data-data='"+date+"'>" + j.toString().padStart(2, '0') + "</a></li>";
        		}
        		
        		sb += "</ul>";
        		
        		tempMonth += 1;
        	}
        	
        	sb += "</ul>";

        	sb += "</li>";
        	
        	sbTotal = sb + sbTotal;
        	
        	tempYear += 1;
    	}
    	
    	timeLineDiv.append(sbTotal);        
    	
    	$(".li_dd1, .li_dd2").on("click", function(e){    		
    		e.stopPropagation(); 
    		if($(this).hasClass("show"))
    			$(this).removeClass("show");
    		else
    			$(this).addClass("show");
    	});   	
    },
    SetTimeLine: function(){
      var date = Page.time != null ? CustomFormatter.ConvertToDate(Page.time) : new Date();
      
      var day = date.getDate();
      var month = date.getMonth()+1;
      var year = date.getFullYear();
      
      $("li[data-data='year_"+year+"']").addClass("show");
      $("li[data-data='month_"+year+"_"+month+"']").addClass("show");
      $("a[data-data='"+jQuery.format.date(date, "dd.MM.yyyy")+"']").addClass("color");
      
    },
    ModalShowInMapClick: function (btn){

    	Page.mapHelper.showInMap($(btn).data("id"));
    },
    OpenDetailModal: function (eventWrapper){
    	
		//Okundu bilgisi icon değiştirme işlemleri
		var sidebarIdIconClose = "#"+eventWrapper.event.sidebarId+" .readStateIconClose";
		var sidebarIdIconOpen = "#"+eventWrapper.event.sidebarId+" .readStateIcon";

		$(sidebarIdIconClose).removeClass();		
		$(sidebarIdIconOpen).css("visibility","visible");
		$(sidebarIdIconOpen).css("color","green");
		
		
			
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
			$(".alert-events").find(".alertReadCount").text(parseInt(str)-alertEventList.length);//-1
			alertEventList[0].readState = true;			
		}
		
		Page.AlertList(alertIds, alertEventIds)
								
    	var myModal = $('#myModal');

    	var event = eventWrapper.event;
    	var mediaList = eventWrapper.mediaList;
    	var tagList = eventWrapper.tagList;
    	  		
    	myModal.find(".card-title-sub").text(event.location);
    	myModal.find(".card-title").text(event.title);
    	myModal.find(".card-text").text(event.spot);
		myModal.find(".card-text-description").html(event.description);
    	myModal.find(".text-time").text(CustomFormatter.GetDatePrettyFormatted(event.eventDate));
    	myModal.find(".text-location").text(CustomFormatter.ConvertDMS(event.latitude, event.longitude));
    	myModal.find(".show-map").data("id", eventWrapper.dbName + event.id);
    	myModal.find(".modal-text-icon").attr("src", event.sidebarSrc);
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
    	
    	myModal.find(".media-area").html("");
    	
    	if(mediaList == null){
    		myModal.find(".media-area").hide();
    	}else{
    		myModal.find(".media-area").show();
    		
    		$.each( mediaList, function( key, media ) {
    			
    			if(media.isVideo){ // TODO tt=Date() kısmı kaldırılacak. test için kullanılmaktadır.
					var videoTag =  '<video class="modal-media" preload="none" controls style="display: block; width: 100%;">' +					
										'<source src="'+contextPath+'video/get/'+media.path+'?tt='+new Date().getTime()+'" type="video/mp4" />' +
										'Your browser does not support the video tag.' +
									'</video>';
					
					myModal.find(".media-area").append(videoTag);
    			}else{
    				
    				let mediaPath = contextPath + 'image/get/' + media.path;
    				if(media.path.startsWith("http")){
    					mediaPath = media.path;
    				}
    				
    				myModal.find(".media-area").append('<img class="media modal-media" src="'+mediaPath+'" alt="Card image cap">');
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
    },
	GetCookieIsAlertEvent: function(){
		return $.cookie("isALertEventClick")
	},    
	AlertList: function(alertIds, alertEventIds){
		$(".alert-area .alert-area-alertName").remove();
		
		var url = '/component/openDetailModalAlert?alertId='+alertIds+'&alertEventIds='+alertEventIds;
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     success : function(alertList)
		     {			
		    	//$('#myModal').find(".alert-area-alertName").remove();
				$.each( alertList, function( key, alertListItem ) {
					
					var myModal = $('#myModal');	
				
					if(alertListItem != null){		
						var alertListName =   alertListItem.name == null ? " " :  alertListItem.name;		
			    		myModal.find(".alert-area").show();			    		
						myModal.find(".alert-area").append(
						'<p class="alert-area-alertName"> <i class="fa fa-bell fa-1x"></i> '+alertListName +"&nbsp " +CustomFormatter.GetDateFormatted(alertListItem.createDate)+ '</p>')
												
					}
				
	   			 });				
		     }
		});
	},
	
	AddAlertToSidebar: function(sidebarAlertList){
		Page.sidebarAlertList = sidebarAlertList;
		$.each(sidebarAlertList,function(key,item){
			 
			$.each(Page.eventGroupList, function(event, eventGroupList){
				var eventGroupDbNameAndId = item.eventGroupDbName + item.eventGroupId;
				var pageEventGroupDbNameAndId = Page.eventGroupList[event].dbName + Page.eventGroupList[event].id;
				
				if(eventGroupDbNameAndId == pageEventGroupDbNameAndId)
					sidebarAlertList[key].eventGroupName = eventGroupList.name;
			});
			
			
			
		});
		
		$.ajax({
             type: "GET",
             url: '/template/alarm-item.html?v=' + Main.version,
		     success : function(template)
		     {
		    	 var html = $.tmpl(template, sidebarAlertList);		    		
		    	 $("#sidebarAlarmContainer").append(html);
		    	
		     },
		     cache: false,
		     async: false
        })	
		
	},
	
	AlertReadCount: function(){		
		
		
		$.ajax({type:"POST",
		url: "/alert/readAlarmCount?layerId=" + Page.layerId,
		async: true,
		success : function(readAlarmCount){
			$(".alert-events").find(".alertReadCount").text(readAlarmCount);
		}
			
			
		});
	},	
	ChooseAlert : function (sidebarAlarmId){
			
		if(Page.mapHelper.lastChoosenAlertId == sidebarAlarmId){			
    		Page.AlarmOnClick(sidebarAlarmId)
			Page.isAlertSharedClick = false;
    		return;
    	}
    	
    	Page.mapHelper.showInAlertMap(sidebarAlarmId);   
							 
    },	

	selectSidebarItem: function (id){

		var sidebarId = "#" + id;
		$(".selected").removeClass("selected");
		$(sidebarId).addClass("selected");
		$("#sidebarAlarmContainer").scrollTo($(sidebarId), 500, {offsettop:100});
			
    },

	AlarmOnClick: function (sidebarAlarmId) {
    		
		if(Page.isAlertSharedClick == false){
			$("#myModalAlert").modal('show');
		}
				    	
			$("#myModalAlert option").remove();
			
			var data = {};
	    	data.layerId = Page.layerId;
	    	
	    	$.ajax({ type: "POST",   
	    	     url: "/alert/get/eventType",
	    	     async: true,
			     data: JSON.stringify(data),
			     datatype: 'json',
			     contentType: "application/json",
	    	     success : function(eventTypeList)
	    	     {
					Page.OpenDetailALertModal(sidebarAlarmId, eventTypeList);
					
	    	     }			
	    	});
			
    },
	
	OpenDetailALertModal: function(id, eventTypeList){
		
		$(".alertName").removeClass("has-error");
		$(".with-errors span").remove()
		
		var alertModal = $("#myModalAlert");

		$(".sidebarAlarmEventGroup").append("<option></option>");
		$(".sidebarAlarmEventType").append("<option></option>");
		
		$("form#alertEditForm>.sidebarAlarmId").remove();
		$("form#alertEditForm>.layerId").remove();
		
		$.each(Page.eventGroupList, function(key, eventGroup){
			
			var eventGroupIdAndDbName = eventGroup.id+eventGroup.dbName;
			$(".sidebarAlarmEventGroup").append("<option data-value='"+eventGroupIdAndDbName+"' value='" + eventGroup.id+ "'  name='"+eventGroup.dbName+"'>" + eventGroup.name + "</option>");
			
		});
		
		$.each(eventTypeList, function(key, eventType){
			
			$(".sidebarAlarmEventType").append("<option data-value='"+eventType.id+"' value='" + eventType.id+ "'  name='"+eventType.id+"'>" + eventType.name + "</option>");
									
		});
				
		$.each(Page.sidebarAlertList, function(key, alertListItem){
			
			if(id == alertListItem.id){
				alertModal.find(".alertQuery").val(alertListItem.query)
				alertModal.find(".alertName").val(alertListItem.name)			
				alertModal.find(".reservedId").val(alertListItem.reservedId)
				alertModal.find(".reservedKey").val(alertListItem.reservedKey)
				alertModal.find(".reservedType").val(alertListItem.reservedType)
				alertModal.find(".reservedLink").val(alertListItem.reservedLink)
				alertModal.find("span#colorText").text(alertListItem.color)
				alertModal.find('input#colorPicker').val(alertListItem.color);	
				alertModal.find("#modalAlarmDelete").attr("data-id", alertListItem.id);
				
				$("form#alertEditForm").append('<input class="sidebarAlarmId" type ="hidden" name="id" value='+id+'>')
				$("form#alertEditForm").append('<input class="layerId" type ="hidden" name="layerId" value='+Page.layerId+'>')
								
				
				var eventGroupIdAndDbName = alertListItem.eventGroupId+alertListItem.eventGroupDbName;									
				$(".form-group>.sidebarAlarmEventGroup option[ data-value='"+ eventGroupIdAndDbName +"']").attr("selected","selected")
								
				$(".form-group>.sidebarAlarmEventType option[data-value='" + alertListItem.eventTypeId +"']").attr("selected","selected")
			}
			
		});
		
		
		document.querySelectorAll('input[type=color]').forEach(function(picker) {

  			picker.addEventListener('change', function() {
				$("span#colorText").text(picker.value)
  			});
		});
		
		$('.sidebarAlarmEventGroup').on('change', function() {
			$("form#alertEditForm>.eventGroupDbName").remove();
			var eventGroupDbName = null;
			var eventGroupDbName = $(this).find('option:selected').attr("name");			
			$("form#alertEditForm").append('<input class="eventGroupDbName" type ="hidden" name="eventGroupDbName" value='+eventGroupDbName+'>')
								
		});	
																		
	},
	
	AlertEditModal: function(){
										
	let formData = $("form#alertEditForm").serializeArray();
		var layerGuid = {};
		layerGuid.name = "layerGuid";
		layerGuid.value = formData["layerId"];
		formData.push(layerGuid);

		formData.layerId = null;
		
		if($.trim(formData[0].value) == ''){
			$(".with-errors span").remove();
			$(".alertName").addClass("has-error");
			$(".with-errors").append('<span>'+ lang.props["label.alert.name.not.null"] +'</span>');
			return;
		}else{
										
			$.post("/alert/editAlertModal", formData)
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (data) {				
				if (data) {				
					$("#myModalAlert").modal('hide');					
					toastr.success(lang.props["label.alert.edit.success"])
					location.reload();					
				}else {
					 toastr.error(lang.props["label.alert.edit.error"])
				}				
			});
		}				

	},
	AlertShare: function(id){
		Page.isAlertSharedClick = true;
						
		$("form#alertShareForm")[0].reset();
		$("#shareableUsersCheck").attr("disabled", false);
		$("#shareableUsers").attr("disabled", false); 
		
		$("#myModalAlertShare option").remove();
		var data = {};
	    data.layerId = Page.layerId;
	    	
	    	$.ajax({ type: "POST",   
	    	     url: "/alert/getAlertPermissionUser?layerId="+data.layerId,
	    	     async: true,
			     data: JSON.stringify(data),
			     datatype: 'json',
			     contentType: "application/json",
	    	     success : function(userList)
	    	     {
					$(".shareableUsers").append("<option> </option>");
					$.each(userList, function(key, user){
						
						$(".shareableUsers").append("<option data-value='"+user.id+"' value='" + user.id+ "'  name='"+user.id+"'>" + user.name + "</option>");
						
					});
							
					var myModalShare = $("#myModalAlertShare");
					
					myModalShare.modal('show');
					
					Page.userList = userList;
					
				
					$('#shareableUsers').on('change', function() {
					 	$("#shareableUsersCheck").attr("disabled", true);
					});
					
					$('#shareableUsersCheck').change(function() {
						
				        if(this.checked) {
				            $("#shareableUsers").attr("disabled", true);
				        }else{
							$("#shareableUsers").attr("disabled", false); 
						}
				          
				    });									
					
	    	     }			
	    	});     

		Page.shareAlertId = id;
		
	},

	AlertShareSave: function(){
				
		var alertId = Page.shareAlertId;
		var selectedUserId = $(".shareableUsers").children("option:selected").val();	
		var userIdList = [];	
			
		if($.trim(selectedUserId) != ''){
			userIdList.push(selectedUserId)
		}
  		
		if ($('#shareableUsersCheck').is(':checked')) {
			
			$.each(Page.userList, function(key, user){
				userIdList.push(user.id)
			})	
		}
						
		$.ajax({
               type: "POST",
               url: "/alert/addSharedAlert?alertId=" +alertId,
               data: JSON.stringify(userIdList),
               contentType: "application/json; charset=utf-8",
               dataType: "json",
               success: function (result) {
	
                  var myModalShare = $("#myModalAlertShare");
				  myModalShare.modal('hide');
				  toastr.success(lang.props["label.alert.shared.success"])
               },
			   error: function (e) {
				
		    	 toastr.error(lang.props["label.alert.shared.error"])
		       }
           });
		
	},
	
	handleEventSearch: function(event){
				
		if(event.keyCode == 13){
			Page.eventSearch();			
		}		
	},
	
	eventSearch: function(){
		
		var eventSearchKey = $("#eventSearch").val();
		Page.eventTextSearch = eventSearchKey;
		$.cookie('eventSearchText', eventSearchKey, { expires: 7, path: '/' }); // 7gün
		window.location = "";
	},
	
    Set: function () {

    },    
    UpdateDate: function () {

    	
    	setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
  
    	
    	$("#updateDate").text(CustomFormatter.GetDateFormatted(new Date()));
    }
    
}

$(document).ready(function () {
    
	Page.InitializeEvents();
	Page.Init();
	
	$("#eventSearch").val($.cookie('eventSearchText'))
	
});