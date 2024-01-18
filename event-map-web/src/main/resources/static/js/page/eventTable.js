var Page = {
	eventGroupTreeViewList: [],
	layerId: null,
	isAlertSharedClick : false,
	theTree: null,
	isEventTable: true,
	
	UsingDefaultSearch: function(isBtn = false ){//ilk defa login olunduğunda cookieye default değerler setlenir. Arama modalından default değere dön denilince de cookieye setlenir ve sayfa refreshlenir.
		
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
		
				
			
			if(userSettingsTypeColumn.anyExistField){
				
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
			
			//Olay Gruplarının default değerlerinin setlenmesi işlemi.
			if(userSettingsTypeColumn.existEventGroups){
				Page.onlygetUserSettingsEventGroup(layerId, userSettingsTypeColumn);	
			}
		});

		
		
		
		
		//Button ile default değere dönülmek istenirse sayfa refresh yapılır.
		if(isBtn){
			window.location = "";
		}
		
		
	},
	
	
	onlygetUserSettingsEventGroup: function(layerId, userSettingsTypeColumn){
		

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
			
		

		
		
	},
	
	
	onlyEventGroupSearch: function(isDefaultChoose = false){
		var urlList = [contextPathWithoutSlash, contextPathWithoutSlash + "/heatmap/" + Page.layerId, contextPathWithoutSlash + "/timeDimension/" + Page.layerId, contextPathWithoutSlash + "/time/" + Page.layerId, contextPathWithoutSlash + "/region/" + Page.layerId, contextPathWithoutSlash + "/event-table/" + Page.layerId];
		
		$.each(urlList, function(key, url){
		    $.removeCookie('removedEventGroups', { path: url});//Olay gruplarının değerinin setlenmesi işlemi.Öncesinde cookie temizleniyor.
		});
	    
		
		$.each(Common.eventGroupCookieSaveList.removedEventGroupSelected, function(key, layerGroupKey){
			Common.SetRemovedEventGroupsCookieValue("removedEventGroupSelected", layerGroupKey)
		});
			
			
		$.each(Common.eventGroupCookieSaveList.removedEventGroupUnSelected, function(key, layerGroupKey){
			Common.SetRemovedEventGroupsCookieValue("removedEventGroupUnSelected", layerGroupKey);
		});
		
		
	},
	
	userSettingsEventGroupSave: function(){
		
		Page.onlyEventGroupSearch();
		
		var cookieModel = Common.ReadCookies();		
		var eventGroupdbNameIdList = Common.GetShowEventGroupDbNameList(Common.eventGroupList, cookieModel.removedEventGroupList);
		
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
			
		return list;
	},
	

	
	userSettingsSave: function(){
		
		Common.setCookies(false);
		var userSettingsTypeColumn = Common.ReadCookies();
		
//		var userSettingsTypeColumn = Page.userSettingsSave();
		var eventGroupList = Page.userSettingsEventGroupSave();
		
			
		var data = {};
		data.eventGroupList = eventGroupList;
		
		data.city = userSettingsTypeColumn.eventSearchCity;
		data.country = userSettingsTypeColumn.eventSearchCountry;
		data.titleAndSpotAndDescription = userSettingsTypeColumn.eventTextSearch;
		data.eventTypeId = userSettingsTypeColumn.eventTypeIdSearch;
		data.alertEvent = userSettingsTypeColumn.alertChecked;
		data.startDate = userSettingsTypeColumn.startDate;
		data.endDate = userSettingsTypeColumn.endDate;
		
		
		var url = "/settings/userSettingsValueSave/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupSave=true&outSideEventGroup=true";
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
	
	
	userSettingsDelete: function(){
				
		var url = "/settings/userSettingsDelete/" + (Page.layerId == null ? 0 : Page.layerId)+"?eventGroupDelete=true&outSideEventGroupDelete=true";
		$.ajax({ type: "POST",   
		     url: url,   
		     async: true,
		     success : function(result)
		     {
				
				window.location = "";

		     }
		});
		
	},	

	
}

