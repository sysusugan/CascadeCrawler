package util.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import util.DateUtil;
import util.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zebin
 * @since 2017-01-05.
 */
public abstract class DateParser {

    private static final Logger LOG = Logger.getLogger(DateParser.class);

    /**
     * 将dateStr的"今天","昨天","xx分钟前"根据timeZoneId替换成精确时间
     *
     * @param dateStr
     * @param timeZone
     * @return
     */
    protected abstract String initDateStr(String dateStr, TimeZone timeZone);

    protected abstract String getLang();

    /**
     * @param dateStr  日期字符串
     * @param timeZone 可能包含"今天","昨天","xx分钟前"等与当前时间相关的需要有该值
     * @return
     */
    public Date parse(String dateStr, TimeZone timeZone, List<String> patterns) throws ParseException {
        dateStr = DateUtil.prepareDateStr(dateStr);
        dateStr = initDateStr(dateStr, timeZone);
        Tuple2<String, Boolean> tuple2 = replacePm(dateStr);
        boolean isPm = tuple2._2();
        dateStr = tuple2._1();
        List<Tuple2<String, String>> monthList = monthList(getLang());
        List<String> weekList = weekList(getLang());
        Date date = tryPatterns(dateStr, timeZone, patterns);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.RHINO_STD_DATE_FORMAT);
        sdf.setTimeZone(timeZone);
        if (date == null) {
            if (StringUtils.isNumeric(dateStr) && dateStr.length() == DateUtil.RHINO_STD_DATE_FORMAT.length()) {
                try {
                    date = sdf.parse(dateStr);
                } catch (ParseException ignored) {
                }
            }
        }
        if (date == null) {
            try {
                dateStr = DateUtil.commonParse(dateStr, monthList, weekList);
                date = sdf.parse(dateStr);
            } catch (ParseException ignored) {
            }
        }
        if (date == null) {
            date = tryPatterns(dateStr, timeZone, Arrays.asList(DateUtil.patterns));
        }
        if (date != null && isPm) {
            date = new Date(date.getTime() + DateUtil.timeUnitHour * 12);
        }
        if (date == null) {
            throw new ParseException("date parse error", -1);
        }
        return date;
    }

    protected Tuple2<String, Boolean> replacePm(String dateStr) {
        return new Tuple2<>(dateStr, false);
    }

    /**
     * @param lang 语言
     * @return text->number
     */
    protected List<Tuple2<String, String>> monthList(String lang) {
        return baseMonthList(lang);
    }

    protected List<String> weekList(String lang) {
        return baseWeekList(lang);
    }

    /**
     * 获取某语言的月份对应表
     *
     * @param lang ar/fr
     * @return list of text->number
     * @throws ParseException
     */
    private List<Tuple2<String, String>> baseMonthList(String lang) {
        List<Tuple2<String, String>> ret = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String[] months = "01 02 03 04 05 06 07 08 09 10 11 12".split(" ");
        try {
            SimpleDateFormat foreignSdf = new SimpleDateFormat("MMMM", new Locale(lang));
            for (String month : months) {
                String m = foreignSdf.format(sdf.parse(month));
                ret.add(new Tuple2<>(m, month));
            }
            foreignSdf = new SimpleDateFormat("MMM", new Locale(lang));
            for (String month : months) {
                String m = foreignSdf.format(sdf.parse(month));
                ret.add(new Tuple2<>(m, month));
            }
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
        }
        return ret;
    }

    private List<String> baseWeekList(String lang) {
        List<String> ret = new LinkedList<>();
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale(lang));
        for (int i = 0; i < 7; ++i) {
            ret.add(sdf.format(new Date(currentTime + i * DateUtil.timeUnitDay)));
        }
        sdf = new SimpleDateFormat("EEE", new Locale(lang));
        for (int i = 0; i < 7; ++i) {
            ret.add(sdf.format(new Date(currentTime + i * DateUtil.timeUnitDay)));
        }
        return ret;
    }

    private Date tryPatterns(String dateStr, TimeZone timeZone, List<String> patterns) {
        if (patterns != null) {
            for (String pat : patterns) {
                SimpleDateFormat sdf = new SimpleDateFormat(pat);
                sdf.setTimeZone(timeZone);
                try {
                    return sdf.parse(dateStr);
                } catch (ParseException ignored) {
                }
            }
        }
        return null;
    }
}
