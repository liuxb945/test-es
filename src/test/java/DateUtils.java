 

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
/**
 * @author hezhengjun
 *   时间工具类
 */
public class DateUtils {
	private static Logger log = LoggerFactory.getLogger(DateUtils.class);
	/**
	 * 安全的格式化时间
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String safeFormatTime(Date date,String pattern){
		FastDateFormat fdf = FastDateFormat.getInstance(pattern);
		return fdf.format(date) ;
	}
	
	/**
	 * 判断给定的的小时和分钟是否比当前的Calendar时间小时和分谁大(比如，给定小时和分的时间是10点30 ,
	 *   现在Calendar时间为10点31,则给定时间--<--当前Calendar时间)
	 * @param cal
	 * @param hour
	 * @param minute
	 * @return 给定小时和分的时间>=当前时间返回true
	 */
	public static boolean isValidHourAndMinute(Calendar cal ,short hour,short minute){
		int timeHour = cal.get(Calendar.HOUR_OF_DAY) ;
		if( timeHour>hour){
			return false ;
		}
		if(timeHour ==hour&&cal.get(Calendar.MINUTE) >minute ){
			return false ;
		}
		return true ;
	}
	
	
	/**
	 * 格式化时间
	 * @param dateString
	 * @param pattern  yyyy-MM-dd
	 * @return
	 */
	public static Date dateToString(String dateString, String pattern) {
		SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
		try {
			Date time = formatDate.parse(dateString);
			return time;
		} catch (ParseException e) {
			log.error("dateToString error:", e);
		}
		return null;
	}
 
	
	
	/**
	 * 以友好的方式显示时间
	 * @param req
	 * @param time
	 * @return
	 */
	public static String friendlyTime(Date date) {
		if(date==null){
			return "" ;
		}
		Calendar cal = Calendar.getInstance();
		int time = (int) ((cal.getTimeInMillis() - date.getTime()) / 1000);
		if (time < 60) {
			return "刚刚";
		}
		if (time > 86400) {
			int day = time / 86400;
			if (day == 1) {
				cal.setTime(date);
				return "昨天";
			} else if (day < 30) {
				return day + "天前";
			} else if (day < 360) {
				int moth = day / 30;
				return moth + "个月前";
			} else {
				int year = day / 360;
				return year + "年前";
			}
		} 
		else if (time > 3600) {
			int hour = time / 3600;
			return hour + "小时前";
		} else {
			int hour = time / 60;
			return hour + "分钟前";
		}
//		return "今天" ;
	}
	
	/**
	 * 计算是否在同一个天(24小时)
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
//	public static boolean isSameDay(long oldTime ,long newTime){
//		long remain = newTime-oldTime ;
//		if( remain < 86400000 ){
//			return true ;
//		}
//		return false ;
//	}
	
	/**
	 * 计算是否在1个小时内
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	public static boolean isSameHour(long oldTime ,long newTime){
		long remain = newTime-oldTime ;
		if( remain < 3600000 ){
			return true ;
		}
		return false ;
	}
	
	
	/**
	 * 计算是否在30分钟内
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	public static boolean isSameHalfHour(long oldTime ,long newTime){
		long remain = newTime-oldTime ;
		if( remain < 1800000 ){
			return true ;
		}
		return false ;
	}
	
	
	public static void main(String[] args) {
		short t = 22;
		short m =51;
		System.out.println(isValidHourAndMinute(Calendar.getInstance(),t,m));
//		String temp[] = "22:00-23:20".split("-")[1].split(":");
//		if (DateUtils.isValidHourAndMinute(Calendar.getInstance(),
//				Short.parseShort(temp[0]), Short.parseShort(temp[1]))) {
//              System.out.println("在此范围内");
//		}
		
	}
	
}
