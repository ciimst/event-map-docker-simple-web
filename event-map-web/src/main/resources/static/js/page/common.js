var Common = {
	timeLineStartDate: "01.01.2020",
	layerId: null,
	tileLayerList: null,
	eventGroupList: null,
	mapHelper: null,
	cookieExpires: 7,
    tileServers: null,
    tileLayerList: null,
	userSettingsListUnderLayer: {},
	writeUserSettingsToCookieAfterLoginState : false,
	eventGroupTreeViewList: [],
	eventGroupCookieSaveList: {"removedEventGroupUnSelected": [], "removedEventGroupSelected": []},
	theTree: null,
	Init: function(mapHelperFunction){
		
		Common.mapHelper = mapHelperFunction;
		Common.SetParamValue(); 
		Common.SetLayerId(); 


	},
	SetParamValue: function(){
		Common.timeLineStartDate = paramTimeLineStartDate;
		Common.writeUserSettingsToCookieAfterLoginState = paramWriteUserSettingsToCookieAfterLogin;
		Common.time = navtime;  
		Common.layerId = paramLayerId;
	},
	SetTimeLineStartDate: function(){
		Common.timeLineStartDate = paramTimeLineStartDate;
	},
	SetLayerId: function(){
		Common.layerId = paramLayerId;
	},
	OpenDetailModal: function () {
	},
	GetEventGroupTreeData: function(eventGroupdbNameIdList){
		
		var data = {};
    	data.layerId = Common.layerId;
		data.eventGroupdbNameIdList = eventGroupdbNameIdList;
		
		$.ajax({ type: "POST",   
		     url: "/component/treeEventGroup",
		     //async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventGroupTreeViewListResult)
		     {				
				Common.eventGroupTreeViewList = eventGroupTreeViewListResult;	
					

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
		    	 Common.tileServers = tileServerList;
		    	 Common.tileLayerList = Common.mapHelper.addTileServers(Common.tileServers);

		    	 Common.AddMapControlBoxWhenReady();
		     }
		});
   	
    },

 	AddMapControlBoxWhenReady: function () {
    	
		Common.mapHelper.addMapControlBox(Common.tileLayerList, Common.mapHelper.eventLayerGroupList);
		
//		Page.LoadGeoLayers();
    },

	LoadEventGroups: function (layerId) {
    	
    	var url = '/component/eventGroups';
    	
    	var data = {};
    	data.layerId = layerId;
		
		$.ajax({ type: "POST",   
		     url: url,   
		     async: false,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventGroupList)
		     {
		    	 Common.eventGroupList = eventGroupList;
		     }
		});

    },
	LoadKey: function(){
		
    	var url = '/component/key';
		
    	var data = {};
    	data.layerId = Common.layerId;
		
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

				
		    },
		});
		 
						
    },
	CreateEventGroupTreeViewForSidebarKey: function(eventGroupTreeViewList){
		//Anahtar sekmesindeki olay grubu ağacını oluşturan methoddur.
		
		new wijmo.nav.TreeView('#eventGroupTreeForKey', {
	  		itemsSource: eventGroupTreeViewList,
			displayMemberPath: 'name',
	    	childItemsPath: 'items',
			showCheckboxes: true,
			checkedMemberPath:['boş'],
			formatItem: function (s, e) {
				var eventGroup = Common.eventGroupList.find(f => f.id == e._data.id && f.dbName == e._data.dbName);
				var imgUrl = SvgIcon.GenerateBase64Icon("", eventGroup.color)
				
				var img = "<img data-toggle='tooltip'  onclick=Common.KeyDetailModalOpen("+eventGroup.id+",'"+eventGroup.dbName+"') title='"+lang.props['label.view.detail']+"'  style='width:32px;' src='" + imgUrl + "'>";
				if(eventGroup.description == ""){
					img = "<img  style='width:32px;' src='" + imgUrl + "'>";
				}
				
				$(e.element).children("span").addClass("wj-node-text-span");
				$(e.element).children('input').remove()
				$(e.element).prepend(img);	
        	},
		});
		
		$('[data-toggle="tooltip"]').tooltip({
			placement: 'left',
			trigger: 'hover',
			container: 'body',
			boundary: 'window',
		});
	},

	KeyDetailModalOpen : function (eventGroupId,eventGroupDbName) {
		
		event.stopPropagation();
		let found = Common.eventGroupList.find(f=>f.id === eventGroupId && f.dbName === eventGroupDbName);
		if(found.description == "" || found.description == null){
		
			return;
		}


		$("#keyDetailDialog").modal('show');
		/*$("#keyDetailDialog").draggable(); */  
	    $(".modal-dialog").draggable({
		  cursor: "move",
	      "handle":".modal-header"
	    });
		let modal = $("#keyDetailDialog");
		modal.find("#keyDetailModalTitle").text(found.name);
		modal.find("#keyDetailModalContent").html(found.description)
	},
	
	LoadTimeLine: function(){
		
		var langiso = lang.props["lang.iso"];
    	if(langiso){
    		moment.locale(langiso);
    	}

		let dateArray =  (Common.timeLineStartDate.toString() ).split(".");

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
    	
    	
    	var sbTotal = "";
    	
    	var tempYear = startYear;
	
		var timeTotalData = {};
		if(Common.time != null && Common.time != ""){
					
			var date = Common.time.split(".");
			 			
			timeTotalData = Common.GetTimeInEventCounts("MONTH", date[2], date[1].toString().padStart(2, '0'), true);
			
		}else{		
			timeTotalData = Common.GetTimeInEventCounts(null, null, null, true);
		}

    	while(tempYear <= currentYear){
	
			var totalYearEventCount = 0;
			var mapYearsInEventCount = timeTotalData.YEARS;
			$.each(mapYearsInEventCount, function(key, value){
				
				if(key == tempYear){
					totalYearEventCount = value
				}

			});
				
        	var sb = "<ul class='dd_1'>";
        	
        	sb += "<li class='li_dd1 year' data-date-type='YEARS' data-data='year_"+tempYear+"'>";
        	sb += "<a class='y' href='#'>" + tempYear + "<span class='badge badge-pill badge-secondary timeEventCountBadge'> "+totalYearEventCount+"</span></a>";
        	
        	sb += "<ul class='dd_2'>";
        	
        	tempMonth = 1;
        	// Aylar oluşturuluyor
			// (tempYear == startYear ? startMonth : 1)
        	for(i = (1) ; i<=(tempYear == currentYear ? currentMonth : 12); i++){
	
				var totalMontEventCount = 0;				
				var mapCurrentMonthsInYear = timeTotalData.MONTHS;
				$.each(mapCurrentMonthsInYear, function(key, value){
					
					if(key == tempMonth){
						totalMontEventCount = value
					}

				});

        		sb += "<li class='li_dd2 month' data-months='"+tempMonth+"_"+tempYear+"' data-date-type='MONTH' data-data='month_"+tempYear+"_"+i+"'>";
        		sb += "<a class='m' href='#'>"+CustomFormatter.GetMonthName(i-1) +"<span class='badge badge-pill badge-secondary timeEventCountBadge'> "+totalMontEventCount+"</span></a>";
        		sb += "<ul class='dd_3'>";

        		var daysInMonth = moment( tempYear + "-" + tempMonth, "YYYY-MM").daysInMonth() // 29
        		
        		// Günler oluşturuluyor
        		for(j = ((tempYear == startYear && tempMonth == startMonth) ? startDay : 1); j<=((tempYear == currentYear && tempMonth == currentMonth) ? currentDay : daysInMonth); j++){
        			var date = j.toString().padStart(2, '0') + "." + tempMonth.toString().padStart(2, '0') + "." + tempYear;
					
					var paramCurrentMonthInDays = timeTotalData.DAYS;
					var eventCount = 0;				
					$.each(paramCurrentMonthInDays, function(key, value){
					
						if(key == j){
						
							eventCount = value
						}

					});
					
					
        		  sb += "<li class='day' data-days='"+j+"_"+tempMonth+"_"+tempYear+"'><a class='d' href='"+contextPathWithoutSlash+"/time/" + Common.layerId + "?time="+date+"' data-data='"+date+"'>" + j.toString().padStart(2, '0') + " " + CustomFormatter.GetMonthName(i-1) + " " + tempYear +"<span class='badge badge-pill badge-secondary timeEventCountBadge'> "+eventCount+"</span></a></li>";  
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

			
			var thisDateType = $(this).attr("data-date-type");
			
			if(thisDateType === "YEARS"){


				var year = $(this).attr("data-data").split("_")[1];
				var mapList = Common.GetTimeInEventCounts("YEARS",year, null, false);
				var resultMonths = mapList.MONTHS;
				
				
				//Aylara olay sayısı ekleniyor.
				$.each(resultMonths, function(key, value){
					var element = $($("[data-months='"+key+"_"+year+"']")).children("a").children("span");
//					var currentText = " "+ value +"";
					$(element).text(value);
				
					
				});
				
				//sıfır gelenleri setlemek için.
				var monhtList = $(this).children("ul").children("li")
				$.each($(monhtList), function(key, value){
					
					var dataMonthsInfo = $(value).attr("data-months").split("_");
					var childrenMonth = dataMonthsInfo[0];					
					var isMonth = Object.keys(resultMonths).filter(f => f === childrenMonth);										
					if(isMonth.length == 0){
						
						$(value).children("a").children("span").text("0");
					}
				});
				
				
				
			}else if(thisDateType === "MONTH"){

				var month = $(this).attr("data-data").split("_")[2];
				var year = $(this).attr("data-data").split("_")[1];
				var mapList = Common.GetTimeInEventCounts("MONTH", year, month, false);
				var resultMonths = mapList.MONTHS;
				var resultDays = mapList.DAYS;
				
				//Günlere olay sayısı ekleniyor.
				$.each(resultDays, function(key, value){
					var element = $($("[data-days='"+key+"_"+month+"_"+year+"']")).children("a").children("span");
//					var currentText = " "+ value +"";
					$(element).text(value);
				
					
				});
				
				
				//Aylara olay sayısı ekleniyor.
				$.each(resultMonths, function(key, value){
					var element = $($("[data-months='"+key+"_"+year+"']")).children("a").children("span");
//					var currentText = " "+ value +"";
					$(element).text(value);
				
					
				});
				
				
				var monhtList = $(this).children("ul").children("li")
				$.each($(monhtList), function(key, value){
					
					var dataMonthsInfo = $(value).attr("data-days").split("_");
					var childrenDay = dataMonthsInfo[0];					
					var isMonth = Object.keys(resultDays).filter(f => f === childrenDay);										
					if(isMonth.length == 0){
						
						$(value).children("a").children("span").text("0");
					}
					
					
				});
				
				
				
			}
			
			
    	});   


		$(".dd_3 > li").on("click", function(e){    
    		e.stopPropagation(); 
    		if($(this).hasClass("show"))
    			$(this).removeClass("show");
    		else
    			$(this).addClass("show");
    	});   
	
    },
    
    SetTimeLine: function(){
      var date = Common.time != null ? CustomFormatter.ConvertToDate(Common.time) : new Date();
      
      var day = date.getDate();
      var month = date.getMonth()+1;
      var year = date.getFullYear();
      
      $("li[data-data='year_"+year+"']").addClass("show");
      $("li[data-data='month_"+year+"_"+month+"']").addClass("show");
      $("a[data-data='"+jQuery.format.date(date, "dd.MM.yyyy")+"']").addClass("color");
      
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
				var i = 0;
											
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
						activeLayerId : Common.layerId,
						dropdownActiveClass : ""
							
					}					
				}else{
					
					menuListW = {
			    		menuList : list,
						activeLayerId : Common.layerId,
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
	
						var currentLayerName = $(".layer-dropdown-item.active").text();
						var currentLayerTitle = $(".layer-dropdown-item.active").attr("title");
						
						$("#navbarDropdown").text(currentLayerName);
						$("#navbarDropdown").attr("title", currentLayerTitle);
		    		},
		    		cache: false,
		    		async: false
		    	});
			}//success end
		});
   	
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

	
	UserSettingsUnderLayer(){
		
				
		//Cuurent layer bir cookiede tutulur ve login olduktan sonra eğer default değerleri ilk defa setleniyorsa ikinci if bloğuna girer. 
		//He login olunduğunda userSettingsSetLayer cookiesi önce silinir. Sonra layerlar arasında gezindikçe yani ayarlardaki değerleri
		//setlendikçe cookieye eklenir. Böylece değerler login olduktan sonra ilk layerı açmada setlenmiş olur.
		//Bunun yapılmasının neden olay gruplarının ve user settings değerlerinin tamamının yerine sadece current layer için çekilip setlenmesini sağlamak.
		
		Common.UserSettingsUnderLayerGetData();
		var userSettingsLayerAddedContol = $.cookie("userSettingsSetLayer") != undefined ? $.cookie("userSettingsSetLayer").includes(Common.layerId) : false;
		
		if(!Common.writeUserSettingsToCookieAfterLoginState){
			$.removeCookie("userSettingsSetLayer", {  path: "/" });
			userSettingsLayerAddedContol = false;
		}
		

		//İlk login olduğunda bu bloğa girer ve default değerleri atar.
		if(!Common.writeUserSettingsToCookieAfterLoginState  || !userSettingsLayerAddedContol ){
			
	
			if(Page.isEventTable)
			{
				//EventTable içindeki methodu çağırır.
				Page.UsingDefaultSearch();
			}else{
			
				if(Object.keys(Common.userSettingsListUnderLayer).length > 0 && Common.userSettingsListUnderLayer[Page.layerId].anyExistField){
					//Home.js  içindeki methodu çağırır.
					Page.UsingDefaultSearch();
					Page.OnlyGetUserSettingsMapInfo();
				}
				//Olay grubu kaydının olup içinin boş olması durumu ile kaydın veritabanında bulunmuyor olması durumunu ayırt etmek için kullanılıyor.
				if(Object.keys(Common.userSettingsListUnderLayer).length > 0 && Common.userSettingsListUnderLayer[Page.layerId].existEventGroups){
					Page.onlygetUserSettingsEventGroup();
				}
				
				//Katmanın kullanıcı ayarları yoksa eğer sayfa yenilendiğinde ayarları tekrar çekmek için veritabanına gitmesin kontrolü.
				if(Object.keys(Common.userSettingsListUnderLayer).length == 0){
					
					
					var list = $.cookie("userSettingsSetLayer") != undefined ?JSON.parse($.cookie("userSettingsSetLayer")) : [];	
					list.push(Page.layerId);
					var value = JSON.stringify(list);
					$.cookie("userSettingsSetLayer", value, { expires: Page.cookieExpires, path: "/" });
				}
				
				
				
			}			
			
				
			Common.DetailedSearchUserSettingsCookieInformationControl();
		}
				
	},
	
	UserSettingsUnderLayerGetData: function(){
		
		$.ajax({ type: "POST",   
		     url: "/settings/layer/" + Common.layerId,
		     datatype: 'json',
		     contentType: "application/json",
			 async: false,
		     success : function(result)
		     {	
				//Cuurent layer bir cookiede tutulur ve login olduktan sonra eğer default değerleri ilk defa setleniyorsa ikinci if bloğuna girer. 
				//He login olunduğunda userSettingsSetLayer cookiesi önce silinir. Sonra layerlar arasında gezindikçe yani ayarlardaki değerleri
				//setlendikçe cookieye eklenir. Böylece değerler login olduktan sonra ilk layerı açmada setlenmiş olur.
				//Bunun yapılmasının neden olay gruplarının ve user settings değerlerinin tamamının yerine sadece current layer için çekilip setlenmesini sağlamak.
				
				Common.userSettingsListUnderLayer = result;
				
		     }
		});
	},
	
	MapSetDefaultInfo: function(){
		
		var coordinateInfo = {
			latitude : 40,
			longitude :35,
			zoom : 5
		}
		try{
	    	var cookieVal = JSON.parse($.cookie('coordinateInfo'));	      
	    	if(cookieVal.zoom != null && cookieVal.longitude != null && cookieVal.latitude != null){
		    	coordinateInfo = {
		    	longitude : cookieVal.longitude,
		    	latitude : cookieVal.latitude,
		    	zoom : cookieVal.zoom
		    	}		    		    	
	    	}
	    }catch(e){	   	 
		    coordinateInfo = {
			    	latitude : 40,
			    	longitude :35,
			    	zoom : 5
			}
			$.cookie('coordinateInfo', JSON.stringify(coordinateInfo));
	    }

		return coordinateInfo;
	},
	

	DetailedSearchUserSettingsCookieInformationControl: function(){
				
		Common.writeUserSettingsToCookieAfterLoginState = true;
	    $.ajax({
	        type: "GET",
	        url: "/settings/detailedSearchCookieDeleteInformation?writeUserSettingsToCookieAfterLogin=" + Common.writeUserSettingsToCookieAfterLoginState,
	     	contentType: "application/json",
	        success: function(data) {
				
	            Common.writeUserSettingsToCookieAfterLoginState = data;
	
	        }
	    });	
	},
	
	StartAndEndDateGreatherControl: function(){
		if($("#startDate").val() != undefined && $("#startDate").val() != "" && $("#endDate").val() != undefined && $("#endDate").val() != ""){
			
			const compareStartDate = moment($("#startDate").val(), DateUtils.ENGLISH).valueOf();
			const compareEndDate = moment($("#endDate").val(), DateUtils.ENGLISH).valueOf();
			
			if(compareStartDate > compareEndDate){
				
				const swalWithBootstrapButtons = Swal.mixin({
				  customClass: {
				    confirmButton: 'btn btn-success',
				  },
				  buttonsStyling: false
				});
				
				swalWithBootstrapButtons.fire({
				  text: lang.props['label.select.start.date.smaller.than.end.date'],
				  icon: 'warning',
				  showCancelButton: false,
				  confirmButtonText: lang.get('label.ok'),
				  reverseButtons: false
				});
				
				return false;
				
			}
			
			return true;
		}
		
		return true;
	},
	
	StateChanged: function(id){
		
		$.ajax({ type: "POST",   
		
		     url: "/event-table-view/stateChange?id="+id,
		     data: {id: id},
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {
				
				if(!result.state){
					
					const swalWithBootstrapButtons = Swal.mixin({
					  customClass: {
					    confirmButton: 'btn btn-success excelDownloadConfirm',
					    cancelButton: 'btn btn-danger'
					  },
					  buttonsStyling: false
					});
					
					
					swalWithBootstrapButtons.fire({
					  text: result.description,
					  icon: 'warning',
					  showCancelButton: false,
					  confirmButtonText: lang.get('label.ok'),
					  reverseButtons: true
					});
			
					
			
		
				}
				
			 }
	
		});
	},
	
	CurlyBracketsLink: function(link, data){
		var regExp = new RegExp("{id}", 'g');

		if(link.includes("{id}")){
			regExp = new RegExp("{id}", 'g');
			link = link.replace(regExp, data.event.id != null ? data.event.id : "")
		}
		
		if(link.includes("{title}")){
			regExp = new RegExp("{title}", 'g');
			link = link.replace(regExp, data.event.title)
		}
		
		if(link.includes("{spot}")){
			regExp = new RegExp("{spot}", 'g');
			link = link.replace(regExp, data.event.spot != null ? data.event.spot : "")
		}
		
		if(link.includes("{description}")){
			regExp = new RegExp("{description}", 'g');
			link = link.replace(regExp, data.event.description != null ? data.event.description : "")
		}
		
		if(link.includes("{eventDate}")){
			regExp = new RegExp("{eventDate}", 'g');
			var date = CustomFormatter.GetDateFormatted(data.event.eventDate)
			link = link.replace(regExp, data.event.eventDate != null ? CustomFormatter.ConvertToLong(date) : "")
		}
		
		if(link.includes("{eventType}")){
			regExp = new RegExp("{eventType}", 'g');
			link = link.replace(regExp, data.event.eventTypeId != null ? data.event.eventTypeId : "")
		}
		
		if(link.includes("{country}")){
			regExp = new RegExp("{country}", 'g');
			link = link.replace(regExp, data.event.country != null ? data.event.country : "")
		}
		
		if(link.includes("{city}")){
			regExp = new RegExp("{city}", 'g');
			link = link.replace(regExp, data.event.city != null ? data.event.city : "")
		}
		
		if(link.includes("{latitude}")){
			regExp = new RegExp("{latitude}", 'g');
			link = link.replace(regExp, data.event.latitude)
		}
		
		if(link.includes("{longitude}")){
			regExp = new RegExp("{longitude}", 'g');
			link = link.replace(regExp, data.event.longitude)
		}
		
		if(link.includes("{eventGroup}")){			
			regExp = new RegExp("{eventGroup}", 'g');
			link = link.replace(regExp, data.event.groupId != null ? data.event.groupId : "")
		}
		
		if(link.includes("{state}")){
			regExp = new RegExp("{state}", 'g');
			link = link.replace(regExp, data.event.state != null ? data.event.state : "")
		}
		
		if(link.includes("{reservedKey}")){
			regExp = new RegExp("{reservedKey}", 'g');
			link = link.replace(regExp, data.event.reservedKey != null ? data.event.reservedKey : "")
		}
		
		if(link.includes("{reservedType}")){
			regExp = new RegExp("{reservedType}", 'g');
			link = link.replace(regExp, data.event.reservedType != null ? data.event.reservedType : "")
		}
		
		if(link.includes("{reservedId}")){
			regExp = new RegExp("{reservedId}", 'g');
			link = link.replace(regExp, data.event.reservedId != null ? data.event.reservedId : "")
		}
		
		if(link.includes("{reservedLink}")){
			regExp = new RegExp("{reservedLink}", 'g');
			link = link.replace(regExp, data.event.reservedLink != null ? data.event.reservedLink : "")
		}		
		
		if(link.includes("{blackListTag}")){
			regExp = new RegExp("{blackListTag}", 'g');
			link = link.replace(regExp, data.event.blackListTag != null ? data.event.blackListTag : "")
		}
		
		if(link.includes("{reserved1}")){
			regExp = new RegExp("{reserved1}", 'g');
			link = link.replace(regExp, data.event.reserved1 != null ? data.event.reserved1 : "")
		}
		
		if(link.includes("{reserved2}")){
			regExp = new RegExp("{reserved2}", 'g');
			link = link.replace(regExp, data.event.reserved2 != null ? data.event.reserved2 : "")
		}
		
		if(link.includes("{reserved3}")){
			regExp = new RegExp("{reserved3}", 'g');
			link = link.replace(regExp, data.event.reserved3 != null ? data.event.reserved3 : "")
		}
		
		if(link.includes("{reserved4}")){
			regExp = new RegExp("{reserved4}", 'g');
			link = link.replace(regExp, data.event.reserved4 != null ? data.event.reserved4 : "")
		}
		
		if(link.includes("{reserved5}")){
			regExp = new RegExp("{reserved5}", 'g');
			link = link.replace(regExp, data.event.reserved5 != null ? data.event.reserved5 : "")
		}
		
		if(link.includes("{layer}")){
			regExp = new RegExp("{layer}", 'g');
			link = link.replace(regExp, data.event.layerId != null ? data.event.layerId : "")
		}
		return link;

	},
	PopoverShow: function(){
		
		$('[data-toggle="popover"]').popover({
			template: '<div class="popover"><div class="arrow"></div><h3 class="popover-header"></h3><div class="popover-body"></div></div>',
		});
		
		$('[data-toggle="popover"]').on('click', function () {				
			
			
			var rowIndex = $(this).attr("data-row-index");	
			var columnId = $(this).attr("data-event-table-column-enum-id");	
			var rowData = EventTable.dataTable.data()[rowIndex];	
	
			var linkList = null;
			var columnIdList =$(this).attr("data-event-table-column-enum-id-list");	
			
			if(columnIdList != undefined){
				var ids = columnIdList.split("_");				
				linkList = Common.GetEventColumnLinks(ids);
			}else{
				linkList = Common.GetEventColumnLinks(columnId);
			}
			
			if(linkList.length > 0){
				var liList = "";
				$.each(linkList, function(key, value){
					var stringValue = Common.CurlyBracketsLink(value.link, rowData);			
					liList += "<li class='eventLinkLiElement'><i class='fas fa-circle' style='font-size:9px; color:"+value.color+"'></i><a class='eventLinkATag' target='_blank' href='"+stringValue+"'>"+value.displayName+"</a></li>";	
				});
				
				var html = "<div id='' class=''><ul class='eventLinkPopoverUlTag'>" + liList+"</ul></div>";
				var ariaDescribedby = $(this).attr("aria-describedby")				
				$("#"+ariaDescribedby).children(".popover-body").html(html)
			}else{
				
				//link yoksa açılmasın.
				$('[data-toggle="popover"]').popover("hide")
			}						
			
		});
		
	},
	GetEventColumnLinks: function(columnId){		

		var list = [];
		var url = '/component/getEventColumnLinks?columnId='+columnId;
		$.ajax({ type: "POST",   
		     url: url,   
		     async: false,
		     success : function(linkList)
		     {			
				list = linkList;
		     }
		});
		
		return list;
		
	},
	
	GetTimeInEventCounts: function(dateTimeTypes, year = null, month = null, isFirstOpening = false){
		
		var url = '/component/getTimeInEventCounts?layerId='+Common.layerId;
				
		if(dateTimeTypes === "YEARS"){
			url += '&year='+year;
		}
		
		if(dateTimeTypes === "MONTH"){
			url += '&year='+year + '&month='+month;
		}
				
		url += '&isFirstOpening='+isFirstOpening;		
		
		var list = [];
		$.ajax({ type: "POST",   
		     url: url,   
		     async: false,
		     success : function(dateInEventCountList)
		     {			
				list = dateInEventCountList;
		     }
		});
		
		return list;
		
	},
	
	AlertEventLoadControl: function(){

		if( $("#alertEventChecked").is(':checked') ){
			
			$("#alertEventChecked").prop('checked', false)
		}else{
			
			$("#alertEventChecked").prop('checked', true)
		}	
				
	},		
	
	setCookies: function(state){
		
		var locationUrlNewLive = contextPathWithoutSlash + "/region/"+ Common.layerId;
		var locationUrlTimeDimension = contextPathWithoutSlash + "/timeDimension/"+ Common.layerId;
		var locationUrlHeatMap = contextPathWithoutSlash + "/heatmap/" + Common.layerId;
		var locationUrlTime = contextPathWithoutSlash + "/time/" + Common.layerId;
		var locationUrlEventTable = contextPathWithoutSlash + "/event-table/" + Common.layerId;
		var urlList = [locationUrlNewLive, locationUrlTimeDimension, locationUrlHeatMap, locationUrlTime, locationUrlEventTable];
		
		if(state){
			
			eventSearchKey = $("#eventSearch").val() != undefined ? $("#eventSearch").val() : "";
			if(typeof EventTable != "undefined"){
				eventSearchKey = $("#eventSearchEventTable").val() != undefined ? $("#eventSearchEventTable").val() : "";
			}
			
			
			$.each(urlList, function(key, url){			
			   $.cookie('eventSearchText', eventSearchKey, { expires: Page.cookieExpires, path: url }); 
			});
		}else{
			
			// Eski cookielerin temizlenmesi icin
			$.removeCookie('eventSearchText',    {  path: "/" }); 
		    $.removeCookie('eventTypeIdSearch',  {  path: "/" });
		    $.removeCookie('eventSearchCity',    {  path: "/" }); 
		    $.removeCookie('eventSearchCountry', {  path: "/" }); 
		    $.removeCookie('isALertEventClick',  {  path: "/" }); 
 			$.removeCookie('heatmapStartDate',   {  path: "/" }); //eskiden olanları silmek için
		 	$.removeCookie('heatmapEndDate',     {  path: "/" }); //eskiden olanları silmek için
			$.removeCookie("timeDimensionDate", {  path: "/" });//eskiden olanları silmek için
			
			
			var eventSearchKey = $("#eventSearchDetailed").val()!= undefined ? $("#eventSearchDetailed").val() : "";
			$("#eventSearch").val(eventSearchKey);					
			var eventTypeSelected = $("#searchEventType option:selected").val() != undefined ? $("#searchEventType").val() : 0;						
			var eventSearchCity = $("#eventSearchCity").val()!= undefined ? $("#eventSearchCity").val() : "";			
			var eventSearchCountry = $("#eventSearchCountry").val()!= undefined ? $("#eventSearchCountry").val() : "";
			var alertChecked = $("#alertEventChecked").is(':checked');						
			var startDate = $("#startDate").val() != undefined ? $("#startDate").val() : "";		
			var endDate = $("#endDate").val() != undefined ? $("#endDate").val() : "";

			
			
			$.each(urlList, function(key, url){
			
			   	$.cookie('eventSearchText', eventSearchKey, { expires: Common.cookieExpires, path: url }); 
			  	$.cookie('eventTypeIdSearch', eventTypeSelected, { expires: Common.cookieExpires, path: url }); 	
			  	$.cookie('eventSearchCity', eventSearchCity, { expires: Common.cookieExpires, path: url }); 	
			  	$.cookie('eventSearchCountry', eventSearchCountry, { expires: Common.cookieExpires, path: url }); 	
			   	$.cookie('isALertEventClick', alertChecked, { expires: Common.cookieExpires, path: url }); 
				$.cookie('startDate', startDate, { expires: Common.cookieExpires, path: url }); 
				$.cookie('endDate', endDate, { expires: Common.cookieExpires, path: url });
				
				//Olay gruplarının değerinin setlenmesi işlemi.Öncesinde cookie temizleniyor.
				$.removeCookie('removedEventGroups', { path: locationUrlNewLive});
			});			


			$.each(Common.eventGroupCookieSaveList.removedEventGroupSelected, function(key, layerGroupKey){
				Common.SetRemovedEventGroupsCookieValue("removedEventGroupSelected", layerGroupKey)
			});
						
			$.each(Common.eventGroupCookieSaveList.removedEventGroupUnSelected, function(key, layerGroupKey){
				Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
			});
			

		}
		
	},
	
	SetRemovedEventGroupsCookieValue: function(cookieNameAndType, layerGroupKey){
		
		
		var cookieName = "removedEventGroups";
		var cookieValue = $.cookie("removedEventGroups");
		var cookieRemoveEventGroups = [];
		if(cookieValue != undefined){
			
			cookieRemoveEventGroups = JSON.parse($.cookie("removedEventGroups"));
		}
		
		
		switch(cookieNameAndType) {
		  case "removedEventGroupSelected":
		
			var found = cookieRemoveEventGroups.includes(layerGroupKey);
			
			if(found == false){ // yoksa ekler
				
				var index;
				cookieRemoveEventGroups.findIndex(function (entry, i) {if (entry == layerGroupKey) {index = i; return true;} });								
				cookieRemoveEventGroups.splice(index, 1); 
			}

			cookieValue = JSON.stringify(cookieRemoveEventGroups)
			
		    break;
		  case "removedEventGroupUnSelected":
				
			if(cookieRemoveEventGroups != undefined && cookieRemoveEventGroups != null && cookieRemoveEventGroups.length != 0){
			
				var found = cookieRemoveEventGroups.includes(layerGroupKey);
				
				if(found == false){// yoksa ekler
					cookieRemoveEventGroups.push(layerGroupKey)
				}
			
			} else{
				cookieRemoveEventGroups=[];
				cookieRemoveEventGroups.push(layerGroupKey)
			}
		

			cookieValue = JSON.stringify(cookieRemoveEventGroups)
			
		    break;
		  default:
		    // code block
		}
		
		var urlList = [contextPathWithoutSlash + "/region/"+ Common.layerId, contextPathWithoutSlash + "/timeDimension/"+ Common.layerId, contextPathWithoutSlash + "/heatmap/" + Common.layerId, contextPathWithoutSlash + "/time/"+Common.layerId, contextPathWithoutSlash + "/event-table/" + Common.layerId];
		
		$.each(urlList, function(key, url){
			
			$.cookie(cookieName, cookieValue, { expires: Common.cookieExpires, path: url});
		})
		

	},
	
	ReadCookies: function(){
		
		var cookieModel = {
			eventTextSearch : "",
			eventTypeIdSearch : "",
			eventSearchCountry : "",
			eventSearchCity : "",
			alertChecked: false,
			removedEventGroupList: [],
			selectedEventGroupList: [],
			startDate: "",
			endDate:"",
			count: 0
		};

		var eventTextSearch = $.cookie("eventSearchText")
		if(eventTextSearch != undefined && eventTextSearch != null && eventTextSearch != ""){					
			cookieModel.eventTextSearch = eventTextSearch;

		}										
		
		var eventTypeIdSearch = $.cookie("eventTypeIdSearch")
		if(eventTypeIdSearch != undefined && eventTypeIdSearch != null && eventTypeIdSearch != "" && eventTypeIdSearch != "0"){					
			
			cookieModel.eventTypeIdSearch = eventTypeIdSearch;
			cookieModel.count++;
		}
		
		var eventSearchCountry = $.cookie("eventSearchCountry")
		if(eventSearchCountry != undefined && eventSearchCountry != null && eventSearchCountry != ""){					
			cookieModel.eventSearchCountry = eventSearchCountry;
			cookieModel.count++;
		}	
		
		var eventSearchCity = $.cookie("eventSearchCity")
		if(eventSearchCity != undefined && eventSearchCity != null && eventSearchCity != ""){					
			cookieModel.eventSearchCity = eventSearchCity;
			cookieModel.count++;
		}
		
		var alertChecked = $.cookie("isALertEventClick")
		if(alertChecked != undefined && alertChecked != null && alertChecked != ""){					
			cookieModel.alertChecked = alertChecked == "true";
			if(cookieModel.alertChecked == true){
				cookieModel.count++;
			}			
		}
		
		var startDate = $.cookie("startDate");
		
		if(startDate != null && startDate != "" && startDate != undefined){
			cookieModel.startDate = startDate;
			cookieModel.count++;
		}
		var endDate = $.cookie("endDate")
		if(endDate != null && endDate != "" && endDate != undefined){
			cookieModel.endDate = endDate;
			cookieModel.count++;
		}
		

		
		var removedEventGroupListStr = $.cookie("removedEventGroups");
		if(removedEventGroupListStr != undefined && removedEventGroupListStr != null && removedEventGroupListStr != "" &&  removedEventGroupListStr != '[]'){
			
			var removedEventGroupList = JSON.parse(removedEventGroupListStr);

			cookieModel.removedEventGroupList = removedEventGroupList;
			cookieModel.selectedEventGroupList = Common.GetShowEventGroupDbNameList(Common.eventGroupList, removedEventGroupList);
			
			Common.eventGroupCookieSaveList  = {"removedEventGroupUnSelected": cookieModel.removedEventGroupList, "removedEventGroupSelected": cookieModel.selectedEventGroupList};
			
			cookieModel.count++;
		}else{
			removedEventGroupList = [];
			cookieModel.removedEventGroupList = removedEventGroupList;
			cookieModel.selectedEventGroupList = Common.GetShowEventGroupDbNameList(Common.eventGroupList, removedEventGroupList);
			
			Common.eventGroupCookieSaveList  = {"removedEventGroupUnSelected": cookieModel.removedEventGroupList, "removedEventGroupSelected": cookieModel.selectedEventGroupList};
			
		}
		
		
		
		
		return cookieModel;
	},
	
	
	
	
	GetShowEventGroupDbNameList(allEventGroupList, removedEventGroupList){ // Selected event group list doner
		
		var eventGroupdbNameIdList = [];
		
		$.each(allEventGroupList, function(key, value){
			
			var item = value.dbName + "_" + value.id
			const result = removedEventGroupList.includes(item);
			
			
			if(result == false){ // yoksa eklenir
				eventGroupdbNameIdList.push(item);
			}
			
		});		
		
		return eventGroupdbNameIdList;
	},
		
	CreateEventGroupTreeView: function(eventGroupTreeViewList){
	//Detaylı arama modalında olay gruplarını ağaç olarak göstermek için kullanılan method.
		
		Common.eventGroupCookieSaveList = {"removedEventGroupUnSelected": [], "removedEventGroupSelected": Common.GetShowEventGroupDbNameList()};
		
		if($.cookie("removedEventGroups") != undefined){
			var list = JSON.parse($.cookie("removedEventGroups"));
			Common.eventGroupCookieSaveList.removedEventGroupUnSelected = list;
		}
		
		
		Common.theTree = new wijmo.nav.TreeView('#eventGroupTree', {
	  		itemsSource: eventGroupTreeViewList,
			displayMemberPath: 'name',
	    	childItemsPath: 'items',
			showCheckboxes: true,
			checkedMemberPath:['showCheckboxes'],
			checkedItemsChanged: function(s) {
				
				Common.eventGroupCookieSaveList = {"removedEventGroupUnSelected": [], "removedEventGroupSelected": []};
				root(eventGroupTreeViewList)
				
			}
		});

		//Tüm alt childları gezer ve duruma göre cookie ye ekler.
		function root(selectedChangeItem){
			

			for( var i = 0 ; i < selectedChangeItem.length; i++ ){
				
				var childSelectedChangeItem = selectedChangeItem[i];
				var layerGroupKey = childSelectedChangeItem.dbName + "_" + childSelectedChangeItem.id;
								
				childSelectedChangeItem.showCheckboxes == true ? Common.eventGroupCookieSaveList.removedEventGroupSelected.push(layerGroupKey) : Common.eventGroupCookieSaveList.removedEventGroupUnSelected.push(layerGroupKey);
				
				
				if(childSelectedChangeItem.items != null &&childSelectedChangeItem.items.length > 0){
					child(childSelectedChangeItem.items);
				}
			}
					
		}
		
		function child(selectedChangeItem){
			
			
							
			for( var i = 0 ; i < selectedChangeItem.length; i++ ){
				
				var childSelectedChangeItem = selectedChangeItem[i];
				var layerGroupKey = childSelectedChangeItem.dbName + "_" + childSelectedChangeItem.id;
								
				childSelectedChangeItem.showCheckboxes == true ? Common.eventGroupCookieSaveList.removedEventGroupSelected.push(layerGroupKey) : Common.eventGroupCookieSaveList.removedEventGroupUnSelected.push(layerGroupKey);
				
				
				if(childSelectedChangeItem.items != null && childSelectedChangeItem.items.length > 0){
					child(childSelectedChangeItem.items);
				}
			}
			
						
		}
		
	},
	
	EventImageLightBox: function(){

		$(".eventDetailModalMediaArea").attr("id", "lightgallery");
		
		$('#lightgallery').lightGallery({
			download:false,
		});		
	},
	
	GotoTop : function(){

		//canlı akış ve zaman akışında çalışması bekleniyor.
		if(!HeatMap.isHeatmap){	
			
			$("#sidebarContainer > .sidebarItem").remove();
			Page.lastSidebarLastOrPrevIndex = 0;			
			Page.sidebarLastIndex = 0;
			Page.InitScroll();
		}
		
		$("#sidebarContainer").scrollTo(0, 500, {offsettop:100});
	},
	
	
	
}

$(document).ready(function () {    
	
	$("#sidebarContainer").scroll(function(e){ 
		
		if($("#sidebarContainer").scrollTop() > 20){
			
			$("#gotoTop").css("display", "block")
		}else{
			$("#gotoTop").css("display", "none")
		}
	});

});