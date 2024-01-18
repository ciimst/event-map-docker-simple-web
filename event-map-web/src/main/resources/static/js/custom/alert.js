var Alert = {    
    Text: "Alert",
	layerId: null,
	userList: null,
	sidebarAlertList: null,
	shareAlertId: 0,
	isAlertSharedClick: false,
	lastChoosenAlertId : 0,// modal açılması için son seçilen item
    mapHelper: null,
    eventGroupList : [],
    Init: function (mapHelperFunction,callbackFunction) {
		
		Alert.layerId = paramLayerId;
		Alert.mapHelper = mapHelperFunction;
		
		Alert.eventGroupList = eventGroupList;
		
		this.LoadAlerts(callbackFunction);
		
		$("form#alertAddForm")[0].reset();
		
		Alert.AlertReadCount();
		
		
    },
    InitializeEvents: function () {
    },
	OpenDetailModal: function(){
		
	},
    Add: function (alertArea) {
    	
    	var data = {};
    	data.layerId = Alert.layerId;
    	//data.coordinateInfo = JSON.stringify(alertArea.getLatLngs())
		
		if(alertArea != null){
			if(alertArea.options.radius == undefined){ // if polygon
    			data.isCircle = false;
    			data.latLongItemArr = alertArea.getLatLngs()[0];	
	    	}else{ // if circle
	    		data.isCircle = true;    		
	        	data.latLongItemArr = [alertArea.getLatLng(), alertArea.getBounds()._northEast, alertArea.getBounds()._southWest];
	    	}
		}
    	
    	
		
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
		else
		{
			data.eventGroupDbName = "default"
		}
			
		$.ajax({   
			 type : "POST",
		     url: "/alert/add",   
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
		     success : function(GenericResponse)
		     {
				var result = GenericResponse.state;
				var alertId = GenericResponse.data;

				if(result == true){
			    	Swal.fire({	 
						icon: 'question',
						text: lang.props["label.alert.add.success"] + " Alarmı paylaşmak ister misiniz?",
						showCancelButton: true,
						confirmButtonText: 'Evet',
						cancelButtonText: 'Hayır'}).then((swalResult) => {
							 
						  if (swalResult.isConfirmed) {
							
						    Alert.AlertShare(alertId);
		
						  } else if (swalResult.isDismissed) {
						   	
					    	 if(result == true){
								 
					    		 toastr.success(lang.props["label.alert.add.success"])
								 location.reload();
					    	 }
						  }
						});
						
						 
		    	 }else{
			
		    		  toastr.error(lang.props["label.alert.add.error"])
		    	 }

					

		     },
		     error: function (e) {
		    	 toastr.error(lang.props["label.alert.add.error"])
		     },
			
		});
		
    },
    Delete: function (alertArea) {
    	
    	
    	var data = {};
    	data.layerId = Alert.layerId;
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
    	data.layerId = Alert.layerId;
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
    	data.layerId = Alert.layerId;

    	$.ajax({ type: "POST",   
    	     url: "/alert/get",   
    	     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
    	     success : function(alertItemList)
    	     {
    	    	Alert.UseAlerts(alertItemList, callbackFunction);				
				Alert.AddAlertToSidebar(alertItemList);
				
    	     }			
    	});
    },

	OpenDetailAlertAddModal: function(){
	
		$("#addAlertModalAlertName").val("");
		$(".alertQuery").val("");
		$("#addAlarmEventGroup").val(null).trigger('change');
		$("#addAlarmEventType").val(null).trigger('change');
		$(".reservedId").val("");
		$(".reservedKey").val("");
		$(".reservedType").val("");
		$(".reservedLink").val("");
		$("span#colorText").text("");
		$('input#colorPicker').val("");

		$('#addAlarmEventType').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#addAlarmEventGroup').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.eventGroup'),
		});
		
		
		var data = {};
	    	data.layerId = Alert.layerId;
	    	
	    	$.ajax({ type: "POST",   
	    	     url: "/alert/get/eventType",
	    	     async: true,			    
	    	     success : function(eventTypeList)
	    	     {					
					$.each(eventTypeList, function(key, eventType){				
						$("#addAlarmEventType").append("<option data-value='"+eventType.id+"' value='" + eventType.id+ "'  name='"+eventType.id+"'>" + eventType.name + "</option>");										
					});
					
					
					
	    	     }			
	    	});
				
			document.querySelectorAll('input[type=color]').forEach(function(picker) {
	  			picker.addEventListener('change', function() {
					$("span#colorText").text(picker.value)
	  			});
			});
						
			$('#addAlarmEventGroup').on('change', function() {
				$("form#alertAddForm>.eventGroupDbName").remove();
				var eventGroupDbName = null;
				var eventGroupDbName = $(this).find('option:selected').attr("name");			
				$("form#alertAddForm").append('<input class="eventGroupDbName" type ="hidden" name="eventGroupDbName" value="default">') //'+eventGroupDbName+'
								
			});	
							
			$.each(Alert.eventGroupList, function(key, eventGroup){				
				var eventGroupIdAndDbName = eventGroup.id/*+eventGroup.dbName;*/;
				$("#addAlarmEventGroup").append("<option data-value='"+eventGroupIdAndDbName+"' value='" + eventGroup.id+ "'  name='"+eventGroup.id+"'>" + eventGroup.name + "</option>");
						
			});	
			
			
					  		
	},
	
	sidebarAlarmDelete: function(id){
		
		$('#myModalAlert').modal('hide');
		
		var data = {};
    	data.layerId = Alert.layerId;
    	data.id = id;

		const swalWithBootstrapButtons = Swal.mixin({
		  customClass: {
		    confirmButton: 'btn alertDeleteConfirmButton',
		    cancelButton: 'btn alertDeleteCancelButton'
		  },
		  buttonsStyling: {
			confirmButton: 'margin:10px;'
		  }
		});
		
		swalWithBootstrapButtons.fire({
		  text: lang.props["label.map.alarm.delete"],
		  showCancelButton: true,
		  confirmButtonText: lang.props["label.delete"],
		  cancelButtonText: lang.props["label.close"],
		  reverseButtons: false,
		 didOpen: () => {			
	   		$(".alertDeleteConfirmButton").attr("data-not-close-id", "myModalAlertAddModalNotClose");
			$(".alertDeleteCancelButton").attr("data-not-close-id", "myModalAlertAddModalNotClose");
		  }
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
	
	LoadAlerts: function (callbackFunction) {
		
    	Alert.Get(callbackFunction);
    },

 	UseAlerts: function (alertItemList, callbackFunction) {
    	
		
		$.each(alertItemList, function (k, alertItem){
		
			callbackFunction(alertItem);
		})
    }, 

	AddAlertToSidebar: function(sidebarAlertList){
		this.sidebarAlertList = sidebarAlertList;
		$.each(sidebarAlertList,function(key,item){
			 
			$.each(Common.eventGroupList, function(event, eventGroupList){
				var eventGroupDbNameAndId = item.eventGroupDbName + item.eventGroupId;
				var pageEventGroupDbNameAndId = Common.eventGroupList[event].dbName + Common.eventGroupList[event].id;
				
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
	
	ChooseAlert : function (sidebarAlarmId){
			
		if(Alert.lastChoosenAlertId == sidebarAlarmId){			
    		Alert.AlarmOnClick(sidebarAlarmId)
			this.isAlertSharedClick = false;
    		return;
    	}
    	
    	Alert.showInAlertMap(sidebarAlarmId, Alert.mapHelper);   
							 
    },

	showInAlertMap: function(sidebarAlarmId, mapHelperFunction){
		
		var alertItem = Alert.sidebarAlertList.find(f => f.id == sidebarAlarmId);
		if(alertItem != undefined){
			
			if(mapHelperFunction != null && !mapHelperFunction.IsMapHelperTable &&  alertItem.coordinateInfo != null){
				mapHelperFunction.addAlertToMap(alertItem)
			}												
				
			Alert.selectSidebarItem(sidebarAlarmId)								
			Alert.lastChoosenAlertId = sidebarAlarmId;
		}
		
	},
	
    
	selectSidebarItem: function (id){

		var sidebarId = "#" + id;
		$(".selected").removeClass("selected");
		$(sidebarId).addClass("selected");
		$("#sidebarAlarmContainer").scrollTo($(sidebarId), 500, {offsettop:100});
			
    },
	
	AlarmOnClick: function (sidebarAlarmId) {
    		
		if(Alert.isAlertSharedClick == false){
			$("#myModalAlert").modal('show');
		}
				    	
		$("#myModalAlert option").remove();
		
		var data = {};
    	data.layerId = Common.layerId;
    	
    	$.ajax({ type: "POST",   
    	     url: "/alert/get/eventType",
    	     async: true,
		     data: JSON.stringify(data),
		     datatype: 'json',
		     contentType: "application/json",
    	     success : function(eventTypeList)
    	     {
				Alert.OpenDetailALertModal(sidebarAlarmId, eventTypeList);
				
    	     }			
    	});
			
    },
	
	OpenDetailALertModal: function(id, eventTypeList){
		
		$('#sidebarAlarmEventType').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#sidebarAlarmEventGroup').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.eventGroup'),
		});
		
		$(".alertName").removeClass("has-error");
		$(".with-errors span").remove()
		
		var alertModal = $("#myModalAlert");

		$("#sidebarAlarmEventGroup").append("<option data-value='0' value='0'  name='0'>" + lang.get('label.eventGroup') + "</option>");
		$("#sidebarAlarmEventType").append("<option data-value='0' value='0'  name='0'>" + lang.get('label.search.event.type') + "</option>");
		
		$("form#alertEditForm>.sidebarAlarmId").remove();
		$("form#alertEditForm>.layerId").remove();
	 
		/*let getEventGroupUrl = "/alert/getEventGroups"*/

		$.each(Alert.eventGroupList, function(key, eventGroup){
			var eventGroupIdAndDbName = eventGroup.id /*+eventGroup.dbName;	*/;		
			$("#sidebarAlarmEventGroup").append("<option data-value='"+eventGroupIdAndDbName+"' value='" + eventGroup.id+ "'  name='"+eventGroup.id+"'>" + eventGroup.name + "</option>");
		});	
			
		$.each(eventTypeList, function(key, eventType){
			
			$("#sidebarAlarmEventType").append("<option data-value='"+eventType.id+"' value='" + eventType.id+ "'  name='"+eventType.id+"'>" + eventType.name + "</option>");
									
		});
				
		var alertListItem = this.sidebarAlertList.find(item => item.id == id);
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
		$("form#alertEditForm").append('<input class="layerId" type ="hidden" name="layerId" value='+Alert.layerId+'>')
		
		$("#modalAlarmDelete").attr("onclick", "Alert.sidebarAlarmDelete('"+id+"')");
		
		
		var eventGroupIdAndDbName = alertListItem.eventGroupId /*+alertListItem.eventGroupDbName;	*/								
		$("#sidebarAlarmEventType").val(alertListItem.eventTypeId).trigger('change');
		
		var value = $('#sidebarAlarmEventGroup option[data-value="'+eventGroupIdAndDbName+'"]').attr("value");
		
		$('#sidebarAlarmEventGroup').val(value).trigger('change');
		
		document.querySelectorAll('input[type=color]').forEach(function(picker) {

  			picker.addEventListener('change', function() {
				$("span#colorText").text(picker.value)
  			});
		});
		
		$('.sidebarAlarmEventGroup').on('change', function() {
			$("form#alertEditForm>.eventGroupDbName").remove();
			var eventGroupDbName = null;
			var eventGroupDbName = $(this).find('option:selected').attr("name");			
			$("form#alertEditForm").append('<input class="eventGroupDbName" type ="hidden" name="eventGroupDbName" value="default">') //'+eventGroupDbName+'
								
		});	
		
		$("#editModalShare").on("click", function(){
			alertModal.modal("hide");
			Alert.AlertShare(id);
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
		
//		$('#shareableUsers').select2({
//			allowClear: true,
//			width: '100%',
//			placeholder: lang.get('label.users'),
//			closeOnSelect: false
//		});
		
		$('#shareableUsers').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.users'),
			multiple:true,
			closeOnSelect: false
		});
		
		this.isAlertSharedClick = true;
						
		$("form#alertShareForm")[0].reset();
		$("#shareableUsersCheck").attr("disabled", false);
		$("#shareableUsers").attr("disabled", false); 
		
		$("#myModalAlertShare option").remove();
		var data = {};
	    data.layerId = this.layerId;
	    	
	    	$.ajax({ type: "POST",   
	    	     url: "/alert/getAlertPermissionUser?layerId="+data.layerId,
	    	     async: true,
			     data: JSON.stringify(data),
			     datatype: 'json',
			     contentType: "application/json",
	    	     success : function(userList)
	    	     {
//					$(".shareableUsers").append("<option> </option>");
					$.each(userList, function(key, user){
						
						$(".shareableUsers").append("<option data-value='"+user.id+"' value='" + user.id+ "'  name='"+user.id+"'>" + user.name + "</option>");
						
					});
							
					var myModalShare = $("#myModalAlertShare");
					
					myModalShare.modal('show');
					
					this.userList = userList;
					
				
					$('#shareableUsers').on('change', function() {
						
						var userIdList = $("#shareableUsers").val();
						
						if(userIdList.length != 0){
							
							$("#shareableUsersCheck").attr("disabled", true);
						}else{
							$("#shareableUsersCheck").attr("disabled", false);
						}
					 	
					});
					
//					
//					$('button[aria-describedby="select2-shareableUsers-container"]').on("change", function(){
//						alert("jdısjfdghj")
//					});
//					$('[aria-describedby="select2-shareableUsers-container"]').on("click", function(){
//						
//						var userIdList = $("#shareableUsers").val();
//						
//						if(userIdList.length != 0){
//							
//							$("#shareableUsersCheck").attr("disabled", true);
//						}else{
//							$("#shareableUsersCheck").attr("disabled", false);
//						}
//					});
					
					
					$('#shareableUsersCheck').change(function() {
						
				        if(this.checked) {
				            $("#shareableUsers").attr("disabled", true);
				        }else{
							$("#shareableUsers").attr("disabled", false); 
						}
				          
				    });									
					
	    	     }			
	    	});     

		this.shareAlertId = id;
		
	},
	
	AlertShareSave: function(){
				
		var alertId = this.shareAlertId;
		var selectedUserId = $(".shareableUsers").children("option:selected").val();	
		var userIdList = [];	
			
//		if($.trim(selectedUserId) != ''){
//			userIdList.push(selectedUserId)
//		}

		userIdList = $("#shareableUsers").val();
  		
		if ($('#shareableUsersCheck').is(':checked')) {
			
			$("#shareableUsers option").each(function()
			{
			    selectedUserId = $(this).val();
			    
			    if($.trim(selectedUserId) != ''){
					userIdList.push(selectedUserId)
				}
			});

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
				  location.reload();
               },
			   error: function (e) {
				
		    	 toastr.error(lang.props["label.alert.shared.error"])
		       }
           });
		
	},

	
	AlertReadCount: function(){		
		
		
		$.ajax({type:"POST",
		url: "/alert/readAlarmCount?layerId=" + Alert.layerId,
		async: true,
		success : function(readAlarmCount){
			$(".alert-events").find(".alertReadCount").text(readAlarmCount);
			$("#sidebarAlertReadCount").text(readAlarmCount)
		}
			
			
		});
	},
	
	SidebarAdd: function(){
		
		var myModal = $("#myModalAlertAdd");
		myModal.modal('show');
			
		Alert.OpenDetailAlertAddModal();
		
		$("#alertAdd").on("click", function(){
			myModal.modal('hide');
			Alert.AlertAdd();
		});
			
	},
	
	AlertAdd: function(){
		
		var alertName = $("#addAlertModalAlertName").val()//document.forms["alertAddForm"]["name"].value;
		
		$(".alertName").removeClass("has-error");
			$(".with-errors span").remove();
			
		if($.trim(alertName) == ''){
			$(".alertName").addClass("has-error");
			$(".with-errors").append('<span>' + lang.props["label.alert.name.not.null"] + '</span>');
			return;
		}
					
		Alert.Add(null);
		myModal.modal('hide');
				
	}


	
}
	
//$(document).ready(function () {


//	Alert.InitializeEvents();
//	Alert.Init();
	
//	$("#modalAlarmDelete").on("click", function(){
//		var alarmId = $(this).attr("data-id");
//		Alert.sidebarAlarmDelete(alarmId);
//		
//	})
	
//	$("#alarmSearchButton").on("click", function(){
//		Alert.alarmSearch();
//	})
	
	
//});