var CustomFormatter = {    
    PageKod: "Custom Formatter",
    ToDegreesMinutesAndSeconds: function (coordinate) {
        var absolute = Math.abs(coordinate);
        var degrees = Math.floor(absolute);
        var minutesNotTruncated = (absolute - degrees) * 60;
        var minutes = Math.floor(minutesNotTruncated);
        var seconds = Math.floor((minutesNotTruncated - minutes) * 60);
    //- 36°30′N 36°51′E
        return degrees + "°" + minutes + "′";// + seconds;
    },
    ConvertDMS: function (lat, lng) {
        var latitude = this.ToDegreesMinutesAndSeconds(lat);
        var latitudeCardinal = lat >= 0 ? "N" : "S";

        var longitude = this.ToDegreesMinutesAndSeconds(lng);
        var longitudeCardinal = lng >= 0 ? "E" : "W";

        return latitude + " " + latitudeCardinal + "\n" + longitude + " " + longitudeCardinal;
    },
    
    GetDateFormattedMoment : function (date, format){// 22.06.2020
    	return moment(date).format(format);
    },
    GetDateFormatted : function (date){
    	return this.GetDateFormattedMoment(date, "DD.MM.YYYY HH:mm:ss")
    },
    GetDateFormattedMomentNavTime : function (date){// 22.06.2020
    	return this.GetDateFormattedMoment(date, ("DD MMM YYYY"));
    },
    GetDatePrettyFormatted : function (date){
    	return moment(date).fromNow();
    },
    GetMonthName : function (monthNumber){ // Ocak : 0
    	return moment.months(monthNumber);
    },
    ConvertToDate : function (dateStr){
    	return moment(dateStr, 'DD.MM.YYYY').toDate();    	
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