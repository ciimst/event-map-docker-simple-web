var TimeDimension = {
	timeDimensionEventIndex: {},
	timeDimensionMode: null,
	timeDimensionDate: null,//moment().subtract(0, 'hour').startOf('days').format("YYYY.MM.DD HH:mm:ss")+"/"+ moment().add(1, 'days').startOf('days').format("YYYY.MM.DD HH:mm:ss"),//"2020.01.01 00:00:00/2021.01.01 00:00:00",
	periodType: "minute",
	periodTime: "5",
	oldKnobCurrentTime: "",
	newKnobCurrentTime: "",	
	timeDimensionPlayerState: false,	
	infiniteScrollTimeDimensionContainer: null,
	timedimensionScrollDestroyControl: false,
	sidebarMaxEventCount: 30,
	Init: function() {
		
	

		$(".leaflet-bar-timecontrol").css("display", "block");
		$("#live-sidebar-header").text(lang.props["label.timeslider"]);

		$("#primaryRightMenuUl li").removeClass("active");
		$("a[name*=timedimension]").parent().addClass("active");

		//detayli aramada startdate veya enddate varsa eğer timesdimension tarih aralığında bu tarihler kullanılır.
		
		var dateModel = TimeDimension.DetailedDateUsingTimeDimension();
		var startDateStr = dateModel.startDateStr;
		var endDateStr = dateModel.endDateStr;
		

		TimeDimension.TimeDimensionDateTimePicker(startDateStr, endDateStr);
		
		TimeDimension.TimedimensionInfiniteScroll();
		
		TimeDimension.TimeDimensionInEvents(startDateStr, endDateStr);

		
		
		CustomDateRangePicker.Init();
	},
	InitializeEvents: function() {


	},
	
	SetDateAndTime: function(){
		
		var locationUrlTimeDimension = contextPathWithoutSlash + "/timeDimension/"+ Page.layerId;
		var dateModel = TimeDimension.DetailedDateUsingTimeDimension();
		
		var startdate = moment(dateModel.startDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE);
		var endDate = moment(dateModel.endDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE);
					
		TimeDimension.timeDimensionDate = startdate + "/" + endDate;
		$.cookie("timeDimensionDate", TimeDimension.timeDimensionDate, { expires: Page.cookieExpires, path: locationUrlTimeDimension });
	},
	
	DetailedDateUsingTimeDimension: function(){
		//01/05/2022 16:43:11 -> startDate
		//2022.09.19 00:00:00/2022.09.20 00:00:00
		
		//TimeDimensionda kullanılan tarih aralığını setleme işlemleri yapılmaktadır.
		//Eğere detaylı arama da herhangi iir tarih arama işlemi varsa eğer bu tarihler kullanılmaktadır.
		//Sadece startDate ile arama yapılmışsa endDate bir gün sonrasına atanmaktadır.
		//Sadece endDate ile arama yapılmışsa eğer startDate bir gün öncesine atanmaktadır.
		//Eğer ikisinde de arama yoksa daha önceden timedimensiona atanmış olan değer kullanılmaktadır.
		
		//Eğer detaylı arama da,timeDimensiondate de, ayarlarda date bilgisi yok ise default olarak today atanır.
		//Eğere sadece ayarlardan değer gelmişse yine ona göre atamaları yapılır.
		
		var startDateStr = "";
		var endDateStr = "";
		
		
		if($.cookie("timeDimensionDate") !=  undefined && $.cookie("timeDimensionDate") != "") {
			
			const dates = $.cookie("timeDimensionDate").split("/");
			startDateStr = dates[0];
			endDateStr = dates[1];
	
			startDateStr = moment(startDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TURKISH);
			endDateStr = moment(endDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TURKISH);
		}
		
		
		
		var cookieStartDate = $.cookie('startDate');
		var cookieEndDate = $.cookie('endDate');
		
		if(cookieStartDate != undefined && cookieStartDate != ''){
			
			startDateStr = moment(cookieStartDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
			
			if(cookieEndDate == undefined || cookieEndDate == ''){
				
				endDateStr = moment(startDateStr, DateUtils.TURKISH).add(1, 'days').format(DateUtils.TURKISH);;
			}
		}
		
		if(cookieEndDate != undefined && cookieEndDate != ''){
			
			endDateStr = moment(cookieEndDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
			
			if(cookieStartDate == undefined || cookieStartDate == ''){
				
				startDateStr = moment(endDateStr,  DateUtils.TURKISH).subtract(1, 'day').format(DateUtils.TURKISH);
			}
		}
		
		//today
		if((cookieStartDate == undefined || cookieStartDate == '') && ( cookieEndDate == undefined || cookieEndDate == '')){
			
			startDateStr = moment().subtract(0, 'hour').startOf('days').format(DateUtils.TURKISH);//YYYY.MM.DD HH:mm:ss
			endDateStr =  moment().add(1, 'days').startOf('days').format(DateUtils.TURKISH);//YYYY.MM.DD HH:mm:ss
		}
		
		
		var dateModel = {
			"startDateStr" : startDateStr,
			"endDateStr" : endDateStr
		}
		
		return dateModel
		
	},
	
	TimeDimensionDateTimePicker: function(startDate, endDate) {

		var newDates = startDate + " - " + endDate;

		$.ajax({
			type: "GET",
			url: '/template/timeDimension.html?v=' + Main.version,
			success: function(template) {
				var data = {
					newDates: newDates
				}
				var html = $.tmpl(template, data);
				$(".leaflet-bar-timecontrol").prepend(html);
			},
			cache: false,
			async: false
		});

		$(".leaflet-bar-timecontrol").addClass("col-xl-12");
		$(".timecontrol-dateslider").addClass("col-lg-12 col-sm-12 row");
		$(".timecontrol-speed").addClass("col-lg-12 col-sm-12 row");

		if ($.cookie("periodType") != undefined && $.cookie("periodTime") != undefined) {

			var periodType = $.cookie("periodType");
			$("#periodType option[value=" + periodType + "]").attr("selected", "selected");

			var periodTime = $.cookie("periodTime");
			$("#periodTime").val(periodTime);
		}


	},


	TimeDimensionInEvents: function(startDateStr, endDateStr) {
		
		var cookieModel = Common.ReadCookies();
		var eventSearch = cookieModel.eventTextSearch;
		
		var eventTypeIdSearch = 0;
		if(cookieModel.eventTypeIdSearch != undefined){
			
			var eventTypeIdSearch = cookieModel.eventTypeIdSearch;
			eventTypeIdSearch = eventTypeIdSearch.split(',');
			if (eventTypeIdSearch == 0) {
					eventTypeIdSearch = [];
			}	
		}	else {
			eventTypeIdSearch = [];
		}	
		
		
		var eventSearchCity = cookieModel.eventSearchCity;
		var eventSearchCountry = cookieModel.eventSearchCountry;		
		var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;
		
		var url = contextPath + 'event/timeDimensionPlayback?layerId=' + Page.layerId + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr + "&eventSearch=" + eventSearch + "&eventTypeIdSearch=" + eventTypeIdSearch 
		+ "&eventSearchCity=" + eventSearchCity + "&eventSearchCountry=" + eventSearchCountry + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;

		XhrProgress(url, function () {
			
			var eventAllWrapper = JSON.parse(this.responseText);
	
			Page.lastScrollDate = eventAllWrapper.lastScrollDate;
	
			eventWrapperList = eventAllWrapper.eventWrapperList;
	
			TimeDimension.UseEvents(eventAllWrapper);//sadece haritaya ekleme ve timedimension liste tutmak için kullanılıyor.
			TimeDimension.TimeDimensionEventFilter(TimeDimension.oldKnobCurrentTime, TimeDimension.newKnobCurrentTime, 0, 1, -1, -1);

		});
	},

	TimedimensionInfiniteScroll: function() {

		var $container = null;

		$container = $('#sidebarContainer').infiniteScroll({
			// options
			path: function() { return contextPath + "event/timeDimensionScroll" },
			scrollThreshold: 300,
			elementScroll: true,
			checkLastPage: true,
			status: '.page-load-status',
			history: false,
			responseType: 'text',
		});

		if (TimeDimension.timedimensionScrollDestroyControl == true) {//en sonuncusu ekrana yüklendiğinde destroy yapıldığında üstten yeni olaylar ekrana basıldığında yeniden aşağıya doğru scroll yapabilmek için initialize etmek gerekiyor.
			TimeDimension.timedimensionScrollDestroyControl = false;

		} else {

			var thirtyEventChecks = false;
			$container.on('load.infiniteScroll', function() {

				if (!TimeDimension.timeDimensionPlayerState && Page.mapHelper.eventWrapperIndexList.length > 0) {

					//üstten silme işlemi									
					if (Page.sidebarLastIndex - Page.sidebarPrevIndex >= TimeDimension.sidebarMaxEventCount && Page.sidebarPrevIndex >= 0) {

						//ilk yüklemeden sonra sayfada scroll yapıp 30 tane olay yüklenince prevIndex değeri setlenir ve aralık içinde bulunan olaylar ekrana gönderilir.	
						Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex + 10, Page.mapHelper.eventWrapperIndexList.length);
						Page.sidebarPrevIndex = Math.min(Page.sidebarPrevIndex + 10, Page.mapHelper.eventWrapperIndexList.length);
						TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, false)

						thirtyEventChecks = true;

						//$('#sidebarContainer').children(".sidebarItem").slice(0, 10).remove();
					}
					else {


						if (thirtyEventChecks == false) {

							Page.sidebarPrevIndex = 0;

						} else {
							// en aşağıya indikden sonra yukarı scroll yapıp yeniden aşağıya scroll yaptığında prev ile last index arası 30 dan az olduğunda bu kısma geliyor.
							Page.sidebarPrevIndex = Math.min(Page.sidebarPrevIndex + 10, Page.mapHelper.eventWrapperIndexList.length);
							thirtyEventChecks = true;
						}
						//ilk 30 tane olay yüklenene kadar bu kısma geliyor.

						Page.sidebarLastIndex = Math.min(Page.sidebarLastIndex + 10, Page.mapHelper.eventWrapperIndexList.length);
						TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, false)

					}

				}
			})

			TimeDimension.infiniteScrollTimeDimensionContainer = $container;
			TimeDimension.infiniteScrollTimeDimensionContainer.infiniteScroll('loadNextPage');

		}



		$(function() {
			var $sidebarWin = $("#sidebarContainer");

			$sidebarWin.scroll(function() {

				if ($sidebarWin.scrollTop() == 0 && Page.sidebarPrevIndex > 0) {

					// birden fazla tetiklenme olması diye kullanılır
					$("#sidebarContainer").scrollTop(10);


					//var firstEventCount = $(".sidebarItem").length;

					// yukarıya scroll yapıldığında çekilen listede filtreye uygun event yoksa tekrar scroll yapılması gerekiyor.


					var sidebarId = "#" + $("#sidebarContainer div:first-child").children("div").attr("id");

					Page.sidebarPrevIndex = Page.sidebarPrevIndex - 10 > 0 ? Page.sidebarPrevIndex - 10 : 0;
					Page.sidebarLastIndex = Page.sidebarLastIndex - 10 > 0 ? Page.sidebarLastIndex - 10 : 0;

					TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, true);

					//destroydan dolayı
					if (TimeDimension.timedimensionScrollDestroyControl == true) {
						TimeDimension.TimedimensionInfiniteScroll();
					}

					if (Page.sidebarPrevIndex != Page.mapHelper.eventWrapperIndexList.length - 1) {//listenin sonuna gelindiğinde sürekli olarak en alta scroll yapmasın diye.
						$('#sidebarContainer').scrollTo(sidebarId);
					}

				}


			});
		});

	},


	TimeDimensionScrollSidebarEvent: function(prevIndex, lastIndex, isPrepend, autoScroll = false) {

		var sidebarHtmlItem = "";
		var eventList = Page.mapHelper.eventWrapperIndexList.slice(prevIndex, lastIndex);//lastIndexi seçmez.

		if (lastIndex == Page.mapHelper.eventWrapperIndexList.length || Page.mapHelper.eventWrapperIndexList.length <= 10) {//en sonuncusu yüklensiyse

			//eventList = Page.mapHelper.eventWrapperIndexList.slice(prevIndex, lastIndex+1);// son olayıda alabilmek için.
			if (TimeDimension.timedimensionScrollDestroyControl != true) {
				TimeDimension.timedimensionScrollDestroyControl = true;
				TimeDimension.infiniteScrollTimeDimensionContainer.infiniteScroll('destroy');
				isPrepend = false;//sonuncu event her zaman en alta eklenir. TODO: kontrol edilecek
			}
		} else {

			if (TimeDimension.timedimensionScrollDestroyControl == true) {
				TimeDimension.TimedimensionInfiniteScroll();
			}
		}



		$.each(eventList, function(key, value) {	//Use evente bu methoddan gittiğine dair parametre göndermek gerekir.

			var sidebarEventWrapper = Page.mapHelper.eventWrapperMap[value];
			eventId = sidebarEventWrapper.eventIdDbName;
			var item = $("div[data-event-id*=" + eventId + "]");

			if (item.length == 0) {


				//TODO: İkiside aynı silinecek
				if (isPrepend == true) {

					sidebarHtmlItem = sidebarHtmlItem + Page.mapHelper.addEventToSidebarHTML(sidebarEventWrapper);

				} else {

					sidebarHtmlItem = sidebarHtmlItem + Page.mapHelper.addEventToSidebarHTML(sidebarEventWrapper);
				}
			}
		})


		if (isPrepend == true) {
			$("#sidebarContainer").prepend(sidebarHtmlItem);

			//Listenin yukarısına olaylar yükleniyorsa ve 30 dan fazla olay varsa aşağıdan 30 dan sonrası siliniyor.
			$('#sidebarContainer').children(".sidebarItem").slice(TimeDimension.sidebarMaxEventCount, $(".sidebarItem").length).remove();


		} else {

			$("#sidebarContainer").append(sidebarHtmlItem);

			//Listenin sonuna ekleniyorsa ve 30 dan fazla olay yüklenmişse yukardan 10 tane siliniyor.
			if ($("#sidebarContainer").children(".sidebarItem").length > TimeDimension.sidebarMaxEventCount) {

				var removeCount = $("#sidebarContainer").children(".sidebarItem").length - TimeDimension.sidebarMaxEventCount;
				$('#sidebarContainer').children(".sidebarItem").slice(0, removeCount).remove();
			}

		}

		// yanlış tetiklenmenin önüne geçmek için kullanılır
		if (autoScroll == true) {
			$("#sidebarContainer").scrollTop(10);
		}


		$.each(Page.removeEventGroupList, function(key, value) {

			var sidebarItem = $("#sidebarContainer").children("#" + value.eventGroupKey);
			$(sidebarItem).remove();

		});

		//Sidear da event resimlerinin gösterilmesi
		setTimeout(function() {
    		SEMICOLON.widget.loadFlexSlider();	
    	}, 1000);
	},

	TimeDimensionSearch: function() {

		var newDate = $(".daterange2").val()
		newDate = newDate.replace(" - ", "/")

		if (TimeDimension.timeDimensionDate == newDate) {

			window.location.reload();

		} else {

			//dd.mm.yyyy -> yyyy.mm.dd formatına çevirme
			const dates = newDate.split("/");
			var startDateStr = dates[0];
			var endDateStr = dates[1];

			var resultDate = moment(startDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE) + "/" + moment(endDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE);
			
			var startDateCookie = moment(startDateStr, DateUtils.TURKISH).format(DateUtils.ENGLISH);
			var endDateCookie = moment(endDateStr, DateUtils.TURKISH).format(DateUtils.ENGLISH);
			
			var locationUrlNewLive = contextPathWithoutSlash + "/region/"+ Page.layerId;
			var locationUrlTimeDimension = contextPathWithoutSlash + "/timeDimension/"+ Page.layerId;
			var locationUrlHeatMap = contextPathWithoutSlash + "/heatmap/" + Page.layerId;
			var locationUrlTime = contextPathWithoutSlash + "/time/"+Page.layerId;
			var locationUrlEventTable = contextPathWithoutSlash + "/event-table/" + Page.layerId;
			var urlList = [locationUrlNewLive, locationUrlTimeDimension, locationUrlHeatMap, locationUrlTime, locationUrlEventTable];
			

			//PeriodType - PeriodTime
			var periodType = $("#periodType option:selected").val();

			$("#periodType option").removeAttr("selected");
			$("#periodType option[value=" + periodType + "]").attr("selected", "selected")

			var periodTime = $("#periodTime").val();

			$.when($.cookie("timeDimensionDate", resultDate, { expires: Page.cookieExpires, path: locationUrlTimeDimension }), 
				   $.cookie("periodType", periodType, { expires: Page.cookieExpires, path: locationUrlTimeDimension }), 
				   $.cookie("periodTime", periodTime, { expires: Page.cookieExpires, path: locationUrlTimeDimension }),
	
				$.each(urlList, function(key, url){
					$.cookie("startDate", startDateCookie, { expires: Page.cookieExpires, path: url });
					$.cookie("endDate", endDateCookie, { expires: Page.cookieExpires, path: url });				
				})
			).done(function() {
				window.location.reload();

			});

		}
	},

	TimeDimensionCookieControl: function() {

		var timeDimensionDate = $.cookie("timeDimensionDate");
		timeDimensionDate = timeDimensionDate.split("/");

		var startDateStr = timeDimensionDate[0].split(" ");
		var startDate = moment(startDateStr[0], 'YYYY.MM.DD').format('MM/DD/YYYY');
		var startDateControl = !/Invalid|NaN/.test(new Date(startDate).toString());
		var startTimeControl = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(startDateStr[1]);

		var endDateStr = timeDimensionDate[1].split(" ");
		var endDate = moment(endDateStr[0], 'YYYY.MM.DD').format('MM/DD/YYYY');
		var endDateControl = !/Invalid|NaN/.test(new Date(endDate).toString());
		var endTimeControl = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(endDateStr[1]);

		var periodTimeControl = /^-?[\d.]+(?:e-?\d+)?$/.test($.cookie("periodTime"));

		var periodType = $.cookie("periodType");
		var periodTypeControl = null;

		switch (periodType) {

			case "minute":
				periodTypeControl = true
				break;

			case "hour":
				periodTypeControl = true
				break;

			default:
				periodTypeControl = false
				break;
		}

		if (!startDateControl || !startTimeControl || !endDateControl || !endTimeControl || !periodTimeControl || !periodTypeControl) {
			$.when($.removeCookie("timeDimensionDate", { path: '/' }), $.removeCookie("periodType", { path: '/' }), $.removeCookie("periodTime", { path: '/' })).done(function() {
				window.location.reload();

			});

		}
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

			Page.mapHelper.addSidebarEventWrapper(eventWrapper);

			Page.mapHelper.addEventToMap(eventWrapper);

			if (TimeDimension.timeDimensionMode == true) {				
				
				var eventDateLongForIndex = eventWrapper.event.eventDate;//moment(eventWrapper.event.eventDate, "YYYY-MM-DD'T'hh:mm:ss.SSSZ").toDate().getTime();
				var availableTimes = Page.mapHelper.MyMap.timeDimension.getAvailableTimes();

				for (var i = 1; i < availableTimes.length; i++) {

					var indexDatePrev = availableTimes[i - 1];
					var indexDateNext = availableTimes[i];

					if (indexDatePrev <= eventDateLongForIndex && eventDateLongForIndex <= indexDateNext) {

						var eventIndexList = TimeDimension.timeDimensionEventIndex[i - 1];
						if (eventIndexList == null) {
							eventIndexList = [];
							TimeDimension.timeDimensionEventIndex[i - 1] = eventIndexList;
						}

						var eventIdForIndex = Page.mapHelper.getDbNameEventId(eventWrapper);
						eventIndexList.push({ "dbNameEventId": eventIdForIndex, "eventGroupName": eventWrapper.event.groupName, "eventIdDbName": eventWrapper.event.id + "_" + eventWrapper.dbName, "eventGroupKey": eventWrapper.dbName + "_" + eventWrapper.event.groupId });

						break;
					}
				}


			}

			if (Page.lastEventId < eventWrapper.event.id) {
				Page.lastEventId = eventWrapper.event.id;
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


	Set: function() {

	},

	timeDimensionOverlaysRemoveEventGroup: function(layerGroupKey) {

		Page.mapHelper.displayBlockEventCount();

		if (TimeDimension.timeDimensionMode) {

			TimeDimension.addSidebarList(Page.mapHelper.MyMap.timeDimension.getCurrentTimeIndex(), Page.mapHelper.MyMap.timeDimension._sliderTime2CurrentIndex);
		}

		if (Page.notNullEventGroupCount != Page.eventGroupList.length && Page.eventCount < 10 && TimeDimension.timeDimensionMode) {
			TimeDimension.infiniteScrollTimeDimensionContainer.infiniteScroll('loadNextPage');
		}

		setTimeout(function() {
			SEMICOLON.widget.loadFlexSlider();
		}, 1000);
	},

	timeDimensionOverlaysAddEventGroup: function(layerGroupKey) {

		Page.mapHelper.displayBlockEventCount();

		if (TimeDimension.timeDimensionMode) {

			TimeDimension.TimeDimensionEventFilter(TimeDimension.oldKnobCurrentTime, TimeDimension.newKnobCurrentTime, Page.mapHelper.MyMap.timeDimension.getCurrentTimeIndex(), Page.mapHelper.MyMap.timeDimension._sliderTime2CurrentIndex, -1, -1);
		}

		if (Page.notNullEventGroupCount != Page.eventGroupList.length && Page.eventCount < 10 && TimeDimension.timeDimensionMode) {

			TimeDimension.infiniteScrollTimeDimensionContainer.infiniteScroll('loadNextPage');
		}

		setTimeout(function() {
			SEMICOLON.widget.loadFlexSlider();
		}, 1000);
	},

	TimeDimensionEventFilter: function(date, date2, index1, index2, prevIndex1, prevIndex2) {

		var indexSmall = index1;
		var indexLarge = index2;
		if (index1 > index2) {
			var indexSmall = index2;
			var indexLarge = index1;
		}

		var prevIndexSmall = prevIndex1;
		var prevIndexLarge = prevIndex2;
		if (prevIndex1 > prevIndex2) {
			var prevIndexSmall = prevIndex2;
			var prevIndexLarge = prevIndex1;
		}

		var joinIndexList = [];


		for (var i = indexSmall; i < indexLarge; i++) {

			for (var y = prevIndexSmall; y < prevIndexLarge; y++) {

				if (i == y && joinIndexList.indexOf(i) == -1) {

					joinIndexList.push(i);
				}
			}
		}


		if (Page.mapHelper != null) {

			Page.mapHelper.eventWrapperIndexList = [];
			Page.sidebarLastIndex = 10;
			Page.sidebarPrevIndex = 0;


			//Bu kısımda çok yavaş ekstra 10sn bekliyor.
			//ilk açıldığında geliyor ve bu kısma kadar zaten herhangi bir marker veya sidebarItem eklenmemeiş oluyor.
			//Dolayısıyla silme işlemime gerek yok.
			/*if (prevIndexSmall == -1 && prevIndexLarge == -1) {
				
			

				for (var idx in TimeDimension.timeDimensionEventIndex) {

					var eventIndexList = TimeDimension.timeDimensionEventIndex[idx];

					for (idx in eventIndexList) {


						var eventIndexIdAndGroupName = eventIndexList[idx];
						var marker = Page.mapHelper.eventWrapperMap[eventIndexIdAndGroupName.dbNameEventId].marker;
						var layerGroup = Page.mapHelper.eventLayerGroupList[eventIndexIdAndGroupName.eventGroupName];
						var eventId = eventIndexIdAndGroupName.eventIdDbName;

						$("div[data-event-id*=" + eventId + "]").remove();						
						layerGroup.removeLayer(marker);
					}
				}
			}*/



			for (var i = prevIndexSmall; i < prevIndexLarge; i++) {

				if (joinIndexList.indexOf(i) == -1) {

					var eventIndexList = TimeDimension.timeDimensionEventIndex[i];

					for (idx in eventIndexList) {


						var eventIndexIdAndGroupName = eventIndexList[idx];
						var marker = Page.mapHelper.eventWrapperMap[eventIndexIdAndGroupName.dbNameEventId].marker;
						var layerGroup = Page.mapHelper.eventLayerGroupList[eventIndexIdAndGroupName.eventGroupName];
						var eventId = eventIndexIdAndGroupName.eventIdDbName;

						$("div[data-event-id*=" + eventId + "]").remove();
						layerGroup.removeLayer(marker);
					}

				}
			}

			TimeDimension.addSidebarList(index1, index2);
			
			for (var i = indexSmall; i < indexLarge; i++) {

				if (joinIndexList.indexOf(i) == -1) {

					var eventIndexList = TimeDimension.timeDimensionEventIndex[i];

					for (idx in eventIndexList) {

						var eventIndexIdAndGroupName = eventIndexList[idx];
						var marker = Page.mapHelper.eventWrapperMap[eventIndexIdAndGroupName.dbNameEventId].marker;
						var layerGroup = Page.mapHelper.eventLayerGroupList[eventIndexIdAndGroupName.eventGroupName];
						var eventId = eventIndexIdAndGroupName.eventIdDbName;
						
						var eventGroupColor = Page.mapHelper.eventWrapperMap[eventIndexIdAndGroupName.dbNameEventId].event.color;
						Page.mapHelper.addMarkerToLayerGroup(marker, eventIndexIdAndGroupName.eventGroupName, eventGroupColor);
						//Page.SetEventWrapperIndexList(eventIndexIdAndGroupName.dbNameEventId);//eklenenlerin hepsini listede tutuyoruz.				
					}

				}
			}
			
		}


	},
	addSidebarList: function(index1, index2) {

		Page.mapHelper.eventWrapperIndexList = [];
		Page.sidebarLastIndex = 10;
		Page.sidebarPrevIndex = 0;

		var indexSmall = index1;
		var indexLarge = index2;
		if (index1 > index2) {
			var indexSmall = index2;
			var indexLarge = index1;
		}

		var cookieEventGroup = $.cookie("removedEventGroups");
		//addSidebarList(index1 ,index2);
		//iki ekranda gösterilen olayların sidebarda gösterilmesi için tutulan liste dolduruluyor.
		for (var i = indexLarge - 1; i >= indexSmall; i--) {

			var eventIndexList = TimeDimension.timeDimensionEventIndex[i];

			for (idx in eventIndexList) {

				var eventIndexIdAndGroupName = eventIndexList[idx];


				if (cookieEventGroup != undefined && cookieEventGroup != null && cookieEventGroup != "[]") {
					var cookieEventGroupObjList = JSON.parse(cookieEventGroup);

					if (cookieEventGroupObjList.some(e => e.eventGroupKey === eventIndexIdAndGroupName.eventGroupKey) == false) {
						Page.mapHelper.eventWrapperIndexList.push(eventIndexIdAndGroupName.dbNameEventId);
					}


				} else {
					Page.mapHelper.eventWrapperIndexList.push(eventIndexIdAndGroupName.dbNameEventId);
				}

			}
		}


		//Sidebarda göstermek için methoda gönderiliyor.
		if (Page.mapHelper.MyMap.timeDimension._sliderTimeBackwardButtonClick != true) {
			TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, true);

		} else {

			Page.mapHelper.MyMap.timeDimension._sliderTimeBackwardButtonClick = false;
			TimeDimension.TimeDimensionScrollSidebarEvent(Page.sidebarPrevIndex, Page.sidebarLastIndex, false);
		}

		//10 taneden fazla olanlar sondan siliniyor.
		var eventCount = Page.mapHelper.eventWrapperIndexList.length;

		if (eventCount > 10) {

			$('#sidebarContainer').children(".sidebarItem").slice(10, eventCount).remove();
		}

	},

	eventGroupSelect: function(timeDimensionPlayerState) {
		var eventGroupCount = Page.mapHelper.eventGroupList.length;
		if (timeDimensionPlayerState) {
			for (var i = 1; i <= eventGroupCount; i++) {

				$("#map > div.leaflet-control-container > div.leaflet-top.leaflet-right > div:nth-child(2) > section > div.leaflet-control-layers-overlays > label:nth-child(" + i + ") > div > input").attr("disabled", "disabled");
				$("#map > div.leaflet-control-container > div.leaflet-top.leaflet-right > div:nth-child(2) > section > div.leaflet-control-layers-overlays ").css("pointerEvents", "none");
			}

		} else {

			for (var i = 1; i <= eventGroupCount; i++) {

				$("#map > div.leaflet-control-container > div.leaflet-top.leaflet-right > div:nth-child(2) > section > div.leaflet-control-layers-overlays > label:nth-child(" + i + ") > div > input").removeAttr("disabled");
				$("#map > div.leaflet-control-container > div.leaflet-top.leaflet-right > div:nth-child(2) > section > div.leaflet-control-layers-overlays").css("pointerEvents", "auto");

			}

		}
	}



}
