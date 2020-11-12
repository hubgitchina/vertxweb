package com.demo.util;

import org.joda.time.DateTime;
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

}
