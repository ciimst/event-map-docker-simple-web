var Alert = {    
    Text: "Alert",
    
    Init: function () {

		$("form#alertAddForm")[0].reset();
    },
    InitializeEvents: function () {
    },
    Add: function (alertArea) {
    	
    	var data = {};
    	data.layerId = Page.layerId;
    	//data.coordinateInfo = JSON.stringify(alertArea.getLatLngs())
    	if(alertArea.options.radius == undefined){ // if polygon
    		data.isCircle = false;
    		data.latLongItemArr = alertArea.getLatLngs()[0];	
    	}else{ // if circle
    		data.isCircle = true;    		
        	data.latLongItemArr = [alertArea.getLatLng(), alertArea.getBounds()._northEast, alertArea.getBounds()._southWest];
    	}
    	
    	
    	
    	
    	console.log(JSON.stringify(data));  	
		
		let formData =  $("form#alertAddForm").serializeArray();
		
		data.name = formData[0].value;			
		data.query = formData[1].value;	
		data.eventTypeId = formData[2].value;
		data.eventGroupId = formData[3].value;
		data.color = formData[4].value;
		data.reservedId = formData[5].value;
		data.reservedKey = formData[6].value;
		data.reservedType = formData[7].value;
		data.reservedLink = formData[8].value;
		if(formData[9] != undefined){
			data.eventGroupDbName = formData[9].value;
		}
			
		$.ajax({   
			 type : "POST",
		     url: "/alert/add",   
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {
		    	 if(result == true){
					 
		    		 toastr.success(lang.props["label.alert.add.success"])
					 location.reload();
		    	 }else{
		    		 toastr.error(lang.props["label.alert.add.error"])
		    	 }
		     },
		     error: function (e) {
		    	 toastr.error(lang.props["label.alert.add.error"])
		     }
		});
    },
    Delete: function (alertArea) {
    	
    	
    	var data = {};
    	data.layerId = Page.layerId;
    	data.id = JSON.stringify(alertArea.id);
    	
		$.ajax({   
			 type : "POST",
		     url: "/alert/delete",   
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {
		    	 if(result == true){
		    		 toastr.success(lang.props["label.alert.delete.success"])
					window.location.href = "";
		    	 }else{
		    		 toastr.warning(lang.props["label.alert.delete.record.not.found"]);
		    	 }
		     },
		     error: function (e) {
		    	 toastr.error(lang.props["label.alert.delete.error"])
		     }
		});
    },
    Edit: function (alertArea) {
    	
    	var data = {};
    	data.id = alertArea.id,
    	data.layerId = Page.layerId;
//    	data.coordinateInfo = JSON.stringify(alertArea.getLatLngs());
    	data.latLongItemArr = alertArea.getLatLngs()[0];
    	
		$.ajax({   
			 type : "POST",
		     url: "/alert/edit",   
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(result)
		     {
		    	 if(result == true){
		    		 toastr.success(lang.props["label.alert.edit.success"])
		    	 }else{
		    		 toastr.warning(lang.props["label.alert.edit.not.found"]);
		    	 }
		     },
		     error: function (e) {
		    	 toastr.error(lang.props["label.alert.edit.error"])
		     }
		});
    },
    Get: function (callbackFunction) {

    	var data = {};
    	data.layerId = Page.layerId;
    	
    	$.ajax({ type: "POST",   
    	     url: "/alert/get",   
    	     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
    	     success : function(alertItemList)
    	     {
    	    	callbackFunction(alertItemList);				
				Page.AddAlertToSidebar(alertItemList);
				
    	     }			
    	});
    },

	OpenDetailAlertAddModal: function(){
	
		var data = {};
	    	data.layerId = Page.layerId;
	    	
	    	$.ajax({ type: "POST",   
	    	     url: "/alert/get/eventType",
	    	     async: true,			    
	    	     success : function(eventTypeList)
	    	     {					
					$.each(eventTypeList, function(key, eventType){				
						$(".addAlarmEventType").append("<option data-value='"+eventType.id+"' value='" + eventType.id+ "'  name='"+eventType.id+"'>" + eventType.name + "</option>");										
					});
					
					
					
	    	     }			
	    	});
				
			document.querySelectorAll('input[type=color]').forEach(function(picker) {
	  			picker.addEventListener('change', function() {
					$("span#colorText").text(picker.value)
	  			});
			});
						
			$('.addAlarmEventGroup').on('change', function() {
				$("form#alertAddForm>.eventGroupDbName").remove();
				var eventGroupDbName = null;
				var eventGroupDbName = $(this).find('option:selected').attr("name");			
				$("form#alertAddForm").append('<input class="eventGroupDbName" type ="hidden" name="eventGroupDbName" value='+eventGroupDbName+'>')
								
			});	
							
			$.each(Page.eventGroupList, function(key, eventGroup){				
				var eventGroupIdAndDbName = eventGroup.id+eventGroup.dbName;
				$(".addAlarmEventGroup").append("<option data-value='"+eventGroupIdAndDbName+"' value='" + eventGroup.id+ "'  name='"+eventGroup.dbName+"'>" + eventGroup.name + "</option>");
						
			});	
			
			
					  		
	},
	
	sidebarAlarmDelete: function(id){
		
		$('#myModalAlert').modal('hide');
		
		var data = {};
    	data.layerId = Page.layerId;
    	data.id = id;

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
		  text: lang.props["label.map.alarm.delete"],
		  showCancelButton: true,
		  confirmButtonText: lang.props["label.delete"],
		  cancelButtonText: lang.props["label.close"],
		  reverseButtons: false,
		}).then((result) => {
		  if (result.isConfirmed) {
				
			$.ajax({   
				 type : "POST",
			     url: "/alert/delete",   
			     data: JSON.stringify(data),
			     datatype: 'json',
			     contentType: "application/json",
			     success : function(result)
			     {
			    	 if(result == true){
						swalWithBootstrapButtons.fire({text: lang.props["label.alert.delete.success"],  confirmButtonText: lang.props["label.close"],}).then((result) =>{
							if(result.isConfirmed){
								location.reload(); 
							}else{
								location.reload(); 
							}						
						})						    		
						 
			    	 }else{			    		 
			    		 toastr.warning(lang.props["label.alert.delete.record.not.found"]);
			    	 }
			     },
			     error: function (e) {
			    	  swalWithBootstrapButtons.fire(lang.props["label.alert.delete.error"])
			     }
				});	
		  }
		})
	},
	
	handleAlarmSearch : function(e){
		
		if(e.keyCode === 13){			
          	Alert.alarmSearch();
        }
	},
	
	alarmSearch(){	
		var searchWord =  $("#alarmSearch").val()

		var sidebarAlarmList = $("#sidebarAlarmContainer > .sidebarItem")
			
		$.each(sidebarAlarmList, function(key, value){
				
			var alarmName = $(this).children(".card").children(".card-body").children(".card-title").children("a").text()
			if(alarmName.toUpperCase().indexOf(searchWord.toUpperCase()) > -1){
					
				$(this).css("display", "block")
			}else{
				$(this).css("display", "none")
			}
				
		});
	},
	
	
}
	
$(document).ready(function () {


	Alert.InitializeEvents();
	Alert.Init();
	
	$("#modalAlarmDelete").on("click", function(){
		var alarmId = $(this).attr("data-id");
		Alert.sidebarAlarmDelete(alarmId);
		
	})
	
	$("#alarmSearchButton").on("click", function(){
		Alert.alarmSearch();
	})
	
	
});