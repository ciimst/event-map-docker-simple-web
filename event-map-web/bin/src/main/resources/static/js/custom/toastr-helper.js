var ToastrHelper = {    
    Text: "ToastrHelper",
    EventType: "event",
    
    Init: function () {
    	
		toastr.options = {
				  "closeButton": true,
				  "debug": false,
				  "newestOnTop": false,
				  "progressBar": true,
				  "positionClass": "toast-bottom-left",
				  "preventDuplicates": true,
				  "onclick": null,
				  "showDuration": "300",
				  "hideDuration": "1000",
				  "timeOut": 0,
				  "extendedTimeOut": 0,
				  "showEasing": "swing",
				  "hideEasing": "linear",
				  "showMethod": "fadeIn",
				  "hideMethod": "fadeOut",
				  "tapToDismiss": true,
				}
    },
    InitializeEvents: function () {
    	
    	toastr.options.onShown = function() {  }
    	toastr.options.onHidden = function() {  }
    	toastr.options.onclick = function(e, k) { 

    		if (this.type == ToastrHelper.EventType){
    			
    			Page.mapHelper.showInMap(this.id);
    		}
    		
    	}
    	toastr.options.onCloseClick = function() {  }
    	
    },
    Event: function (title, spot, eventId){
    	toastr.error(spot, title, { id: eventId, type: this.EventType })
    },

}

$(document).ready(function () {
    
	ToastrHelper.Init();
	ToastrHelper.InitializeEvents();
});