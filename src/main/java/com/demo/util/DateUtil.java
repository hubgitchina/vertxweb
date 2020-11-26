package com.demo.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

/**
 * @ClassName: DateUtil
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-11-12 11:22
 * @Version 1.0
 */
public class DateUtil {

	/**
	 * 获取每周的开始日期、结束日期
	 * 
	 * @param week
	 *            周期 0本周，-1上周，-2上上周，1下周，2下下周；依次类推
	 * @return 返回date[0]开始日期、date[1]结束日期
	 */
	public static LocalDate[] getBeginAndEndOfTheWeek(int week) {

		DateTime dateTime = new DateTime();
		LocalDate date = new LocalDate(dateTime.plusWeeks(week));

		date = date.dayOfWeek().withMinimumValue();
		return new LocalDate[] { date, date.plusDays(1), date.plusDays(2), date.plusDays(3),
				date.plusDays(4), date.plusDays(5), date.plusDays(6) };
	}

	/**
	 * @Author wangpeng
	 * @Description 计算特定日期是否在该区间内
	 * @Date 11:32
	 * @Param
	 * @return
	 */
	public static boolean isExistScope(DateTime begin, DateTime end, DateTime custom) {

		Interval i = new Interval(begin, end);
		boolean contained = i.contains(custom);
		return contained;
	}

	/**
	 * @Author wangpeng
	 * @Description 获取当前是一周星期几
	 * @Date 11:27
	 * @Param
	 * @return
	 */
	public static String getWeek() {

		DateTime dts = new DateTime();
		String week = null;
		switch (dts.getDayOfWeek()) {
		case DateTimeConstants.SUNDAY:
			week = "星期日";
			break;

		case DateTimeConstants.MONDAY:
			week = "星期一";
			break;

		case DateTimeConstants.TUESDAY:
			week = "星期二";
			break;
		case DateTimeConstants.WEDNESDAY:
			week = "星期三";
			break;
		case DateTimeConstants.THURSDAY:
			week = "星期四";
			break;
		case DateTimeConstants.FRIDAY:
			week = "星期五";
			break;
		case DateTimeConstants.SATURDAY:
			week = "星期六";
		default:
			break;
		}
		return week;
	}

	/**
	 * @Author wangpeng
	 * @Description 获取指定时间是一周的星期几
	 * @Date 11:29
	 * @Param
	 * @return
	 */
	public static String getWeekPoint(Integer year, Integer month, Integer day) {

		LocalDate dts = new LocalDate(year, month, day);
		String week = null;
		switch (dts.getDayOfWeek()) {
		case DateTimeConstants.SUNDAY:
			week = "星期日";
			break;
		case DateTimeConstants.MONDAY:
			week = "星期一";
			break;
		case DateTimeConstants.TUESDAY:
			week = "星期二";
			break;
		case DateTimeConstants.WEDNESDAY:
			week = "星期三";
			break;
		case DateTimeConstants.THURSDAY:
			week = "星期四";
			break;
		case DateTimeConstants.FRIDAY:
			week = "星期五";
			break;
		case DateTimeConstants.SATURDAY:
			week = "星期六";
			break;

		default:
			break;
		}
		return week;
	}

	public static void main(String[] args) {

		DateTime dateTime = new DateTime();

		// 一周的开始日期
		String monday = dateTime.dayOfWeek().withMinimumValue().toString("yyyyMMdd");

		// 一周的结束日期
		String sunday = dateTime.dayOfWeek().withMaximumValue().toString("yyyyMMdd");

		System.out.println("一周的开始日期：" + monday);
		System.out.println("一周的结束日期：" + sunday);

		DateTime begin = new DateTime("2020-11-23");
		DateTime end = new DateTime("2020-11-29");
		DateTime custom = new DateTime("2020-11-23");
		System.out.println(isExistScope(begin, end, custom));
	}
}
