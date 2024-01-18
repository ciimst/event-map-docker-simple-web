
const BlackListTable = {
    dataTable: null,
    layerId: 0,
    cookieExpires: 7,
    dataTableRowCount: 20,
	isBlackListTablePage: true,
    tileLayerList : null,
    lastToggledBlackListId : null,
    datatableCriteria : null,
    pageRefreshDate: null,
    pageRefreshTimeInterval: null,
    currentPage: 0,
    SearchParams: {},
    eventGroupId: "",
    
    Init: function () {

		Main.blackListMode = true;
		BlackListTable.layerId = paramLayerId;
		Common.SetLayerId();
		Common.SetTimeLineStartDate();
		Common.LoadEventGroups(BlackListTable.layerId); // sync
		BlackListTable.GetEventGroupTreeData();
		Common.LoadKey();
    	Common.LoadTimeLine();
    	Common.SetTimeLine();
		Alert.Init(BlackListTable.mapHelper,BlackListTable.addAlertArea);
	
		HeaderMenu.SidebarOpenAndCloseOperations();
		BlackListTable.pageRefreshDate = pageRefreshDate;
		BlackListTable.pageRefreshTimeInterval = pageRefreshTimeInterval;

        BlackListTable.dataTableRowCount = dataTableRowCount;

        
 
        BlackListTable.PrepareHtmlElements(); 



        BlackListTable.LoadTable();
        

		
	
	    $("#blackListTable_filter").css("display", "none");

      
	    
		//mobil görünüme geçtiğinde
			
			$("#content").show();
			$('#news-live').hide();						
		//

     },

     InitializeEvents: function () {

        var options = $('#eventGroupId option');
		$("#layerId").on("change", function(){	
			
			var layerId = $(this).val();	
			var optionsList = $('#eventGroupId option');
			
			$('select#eventGroupId').val("").trigger('change');
			
			if(layerId == ""){
				
				reloadEventGroup()
			}
			
			let eventGroupFilterUrl = "/eventGroupFilter"
			$.post(eventGroupFilterUrl, {layerId: layerId})
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (response) {
				
				let unremoveList = [];
				$.each(response, function(key, response){
					
					unremoveList.push(response);
				});
				
				for(var i = 1; i<optionsList.length; i++){												
					optionsList[i].remove();
				}
				
				for(var j = 0; j<unremoveList.length; j++){			
					$('#eventGroupId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
				}		
			});
		});
		
		function reloadEventGroup(){
			
			for(var i = 1; i<options.length; i++){			
				$('#eventGroupId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
			}
		 }
	
	     let $editModal = $("#addBlackListModal"); 
         var options = $('#modalEventGroupId option');

	     $("#modalLayerId").on("change", function(){	
		
			var layerId = $(this).val();	
			var optionsList = $('#modalEventGroupId option');
			
			if(layerId == ""){
				reloadModalEventGroup()
			}
			let eventGroupFilterUrl = "/eventGroupFilter"
			$.post(eventGroupFilterUrl, {layerId: layerId})
			.fail(function (xhr) {
				xhr.state = false;
				console.error(xhr);
				xhr.description = lang.get("label.unknown.error");
				xhr.redirectUrl = null;
			})
			.always(function (response) {
				
				let unremoveList = [];
				$.each(response, function(key, response){
					unremoveList.push(response);
				});
				
				for(var i = 1; i<optionsList.length; i++){												
					optionsList[i].remove();
				}
				
				for(var j = 0; j<unremoveList.length; j++){			
					$('#modalEventGroupId').append('<option value='+unremoveList[j].id+' >'+unremoveList[j].name+'</option>')			
				}		
				
				if(BlackListTable.eventGroupId != ""){
					
					$editModal.find('select#modalEventGroupId').val(BlackListTable.eventGroupId).trigger('change');
				}
				else {
				
				    $editModal.find('select#modalEventGroupId').val("").trigger('change');
				}
				
			});
	   	 });

        function reloadModalEventGroup(){
	
		   for(var i = 1; i<options.length; i++){			
			   $('#modalEventGroupId').append('<option value='+options[i].value+' >'+options[i].text+'</option>')			
		    }
	    }
		
		$('#eventTypeId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#layerId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#eventGroupId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#modalEventTypeId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#modalLayerId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#modalEventGroupId').select2({
			allowClear: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
		
		$('#state').select2({
			allowClear: true,
			autoclose: true,
			width: '100%',
			placeholder: lang.get('label.search.event.type'),
		});
        
        $("#search-button").click(function() {

			BlackListTable.Search();
		});
	
    },

	addAlertArea: function(){
	
	},
	OpenDetailModal: function () {
	},
	GetEventGroupTreeData: function(){
		
		var eventGroupdbNameIdList = BlackListTable.GetShowEventGroupDbNameList(Common.eventGroupList, []);
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
	
    PrepareHtmlElements(){
		
		$("#primaryRightMenuUl li").removeClass("active")
		$("a[name*=black-list]").parent().addClass("active")

	},


	LoadTable: function () {
    	
		BlackListTable.dataTable = $('#blackListTable').DataTable({
			pageLength: BlackListTable.dataTableRowCount,
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
	            "url": "/black-list",
	            "type": "POST",
	            "dataType": "json",
	            "contentType": "application/json",
	            "data": function (d) {

					var order = [];
					var columnName = d.columns[d.order[0].column].name
					order.push(d.order[0])
					order[0].column = columnName;
//					order[0].dir = order[0].dir.toUpperCase();
					
					
					
					$.each(BlackListTable.SearchParams, function(key, value){
						d.columns.filter(f => f.name == key)[0].search.value = value;
					});
					
					BlackListTable.datatableCriteria = d;
					BlackListTable.currentPage = d.start;
					
					BlackListTable.datatableCriteria.order = order;
					
				/*	console.log(BlackListTable.datatableCriteria); */
					
					//BlackListTable.datatableCriteria.sortedColumnDefs = ["name", "asc"];
					
	                return JSON.stringify(d);
                     
	            },
	            beforeSend: function( xhr ) {
			    	$("#tableContainer").block({ message: null }); 
			  	},
			  	complete: function( xhr ) {
			    	$("#tableContainer").unblock({ message: null }); 
			  	},
			  	
	        },
	       order: [[7, 'desc']],
	        fnCreatedRow: function (nRow, aData, iDataIndex) { // Her bir satira rowid eklemek icin

		        $(nRow).attr('id', 'rowId' + aData.id); // or whatever you choose to set as the id
		        if(aData.id == null){
					$(nRow).addClass("d-none");
				}
		    },
	        columns: [
				{

					orderable: false,
					data: null,
					defaultContent: '',
					width: "10px",
				}, 
				{
					data: "name",
					name: "name",
					orderable: true,
	            	render: function(data, type, row) {		
		
						return !data ? "-" : row.name;
					}
				},
				{
					data: "layerName",
					name: "layerId",
					orderable: false,
	            	render: function(data, type, row) {		
						return !data ? "-" : "<span style='color:{1}'>{0}</span>".format(row.layerName, row.color) ;
					}
				},
				{
					data: "eventGroupName",
					name: "eventGroupId",
					orderable: false,
	            	render: function(data, type, row) {		
						return !data ? "-" : "<span style='color:{1}'>{0}</span>".format(row.eventGroupName, row.color) ;
					}
				},
				{
					data: "eventTypeName",
					name: "eventTypeId",
					orderable: false,
	            	render: function(data, type, row) {		
						return !data ? "-" : "<span style='color:{1}'>{0}</span>".format(row.eventTypeName, row.color) ;
					}
				},
				{
					data: "tag",
					name: "tag",
					orderable: true,
	            	render: function(data, type, row) {		
						return !data ? "-" : row.tag;
					}
				},
				{
					data: "createUser",
					name: "createUser",
					orderable: true,
	            	render: function(data, type, row) {		
						return !data ? "-" : row.createUser;
					}
				},
				{
					data: "createDate",
					name: "createDate",
					orderable: true,
	            	render: function(data, type, row) {		
						var createDateFormatted = CustomFormatter.GetDateFormatted(row.createDate);
						
						return !data ? "-" : createDateFormatted;
					}
				},
				{
					data: "state",
					name: "state",
					orderable: false,
					searchable: true,
					className: "dt-center",
					width: "20px",
	            	render: function(data, type, row) {
						
						var auhtroizeControl = $("#blackListState").attr("data-authorize-value")
						
						if(auhtroizeControl == "true" || auhtroizeControl == true){
							$(function() {
							    $('.state-toggle').bootstrapToggle({
									size: "small",
									offstyle: "danger blackListStateToggle",
									onstyle: "success blackListStateToggle",
									on: lang.get("label.active"),
							      	off: lang.get("label.passive" )
									
							    });
					  		})
	
							let isChecked = data ? "checked" : "";
							return '<input data-width="40" data-height="20" data-toggle="toggle" onChange="Common.StateChanged('+row.id+')" class="state-toggle" '+isChecked+' type="checkbox">';		
						
						}else{
							return row.state ?  "<span style='color:{1}'>{0}</span>".format("Aktif", "green") : "<span style='color:{1}'>{0}</span>".format("Pasif", "red");
						}
					}
				},
				{
					data: "actionStateType",
					name: "actionStateId",
					orderable: false,
					searchable: true,
					className: "dt-center",
					width: "20px",
	            	render: function(data, type, row) {		
						
						var color="";
						var actionStateType = row.actionStateType
						
						if(actionStateType == "finished"){
							
							color="green";
						}
						else if(actionStateType == "running"){
							
							color="red";
						}
						else {
							
							color="yellow";
						}
						
						return "<span style='color:{1}'>{0}</span>".format(lang.get( "label." +row.actionStateType), color);	
					}
				},
				
				{
					data: "operations",
					name: "operations",
					orderable: false,
	            	render: function(data, type, row) {		
						    return '<div class="dropdown" style="float: left;margin-left: 10px;"><button class="btn btn-sm btn-primary dropdown-toggle black-list-operation" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-cog"></i></button><div class="dropdown-menu" aria-labelledby="dropdownMenuButton"><a class="dropdown-item" onclick="BlackListTable.editBlackListModal('+row.id+')" href="#"><i class="fa fa-edit"></i> Düzenle</a><a class="dropdown-item user-settings-operation" data-toggle="modal" onclick="BlackListTable.deleteBlackListModal('+row.id+')" href="#"><i class="fa fa-trash"></i> Sil</a></div></div>'

					}
				},
					
	        ]
	    });

    },

   
       
	scrollToTop: function (rowId) {

		var targetEle = document.getElementById(rowId);
		var pos = targetEle.style.position;
		var top = targetEle.style.top;
		targetEle.style.position = 'relative';
		targetEle.style.top = '-55px';
		targetEle.scrollIntoView({behavior: 'smooth', block: 'start'});
		targetEle.style.top = top;
		targetEle.style.position = pos;
	 },

    Search: function () {// simple search
	
			BlackListTable.dataTable.draw();

    },
	
/*  filterGlobal :function() {
	    $('#blackListTable').DataTable().search(
	    $('#global_filter').val()).draw();
	}, */
    
	filter: function(){

		$('input.column_filter').on( 'keyup', function (e) {
			
			if(e.keyCode == 13){
				
				var i = $(this).parents('div').attr('data-column');
				BlackListTable.SearchParams[i] = $('input[name='+i+']').val();
				BlackListTable.dataTable.draw();
			}
	       	
	    });

        $("#layerId").on("change", function (e) {
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            BlackListTable.SearchParams[columnName] = value;
			BlackListTable.dataTable.draw();
        });

		$("#eventTypeId").on("change", function (e) {
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            BlackListTable.SearchParams[columnName] = value;
			BlackListTable.dataTable.draw();
        });

		$("#eventGroupId").on("change", function (e) {
			var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            BlackListTable.SearchParams[columnName] = value;
			BlackListTable.dataTable.draw();
        });

		 $("#state").on("change", function (e) {
          	var columnName = $(e.target.parentElement).attr('data-column')
            var value = e.target.value;
            BlackListTable.SearchParams[columnName] = value;
			BlackListTable.dataTable.draw();
        });


		$('.searchButton').on("click", function (e) {
            var items = $(".searchText");
            items.each(function (item, index) {
                if (index.value != null) {
					
                    var columnName = $(index).parents('div').attr('data-column')
                    var value = index.value;
                    BlackListTable.SearchParams[columnName] = value;
                }
            });
			
            BlackListTable.dataTable.draw();
        });
	},
	
	
	openBlackListModal: function() {
	
	    $("#inputTag").css("display", "none");
        $("#textareaTag").css("display", "block");
      
        let $editModal = $("#addBlackListModal");  

        BlackListTable.eventGroupId = "";

        $editModal.find('input#id').val("");
		$editModal.find('input#name').val("");
		$editModal.find('textarea#tag').val("");	
	    $editModal.find('select#modalLayerId').val("").trigger('change');
		$editModal.find('select#modalEventGroupId').val("").trigger('change');
     	$editModal.find('select#modalEventTypeId').val("").trigger('change');
		$editModal.find('input#state').prop("checked", false);		
		
		$editModal.find('#generalTagDiv').css("display", "block");
		$editModal.find('#generalLayerDiv').css("display", "block");
		$editModal.find('#generalEventGroupDiv').css("display", "block");
		$editModal.find('#generalEventTypeDiv').css("display", "block");

	    var options = $('#modalEventGroupId option');
		
	    $("#addBlackListModal").modal('show') 
		
		$("#addBlackListModal").find('.modal-title').text(lang.get("label.add.blackList"));

   },

   saveBlackListModal: function() {
	
	    $('#addBlackListModal').modal({backdrop: 'static', keyboard: false})    
	    $("#addBlackListModal").modal('show')   
	     
  /*    $("input[name=name]").val();
	    let name = $("#name") */
     
		if ($('input#name').val() == "") {
		
		    Swal.fire({	 
				  icon: 'warning',
				  text: lang.get("label.black.list.name.correctly"),
				  showConfirmButton: false,
				  timer: 1500
			  })
	        
            return;
		}
		
		if (document.getElementById("textareaTag").style.display == "block") {
		
			if ($('textarea#textareaTag').val() == "") {
			
			    Swal.fire({	 
					  icon: 'warning',
					  text: lang.get("label.black.list.tag.correctly"),
					  showConfirmButton: false,
					  timer: 1500
				  })
		         
		        return;
			}
		}	
			
		if (document.getElementById("inputTag").style.display == "block") {
			
			if ($('input#inputTag').val() == "") {
			
			    Swal.fire({	 
					  icon: 'warning',
					  text: lang.get("label.black.list.tag.correctly"),
					  showConfirmButton: false,
					  timer: 1500
				  })
		         
		        return;
			}
		}	

		if ($('select#modalLayerId').val() == "") {
		
		    Swal.fire({	 
				  icon: 'warning',
				  text: lang.get("label.layer.correctly"),
				  showConfirmButton: false,
				  timer: 1500
			  })
	        
           return;
		}
			
		if ($('select#modalEventGroupId').val() == null || $('select#modalEventGroupId').val() == "") {
		
		    Swal.fire({	 
				  icon: 'warning',
				  text: lang.get("label.event.group.correctly"),
				  showConfirmButton: false,
				  timer: 1500
			  })
	          
	        return;
		}
       
       	var message = "";
       	if( $("input#state").is(':checked') ){
			message = lang.get("label.you.cannot.undo.this.action.events.eligible.blacklist.deactivated");      		
       	}
       

		Swal.fire({
		  title: lang.get("label.are.you.sure.want.save"),
	      text: message,
		  showCancelButton: true,
		  confirmButtonText:lang.get("label.save"),
		  cancelButtonText: lang.get("label.no"),
		}).then((result) => {
	
		  if (result.isConfirmed) {	
	         let modalData = $('#addBlackListModal *').serializeArray();
	
	         if (modalData.find(f=>f.name=="id").value=="") {
	               
	             modalData.find(f=>f.name=="inputTag").value="";
	             modalData[3].name="tag";
	         }
	
	        else {
	               
	             modalData.find(f=>f.name=="textareaTag").value=="";
	             modalData[2].name="tag";
	         }
	
	         $.ajax({   
					 type : "POST",
				     url: contextPath + "save",   
				     data: modalData,
				     success : function(result)
				     {
				    	 if(result.state == true){
	
	                         setInterval(()=>window.location.reload(false),500);
	
	                          Swal.fire({
								  icon: 'success',
								  text: "Kaydedildi!",
							   })  
				    	
				    	 }else{
	 
	                       /*  Swal.fire({
									 icon: 'error',
									 title: 'Oops...',
									 text: lang.get("label.failed.transaction"),
									 
							}) */
							
	                        
				    		 Swal.fire({
								  icon: 'error',
								  title: 'Uyarı!',
								  text: result.description ? result.description : lang.get("label.unknown.error"),
								 
							 })  
				    	 }
				     },
				    
		    });
	
		  } else if (result.isDenied) {
	
	        Swal.fire({
				  icon: 'info',
				  text: "Değişiklikler kaydedilmedi!",
			 })  
	
		  }
		});
	 
  },
 
  editBlackListModal: function(rowId) {

       $("#textareaTag").css("display", "none");
       $("#inputTag").css("display", "block");
 
      
  
       $.ajax({   
				type : "POST",
			    url: contextPath + "edit",    
			    data: {blackListId: rowId},
			    success : function(response)
			    {
			    	   

					if(response.state){
						
						 let $editModal = $("#addBlackListModal"); 
					     $("#addBlackListModal").modal('show')    
					     $("#addBlackListModal").find('.modal-title').text(lang.get("label.edit.blackList"));	

						var result = response.data
						BlackListTable.eventGroupId = result.eventGroupId;
		
						$editModal.find('input#id').val(result.id);
						$editModal.find('input#name').val(result.name);
						$editModal.find('input#inputTag').val(result.tag);	
						$editModal.find('select#modalLayerId').val(result.layerId).trigger('change');
						$editModal.find('select#modalEventGroupId').val(result.eventGroupId).trigger('change');
						$editModal.find('select#modalEventTypeId').val(result.eventTypeId).trigger('change');
						$editModal.find('input#state').prop("checked", result.state);	
						
					
						
						$editModal.find('#generalTagDiv').css("display", "none");
						$editModal.find('#generalLayerDiv').css("display", "none");
						$editModal.find('#generalEventGroupDiv').css("display", "none");
						$editModal.find('#generalEventTypeDiv').css("display", "none");
		
		
		
					}else{
						Swal.fire({
						  icon: 'error',
						  title: 'Uyarı!',
						  text: response.description
						 
					 	}) ;
					}  
                    
			
			     },
			    
		 });
 
   },

   deleteBlackListModal: function(rowId){

		Swal.fire({
		  title: lang.get("label.are.you.sure.want.delete"),
		  text: "Silinen blackliste ait olayların durumu aktif yapılacaktır. Bu işlemi geri alamazsınız! ",
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: lang.get("label.yes"),
		  cancelButtonText: lang.get("label.no"),
		 }).then((result) => {
		   if (result.isConfirmed) {
	
			 $.ajax({   
					type : "POST",
				    url: contextPath + "delete",    
				    data: {blackListId: rowId},
				    success : function(result)
				    {
				    	if(result.state){
	
		                    setInterval(()=>window.location.reload(false),500);
	
	                        Swal.fire(
						      'Silindi!'
						     )
		
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
	
}

document.addEventListener('DOMContentLoaded', function () {

    BlackListTable.Init();
    BlackListTable.InitializeEvents();
	
	BlackListTable.filter();

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



