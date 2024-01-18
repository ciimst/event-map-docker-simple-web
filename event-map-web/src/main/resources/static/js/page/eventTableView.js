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
    SearchParams: {},
	userSettingsTypeColumn: [],
    Init: function () {
		
		EventTable.UserSettingsGetData();
		Main.eventTableViewMode = true;
        EventTable.layerId = paramLayerId;
        EventTable.eventTypeList = eventTypeList;
        EventTable.eventGroupList = eventGroupList;

        EventTable.dataTableRowCount = dataTableRowCount;
        
        
   
     	Common.LoadEventGroups(EventTable.layerId);//sync
        
        EventTable.mapHelper = new MapHelper(EventTable.OpenDetailModal, Common.eventGroupList); 
       	HeaderMenu.SidebarOpenAndCloseOperations();
        
		Common.Init(EventTable.mapHelper);
		EventTable.GetEventGroupTreeData();
	
        Common.LoadLayers();
        Common.LoadTileServers();
        Common.LoadKey();
		Common.LoadTimeLine();
    	Common.SetTimeLine();

        EventTable.LoadTable();
        		
		Alert.Init(EventTable.mapHelper,EventTable.addAlertArea);
	    
	    $("#eventTable_filter").css("display", "none");
	    

		//mobil görünüme geçtiğinde
			
			$("#content").show();
			$('#news-live').hide();						
    },

    InitializeEvents: function () {
		
		$('#eventTypeId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
			multiple:true,
		}).on("select2:unselecting", function(e) {
			$(this).data('state', 'unselected');
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            EventTable.SearchParams[columnName] = value;
			$('select#eventTypeIdColumn').val(value).change();
			EventTable.dataTable.draw();				    		
		}).on("select2:select", function(e) {
						
			var columnName = $(e.target.parentElement).attr('data-column')
            //var value = e.target.value;
            let valuesArr = Array.from(e.target.selectedOptions, option => option.value);
            values = valuesArr.toString();
            EventTable.SearchParams[columnName] = values; 
            $('select#eventTypeIdColumn').val(valuesArr).change();          
			EventTable.dataTable.draw();
				    			
		});
		
		$('#eventGroupId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		}).on("select2:unselecting", function(e) {
			$(this).data('state', 'unselected');
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            EventTable.SearchParams[columnName] = value;
			$('select#eventGroupIdColumn').val(value).change();
			EventTable.dataTable.draw();				    		
		}).on("select2:select", function(e) {
							    		
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            EventTable.SearchParams[columnName] = value;
            $('select#eventGroupIdColumn').val(value).change();
			EventTable.dataTable.draw();	
		});
		
		$('#state').select2({
			allowClear: true,
			autoclose: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		}).on("select2:unselecting", function(e) {
			$(this).data('state', 'unselected');
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            EventTable.SearchParams[columnName] = value;
			$('select#stateColumn').val(value).change();
			EventTable.dataTable.draw();				    		
		}).on("select2:select", function(e) {
			
				    		
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            EventTable.SearchParams[columnName] = value;
            $('select#stateColumn').val(value).change();
			EventTable.dataTable.draw();	
		});
		

		$('#startDate').datetimepicker({
			format: DateUtils.ENGLISH,
			locale: "tr-TR",
			ampm: false,
			showClose: true,
		});
		
		$('#endDate').datetimepicker({
			format: DateUtils.ENGLISH,
			locale: "tr-TR",
			ampm: false,
			showClose: true,
		});

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
    
    ClearFilters() {
		$('#searchForm')[0].reset();
		$('#searchForm select').change()
		
		$(".filters th input").each(function() {
			
			if (!this.checkVisibility()) {
				return;
			}
			
			if (this.id == "alertColumn") {
				$('#alertColumn').prop("checked", false);
				$(this).change();
			}
			else {
				$(this).val('');
			}
									
		});
		
		$(".filters th select").each(function() { 
			
			if (!this.checkVisibility()) {
				return;
			}
			
			$(this).val('').change();			
		});	
		
		EventTable.SearchParams = {};
		$('.searchButton').trigger("click");		
//		$("#eventTable").DataTable().search("").draw()		
//		EventTable.dataTable.draw();
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
		  text: lang.get('label.are.you.sure.you.want.to.export.to.excel') + message,
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonText: lang.get('label.ok'),
		  cancelButtonText: lang.get('label.no'),
		  reverseButtons: true
		}).then((result) => {
		  if (result.isConfirmed) {
			
			var eventGroupSelectedControl = EventTable.BatchOperationsEventGroupSelectedControl();
			if(!eventGroupSelectedControl){
				return;
			}
			
			var eventTypeIds = EventTable.SearchParams.eventTypeId != undefined ? EventTable.SearchParams.eventTypeId :  0;
			if (eventTypeIds != 0) {
				var eventTypeIdsInt = eventTypeIds.split(',').map(function(item) {
    				return parseInt(item);
				});
			}
			else {
				var eventTypeIdsInt = [];
			}
			
			var eventTableViewExcelItem = {
				
				title: EventTable.SearchParams.title != undefined ? EventTable.SearchParams.title :  "",
				spot: EventTable.SearchParams.spot != undefined ? EventTable.SearchParams.spot :  "",
				description: EventTable.SearchParams.description != undefined ? EventTable.SearchParams.description :  "",
				city: EventTable.SearchParams.city != undefined ? EventTable.SearchParams.city :  "",
				country: EventTable.SearchParams.country != undefined ? EventTable.SearchParams.country :  "",
				eventGroupId: EventTable.SearchParams.eventGroupId != undefined ? EventTable.SearchParams.eventGroupId :  0,
				eventTypeId: eventTypeIdsInt,
				startDateStr: EventTable.SearchParams.startDateStr != undefined ? EventTable.SearchParams.startDateStr :  "",
				endDateStr: EventTable.SearchParams.endDateStr != undefined ? EventTable.SearchParams.endDateStr :  "",
				state: EventTable.SearchParams.state != undefined ? EventTable.SearchParams.state : null,
				blackListTag: EventTable.SearchParams.blackListTag != undefined ? EventTable.SearchParams.blackListTag : null,
				reserved1: EventTable.SearchParams.reserved1 != undefined ? EventTable.SearchParams.reserved1 : null,
				reserved2: EventTable.SearchParams.reserved2 != undefined ? EventTable.SearchParams.reserved2 : null,
				reserved3: EventTable.SearchParams.reserved3 != undefined ? EventTable.SearchParams.reserved3 : null,
				reserved4: EventTable.SearchParams.reserved4 != undefined ? EventTable.SearchParams.reserved4 : null,
				reserved5: EventTable.SearchParams.reserved5 != undefined ? EventTable.SearchParams.reserved5 : null,
				isAlertEvent: EventTable.SearchParams.alert != undefined ? EventTable.SearchParams.alert : false
				
			}
		
			var url = "";
			url = contextPath + "event-table-view/export/"+ EventTable.layerId + "/" + encodeURIComponent(JSON.stringify(eventTableViewExcelItem));
	
			$("#timeDimensionSpinner").show();
			EventTable.ExcelStateInformationControl();
			window.location.href = url;		
		    
		  } 
		});

	},
	
	ExcelStateInformationControl() {

	    var excelStateInformation = "started";
	    setTimeout(function() {
	
	
	        $.ajax({
	            type: "GET",
	            url: "/event-table-view/excelStateInformation",
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
		
//		$("#primaryRightMenuUl li").removeClass("active")
//		$("a[name='event-table-view']").parent().addClass("active")
		
//		var cookieModel = EventTable.ReadCookies();
//		$("#eventSearchEventTable").val(cookieModel.eventTextSearch);

	},
    
    childRowFormater: function(data, rowIndex){
		
		var mediaListStr = JSON.stringify(data.mediaList);
		let compareImageButtonText = "<button type='button' class='btn btn-primary' id='choosePictures' data-id='choosePictures' onclick=ImageCompare.ChoosePictures('"+mediaListStr+"') data-dismiss='modal'  data-toggle='modal' data-type='add' data-target='#chooseModalPictures'>"+lang.get('label.compare.pictures')+"</button>";

		let eventMediaText = ""
		let eventMediaVideoText = ""
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
		
//		let eventBlackListTagText = "";
//		if(data.event.blackListTag != null){
//			eventBlackListTagText = '<a style="cursor: pointer;">{0}</a>'.format( data.event.blackListTag ) ; 
//		}
		
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
		
		if(EventTable.userSettingsTypeColumn.includes("alertEvent")){
			Common.AlertList(alertIds, alertEventIds)
		}
		
		
		var description ="";
		if(data.event.description != ""){				
			var cellData = data.event.description != null ? data.event.description : "-";	
			description = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.DESCRIPTION+'"style="cursor: pointer; width: fit-content;">'+cellData+'</div>';
		}
		
		var reservedKey ="";
		if(data.event.reservedKey != null && data.event.reservedKey != ""){
			var cellData = data.event.reservedKey != null ? data.event.reservedKey : "-";	
			reservedKey = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVEDKEY+'"style="cursor: pointer; width: fit-content;">'+cellData+'</div>';
		}
		
		var reservedType ="";
		if(data.event.reservedType != null){
			var cellData = data.event.reservedType != null ? data.event.reservedType : "-";	
			reservedType = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVEDTYPE+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
		}
		
		var reservedId ="";
		if(data.event.reservedId != null){
			var cellData = data.event.reservedId != null ? data.event.reservedId : "-";	
			reservedId = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVEDID+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
		}
		
		var reservedLink ="";
		if(data.event.reservedLink != null){
			
			var replaceString = "{providerUserId}";
			var regExp = new RegExp(replaceString, 'g');
			data.event.reservedLink = data.event.reservedLink.replace(regExp, providerUserId)
			
			var cellData = data.event.reservedLink != null ? data.event.reservedLink : "-";	
			reservedLink = '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+rowIndex+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVEDLINK+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
		}
		
		
	
		return `<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px; " >
                
                <!--
                <tr class="d-none">
                    <td>Id:</td>
                    <td>${data.event.id}</td>
                </tr>-->
                

                ${!EventTable.userSettingsTypeColumn.includes("description") ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(description, lang.get('label.event.description'))}

                ${!EventTable.userSettingsTypeColumn.includes("reservedLink")? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(reservedLink, lang.get('label.event.reserved.link'))}
				
				${!EventTable.userSettingsTypeColumn.includes("reservedType") ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(reservedType, lang.get('label.event.reserved.type'))}
		
		  		${!EventTable.userSettingsTypeColumn.includes("reservedId")? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(reservedId, lang.get('label.event.reserved.id'))}

  				${!EventTable.userSettingsTypeColumn.includes("reservedKey") ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> {0} </td> </tr>".format(reservedKey, lang.get('label.event.reserved.key'))}
                
                ${!EventTable.userSettingsTypeColumn.includes("media") || eventMediaText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{2}:</td> <td> <div class='eventDetailModalMediaArea'>{0}</div> {1} </td></tr>".format(eventMediaText,eventMediaVideoText, lang.get('label.event.images')) + "<tr><td> {0} </td></tr>".format(compareImageButtonText) }
                
                ${!EventTable.userSettingsTypeColumn.includes("tag") || eventTagText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventTagText, lang.get('label.news.tags'))}
                
			
                ${!EventTable.userSettingsTypeColumn.includes("alertEvent") || eventAlertText == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventAlertText, lang.get('label.news.alarms'))}
				${!EventTable.userSettingsTypeColumn.includes("alertEvent") || eventAlertDate == "" ? "" : "<tr> <td class='childFormatterTableTd'>{1}:</td> <td> <div class='tagcloud'> {0} </div> </td> </tr>".format(eventAlertDate,  lang.get("label.alert.event.create.date"))}


                
            </table>`

	},
	LoadTable: function () {
		
		$('#eventTable thead tr').clone(true).addClass('filters').appendTo('#eventTable thead');
    	
		EventTable.dataTable = $('#eventTable').DataTable({
	        orderCellsTop: true,
    		fixedHeader: true,
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
	        width:"100%",
	       	
	        scrollY: true,
	        scrollX: true,
	        
	        initComplete: function () {
							    
            	var api = this.api();        
            	
   				api.columns().eq(0).each(function (colIdx) {
					   
					if (colIdx < 1) {
						return;
					}
					
                    var cell = $('.filters th').eq( $(api.column(colIdx).header()).index() );
                    
                    if (!$(api.column(colIdx).header())[0].checkVisibility()) {
						return;
					}
					
                    var title = $(cell).text();
					if ($(api.column(colIdx).header()).hasClass('selectSearch')) {
												
						var select = $('<select id="' + $(cell).attr('data-search') + 'Column" class="form-control m-select2 m_select2_12_4 select2 searchColumn"><option value=""></option></select>');
						
						if ($(cell).attr('data-search') == "eventTypeId") {
		                	var select = $('<select multiple="multiple" id="' + $(cell).attr('data-search') + 'Column" class="form-control m-select2 m_select2_12_4 select2 searchColumn"></select>');
						}
					
						// select on change
	                    select.appendTo($(cell).empty())
	                    .on('change', function (e) {
														
													
//	                        api.column(colIdx).search(val ? val : '', true, false).draw(); // Gerekli olabilir silme
	                    });
	                    
	                    //select2
                		$('#' + $(cell).attr('data-search') + 'Column').select2({
							allowClear: true,
							placeholder: title,
							dropdownAutoWidth: true,
						}).on("select2:unselecting", function(e) {
							
							if (this.id == "eventTypeIdColumn") {
								var valArray = Array.from(e.target.selectedOptions, option => option.value);
								val = valArray.toString();
							}
							else {
//								var val = $.fn.dataTable.util.escapeRegex($(this).val());
								var val = $(this).val();
							}
				    		
				    		var columnName = $(this).parent().attr('data-search');
                        	if (columnName != null) {
								EventTable.SearchParams[columnName] = val;
							}
							
							if (this.id == "eventTypeIdColumn") {
								$("#" + columnName).val(valArray).change();								
							}
							else {
								$("#" + columnName).val(EventTable.SearchParams[columnName]).change();
							}
							
							EventTable.dataTable.draw();
							
				    		$(this).data('state', 'unselected');	
				    					    		
						}).on("select2:open", function(e) {
						    if ($(this).data('state') === 'unselected') {
								
						        $(this).removeData('state'); 
						
						        var self = $(this);
						        setTimeout(function() {
						            self.select2('close');
						        }, 1);
						    }    
						}).on("select2:select", function(e) {
				    		
				    		var valArray;
				    		
				    		if (this.id == "eventTypeIdColumn") {
								valArray = Array.from(e.target.selectedOptions, option => option.value);
								val = valArray.toString();
							}
							else {
								var val = $(this).val();
							}
				    		
				    		var columnName = $(this).parent().attr('data-search');
                        	if (columnName != null) {
								EventTable.SearchParams[columnName] = val;
							}
							
							if (this.id == "eventTypeIdColumn") {
								$("#" + columnName).val(valArray).change();								
							}
							else {
								$("#" + columnName).val(EventTable.SearchParams[columnName]).change();
							}	
							EventTable.dataTable.draw();
						});
						
						//Select boxların icinin doldurulması
	                    if ($(cell).attr('data-search') == 'eventTypeId') {
							EventTable.eventTypeList.forEach(function(e) {
								select.append('<option value="' + e.id + '">' + e.name + '</option>');
							});
						}
						else if ($(cell).attr('data-search') == 'eventGroupId') {
							EventTable.eventGroupList.forEach(function(e) {
								select.append('<option value="' + e.id + '">' + e.name + '</option>');
							});
						}
						else if ($(cell).attr('data-search') == 'state') {
							select.append('<option value="true">Aktif</option>');
							select.append('<option value="false">Pasif</option>');
						}	
						else {
							select.append('<option value="true">True</option>');
							select.append('<option value="false">False</option>');
						}				
				
					} 
					else if ($(api.column(colIdx).header()).hasClass('nonSearchable')) {
						$(cell).empty();
					}
					else if ($(api.column(colIdx).header()).hasClass('datepickerSearch')) {
						$(cell).html('<div class="input-group date"><input id="startEndDate" name="dates" type="text" class="form-control datetimepicker-input daterange2 timeDimensionDateRange" autocomplete="off"/><div class="input-group-append" data-toggle="datetimepicker"><div class="input-group-text"><i class="fa fa-calendar"></i></div></div></div>');
					}
					else if ($(api.column(colIdx).header()).hasClass('alertSearch')) {
						$(cell).html('<input id="' + $(cell).attr('data-search') + 'Column" class="form-control" style="height: 25px !important; margin-bottom: -8px;" type="checkbox"/>');
					}
					else {
						$(cell).html('<input id="' + $(cell).attr('data-search') + 'Column" class="form-control" style="width:100%;" type="text" placeholder="' + title + '" />');
					}                              
 
 					//Input on change, keyup
                    $('input', $('.filters th').eq($(api.column(colIdx).header()).index()) )
                    .off('keyup change')
                    .on('change', function () {
                        var columnName = $(this).parent().attr('data-search');
                        if (columnName != null) {
							EventTable.SearchParams[columnName] = this.value;
						}
                        $(this).attr('title', $(this).val());
                        $('input[name='+columnName+']').val(this.value);
                        
                        if (columnName == "alert") {
							this.value = $('#alertColumn').is(":checked").toString();
							EventTable.SearchParams[columnName] = this.value;
						}
						
						if (this.id == "startEndDate") {
							var dates = this.value;
							dates = dates.replace(" - ", "/");
							dates = dates.split("/");
							var startDate = dates[0];
							var endDate = dates[1];
							$('input[name="startDate"]').val(startDate);
							$('input[name="endDate"]').val(endDate);
							EventTable.SearchParams["startDateStr"] = startDate;							
							EventTable.SearchParams["endDateStr"] = endDate;

						}

                        api.column(colIdx).search(this.value != '' ? this.value : '', this.value != '', this.value == '').draw();
                    })
                    .on('keyup', function (e) {
                    	e.stopPropagation();
                    	
                    	if(e.keyCode == 13){
							$(this).trigger('change');
						}                        
            		}); 
        			
        			// daterangepicker on change
            		$('input[name="dates"]').daterangepicker({
						autoUpdateInput: false,
						timePicker: true,
						timePicker24Hour:true,
						timePickerSeconds:true,
						timePickerIncrement: 1,
						"locale": {
							"format": "DD.MM.YYYY HH:mm:ss",
							applyLabel: 'Uygula', //TODO : dilden gelecek
							cancelLabel: 'Temizle',
						},
					});
						
				  	$('input[name="dates"]').on('apply.daterangepicker', function(ev, picker) {
				    	$(this).val(picker.startDate.format('DD.MM.YYYY HH:mm:ss') + ' - ' + picker.endDate.format('DD.MM.YYYY HH:mm:ss'));
				    	$('input[name="dates"]').trigger('change');
				  	});

				  	$('input[name="dates"]').on('cancel.daterangepicker', function(ev, picker) {
			      		$(this).val('');
			      		$('input[name="dates"]').trigger('change');
				  	});
				  	              		
                }); 
                
//        		$('.searchColumn').select2({
//					allowClear: true,
////					width: '100%',
//					dropdownAutoWidth: true,
//					placeholder: ' ',
//				}).on("select2:unselecting", function(e) {
//				    $(this).data('state', 'unselected');
//				}).on("select2:open", function(e) {
//				    if ($(this).data('state') === 'unselected') {
//						
//				        $(this).removeData('state'); 
//				
//				        var self = $(this);
//				        setTimeout(function() {
//				            self.select2('close');
//				        }, 1);
//				    }    
//				});          
                
        	},


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
	            "url": "/event-table-view/" + EventTable.layerId,
	            "type": "POST",
	            "dataType": "json",
	            "contentType": "application/json",
	            "data": function (d) {

//					var cookieModel = EventTable.ReadCookies();
					
//					d.columns[3].search.value = cookieModel.eventTextSearch;


//					d.columns[10].search.value = JSON.stringify(cookieModel.selectedEventGroupList);
					
					
//					var order = {
//						"name": d.columns[d.order[0].column].name,
//						"dir": d.order[0].dir
//					}
//					
					
//					console.log(d)	

					var order = [];
					var columnName = d.columns[d.order[0].column].name
					order.push(d.order[0])
					order[0].column = columnName;
//					order[0].dir = order[0].dir.toUpperCase();
					
					
					
					$.each(EventTable.SearchParams, function(key, value){
						d.columns.filter(f => f.name == key)[0].search.value = value;
					});
					
					EventTable.datatableCriteria = d;
					EventTable.currentPage = d.start;
					
					EventTable.datatableCriteria.order = order;
					
					
					//EventTable.datatableCriteria.sortedColumnDefs = ["title", "asc"];
					
	                return JSON.stringify(d);
	            },
	            beforeSend: function( xhr ) {
			    	$("#tableContainer").block({ message: null }); 
					$('[data-toggle="popover"]').popover('hide');
			  	},
			  	complete: function( xhr ) {
			    	$("#tableContainer").unblock({ message: null }); 
					Common.PopoverShow();
			  	},
			  	
	        },

	        order: [7, "desc"],
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
					visible: EventTable.userSettingsTypeColumn.includes("media") || EventTable.userSettingsTypeColumn.includes("description") || 
					EventTable.userSettingsTypeColumn.includes("reservedKey") || EventTable.userSettingsTypeColumn.includes("reservedType") || 
					EventTable.userSettingsTypeColumn.includes("reservedId") || EventTable.userSettingsTypeColumn.includes("reservedLink")|| 
					EventTable.userSettingsTypeColumn.includes("tag")|| EventTable.userSettingsTypeColumn.includes("alertEvent"),
					width: "20px",
				},
				{
					data: "event.eventTypeId",
					name: "eventTypeId",
					orderable: false,
					searchable: true,
					className: "dt-left",
					visible: EventTable.userSettingsTypeColumn.includes("eventType"),
					width: "",
	            	render: function(data, type, row, full) {	            	
	            		var margin = 'style="margin-top:10px;"';
						if (row.event.eventTypeName != null && row.event.eventTypeName.length > 25) {
							margin = "";
						} 
						
						return !data ? "-" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTTYPE+'"style="cursor: pointer;width: fit-content; display:inline-flex;"><img class="i-circled i-custom" src="{3}image/markerImg?eventTypeId={0}&amp;color={1}&amp;selectedIconControl=false"><div {4}>{2}</div></div>'.format(row.event.eventTypeId, row.event.color.replace("#", "%23"),row.event.eventTypeName, contextPath, margin);
					}
				},
				{
					data: "alertList",
					name: "alert",
					orderable: false,
					searchable: false,
					className: "dt-center",
					visible: EventTable.userSettingsTypeColumn.includes("alertEvent"),
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
					visible: EventTable.userSettingsTypeColumn.includes("title"),
					searchable: true,
					orderable: true,
					render: function(data, type, row, full) {

						var cellData = data != null ? data : "-";
						
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.TITLE+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
						
					}
				},
				
				{
					data: 'event.spot',
					name: 'spot',
					visible: EventTable.userSettingsTypeColumn.includes("spot"),
					searchable: true,
					orderable: true,
					render: function(data, type, row, full) {
						
						var cellData = data != null ? data : "-";
						
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.SPOT+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
						
					}
				},
				{//aramada kullanabilmek içn.
					data: 'event.description',
					name: 'description',
					visible: false,
					searchable: true,
					orderable: false,
					render: function(data, type, row) {
						return data != null ? row.event.description : "-"; 
					}
				},
				
	            {	
					data: "event.latitude",	
					searchable: true,
					orderable: false,
					visible: EventTable.userSettingsTypeColumn.includes("latitudeAndLongitude"),
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
					visible: EventTable.userSettingsTypeColumn.includes("eventDate"),
					orderable: true,
					name:"eventDate",
	            	render: function(data, type, row, full) {
						
						var eventDateFormatted = CustomFormatter.GetDateFormatted(row.event.eventDate);
						return !data ? "-" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTDATE+'"style="cursor: pointer;width: fit-content;">'+eventDateFormatted+'</div>';
					}
				},	
				 {//aramada kullanabilmek içn.
					data: "event.eventDate",
					name: "startDateStr",
					orderable: false,
					visible:false,
	            	render: function(data, type, row) {
						
						var eventDateFormatted = CustomFormatter.GetDateFormatted(row.event.eventDate);
						
						return !data ? "-" : eventDateFormatted;
					}
				},
				 {//aramada kullanabilmek içn.
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
					visible: EventTable.userSettingsTypeColumn.includes("city"),
					orderable: true,
					render: function(data, type, row, full) {
						
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.CITY+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
							
	           	{
					data: "event.country",
					name: "country",
					visible: EventTable.userSettingsTypeColumn.includes("country"),
					orderable: true,
	            	render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.COUNTRY+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},	

				{
					data: "event.groupName",
					name: "eventGroupId",
					visible: EventTable.userSettingsTypeColumn.includes("eventGroup"),
					orderable: false,
	            	render: function(data, type, row, full) {
						var eventGroupHtml = "<span style='color:{1}'>{0}</span>".format(row.event.groupName, row.event.color) ;
						return !data ? "-" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.EVENTGROUP+'"style="cursor: pointer;width: fit-content;">'+eventGroupHtml+'</div>';		
					}
				},
				
				{
					data: "event.blackListTag",
					name: "blackListTag",
					visible: EventTable.userSettingsTypeColumn.includes("blackListTag"),
					orderable: true,
	            	render: function(data, type, row, full) {

						var cellData = data != null ? data : "-";
						
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.BLACKLISTTAG+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
						

					}
				},	
				
				
				{
					data: "event.reserved1",
					name: "reserved1",
					visible: EventTable.userSettingsTypeColumn.includes("reserved1"),
					orderable: true,
					render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED1+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
				{
					data: "event.reserved2",
					name: "reserved2",
					visible: EventTable.userSettingsTypeColumn.includes("reserved2"),
					orderable: true,
					render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED2+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
				{
					data: "event.reserved3",
					name: "reserved3",
					visible: EventTable.userSettingsTypeColumn.includes("reserved3"),
					orderable: true,
					render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED3+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
				{
					data: "event.reserved4",
					name: "reserved4",
					visible: EventTable.userSettingsTypeColumn.includes("reserved4"),
					orderable: true,
					render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED4+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
				{
					data: "event.reserved5",
					name: "reserved5",
					visible: EventTable.userSettingsTypeColumn.includes("reserved5"),
					orderable: true,
					render: function(data, type, row, full) {
						var cellData = data != null ? data : "-";					
						return data == "" ? "" : '<div data-html="true" data-toggle="popover" title="<b>Linkler</b>" data-content="" data-row-index="'+full.row+'" data-event-table-column-enum-id="'+EventTableColumnE.RESERVED5+'"style="cursor: pointer;width: fit-content;">'+cellData+'</div>';
					
					}
				},
		
				{
					data: "event.state",
					name: "state",
					visible: EventTable.userSettingsTypeColumn.includes("state"),
					orderable: false,
					searchable: true,
					className: "dt-center",
					width: "20px",
	            	render: function(data, type, row) {
						
						var auhtroizeControl = $("#eventBacthOperationsState").data('has-role');
						
						if(auhtroizeControl == "true" || auhtroizeControl == true){
							$(function() {
							    $('.state-toggle').bootstrapToggle({
									size: "small",
									offstyle: "danger eventTableViewStateToggle",
									onstyle: "success eventTableViewStateToggle",
									on: lang.get("label.active"),
							      	off: lang.get("label.passive" )
									
							    });
					  		})
	
							let isChecked = data ? "checked" : "";
							return '<input data-width="40" data-height="20" data-toggle="toggle" id="eventTableViewStateToggle'+row.event.id+'" onChange="Common.StateChanged('+row.event.id+')" class="state-toggle" '+isChecked+' type="checkbox">';		
						
						}else{
							return row.event.state ?  "<span style='color:{1}'>{0}</span>".format("Aktif", "green") : "<span style='color:{1}'>{0}</span>".format("Pasif", "red");
						}
					}
				},
				
				
				
	        ]
	    });
	    

	    
   	
    },

	
	
	BatchOperationsEventGroupSelectedControl: function(){
		
		const swalWithBootstrapButtons = Swal.mixin({
		  customClass: {
		    confirmButton: 'btn btn-success excelDownloadConfirm',
		    cancelButton: 'btn btn-danger'
		  },
		  buttonsStyling: false
		})
		
		if(EventTable.SearchParams == null || EventTable.SearchParams.eventGroupId == undefined || EventTable.SearchParams.eventGroupId == ""){
				swalWithBootstrapButtons.fire({
				  text: lang.get('label.please.event.group.select'),
				  icon: 'warning',
				  showCancelButton: false,
				  confirmButtonText: lang.get('label.ok'),
				  reverseButtons: true
				});
				
				return false;
			}
			
			return true;
	},
	
	BacthStateChangedOpenModal: function(){
		
		$("#stateChangeBatchOperations").modal('show');
		let modal = $("#stateChangeBatchOperations");
		modal.find("#eventBatchOperationsInfo").text("");
		
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
		var batchState = $("#eventBatchOperationsState").is(":checked")
		
		
		let batchStateText = batchState ? "Aktif" : "Pasif"	
		var stateColor = batchState ? "green" : "red";	
		var html = '<p>Toplam <span style="font-weight: bold">'+ tableTotalEventCount +'</span> kayıt <span style="color: '+ stateColor +'">'+ batchStateText +'</span> durumuna çekilmektedir.</p>'	
		modal.find("#eventBatchOperationsInfo").append(html);


		$("#eventBatchOperationsState").on("change", function(){
			modal.find("#eventBatchOperationsInfo").text("");
			batchState = $("#eventBatchOperationsState").is(":checked")
			let batchStateText = batchState ? "Aktif" : "Pasif"
			
			var stateColor = batchState ? "green" : "red";
			var html = '<p>Toplam <span style="font-weight: bold">'+ tableTotalEventCount +'</span> kayıt <span style="color: '+ stateColor +'">'+ batchStateText +'</span> durumuna çekilmektedir.</p>'
		
			modal.find("#eventBatchOperationsInfo").append(html);
		});
	},
	BatchStateChanged: function(){
		
		//olay grubu seçimi kontrol edilecek.
		
		var eventGroupSelectedControl = EventTable.BatchOperationsEventGroupSelectedControl();
		if(!eventGroupSelectedControl){
			return;
		}

		var batchState = $("#eventBatchOperationsState").is(":checked")
		
		var eventTypeIds = EventTable.SearchParams.eventTypeId != undefined ? EventTable.SearchParams.eventTypeId :  0;
		if (eventTypeIds != 0) {
			var eventTypeIdsInt = eventTypeIds.split(',').map(function(item) {
				return parseInt(item);
			});
		}
		else {
			var eventTypeIdsInt = [];
		}
		
		var eventTableViewExcelItem = {
				
				title: EventTable.SearchParams.title != undefined ? EventTable.SearchParams.title :  "",
				spot: EventTable.SearchParams.spot != undefined ? EventTable.SearchParams.spot :  "",
				description: EventTable.SearchParams.description != undefined ? EventTable.SearchParams.description :  "",
				city: EventTable.SearchParams.city != undefined ? EventTable.SearchParams.city :  "",
				country: EventTable.SearchParams.country != undefined ? EventTable.SearchParams.country :  "",
				eventGroupId: EventTable.SearchParams.eventGroupId != undefined ? EventTable.SearchParams.eventGroupId :  0,
				eventTypeId: eventTypeIdsInt,
				startDateStr: EventTable.SearchParams.startDateStr != undefined ? EventTable.SearchParams.startDateStr :  "",
				endDateStr: EventTable.SearchParams.endDateStr != undefined ? EventTable.SearchParams.endDateStr :  "",
				state: EventTable.SearchParams.state != undefined ? EventTable.SearchParams.state : null,
				blackListTag: EventTable.SearchParams.blackListTag != undefined ? EventTable.SearchParams.blackListTag : null,
				reserved1: EventTable.SearchParams.reserved1 != undefined ? EventTable.SearchParams.reserved1 : null,
				reserved2: EventTable.SearchParams.reserved2 != undefined ? EventTable.SearchParams.reserved2 : null,
				reserved3: EventTable.SearchParams.reserved3 != undefined ? EventTable.SearchParams.reserved3 : null,
				reserved4: EventTable.SearchParams.reserved4 != undefined ? EventTable.SearchParams.reserved4 : null,
				reserved5: EventTable.SearchParams.reserved5 != undefined ? EventTable.SearchParams.reserved5 : null,
				isAlertEvent: EventTable.SearchParams.alert != undefined ? EventTable.SearchParams.alert : false
				
		}
		
		$.ajax({ type: "POST",   
		  	 url: "/event-table-view/batchOperations?layerId=" + EventTable.layerId + "&batchState=" + batchState,
		     data: JSON.stringify(eventTableViewExcelItem),//EventTable.SearchParams
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
			
					
			
		
				}else{
					EventTable.dataTable.draw();
				}
				
			 }
	
		});
	},
	
	
	GetEventGroupTreeData: function(){
		var eventGroupdbNameIdList =  Common.GetShowEventGroupDbNameList(EventTable.mapHelper.eventGroupList, []);
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
    
	Search: function () {// simple search

		EventTable.setCookies(true);
		
		EventTable.dataTable.draw();

    },
	SetSearchModel: function(cookieModel){
		
		EventTable.dataTable.column(3).search(cookieModel.eventTextSearch);	
		
	},


   addAlertArea: function(){
	
	},
	
	filterGlobal :function() {
	    $('#eventTable').DataTable().search(
	    $('#global_filter').val()).draw();
	 },
    
	
	filter: function(){

		$('input.column_filter').on( 'keyup', function (e) {
			
			if(e.keyCode == 13){
				
				var i = $(this).parents('div').attr('data-column');				
				EventTable.SearchParams[i] = $('input[name='+i+']').val();
				$('input#'+i+'Column').val(EventTable.SearchParams[i]);
				EventTable.dataTable.draw();
			}
	       	
	     });
	     
	     $('input.column_filter').on( 'change', function (e) {
			
				var i = $(this).parents('div').attr('data-column');				
				EventTable.SearchParams[i] = $('input[name='+i+']').val();
				$('input#'+i+'Column').val(EventTable.SearchParams[i]);
				EventTable.dataTable.draw();
	       	
	     });

		$('.searchButton').on("click", function (e) {
            var items = $(".searchText");
            items.each(function (item, index) {
                if (index.value != null) {
					
                    var columnName = $(index).parents('div').attr('data-column')
                    var value = index.value;
                    EventTable.SearchParams[columnName] = value;
                }
            });
			
			//start-end Date
		
			var startDate = $("input[name=startDate]").val()
			var endDate = $("input[name=endDate]").val()
			
			if(startDate != null && startDate != undefined){
				EventTable.SearchParams['startDateStr'] = moment(startDate, DateUtils.ENGLISH).format(DateUtils.TURKISH)
			}
			
			if(endDate != null && endDate != undefined){
				EventTable.SearchParams['endDateStr'] =  moment(endDate, DateUtils.ENGLISH).format(DateUtils.TURKISH)
			}
			
			if (startDate != "" && endDate != "") {
				var total = [startDate, endDate];
				var startEnd = total.join(" - ");
				startEnd = startEnd.replaceAll("/",".");
				$("#startEndDate").val(startEnd); // TODO datetimepicker tetiklenecek
			}
		
            EventTable.dataTable.draw();
        });
        
        $("#close-button").click(function(){
			
			if ($("#searchForm").is(":visible")) {
				$('.stbuttontext').removeClass('fa fa-angle-up');
				$('.stbuttontext').addClass('fa fa-angle-down');
			}
			else {
				$('.stbuttontext').removeClass('fa fa-angle-down');
				$('.stbuttontext').addClass('fa fa-angle-up');
			}	
			
        	$("#searchForm").slideToggle();

    	});
	},
	
	UserSettingsGetData: function(){
		
		$.ajax({ type: "POST",   
		     url: "/event-table-view/user-settings/",
		     datatype: 'json',
		     contentType: "application/json",
			 async: false,
		     success : function(result)
		     {	
				
				
				EventTable.userSettingsTypeColumn = result;
				
		     }
		});
	},

}

document.addEventListener('DOMContentLoaded', function () {

    EventTable.Init();
    EventTable.InitializeEvents();
	
	EventTable.filter();


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



