var CustomDateRangePicker = {
	Init: function(){

		 $('input[name="daterange2"]').daterangepicker({
	    	"alwaysShowCalendars": true,
			"opens": "center",
			"drops":"up",
			"applyClass": "btn-primary daterangeApplyButton",
			"cancelClass": "btn-default daterangeCancelButton",			
			timePicker: true,
			timePicker24Hour:true,
			timePickerSeconds:true,
			timePickerIncrement: 1,
			showDropdowns:true,
			
	        ranges: {
	           'Bugün': [moment().subtract(0, 'hour').startOf('days'), moment().subtract(0, 'hour').endOf('days')],
	           'Dün': [moment().subtract(1, 'days').subtract(0, 'hour').startOf('days'), moment().subtract(1, 'days').subtract(0, 'hour').endOf('days')],
	           'Son 7 gün': [moment().subtract(6, 'days').subtract(0, 'hour').startOf('days'), moment()],
	           'Son 30 gün': [moment().subtract(29, 'days'), moment()],
	           'Bu ay': [moment().startOf('month'), moment().endOf('month')],
	           'Geçen ay': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	        },
			"locale": {
	        "format": "DD.MM.YYYY HH:mm:ss",
			"language":"tr-TR",
	        "separator": " - ",
	        "applyLabel": "Uygula",
	        "cancelLabel": "Vazgeç",
	        "fromLabel": "Dan",
	        "toLabel": "a",
	        "customRangeLabel": "Tarih Aralığı",
	        "daysOfWeek": [
	            "Pt",
	            "Sl",
	            "Çr",
	            "Pr",
	            "Cm",
	            "Ct",
	            "Pz"
	        ],
	        "monthNames": [
	            "Ocak",
	            "Şubat",
	            "Mart",
	            "Nisan",
	            "Mayıs",
	            "Haziran",
	            "Temmuz",
	            "Ağustos",
	            "Eylül",
	            "Ekim",
	            "Kasım",
	            "Aralık"
	        ],
			
	        "firstDay": 1
	    }
	    });
	}
}	
	   