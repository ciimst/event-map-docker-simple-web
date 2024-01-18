var HeaderMenu = {
	layerId: null,
	Page : null,
	HeatMap: null,
	TimeDimension: null,
	Initialize: function(){
		
		HeaderMenu.layerId = paramLayerId;
		HeaderMenu.MenuSetActive();
	},
	Init: function(){

		$("#primaryRightMenuUl li").on("click", function(){//menü seçme işlemi
		
			event.stopPropagation();
			
			var name = $(this).children("a").attr("name");		
			$("#primaryRightMenuUl li").removeClass("active")		
			$("a[name!="+name+"]").removeClass("active")				
			$("a[name="+name+"]").parent().addClass("active")	
			
//			$("#navbarDropdown > a > i").css("color", "#181dec")

			if(name == "alarm" || name =="language" || name =="key"){
				$("#navbarDropdown > a > i").css("color", "#181dec")
			}else{
				$("#navbarDropdown > a > i").css("color", "#444")
			}
			
			if(name == "news-live" && window.location.pathname === contextPathWithoutSlash + "/region/"+HeaderMenu.layerId){
				
				$("#alarm").hide();
				$("#language").hide();
				$("#time").hide();
				$("#key").hide();
			}
			
			
			var locationUrlTimeDimension = contextPathWithoutSlash + "/timeDimension/"+ HeaderMenu.layerId;
			
			if(name == "alarm" || name =="language" || name =="time" || name =="key"){
				
				$(".eventTableSection").show();
			}
			
			if(name == 'timedimension'){
					
				$.removeCookie("timeDimensionDate", {  path: "/" });
				$.removeCookie("periodType", {  path: "/" });
				$.removeCookie("periodTime", {  path: "/" });
				
				if($.cookie("timeDimensionDate") == undefined || $.cookie("periodType") == undefined || $.cookie("periodTime") == undefined){
			
					
					var dateModel = HeaderMenu.DetailedDateUsingTimeDimension();
					var startdate = moment(dateModel.startDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE);
					var endDate = moment(dateModel.endDateStr, DateUtils.TURKISH).format(DateUtils.TURKISH_REVERSE);
					
					var timeDimensionDate = startdate + "/" + endDate;
					//moment().subtract(0, 'hour').startOf('days').format("YYYY.MM.DD HH:mm:ss")+"/"+ moment().add(1, 'days').startOf('days').format("YYYY.MM.DD HH:mm:ss")//"2020.01.01 00:00:00/2021.01.01 00:00:00",
					$.cookie("timeDimensionDate", timeDimensionDate, { expires: Page.cookieExpires, path: locationUrlTimeDimension });
					$.cookie("periodType",  "minute", { expires: Page.cookieExpires, path: locationUrlTimeDimension });
					$.cookie("periodTime",  "5", { expires: Page.cookieExpires, path: locationUrlTimeDimension });
	
				}			
			}	
			
		});
		

		document.addEventListener("DOMContentLoaded", function(){
		// make it as accordion for smaller screens
		//if (window.innerWidth > 992) {
		
			document.querySelectorAll('#primaryRightMenuUl .nav-item').forEach(function(everyitem){
		
				everyitem.addEventListener('mouseover', function(e){
		
					let el_link = this.querySelector('a[data-bs-toggle]');
		
					if(el_link != null){
						let nextEl = el_link.nextElementSibling;
						el_link.classList.add('show');
						nextEl.classList.add('show');
					}
		
				});
				everyitem.addEventListener('mouseleave', function(e){
					let el_link = this.querySelector('a[data-bs-toggle]');
		
					if(el_link != null){
						let nextEl = el_link.nextElementSibling;
						el_link.classList.remove('show');
						nextEl.classList.remove('show');
					}
		
		
				})
			});
		
		//}
		// end if innerWidth
		}); 

		
	},
	MenuSetActive: function(){
		
		var url = contextPath + "time/" + HeaderMenu.layerId;
		
		if(window.location.pathname === url){
			$("#primaryRightMenuUl li").removeClass("active")	
			$("a[name=time]").parent().addClass("active")	
		}
		
		url = contextPath + "settings/" + HeaderMenu.layerId;
		if(window.location.pathname.includes(url)){
			$("#primaryRightMenuUl li").removeClass("active")	
			$("a[name=settings]").parent().addClass("active")				
			$("#navbarDropdown > a > i").css("color", "#181dec")
			$("#primaryRightMenuStepBars").remove();
			
		}
		
		url = contextPath + "event-table-view/" + HeaderMenu.layerId;
		if(window.location.pathname.includes(url)){
			$("#primaryRightMenuUl li").removeClass("active")	
			$("a[name=event-table-view]").parent().addClass("active")	
			$("#primaryRightMenuStepBars").remove();
		}
		
		
		url = contextPath + "event-table/" + HeaderMenu.layerId;
		if(window.location.pathname.includes(url)){
			$("#primaryRightMenuUl li").removeClass("active")
			$("a[name=event-table]").parent().addClass("active")
			$("#primaryRightMenuStepBars").remove();
		}
		
		
		url = contextPath + "heatmap/" + HeaderMenu.layerId;
		if(window.location.pathname === url){
			$("#live-sidebar-header").text(lang.props["label.heatmap"])
			$("#primaryRightMenuUl li").removeClass("active")
			$("a[name=heatmap]").parent().addClass("active")
		}
		
		url = contextPath + "black-list"
		if(window.location.pathname === url){
			$("#primaryRightMenuUl li").removeClass("active")
			$("a[name=blackList]").parent().addClass("active")
			$("#navbarDropdown > a > i").css("color", "#181dec")
			$("#primaryRightMenuStepBars").remove();
		}
		
		
		if(window.location.pathname !== contextPath + "region/" + HeaderMenu.layerId){
			$("a[name=news-live]").attr("href", contextPath + "region/" + HeaderMenu.layerId );
		}
		
	},
	SidebarOpenAndCloseOperations: function(){
		
		$(document).mouseup(function(e) 
		{

			var menu = $("#" + $(".rightMenuDropDown").children("li.active").children("a").attr("name"));
			
			if (!(menu.is(e.target) && menu.has(e.target).length !== 0) && menu.length === 0) {
				
				menu = $("#" + $("#primaryRightMenuUl").children("li.active").children("a").attr("name"));
			}
			
		    if ((!menu.is(e.target) && menu.has(e.target).length === 0)) 
		    {
			
				
				if(name == "alarm" || name =="language" || name =="key" || name =="time"){
					$("#navbarDropdown > a > i").css("color", "#181dec")
				}else{
					$("#navbarDropdown > a > i").css("color", "#444")
				}
				
				
				//Anahtar sekmesinde olay gruplarının detay modalı kapatılırken ve alarm ile ilgili işlemler yapılırken sidebarın kapanmasını önlemek için.
				if(e.target.id == "eventGroupModal" || e.target.id == "keyDetailDialog" || e.target.id == "eventGroupDetailModalCloseIcon" || e.target.id == "myModalAlertAdd" || e.target.id == "myModalAlert" || e.target.id == "myModalAlertShare"
				|| e.target.getAttribute("data-not-close-id") === "myModalAlertAddModalNotClose"){
					return;
				}
				
		        menu.hide();
				HeaderMenu.MenuSetActive();
		    }

			if (($('.popover').has(e.target).length == 0) || $(e.target).is('.close')) {
		        $('.popover').popover('hide');
				$('.popover').remove();

		    }
		});
		
		
	},

	DetailedDateUsingTimeDimension: function(){
		//01/05/2022 16:43:11 -> startDate
		//2022.09.19 00:00:00/2022.09.20 00:00:00
		
		//TimeDimensionda kullanılan tarih aralığını setleme işlemleri yapılmaktadır.
		//Eğere detaylı arama da herhangi iir tarih arama işlemi varsa eğer bu tarihler kullanılmaktadır.
		//Sadece startDate ile arama yapılmışsa endDate bir gün sonrasına atanmaktadır.
		//Sadece endDate ile arama yapılmışsa eğer startDate bir gün öncesine atanmaktadır.
		//Eğer ikisinde de arama yoksa daha önceden timedimensiona atanmış olan değer kullanılmaktadır.
		
		//Eğer detaylı arama da,timeDimensiondate de, ayarlarda date bilgisi yok ise default olarak today atanır.
		//Eğere sadece ayarlardan değer gelmişse yine ona göre atamaları yapılır.
		
		var startDateStr = "";
		var endDateStr = "";
		
		
		if($.cookie("timeDimensionDate") !=  undefined && $.cookie("timeDimensionDate") != "") {
			
			const dates = $.cookie("timeDimensionDate").split("/");
			startDateStr = dates[0];
			endDateStr = dates[1];
	
			startDateStr = moment(startDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TURKISH);
			endDateStr = moment(endDateStr, DateUtils.TURKISH_REVERSE).format(DateUtils.TURKISH);
		}
		
		
		
		var cookieStartDate = $.cookie('startDate');
		var cookieEndDate = $.cookie('endDate');
		
		if(cookieStartDate != undefined && cookieStartDate != ''){
			
			startDateStr = moment(cookieStartDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
			
			if(cookieEndDate == undefined || cookieEndDate == ''){
				
				endDateStr = moment(startDateStr, DateUtils.TURKISH).add(1, 'days').format(DateUtils.TURKISH);;
			}
		}
		
		if(cookieEndDate != undefined && cookieEndDate != ''){
			
			endDateStr = moment(cookieEndDate, DateUtils.ENGLISH).format(DateUtils.TURKISH);
			
			if(cookieStartDate == undefined || cookieStartDate == ''){
				
				startDateStr = moment(endDateStr,  DateUtils.TURKISH).subtract(1, 'day').format(DateUtils.TURKISH);
			}
		}
		
		//today
		if((cookieStartDate == undefined || cookieStartDate == '') && ( cookieEndDate == undefined || cookieEndDate == '')){
			
			startDateStr = moment().subtract(0, 'hour').startOf('days').format(DateUtils.TURKISH);//YYYY.MM.DD HH:mm:ss
			endDateStr =  moment().add(1, 'days').startOf('days').format(DateUtils.TURKISH);//YYYY.MM.DD HH:mm:ss
		}
		
		
		var dateModel = {
			"startDateStr" : startDateStr,
			"endDateStr" : endDateStr
		}
		
		return dateModel
		
	},
	
	DropDownMenuSetCss: function(){
		
		$( '.rightMenuDropDown' ).each(function () {
		    this.style.setProperty( 'background-color', '#fff', 'important' );
		});
	}
	
}


$(document).ready(function () {
    
	HeaderMenu.Initialize();
	HeaderMenu.Init();
	HeaderMenu.DropDownMenuSetCss();
	

});