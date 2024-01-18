package com.imst.event.map.web.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DateUtils {
	
	public static final String TURKISH 			= "dd.MM.yyyy HH:mm:ss";
	public static final String HM 				= "HH:mm";
	public static final String FILE_NAME 		= "yyyy-MM-dd_HH-mm-ss";
	public static final String FOLDER 			= "ddMMyyyyHHmmssSS";
	public static final String FOLDER_TREE		= "yyyy/MM/dd/";
	public static final String W3C				= "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String NEWSML 			= "yyyyMMdd'T'HHmmssZ";
	public static final String TURKISH_DATE 	= "dd.MM.yyyy";
	public static final String ENGLISH 			= "dd/MM/yyyy HH:mm:ss";
	public static final String ENGHLISH_DATE 	= "dd/MM/yyyy";
	public static final String DATABASE 		= "yyyy-MM-dd HH:mm:ss.SS";
	public static final String DATABASE_DATE	= "yyyy-MM-dd";
	//public static final String ABONE            = "yyyyMMddHHmmssSS";
	public static final String TEXT				= "HH:mm dd/MM/yy";
	public static final String COMPARE          = "dd-MM-yyyy";
	public static final String NAV_TIME		 	= "dd MMM yyyy";
	
	public static Calendar getCalendar(){
		
		return Calendar.getInstance();
	}
	
	public static Timestamp toTimestamp(Date date){
		
		if(date == null) return null;
		return new Timestamp(date.getTime());
	}
	
	public static Date toDate(Timestamp timestamp){
		
		if(timestamp == null) return null;
		return new Date(timestamp.getTime());
	}
	
	public static Date now(){
		
		return getCalendar().getTime();
	}
	
	public static Timestamp nowT(){
		
		return toTimestamp(now());
	}
	
	public static String nowFormatted(String format){
		
		return format(now(), format);
	}
	
	public static String format(Date date, String format){
		
		if(date == null) return "";
		return new SimpleDateFormat(format, new Locale("fr")).format(date);
	}
	
	public static String formatWithLocale(Date date, String format, String locale){
		
		if(date == null) return "";
		return new SimpleDateFormat(format, new Locale(locale)).format(date);
	}
	
	public static String format(Timestamp timestamp, String format){
		
		return format(toDate(timestamp), format);
	}
	
	public static String formatNow(String format){
		
		return new SimpleDateFormat(format).format(new Date());
	}
	
	public static String formatWithCurrentLocale(Timestamp timestamp){
		
		return format(toDate(timestamp), ApplicationContextUtils.getMessage("label.time.format"));
	}
	
	public static String formatWithCurrentLocale(Date date){
		
		return format(date, ApplicationContextUtils.getMessage("label.time.format"));
	}
	
	public static Date convertToDate(String dateString, String dateFormat){
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date parsedDate;
		try {
			
			parsedDate = sdf.parse(dateString);
		} catch (ParseException e) {
			
			return null;
		}
		return parsedDate;
	}
	
	public static Timestamp convertToTimestamp(String dateString, String dateFormat){
		
		return toTimestamp(convertToDate(dateString, dateFormat));
	}
	
	public static Date getDateOnly(Date date){
		
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static Timestamp getDateOnly(Timestamp timestamp){
		
		return toTimestamp(getDateOnly(toDate(timestamp)));
	}
	
	public static Date today(){
		
		return getDateOnly(now());
	}
	
	public static Timestamp todayT(){
		
		return getDateOnly(nowT());
	}
	
	
	/**
	 * @param dayCount Önceki günler için eksi değer, sonraki günler için artı değer girilir.
	 * @return 
	 */
	public static Timestamp getFromGivenTime(int dayCount, Timestamp ts) {
		
		Timestamp calculated;
		if(ts == null){//null girilirse şimdiden itibaren alınıyor.
			calculated = new Timestamp(new Date().getTime());
		} else {
			calculated = new Timestamp(ts.getTime());
		}
		long day = dayCount * (1000L * 60L * 60L * 24L);
		calculated.setTime(calculated.getTime() + day);
		return calculated;
	}
	
	/**
	 * @param dayCount Önceki günler için eksi değer, sonraki günler için artı değer girilir.
	 * @return
	 */
	public static Timestamp getFromNow(int dayCount) {
		
		return getFromGivenTime(dayCount, null);
	}
	
	public static Timestamp getBeforeGivenTime(int dayCount, Timestamp ts) {
		
		return getFromGivenTime(dayCount * -1, ts);
	}
	
	public static Timestamp getAfterGivenTime(int dayCount, Timestamp ts) {
		
		return getFromGivenTime(dayCount, ts);
	}
	
	public static String getDayName(Date date, Locale locale)
    {
		
		SimpleDateFormat df=new SimpleDateFormat("EEEE", locale);
		return df.format(date);
    }
	
	public static String getDayName(Date date, String locale){
		
		return getDayName(date, new Locale(locale));
	}
	
	public static String putNowToFormat(String text, String dateFormat) {
		return String.format(text, formatNow(dateFormat));
	}
	
	public static int getCurrentYear(){
		
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	/**
	 * @return date2 - date1
	 */
	public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
		long milliesRest = diffInMillies;
		for ( TimeUnit unit : units ) {
			long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit,diff);
		}
		return result;
	}
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * @return ts2 - ts1
	 */
	public static Map<TimeUnit,Long> computeDiff(Timestamp ts1, Timestamp ts2) {
		
		return computeDiff(new Date(ts1.getTime()), new Date(ts2.getTime()));
	}
	
	public static String computeDiffFormatted(Timestamp ts1, Timestamp ts2) {
		
		Map<TimeUnit, Long> timeUnitLongMap = computeDiff(ts1, ts2);
		Long day = timeUnitLongMap.get(TimeUnit.DAYS);
		Long hour = timeUnitLongMap.get(TimeUnit.HOURS);
		Long min = timeUnitLongMap.get(TimeUnit.MINUTES);
		Long sec = timeUnitLongMap.get(TimeUnit.SECONDS);
//		Long mili = timeUnitLongMap.get(TimeUnit.MILLISECONDS);
		String diff = "";
		diff += (day == null || day == 0L) ? "" : day + ApplicationContextUtils.getMessage("day.short") + " ";
		diff += timeFix(hour) + ":";
		diff += timeFix(min) + ":";
		diff += timeFix(sec);
//		diff += timeFix(mili);
		return diff;
	}
	
	private static String timeFix(long time) {
		String result = "" + time;
		if (time < 10L) {
			result = "0" + result;
		}
		return result;
	}
}
