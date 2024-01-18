var JqueryTmplHelper = {    
    PageKod: "Jquery template helper",
    startsWithHttp: function (content) {
        
    	return content.startsWith('http');
    },
}



//function toDegreesMinutesAndSeconds (coordinate) {
//    var absolute = Math.abs(coordinate);
//    var degrees = Math.floor(absolute);
//    var minutesNotTruncated = (absolute - degrees) * 60;
//    var minutes = Math.floor(minutesNotTruncated);
//    var seconds = Math.floor((minutesNotTruncated - minutes) * 60);
////- 36°30′N 36°51′E
//    return degrees + "°" + minutes + "′";// + seconds;
//}
//
//function convertDMS (lat, lng) {
//    var latitude = this.toDegreesMinutesAndSeconds(lat);
//    var latitudeCardinal = lat >= 0 ? "N" : "S";
//
//    var longitude = this.toDegreesMinutesAndSeconds(lng);
//    var longitudeCardinal = lng >= 0 ? "E" : "W";
//
//    return latitude + " " + latitudeCardinal + "\n" + longitude + " " + longitudeCardinal;
//}
//
//function getDatePrettyFormatted (date){
//	return jQuery.format.prettyDate(date);
//}