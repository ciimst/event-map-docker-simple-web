var ImageCompare = {
	
	Init: function(){
		
//		this.ComparePictures();
		//this.ChoosePictures();
	},
	InitializeEvents: function(){
		
	},
	
	ComparePictures: function(){ 
	  
	    var imageSrcList = [];
          
          
		var checkedList =  $('input[name = "imageCheckbox"]:checked');
		
		if(checkedList.length != 2){ 
				      
	        Swal.fire({
			   icon: 'error',
			   title: lang.get("label.warning"),
	           text: lang.get("label.you.must.select.2.images.to.compare")
			 })
		   
		}
	    else {
	  
		 
			$.each(checkedList, function(key, value){
				
				var id = $(value).attr("id");
				var imageSrc = $("#ImageMediaId_" + id).attr("src");
				
				 imageSrcList.push(imageSrc);  
		
			});
			
			$("#image-comparison__image_1").attr("src", imageSrcList[0]);  
			$("#image-comparison__image_2").attr("src", imageSrcList[1]);		  
				
	      	$("#chooseModalPictures").modal('hide')   
	        $("#compareModalPictures").modal('show') 
	   	    let modal = $("#compareModalPictures");
		
	        modal.find('.modal-title').text(lang.get("label.compare.pictures"));
	        
	     } 		
	
     },

	ChoosePictures: function(mediaListStr){
		
		var mediaList = JSON.parse(mediaListStr);
		

	    $("#chooseModalPictures").modal('show') 
		let modal = $("#chooseModalPictures");
		
		modal.find('.modal-title').text(lang.get("label.compare.pictures"));
		
	    modal.find(".media-area").html("");
	    
	    if(mediaList == null){
	        modal.find(".media-area").hide();
	    }else{
	        modal.find(".media-area").show();
	                     
	    	$.each( mediaList, function( key, media ) {
	              	
			    if(media.isVideo){ 
					/*continue; */
				}else{
			                
	        	    let mediaPath = contextPath + 'image/get/' + media.path;
	           		if(media.path.startsWith("http")){
	                	mediaPath = media.path;
		        	}
	       
					var html = '<label class="col-md-4"> <input id="'+media.id+'" type="checkbox" name = "imageCheckbox"/> <img id="ImageMediaId_'+media.id+'"src='+mediaPath+' /> </label>';
					
					$("#root").append(html)
	
	       		}
       		}); 
     
        }  
		        
  },

}