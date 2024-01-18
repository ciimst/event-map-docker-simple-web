var Settings = {
	layerId: null,
	 mapHelper: null,
	Init: function(){
	
		Settings.layerId = paramLayerId;
		
		Common.SetLayerId();
		Common.SetTimeLineStartDate();
		
		Common.LoadEventGroups(Settings.layerId); // sync
		Settings.GetEventGroupTreeData();
		Common.LoadKey();
    	Common.LoadTimeLine();
    	Common.SetTimeLine();

		HeaderMenu.SidebarOpenAndCloseOperations();		
		Alert.Init(Settings.mapHelper,Settings.addAlertArea);
	
		$('#selectId').select2({
				allowClear: true,
				width: '100%',
				placeholder: lang.get('label.search.event.type'),
				multiple: true,
		});
		
		$('#openPageSelectId').select2({
				allowClear: true,
				width: '100%',
				placeholder: lang.get('label.search.page'),
		});
		
		
		
		var dateTimePickerList = $(".dateTimePickerDiv").children("input");
	
		if ($("#dropdownMenu a.active").length > 0) {
			$(".dropdown-toggle").addClass("active");
		};
		
		$.each(dateTimePickerList, function(key, input){

			var inputName = input.getAttribute('name');
			$('input[name="'+inputName+'"]').datetimepicker({
				format: DateUtils.ENGLISH,
				locale: lang.props["lang.iso"],
				ampm: false,
				showClose: true,
			});
			
			var inputValue = input.getAttribute('defaultValue')		
			var inputId = input.getAttribute('id')
			$("#"+inputId).val(inputValue);
			
		});
		
		
	/*
		SEMICOLON.header.init();
		SEMICOLON.widget.init(); */

	},
	addAlertArea: function(){
	
	},
	OpenDetailModal: function () {
	},
	GetEventGroupTreeData: function(){
		
		var eventGroupdbNameIdList = Settings.GetShowEventGroupDbNameList(Common.eventGroupList, []);
		Common.GetEventGroupTreeData(eventGroupdbNameIdList);
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
		
		if(eventGroupdbNameIdList.length == 0){
//			eventGroupdbNameIdList.push("default_0");
		}
		
		return eventGroupdbNameIdList;
	},
	
	Save: function(btn){

		var groupName = $(".userSettingsGroupName").attr("data-group-name");
	
	
	 	 let formData = $('#settingsForm').serializeArray();
	
		 $.ajax({   
				 type : "POST",
			     url: contextPath + "settings/save/" + groupName,   
			     data: formData,
			     success : function(result)
			     {
			    	 if(result == true){
			    		 Swal.fire({	 
						  icon: 'success',
						  text: lang.get("label.successful.transaction"),
						  showConfirmButton: false,
						  timer: 1500
						  })
	
			    	 }else{
			    		 Swal.fire({
							  icon: 'error',
							  title: 'Oops...',
							  text: lang.get("label.failed.transaction"),
							 
						 })
			    	 }
			     },
			    
			});

	},
	
	Clean: function(btn){
	
		Swal.fire({
		  title: lang.get("label.are.you.sure.want.cleaning"),
		  text: "Bunu işlemi geri alamazsınız!",
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: lang.get("label.yes"),
		  cancelButtonText: lang.get("label.no"),
		 }).then((result) => {
		   if (result.isConfirmed) {

             var groupName = $(".userSettingsGroupName").attr("data-group-name");
	
		 	 let formData = $('#settingsForm').serializeArray();
		
			 $.ajax({   
					type : "POST",
				    url: contextPath + "settings/clean/" + groupName,   
				    data: formData,
				    success : function(result)
				    {
				    	if(result == true){
	
		                    setInterval(()=>window.location.reload(false),500);
	
	                        Swal.fire({
						       text: lang.get("label.cleaned"),
						      })
		
				    	}else{
				    		Swal.fire({
								 icon: 'error',
								 title: 'Oops...',
								 text: lang.get("label.failed.transaction"),
								 
							})
				    	}
				    },
				    
			 });

		   }
		 })
		
		 

	},
	

	
	
	onlyEventGroupClear: function(btn){
		var layerGuid = btn.getAttribute("id")
		var url = "/settings/userSettingsValueSave/" + layerGuid+"?eventGroupSave=true&outSideEventGroup=false";
		
		var data = {};
		data.eventGroupList = [];
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

}


/*
$(document).ready(function(){
  $("#page-menu li").click(function(){
    $("#page-menu li").removeClass("aktif");
    $(this).addClass("aktif");
  });
});  

$("#page-menu li").removeClass("active")
$("a[name=${instance.key}]").parent().addClass("active")  */

$(document).ready(function () {    	
	Settings.Init();
});

