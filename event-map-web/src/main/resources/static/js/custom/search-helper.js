var SearchHelper = {    
    Text: "SearchHelper",
        
    Init: function () {
    	
    },
    InitializeEvents: function () {
    	
    },
    Search: function (searchbox, markersLayer){
    	
    	try {
					
			var searchCoor = searchbox.getValue();
			
			var regex = new RegExp("MGRS", "i");
			//					39.9958, 32.7525 t.query.includes("MGRS")
			//					MGRS: 36S VK 78878 27326
			if (regex.test(searchCoor)){
				var splittedMgrs = searchCoor.split(":");
				var latlongMgrs = Page.MgrsToLatLong(splittedMgrs[1].trim());
				if (latlongMgrs[0]){
					var latMgrs = latlongMgrs[1];
					var longMgrs = latlongMgrs[2];
					var queryMgrs = latMgrs.toString() + "," + longMgrs.toString()
					searchCoor = queryMgrs;
				}
			}

			
			var splittedSearchCoor = searchCoor.split(",");
			var objSearchCoor = { lat : splittedSearchCoor[0].trim(), lng : splittedSearchCoor[1].trim()}
			
			var customSearchCoor = new L.latLng(objSearchCoor);
			
			Page.mapHelper.MyMap.setView(customSearchCoor);
			
			var marker = new L.Marker(customSearchCoor);

			markersLayer.clearLayers();
			markersLayer.addLayer(marker);
			
		} catch (error) {

		  	ToastrHelper.Event("Arama format hatası", 
		  		"Arama yapılacak query formatında hata bulunmaktadır. <br> Koordinat örneği: 39.9958, 32.7525 <br>Mgrs örneği : MGRS: 36S VK 78878 27326", 
		  		"1234");
		}
		
		
        setTimeout(function () {
            searchbox.hide();
            searchbox.clear();
        }, 300);
    	
    },

}

$(document).ready(function () {
    
	SearchHelper.Init();
	SearchHelper.InitializeEvents();
});