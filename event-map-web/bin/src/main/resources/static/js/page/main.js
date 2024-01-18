var Main = {    
    PageText: "Ana Sayfa",
    PageUrl: "/home",
    PageKod: "main",    
    version: null,
    time: null,
    pageWidth: 0,
	menuOff: false,
	resizeTime: null,
	resizeDelta: 200,
	resizeTimeout: false,
    Init: function () {
    	
    	this.time = navtime;    	

    	Main.version = $.now();
    	//Main.SetTime();
    	Main.SetLanguage();
    	Main.Show();
    },
    InitializeEvents: function () {

    	Main.LocaleChangeEvent();

//    	$(".course-card div p, .course-card div h4, .course-card img").click( function(){
//    		$(".course-card").removeClass("selected");
//    		$(this).closest(".course-card").addClass("selected");
//    	});
//    	
      	var map = $("#map");
      	var pageWidth = map.width();
		var sidebarW = $(".sidebar-w").width();
		
		
		if (Main.menuOff == true) {
	        $("#map").width($(window).width());
	    } else {
	        $("#map").width($(window).width() - $(".sidebar-w").width());
	    }

	    $(".menu-sidebar").unbind("click");
	    $(".menu-sidebar").bind("click", function(e) {

	   		var name = $(this).attr("name");

	        if (name == "menu") {

	        	var toggle = $(this).attr("data-t");

	            if (toggle == "off") {
	               
	                $(this).attr("data-t", "on");
	                $("div.inside-sidebar").hide("slow");
	                map.css("max-width", "100%");
	                $("#map").css("width", "100%");
	                Main.menuOff = true;

	            } else {

	                Main.menuOff = false;
	                $("#news-live").show("slow", function() {
	                    map.width($("#map").width() - sidebarW); //395
	                });
	                $(this).attr("data-t", "off");
	            }

	            Page.mapHelper.MyMap._onResize();

	        } else {

	            var toggle = $(".menu-sidebar[name='menu']").attr("data-t");

	            if (toggle == "on") {
		
	                $("div#" + name).show("slow", function() {
	                    map.width(pageWidth - $(".sidebar-w").width()); //395					
	                });

	                $(".menu-sidebar[name='menu']").attr("data-t", "off");

	            } else {
		
	                $("div.inside-sidebar").hide();
	                $("div#" + name).show();
	            }
	        }

	    });
    	
//    	$(".li_dd1").click( function(){
//    		$(".li_dd1").removeClass("show");
//    		$(this).addClass("show");
//    	});
//    	$(".li_dd2").click( function(){
//    		$(".li_dd2").removeClass("show");
//    		$(this).addClass("show");
//    	});
//    	$(".d").click( function(){
//    		$(".d").removeClass("color");
//    		$(this).addClass("color");
//    	});
    	$(".right.sf-js-enabled a").click( function(){
    		$(".right.sf-js-enabled li").removeClass("active");
    		$(this).closest("li").addClass("active");
    	});
    	  

    }, 
   
    
    LocaleChangeEvent: function(){
    	
    	$.each($("#locales.language a"), function( i, l ){
    		
    		$(this).click(function(e) {
    			e.preventDefault(); 
    			
    			if ($(this).data("iso") != undefined) {    			  
    				window.location.replace('?lang=' + $(this).data("iso"));	 
				}
    		});
		}); 	

    },
    Set: function () {

    },
//    SetTime: function () {
//    	
//    	var navTimeDate = new Date();
//    	if (Main.time != null) {
//    		navTimeDate = CustomFormatter.ConvertToDate(Main.time);
//		}
//    	
//    	var navtimeFormatted = CustomFormatter.GetDateFormattedMomentNavTime(navTimeDate);
//    	$("#navtime").text(navtimeFormatted);
//    	
//    },
    SetLanguage: function(){
    	var langiso = lang.props["lang.iso"];
    	if(langiso){
    		moment.locale(langiso);
    	}
    },
    Show: function(){
    	setTimeout(function() {
    		$("body").addClass("no-transition");
    	}, 100)
    },
	WindowResize: function(){
		
		$(window).resize(function(){
			
			Main.resizeTime = new Date();
		    if (Main.resizeTimeout === false) {
		        Main.resizeTimeout = true;
		        setTimeout(Main.ResizeEnd, Main.resizeDelta);
		    }

		});
	},
	ResizeEnd: function(){		
				
		if (new Date() - Main.resizeTime < Main.resizeDelta) {			
		    setTimeout(Main.ResizeEnd, Main.resizeDelta);
		} else {
			
		    Main.resizeTimeout = false;
			Main.InitializeEvents();		        
		} 
	},	
}

$(document).ready(function () {
    
	Main.InitializeEvents();
	Main.Init();
	Main.WindowResize();	

});