const EventTable = {
    dataTable: null,
    mapHelper: null,
    layerId: 0,
    cookieExpires: 7,
    dataTableRowCount: 20,
	isEventTablePage: true,
    tileLayerList : null,
    lastToggledEventId : null,
    datatableCriteria : null,
    pageRefreshDate: null,
    pageRefreshTimeInterval: null,
    currentPage: 0,
    
    Init: function () {

		Main.eventTableMode = true;
        EventTable.layerId = paramLayerId;
		Page.layerId = paramLayerId;
		Page.timeLineStartDate = paramTimeLineStartDate;
		EventTable.pageRefreshDate = pageRefreshDate;
		EventTable.pageRefreshTimeInterval = pageRefreshTimeInterval;

        EventTable.dataTableRowCount = dataTableRowCount;
        
        HeaderMenu.SidebarOpenAndCloseOperations();
   
     	Common.LoadEventGroups(EventTable.layerId);//sync
        
        EventTable.mapHelper = new MapHelper(EventTable.OpenDetailModal, Common.eventGroupList); 
       
        EventTable.PrepareHtmlElements(); 
        
		Common.Init(EventTable.mapHelper);
		
		DetailedSearch.Init();
		DetailedSearch.ShowDetailedSearchBadge();
		
		EventTable.GetEventGroupTreeData();
		Common.UserSettingsUnderLayer();
	
        Common.LoadLayers();
        Common.LoadTileServers();
        Common.LoadKey();
		Common.LoadTimeLine();
    	Common.SetTimeLine();

        Common.EventImageLightBox();
        EventTable.IntroToEventTable();
        
        EventTable.LoadTable();
        
		
		DetailedSearch.AdvancedSearchModalClose();

		Alert.Init(EventTable.mapHelper,EventTable.addAlertArea);
		
		
	    
	    
	    $("#eventTable_filter").css("display", "none");
	    
	    EventTable.RefreshTableSchedular();

		//mobil görünüme geçtiğinde
			
			$("#content").show();
			$('#news-live').hide();						
		//

    },

    InitializeEvents: function () {
	
		// harita disinda bir yere basinca kapanmasi icin
		$(document).mouseup(function(e) 
		{
		    var container = $("#mapContainer");
		
		    // if the target of the click isn't the container nor a descendant of the container
		    if (!container.is(e.target) && container.has(e.target).length === 0) 
		    {
		        container.hide();
		    }
		});
	
        document.getElementById('btnCloseMap').addEventListener("click", function () {
            EventTable.mapToggle(false);
        });
        
        $('#searchButton').click(function() {

			EventTable.Search();
		});

		// arti butonuna basilinca tablonun asagiya dogru acilmasi icin
        $('#eventTable tbody').on('click', 'td.dt-control', function () {
            let tr = $(this).closest('tr');
            let row = EventTable.dataTable.row(tr);
            /*
            var currentRowId = tr.attr("id");
            
    		$.each(EventTable.dataTable.rows().data(), function(index, value){
				
				var tempId = "rowId"+value.event.id;
				if(currentRowId == tempId){
					row = EventTable.dataTable.row(index);
				}
				
			});
			*/
            if (row.child.isShown()) {
                row.child.hide();
                tr.removeClass('shown');
                EventTable.lastToggledEventId = null;
            } else {
                EventTable.dataTable.rows().every(function () {
                    let row = this;
                    if (row.child.isShown()) {
                        row.child.hide();
                        $(this.node()).removeClass('shown');
                    }
                });
                
                row.child(EventTable.childRowFormater(row.data(), row[0])).show();
                tr.addClass('shown');
				$(".eventDetailModalMediaArea").removeAttr("id");
				Common.EventImageLightBox();
				Common.PopoverShow();				
                EventTable.lastToggledEventId = tr.attr("id");
            }
        });
    },
	
	ExcelFileDownload(){
		
		
		var maxCountEventsExcel = paramMaxCountEventsExcel;
		var tableTotalEventCount = $("#eventTable_info").text().split(" ")[0];
		tableTotalEventCount = tableTotalEventCount.split(",");		
		
		
		var resultCount = "";	
		if(tableTotalEventCount.length > 1){
			$.each(tableTotalEventCount, function(key, value){
				resultCount += value;
			});
		}else{
			resultCount = tableTotalEventCount[0];
		}
				
		tableTotalEventCount = parseInt(resultCount)
		
		
		var message = " " + lang.get('label.export.excel.limited.event.download').format(maxCountEventsExcel.toLocaleString()); 
		if(maxCountEventsExcel > tableTotalEventCount){
			maxCountEventsExcel = tableTotalEventCount;
			message = " " +maxCountEventsExcel + " kayıt indirilecektir.";
		}
		
		
		
		
		const swalWithBootstrapButtons = Swal.mixin({
		  customClass: {
		    confirmButton: 'btn btn-success excelDownloadConfirm',
		    cancelButton: 'btn btn-danger'
		  },
		  buttonsStyling: false
		})
		
		swalWithBootstrapButtons.fire({
//		  title: lang.get('label.are.you.sure.you.want.to.export.to.excel'),
		  text: lang.get('label.are.you.sure.you.want.to.export.to.excel') + message,
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonText: lang.get('label.ok'),
		  cancelButtonText: lang.get('label.no'),
		  reverseButtons: true
		}).then((result) => {
		  if (result.isConfirmed) {
			
			var cookieModel = Common.ReadCookies();

			var  url= contextPath + "event-table/export?layerId=" + (EventTable.layerId == null ? 0 : EventTable.layerId);
			
			var eventTextSearch = cookieModel.eventTextSearch;
			url = url + "&eventSearch=" + $.cookie("eventSearchText");

			var eventTypeIdSearch = cookieModel.eventTypeIdSearch;
			if(eventTypeIdSearch != null && eventTypeIdSearch != ""){	
				eventTypeIdSearch = eventTypeIdSearch.split(',');	
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
						
			var eventGroupdbNameIdList = cookieModel.selectedEventGroupList;//Common.GetShowEventGroupDbNameList(EventTable.mapHelper.eventGroupList, cookieModel.removedEventGroupList);
			url = url + "&eventGroupdbNameIdList=" + eventGroupdbNameIdList;
			
			
	
			var startDateStr = moment(cookieModel.startDate, DateUtils.ENGLISH).format(DateUtils.TURKISH)
			var endDateStr = moment(cookieModel.endDate, DateUtils.ENGLISH).format(DateUtils.TURKISH)
		
			url = url + "&startDateStr=" + startDateStr + "&endDateStr=" + endDateStr;	
			url = url + "&isAlertEvent=" + cookieModel.alertChecked;
		
		
			$("#timeDimensionSpinner").show();
			EventTable.ExcelStateInformationControl();
			
			window.location.href = url
			
			

		    
		  } 
		});

	},
	
	ExcelStateInformationControl() {

	    var excelStateInformation = "started";
	    setTimeout(function() {
	
	
	        $.ajax({
	            type: "GET",
	            url: "/event-table/excelStateInformation",
		     	contentType: "application/json",
	            success: function(data) {
	                excelStateInformation = data;
	
	                if (excelStateInformation == "finished") {
	
	                    $("#timeDimensionSpinner").hide();
	                } else {
	                    EventTable.ExcelStateInformationControl();
	                }
	
	            }
	        });
	
	    }, 1000)
	},

   	PrepareHtmlElements(){
			
		var cookieModel = Common.ReadCookies();
		$("#eventSearchEventTable").val(cookieModel.eventTextSearch);

	},
    
    childRowFormater: function(data, rowIndex){
		
		var mediaListStr = JSON.stringify(data.mediaList);
		let compareImageButtonText = "<button type='button' class='btn btn-primary' id='choosePictures' data-id='choosePictures' onclick=ImageCompare.ChoosePictures('"+mediaListStr+"') data-dismiss='modal'  data-toggle='modal' data-type='add' data-target='#chooseModalPictures'>"+lang.get('label.compare.pictures')+"</button>";

		let eventMediaText = "";
		let eventMediaVideoText = "";
		if(data.mediaList != null){
			data.mediaList.forEach( mediaItem => {
				
				if(mediaItem.isVideo){ 
					
					var path = mediaItem.path;
					path = path.startsWith("http") ? path : contextPathWithoutSlash + "/video/get/" + path;
					
					var videoTag =  '<video class="modal-media eventTableVideo" preload="none" controls style="display: block; width: 100%;">' +					
										'<source src="'+ path +'" type="video/mp4" />' +
										'Your browser does not support the video tag.' +
									'</video>';
					
					eventMediaVideoText += videoTag;
    
    			}else{
	
					var path = mediaItem.path;
					path = path.startsWith("http") ? path : contextPath + "image/get/" + path;
					eventMediaText += '<div data-responsive="" data-src="{0}" data-sub-html=""><img class="rounded modal-media" style="max-height:150px;" class="img-responsive" src="{0}"></div>'.format( path );	
	
				}
				
			});	
		}
							 
					
		
		let eventTagText = "";
		if(data.tagList != null){
			data.tagList.forEach( tagItem => { 
				eventTagText += '<a style="cursor: pointer;">{0}</a>'.format( tagItem.tagName ) ; 
			});	
		}
		
		let eventAlertText = "";
		let eventAlertDate = "";
		if(data.alertList != null){
			data.alertList.forEach( alertItem => { 
				eventAlertText += '<a style="cursor: pointer;">{0}</a>'.format( alertItem.alertName) ; 
				eventAlertDate = '<a style="cursor: pointer;">{0}</a>'.format(CustomFormatter.GetDateFormatted(alertItem.createDate)) ; 
			});	
		}
		
		if(data.alertList != null){
			$("#alertIconDiv_"+ data.dbName + data.event.id).children(".alertEventIcon").remove();
			$("#alertUnReadIcon_" + data.dbName + data.event.id).remove();
			$("#alertIconDiv_"+ data.dbName + data.event.id).prepend("<i class='fa fa-envelope-open fa-1x readStateIcon alertEventIcon' title='"+lang.get('label.alert.event.read')+"'  id='readStateIcon' style='color:green' aria-hidden='true'></i>")
	
		}
		
		var alertEventList = data.alertList;
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
		
		var description ="";
		if(data.event.description != ""){
			var cellData = data.event.description != null ? data.event.description : "-";	
			description = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.DESCRIPTION+'"style="cursor: pointer; width: fit-content;">'+cellData+'</div>';
		}
		
		var reservedLink ="";
		if(data.event.reservedLink != null){
			var replaceString = "{providerUserId}";
			var regExp = new RegExp(replaceString, 'g');
			data.event.reservedLink = data.event.reservedLink.replace(regExp, providerUserId);
			var cellData = data.event.reservedLink != null ? data.event.reservedLink : "-";	
			reservedLink = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVEDLINK+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
		}
		
		
	
		return `<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px; " >
                
                <!--
                <tr class="d-none">
                    <td>Id:</td>
                    <td>${data.event.id}</td>
                </tr>-->
                

                ${"<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(description, lang.get('label.event.description'))}

                ${"<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(reservedLink, lang.get('label.event.reserved.link'))}
                
                ${eventMediaText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{2}:</td> <td> <div class='eventDetailModalMediaArea'>{0}</div> {1} </td></tr>".format(eventMediaText,eventMediaVideoText, lang.get('label.event.images')) + "<tr><td> {0} </td></tr>".format(compareImageButtonText) }
                
                ${eventTagText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventTagText, lang.get('label.news.tags'))}
                
                ${eventAlertText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventAlertText, lang.get('label.news.alarms'))}

 				${eventAlertDate == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventAlertDate,  lang.get("label.alert.event.create.date"))}

                
            </table>`
	
	},
	LoadTable: function () {
    	
		EventTable.dataTable = $('#eventTable').DataTable({
			pageLength: EventTable.dataTableRowCount,
			"lengthChange": false,
			"autoWidth": true,
			"orderMulti": false,
			"paging": true,
			"pagingType": "full_numbers",
	        "processing": true,
	        "serverSide": true,
	        layerId: "0",
	        searching: true,
	       
	        scrollY: true,
	        scrollX: true,
	        language: {
    			infoFiltered: "",
    			info: lang.get('label.datatable.page.info'), // TODO: dil dosyasından gelmeli
    			"decimal":        "",
			    "emptyTable": lang.get('label.datatable.no.data.available'),
			    //"info":           "Showing _START_ to _END_ of _TOTAL_ entries",
			    "infoEmpty":      lang.get('label.datatable.page.info.empty'),
			    //"infoFiltered":   "(filtered from _MAX_ total entries)",
			    "infoPostFix":    "",
			    "thousands":      ",",
			    //"lengthMenu":     "Show _MENU_ entries",
			    //"loadingRecords": "Loading...",
			    "processing":     "",
			    //"search":         "Search:",
			    //"zeroRecords":    "No matching records found",
			    //"processing": "<span class='fa-stack fa-lg'>\n\
		        //                    <i class='fa fa-spinner fa-spin fa-stack-2x fa-fw'></i>\n\
		        //               </span>&emsp;Processing ...",
		        


			    "paginate": {
			        "first":      lang.get('label.datatable.first'),
			        "last":       lang.get('label.datatable.last'),
			        "next":       lang.get('label.datatable.next'),
			        "previous":   lang.get('label.datatable.previous')
			    },
			    //"aria": {
			    //    "sortAscending":  ": activate to sort column ascending",
			    //    "sortDescending": ": activate to sort column descending"
			    //}
  			},
	        ajax: {
	            "url": "/event-table/" + EventTable.layerId,
	            "type": "POST",
	            "dataType": "json",
	            "contentType": "application/json",
	            "data": function (d) {

					var cookieModel = Common.ReadCookies();
					
					d.columns[3].search.value = cookieModel.eventTextSearch;
					d.columns[8].search.value = cookieModel.eventSearchCity;
					d.columns[9].search.value = cookieModel.eventSearchCountry;
					d.columns[1].search.value = cookieModel.eventTypeIdSearch;
					
					
					d.columns[10].search.value = JSON.stringify(cookieModel.selectedEventGroupList);
					
					d.columns[2].search.value = cookieModel.alertChecked;
					
					d.columns[6].search.value = moment(cookieModel.startDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
					
					d.columns[7].search.value = moment(cookieModel.endDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
					
					
					EventTable.datatableCriteria = d;
					EventTable.currentPage = d.start;
					
	                return JSON.stringify(d);
	            },
	            beforeSend: function( xhr ) {
			    	$("#tableContainer").block({ message: null }); 
					$('[data-toggle="popover"]').popover('hide');
			  	},
			  	complete: function( xhr ) {
			    	$("#tableContainer").unblock({ message: null }); 
					Common.PopoverShow();
					DetailedSearch.ShowDetailedSearchBadge();
			  	},
			  	
	        },
	        order: [[5, 'desc']],
	        fnCreatedRow: function (nRow, aData, iDataIndex) { // Her bir satira rowid eklemek icin

		        $(nRow).attr('id', 'rowId' + aData.event.id); // or whatever you choose to set as the id
		        if(aData.event.id == null){
					$(nRow).addClass("d-none");
				}
		    },
	        columns: [
				{
					// En soldaki child row'u açma butonu sütunu.
					className: 'dt-control',
					orderable: false,
					data: null,
					defaultContent: '',
					width: "20px",
				},
				{
					data: "event.eventTypeId",
					name: "eventTypeId",
					orderable: false,
					searchable: true,
					className: "dt-center",
					width: "20px",
	            	render: function(data, type, row, full) {
						
						return !data ? "-" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTTYPE+'"style="cursor: pointer;width: fit-content;"><img class="i-circled i-custom" src="{2}image/markerImg?eventTypeId={0}&amp;color={1}&amp;selectedIconControl=false">'.format(row.event.eventTypeId, row.event.color.replace("#", "%23"), contextPath)+'</div>';		
					}
				},
				{
					data: "alertList",
					name: "alert",
					orderable: false,
					searchable: false,
					className: "dt-center",
					width: "20px",
	            	render: function(data, type, row) {
						
						var alarmText = "";
						
						if(row.alertList != null && !row.alertList[0].readState){										
                            alarmText += "<i class='fa fa-envelope fa-1x readStateIconClose alertEventIcon' id='alertUnReadIcon_"+row.dbName+row.event.id+"'title='"+lang.get('label.alert.event.unread')+"'  style='color:red;' aria-hidden='true'></i>"
						}
						
						if(row.alertList != null && row.alertList[0].readState){							
							alarmText += "<i class='fa fa-envelope-open fa-1x alertEventIcon' title='"+lang.get('label.alert.event.read')+"' style='color:green;' aria-hidden='true'></i>"
						}
						
						if(row.alertList != null){
							alarmText += '<i class="fa fa-bell fa-lg" title="'+lang.get('label.alarm')+'" style="color: red;" aria-hidden="true"></i>';
						}
						
						var alertDiv = "<div id='alertIconDiv_"+row.dbName+row.event.id+"'>"+alarmText+"</div>";
						
						
						return alertDiv;
					}
				},
				{
					data: 'event.title',
					name: 'title',
					searchable: true,
					orderable: false,
					render: function(data, type, row, full) {
						
						var cellData = data != null ? data : "-";	
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.TITLE+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
	            {	
					data: "event.latitude",	
					searchable: true,
					orderable: false,
					width: "70px",
					render: function(data, type, row, full) {
						
						var coordinateFormatted = CustomFormatter.ConvertDMS(row.event.latitude, row.event.longitude, "</br>");
						
						var result = `<div onclick="EventTable.mapSwitch(${row.event.eventTypeId}, '${row.event.color}', ${row.event.latitude}, ${row.event.longitude}, '${row.dbName}', ${row.event.id});" style="cursor: pointer; color: DodgerBlue; font-weight:bold;">
                            
			                                ${coordinateFormatted}
			                            
			                        </div><div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="${full.row}" data-event-table-column-enum-id-list="${EventTableColumnE.LATITUDE}_${EventTableColumnE.LONGITUDE}" style="cursor: pointer;width: fit-content;"><i class="fas fa-link"></i></div>`;
						
						return result;
					}
					                        
				},
	            {
					data: "event.eventDate",
					orderable: false,
	            	render: function(data, type, row, full) {
						
						var eventDateFormatted = CustomFormatter.GetDateFormatted(row.event.eventDate);					
						var cellData = data != null ? eventDateFormatted : "-";	
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTDATE+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},	
				 {
					data: "event.eventDate",
					name: "startDateStr",
					orderable: false,
					visible:false,
	            	render: function(data, type, row) {
						
						var eventDateFormatted = CustomFormatter.GetDateFormatted(row.event.eventDate);
						
						return !data ? "-" : eventDateFormatted;
					}
				},
				 {
					data: "event.eventDate",
					name:"endDateStr",
					orderable: false,
					visible:false,
	            	render: function(data, type, row) {
						
						var eventDateFormatted = CustomFormatter.GetDateFormatted(row.event.eventDate);
						
						return !data ? "-" : eventDateFormatted;
					}
				},	
					
				{
					data: "event.city",
					name: "city",
					visible: false,
					orderable: false,
					render: function(data, type, row) {
						return !data ? "-" : (row.event.city + " / " + row.event.country);
					}
				},
							
	           	{
					data: "event.country",
					name: "country",
					orderable: false,
	            	render: function(data, type, row, full) {
						var cityAndCountry = "";						
						if(row.event.city != ""){
							cityAndCountry = row.event.city != null ? row.event.city + " / " : "-";								
						}
						
						if(row.event.country != ""){
							cityAndCountry += row.event.country != null ? row.event.country : "-";								
						}
						
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id-list="'+EventTableColumnE.COUNTRY+'_'+EventTableColumnE.CITY+'" style="cursor: pointer;width: fit-content;">'+cityAndCountry+'</div>';
					}
				},	

				{
					data: "event.groupName",
					name: "eventGroupIdList",
					orderable: false,
	            	render: function(data, type, row, full) {		
						var eventGroupHtml = "<span style='color:{1}'>{0}</span>".format(row.event.groupName, row.event.color) ;
						return !data ? "-" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTGROUP+'"style="cursor: pointer;width: fit-content;">'+eventGroupHtml+'</div>';		
					}
				},
				
				{
					data: "event.spot",
					name: "spot",
					orderable: false,
	            	render: function(data, type, row, full) {		
						var cellData = data != null ? data : "-";						
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.SPOT+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
						
					}
				},
				
				{
					data: "event.reserved1",
					name: "reserved1",
					visible: true,
					orderable: false,
					render: function(data, type, row, full) {
						var cellData = row.event.reserved1 != null ? row.event.reserved1 : "-";
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED1+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
				{
					data: "event.reserved2",
					name: "reserved2",
					visible: true,
					orderable: false,
					render: function(data, type, row, full) {
						var cellData = row.event.reserved2 != null ? row.event.reserved2 : "-";
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED2+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
				{
					data: "event.reserved3",
					name: "reserved3",
					visible: true,
					orderable: false,
					render: function(data, type, row, full) {
						var cellData = row.event.reserved3 != null ? row.event.reserved3 : "-";
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED3+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
				{
					data: "event.reserved4",
					name: "reserved4",
					visible: true,
					orderable: false,
					render: function(data, type, row, full) {
						var cellData = row.event.reserved4 != null ? row.event.reserved4 : "-";
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED4+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
				{
					data: "event.reserved5",
					name: "reserved5",
					visible: true,
					orderable: false,
					render: function(data, type, row, full) {
						var cellData = row.event.reserved5 != null ? row.event.reserved5 : "-";
						return '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED5+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					}
				},
				
	        ]
	    });
	    

	    
   	
    },
    RefreshTableSchedular: function(){
	
		setTimeout(EventTable.RefreshTable, EventTable.pageRefreshTimeInterval * 1000);
	},    
    RefreshTable: function(){
	
		// Birinci sayfa dışında refresh çalışmasın diye
		if(EventTable.currentPage != 0){
			EventTable.RefreshTableSchedular();
			return;
		}
		
		var data = EventTable.datatableCriteria;
		data.search.value = EventTable.dataTable.row().data().event.id;
		data.refresh = true;
		data.pageRefreshDate = EventTable.pageRefreshDate;
	
		$.ajax({ type: "POST",   
		
		     url: "/event-table/" + EventTable.layerId,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {				
				
				
				var dataLength = result.data.length;
				if(dataLength == undefined || dataLength < 1){
					
					return;	
				}
				
				
				
				var allData = EventTable.dataTable.rows().data();
				
				
				for(var i = 0; i < dataLength; i++){
					allData.pop();
				}
				
				
				
				$.each(result.data.reverse(), function( index, value ) {
					
					allData.unshift(value);
				});
				
				
				$.each(allData, function( index, value ) {
				
					
					if(value.event.id == null){
						return;
					}
					
					var currentNode = EventTable.dataTable.row(index).node();
					$(currentNode).attr("id", "rowId"+value.event.id);
					$(currentNode).removeClass("d-none");
					
					
					EventTable.dataTable.row(index).data(value);
					
				});
				
				
				if(EventTable.lastToggledEventId != null){
					
					$("#" + EventTable.lastToggledEventId + " td").first().click();
				}
			
		     }
		}).always(function() {
		    EventTable.RefreshTableSchedular();
		});
		
	},
	
	
	GetEventGroupTreeData: function(){
		var cookieModel = Common.ReadCookies();
		var eventGroupdbNameIdList = Common.GetShowEventGroupDbNameList(Common.eventGroupList, cookieModel.removedEventGroupList);
		Common.GetEventGroupTreeData(eventGroupdbNameIdList);

	},
	
	scrollToTop: function (rowId) {
		
		
		// en yukarinin biraz asagisina scroll yapar
		// TODO: bunun yerine asagidaki commenyli yer denenecek.
		/*
		.example {
		  scroll-margin-top: 10px;
		}
		
		This affects scrollIntoView code, like this code:
		
		const el = document.querySelector(".example");
		el.scrollIntoView({block: "start", behavior: "smooth"});
		*/
		
		
		var targetEle = document.getElementById(rowId);
		var pos = targetEle.style.position;
		var top = targetEle.style.top;
		targetEle.style.position = 'relative';
		targetEle.style.top = '-55px';
		targetEle.scrollIntoView({behavior: 'smooth', block: 'start'});
		targetEle.style.top = top;
		targetEle.style.position = pos;
	},
    mapSwitch: function (eventTypeId, color, latitude, longitude, dbName, id) {
		
		EventTable.scrollToTop('rowId'+id);

		var markerItem = {
			eventTypeId: eventTypeId,
			color: color,
			latitude: latitude, 
			longitude: longitude,
			dbName: dbName,
			id: id
		}
		EventTable.mapHelper.addEventToMap(markerItem);
		
		EventTable.mapToggle(true);
		
	
    },
    mapToggle: function (state) {
	
		if(state){
			$("#mapContainer").show();
			EventTable.mapHelper.MyMap.invalidateSize();
		}else{
			$("#mapContainer").hide();
		}
	},
    
    OpenDetailModal: function () {
	},
	

	LoadTileServers: function () {
    	
    	var url = '/component/tileServer';

		$.ajax({ type: "GET",   
		     url: url,   
		     async: true,
		     success : function(tileServerList)
		     {
		    	 var tileServers = tileServerList;
		    	 EventTable.tileLayerList = EventTable.mapHelper.addTileServers(tileServers);

		    	 Common.AddMapControlBoxWhenReady();
		     }
		});
   	
    },
 
    
    getExtraParams : function (params) {
		
		$(".advanced-search").find("input, textarea, select").each(function(){
			
			if($(this).attr("name") === undefined) return;
			if($(this).is(":checkbox")){
				
				if($(this).is(":checked")){
					//params.push({name: $(this).attr("name"), value: $(this).val()});
					
					EventTable.dataTable.column($(this).attr("cid")).search($(this).val());				
				}
				return;
			}
			//params[$(this).attr("name")] = $(this).val();

			EventTable.dataTable.column($(this).attr("cid")).search($(this).val());			
		});
		
	},
	Search: function () {// simple search

        
		  	//var params = [];
			//EventTable.getExtraParams(params);
			//EventTable.dataTable.draw();
			
			Common.setCookies(true);
			
			EventTable.dataTable.draw();

    },
    
	SetSearchModel: function(cookieModel){
		
		EventTable.dataTable.column(3).search(cookieModel.eventTextSearch);	
		
	},

	
	IntroToEventTable: function(){
		   	
        
        $("#intro-to").click( function(){
        
        	const introTable = introJs();
	
	        let td = $("td.dt-control");
			let tr = $("td.dt-control").closest('tr');
            let row = EventTable.dataTable.row(tr); 
            var trFirstItem = row.data();
	
			var stepsEventTable = [
				{intro:"Katmana ait olayların listelendiği tablo sayfasıdır.Olaylar günümüzden geçmişe doğru kronolojik sıra ile listelenmektedir. Yeni tarihli bir olay geldiğinde en üstte görüntülenmektedir.", name:"EventTableIntro"},
				{element:"#eventSearchEventTable", intro:"Başlık, kısa açıklama ve açıklama alanında arama yapılmaktadır.", name:"SearchEventTable"},
				{element:".btn.btn-sm.eventTableExcelExport", intro:"Tabloda bulunan olayları, filtrelere uygun olarak excel dosyası formatında indirmek için kullanılır.", name:"ExcelExport"},
				{element:$(".detailedSearchSpan")[2], intro:"Olaylar üzerinde arama yapmak için detaylı arama butonu.", name:"DetailedSearchButton"},
				{element:".detailed-search-form-step", intro:"Olaylar üzerinde filtreleme yapabilmek için kullanılır. Aranan değerlere uygun sonuçlar tabloda gösterilir. Yeni bir olay geldiğinde filtreye uygun ise tabloda gösterilir. Arama kriterlerini temizlemek için temizle butonu kullanılabilir.", name:"DetailedSearchModal"},
				{element:"#eventSearchDetailed", intro:"Başlık, kısa açıklama ve açıklama alanına girilen kriter bu üç alandan herhangi birinde uygun olay var ise getirilir.", name:"DetailedSearchModal"},
				{element:"#eventSearchCity", intro:"Şehir alanına girilen bilgi şehir alanında aranmaktadır. Uygun veri var ise getirilir.", name:"DetailedSearchModal"},
				{element:"#eventSearchCountry", intro:"Ülke alanına girilen bilgi ülke alanında aranmaktadır. Uygun veri var ise getirilir.", name:"DetailedSearchModal"},
				{element:".form-group.m-select2.search", intro:"Olay türü seçilmişse seçilen olay türüne ait olaylar getirilmektedir.", name:"DetailedSearchModal"},
				{element:".alert-events", intro:"Alarmlı olay butonu seçilmişse belirtilen kriterlere uygun olan alarmlı olaylar getirilmektedir.", name:"DetailedSearchModal"},
				{element:"#theTree", intro:"Olay gruplarına göre filtreleme yapılabilmektedir. Seçili olan olay gruplarına ait olaylar ekranda gösterilmektedir. Olay grubuna göre filtreleme yapabilmek için gösterilmek istenen gruplar seçilir ve "
				+"arama işlemi gerçekleştirilir.", name:"DetailedSearchModalTree"},
				{element:tr[0], intro:"Olay bilgileri gösterilir. Marker, alarm bilgisi, alarmlı olayın okunma durumu, başlık, koordinat bilgisi, olayın gerçekleştiği tarih, şehir, ülke, ve olay grubu bilgileri listelenmektedir.", name:"TrFirst"},
				{element:td[0], intro:"Olay detayı buraya tıklayarak görüntülenebilir. Detay bilgilerinde kısa ve uzun açıklama, ayrılmış anahtar, medyalar, alarmlar ve alarmlı olayın oluştuğu tarih bilgisi gösterilmektedir. Aynı zamanda bir alarmlı olayın "
				+"detay bilgisi açılığında okunmuş olarak işaretlenir.", name:"TdFirst"},
				{element:tr[0].children[4], intro:"Olayın kordinat bilgileri gösterilmektedir. Tıklanınca olayın haritadaki konumu gösterilir.", name:"LatLong"},
				{element:"#mapContainer", intro:"Olayın harita üzerinde gösterilmesi.", name:"MiniMap"},
				{element:".dataTables_paginate.paging_full_numbers", intro:"Sayfalama.", name:"Paging"},
				{intro:"Son.", name:"Finish"},
				];
				
			var detailedSearchModal = $("#detailModalSearch");
			
			var modals = [detailedSearchModal]
			var stepsEventTableModified = [];
			
			for (var i = 0;i<stepsEventTable.length; i++) {
				if ($(stepsEventTable[i].element).is(':hidden') && (['TrFirst','TdFirst','LatLong'].includes(stepsEventTable[i].name))) {
					if (stepsEventTable[i].name == "LatLong") {
						//Eger tabloda hic olay yoksa kordinat gösterme adımından sonra minimapi adımını atla
						i++;
						continue;
					}
					continue;
				}
				stepsEventTableModified.push(stepsEventTable[i])		
			}
			
			introTable.onexit(function() {
				for (var i = 0; i<modals.length; i++){
					if(modals[i].is(':visible')) {
						modals[i].modal('hide');
					}
				}
				if (row.child.isShown()) {
	                row.child.hide();
	                tr.removeClass('shown');
            	}
			});	
			
			introTable.onchange(function() {
				
				if (introTable._introItems[introTable._currentStep].name == "DetailedSearchModal" || introTable._introItems[introTable._currentStep].name == "DetailedSearchModalTree") {
					if (detailedSearchModal.is(':hidden')){
						DetailedSearch.AdvancedSearchModalOpen();
					}
				}
				else {
					if(detailedSearchModal.is(':visible')) {
						detailedSearchModal.modal('hide');
					}
				}
				
				if (introTable._introItems[introTable._currentStep].name == "TdFirst") {
	                row.child(EventTable.childRowFormater(row.data())).show();
	                tr.addClass('shown');
				}
				else {
		            if (row.child.isShown()) {
		                row.child.hide();
		                tr.removeClass('shown');
            		}
				}
				
				if (introTable._introItems[introTable._currentStep].name == "MiniMap") {
					EventTable.mapSwitch(row.data().event.eventTypeId, row.data().event.color, row.data().event.latitude, row.data().event.longitude, row.data().event.dbName, row.data().event.id);
				}
				else {
					if (EventTable.mapToggle()) {
						EventTable.mapToggle(false);
					}
				}
				
				promise = new Promise((resolve) => {setInterval(resolve, 150)} )				
				return promise;
			});
			
			introTable.setOptions({
				steps:stepsEventTableModified,
				nextLabel:"İleri",
				prevLabel:"Geri",
				doneLabel:"Son",
				showStepNumbers:true
			}).start();
		});
	},

   addAlertArea: function(){
	
	}
    
    
}

document.addEventListener('DOMContentLoaded', function () {

    EventTable.Init();
    EventTable.InitializeEvents();
});

// TODO: string formatter override, generic bir yere tasinacak
if (!String.prototype.format) {
  String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) { 
      return typeof args[number] != 'undefined'
        ? args[number]
        : match
      ;
    });
  };
}



