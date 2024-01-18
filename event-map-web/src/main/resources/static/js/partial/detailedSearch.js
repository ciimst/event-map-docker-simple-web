var DetailedSearch = {
	layerId: null,	
	Init: function () {
		DetailedSearch.layerId = paramLayerId;
    },

    InitializeEvents: function () {

    },
	AdvancedSearchModalOpen(){
			
		$('input[name="startDate"]').datetimepicker({
			format: DateUtils.ENGLISH,
			locale: lang.props["lang.iso"],
			ampm: false,
			showClose: true

		});

		$('input[name="endDate"]').datetimepicker({
			format: DateUtils.ENGLISH,
			locale: lang.props["lang.iso"],
			ampm: false,
			showClose: true
		});
		
		
		$('#searchEventType').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
			multiple:true,
		});
		
		$("#detailModalSearch").modal('show');
		let modal = $("#detailModalSearch");
		
		
		//Default değer yok ise button görünmesin.
		if(!Object.keys(Common.userSettingsListUnderLayer).length > 0 || !Common.userSettingsListUnderLayer[DetailedSearch.layerId].anyExistField){
			modal.find("#detailedSearchDefaultValueSet").css("display", "none");
			modal.find(".user-settings-operation").css("display", "none");
		}
		
		var cookieModel = Common.ReadCookies();
		
		modal.find("#eventSearchDetailed").val(cookieModel.eventTextSearch);
		modal.find("#eventSearchCity").val(cookieModel.eventSearchCity);
		modal.find("#eventSearchCountry").val(cookieModel.eventSearchCountry);		
		$('#searchEventType option').remove();
//		$('#searchEventType option:not(:first)').remove();
		
		modal.find("#startDate").val(cookieModel.startDate);
		modal.find("#endDate").val(cookieModel.endDate);
		
		if (typeof Page != 'undefined' && Page.time != null){
			modal.find("#detailedSearchDateFilter").css("display", "none");
			
		}
		
		DetailedSearch.AlertButtonStyle();

		DetailedSearch.EventTypeSelectBox("#searchEventType");
		
		if(typeof EventTable != "undefined"){
			$("#theTree").append('<div id="eventGroupTree" ></div>');						
			Common.CreateEventGroupTreeView(Common.eventGroupTreeViewList);
		}else{
			$("#detailed-search-event-group").remove();
		}

	},
	
	AlertButtonStyle: function(){
		var cookieModel = Common.ReadCookies();
		if(cookieModel.alertChecked == true){
			$("#alertEventChecked").prop('checked', true);
			$(".alert-events").css('background-color', '#dc3545');
			$(".alert-events").css('border-color', '#dc3545');
			$(".alert-events").css('box-shadow', '0 0 0 0.2rem, #dc3545');
		}else{
			$('#alertEventChecked').prop('checked', false);				
		}
				
	},
	
	EventTypeSelectBox: function(hmtlId){
		
		var data = {};
    	data.layerId = DetailedSearch.layerId;

		$.ajax({ type: "POST",   
		     url: "/event/searchEventType",   
		     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(eventTypeList)
		     {				
				
				$.each(eventTypeList, function(key, value){
					
					var option = new Option(value.name, value.id);
					
					$(hmtlId).append(option);
				});	
				
				var ids = $.cookie('eventTypeIdSearch');
				
				ids = ids != "" && ids != null ? ids : "0";
				
				var idArray = ids.split(',');
				
				$.each(idArray, function(){
					id = this;
					$(hmtlId + " select").val(id);
					$(hmtlId + ' option[value='+id+']').attr('selected','selected');
				});
		     }
		});		
	},
	
	ShowDetailedSearchBadge: function(){
		
		var cookieModel = Common.ReadCookies();
		
		var count = cookieModel.count;
		
		$("#detailedSearchBadgeEventMap").text("+" + count);
		$("#detailedSearchBadge").text("+" + count);
		$("#detailedSearchBadgeHeatMap").text("+" + count);
		
		if(count == 0){
			$("#detailedSearchBadgeEventMap").css("display", "none");
			$("#detailedSearchBadge").css("display", "none");
			$("#detailedSearchBadgeHeatMap").css("display", "none");
		}else{
			$("#detailedSearchBadgeEventMap").css("display", "inline-block");
		}
	},

		
	EventSearch: function () {
		
		//start-end date kontrolü
		if(!Common.StartAndEndDateGreatherControl()){
			return;
		}
			
		if(typeof EventTable != 'undefined'){
					
			DetailedSearch.AdvancedSearchModalClose();
			Common.setCookies(false);
				
			EventTable.dataTable.draw();
			DetailedSearch.AdvancedSearchModalClose();
		
		}else{
			
			Common.setCookies(false);
			window.location = "";			
		}

	},
	
	HandleEventSearch: function(event){
			
		if(event.keyCode != 13){
			return;
		}
		
		if(typeof EventTable != 'undefined'){		
			EventTable.Search();			
		}else{
			
			Common.setCookies(true);
			window.location = "";		
		}		
	},
	EventSearchClear: function(){
		
		//Hem bulunulan locationdan hem de /'dan silindi.
		
		var urlList = [contextPathWithoutSlash , contextPathWithoutSlash + "/region/"+ DetailedSearch.layerId, contextPathWithoutSlash + "/timeDimension/"+ DetailedSearch.layerId, contextPathWithoutSlash + "/heatmap/" + DetailedSearch.layerId, contextPathWithoutSlash + "/time/"+DetailedSearch.layerId, contextPathWithoutSlash + "/event-table/" + DetailedSearch.layerId];
		
		
		$.each(urlList, function(key, url){
			
			$.removeCookie('eventSearchText', { path: url });
			$.removeCookie('eventTypeIdSearch', { path: url });
			$.removeCookie('eventSearchCity', { path: url });
			$.removeCookie('eventSearchCountry', { path: url });
			
			if(typeof EventTable != 'undefined'){
				$.removeCookie('removedEventGroups', { path: url});
			}
			
			$.removeCookie('startDate', { path: url});
			$.removeCookie('endDate', { path: url});
			$.cookie("isALertEventClick", false, { path: url})
			
		});
		
		if(typeof EventTable != 'undefined'){
			
//			Common.theTree.checkAllItems(true);
			EventTable.PrepareHtmlElements();
			EventTable.dataTable.draw();
			
			//Tüm Nodeları seçme işlemi yapıldı.
			$.each(Common.theTree.nodes, function(key, value){			
				$(this)[0].setChecked(true)			
			});
			
		}else{
			window.location = "";
		}
		
	},
	
	AdvancedSearchModalClose: function(){
		
		
		$('#detailModalSearch').modal('hide');
		
		$('#detailModalSearch').on('hidden.bs.modal', function () {
	
			$("#eventGroupTree").remove();
		})
	},

	
}

