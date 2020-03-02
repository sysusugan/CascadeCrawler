package util;

import com.yeezhao.commons.util.Pair;
import com.yeezhao.commons.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import util.date.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zebin
 * @since 2016-06-02.
 */
public class DateUtil {

    public static final long timeUnitHour = 1000L * 60 * 60;
    public static final long timeUnitDay = timeUnitHour * 24;
    public static final long timeUnitWeek = timeUnitDay * 7;
    public static final long timeUnitMonth = timeUnitDay * 31;
    public static final long timeUnitYear = timeUnitDay * 366;
    private static final Logger LOG = Logger.getLogger(DateUtil.class);
    public static Pattern NUMBER_PATTERN = Pattern.compile("([\\d]+)");
    public static final String RHINO_STD_DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String[] patterns = {
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd.HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy.MM.dd",
            "yyyyMMddHHmmss",
            "MM-dd HH:mm:ss",
            "MM-dd.HH:mm:ss",
            "MM-dd HH:mm",
            "MM-dd",
            "yyyy.MM.dd HH:mm",
    };


    private static ThreadLocal<SimpleDateFormat[]> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd-HH:mm"),
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"),
            new SimpleDateFormat("yyyy年MM月dd日HH:mm分"),
            new SimpleDateFormat("yyyy-MM-dd/HH:mm"),
            new SimpleDateFormat("yyyy. MM. dd"),
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd |"),
            new SimpleDateFormat("yyyy/MM/dd/ HH:mm"),
            new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.ENGLISH)
    });


    public static Date str2date(String string) throws ParseException {
        return new SimpleDateFormat(RHINO_STD_DATE_FORMAT).parse(string);
    }

    public static String toRhinoStandardDate(String dateStr) throws ParseException {
        return toRhinoStandardDate(smartParse(dateStr));
    }

    public static String toRhinoStandardDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(RHINO_STD_DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date parseDate(String pattern, String dateStr) throws ParseException {
        return new SimpleDateFormat(pattern).parse(dateStr);
    }

    public static Date date(String dateStr) {
        try {
            return smartParse(dateStr);
        } catch (Exception e) {
            return guessDate(dateStr);
        }
    }

    public static Date foreignDate(String dateStr) throws ParseException {
        if (StringUtils.isNumeric(dateStr) && dateStr.length() == RHINO_STD_DATE_FORMAT.length()) {
            // yyyyMMddHHmmss
            try {
                return new SimpleDateFormat(RHINO_STD_DATE_FORMAT).parse(dateStr);
            } catch (Exception ignored) {
            }
        }
        if (dateStr.contains("#")) {
            String[] split = dateStr.split("#");
            // text#"ar"#timezoneid(可选)
            String type = split[1];
//            if ("ar".equals(type)) {
//                return ArabUtil.date(dateStr.replace("#ar", ""));
//            }
            // text#"multi_format"#timezoneid#format1#format2
            if ("multi_format".equals(type)) {
                for (int i = 3; i < split.length; i++) {
                    try {
                        Pattern pattern = Pattern.compile("\\d{4}");
                        Matcher matcher = pattern.matcher(split[0]);
                        if (!matcher.find()) {
                            // 待解析的日期文本串不含年份
                            Date date = getCalendar(split[2]).getTime();
                            String yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(date);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(split[i]);
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(split[2]));
                            String p1 = "(\\d{1,2}:\\d{1,2})";
                            String hourMin = TextUtil.extractFirstWord(dateStr, Pattern.compile(p1)).replace(":", "") + "00";
                            String s = yyyyMMdd + hourMin;
                            return new SimpleDateFormat(RHINO_STD_DATE_FORMAT).parse(s);
                        }
                        return new SimpleDateFormat(split[i]).parse(split[0]);
                    } catch (Exception e) {

//                        if (i == split.length - 1) {
//                            return ArabUtil.date(dateStr.replace("#multi_format", ""));
//                        }
                    }
                }
            }
            // text#"format_lang"#format#language
            // 2016-11-10#"format_lang"yyyy-MM-dd#en
            if ("format_lang".equals(type)) {
                return new SimpleDateFormat(split[2], new Locale(split[3])).parse(split[0]);
            }
            // text#format,不建议使用这个，建议使用 text#"multi_format"#timezoneid#format1#format2
            if (split.length == 2) {
                return new SimpleDateFormat(split[1]).parse(split[0]);
            }
        }
        try {
            return smartParse(dateStr, patterns);
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static String prepareDateStr(String dateStr) {
        char[] spaces = new char[]{160, 12288};// 空格替换成32的空格
        for (char space : spaces) {
            dateStr = dateStr.replace(space + "", " ");
        }
        return dateStr.trim();
    }

    /**
     * 智能识别日期格式字符串,转换失败就抛异常
     *
     * @param dateStr  待转换的日期字符串
     * @param patterns
     * @return 日期
     * @throws ParseException
     */
    public static Date smartParse(String dateStr, String[] patterns) throws ParseException {
        final String backup = dateStr;
        if (StringUtil.isNullOrEmpty(dateStr)) {
            throw new ParseException("日期转换失败,dateStr=" + dateStr, 0);
        }
        dateStr = prepareDateStr(dateStr);
        try {
            if (dateStr.contains("今天")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String curDate = sdf.format(new Date());
                dateStr = dateStr.replace("今天", curDate);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        dateStr = dateStr.replaceAll("\\/", "-");
        dateStr = dateStr.replace("年", "-").replace("月", "-").replace("日", " ")
                .replace("时", ":").replace("分", ":").replace("秒", " ").replaceAll("\\s+", " ").trim();
        for (String pattern : patterns) {
            Date date = tryPatternDate(dateStr, pattern);
            if (date != null) {
                return date;
            }
        }

        for (SimpleDateFormat datePattern : threadLocal.get()) {
            try {
                return datePattern.parse(backup.trim());
            } catch (ParseException ignored) {
            }
        }
        throw new ParseException("Smart Parse Error, return null, original string: " + backup, 0);
    }

    public static Date smartParse(String dateStr) throws ParseException {
        return smartParse(dateStr, patterns);
    }


    public static Date tryPatternDate(String dateStr, String pattern) {
        try {
            DateTime dt = DateTimeFormat.forPattern(pattern).parseDateTime(dateStr);
            if (dt != null) {
                if (!pattern.contains("yyyy")) { // 年份缺失，设置为当年, e.g. 01-01
                    dt = dt.plusYears(new DateTime().getYear() - 2000);
                }
                if (dt.getYear() < 1900) { // 缺失世纪，一般按2000年之后算, e.g. 15-01-01
                    dt = dt.plusYears(2000);
                }
                return dt.toDate();
            }
        } catch (Exception e) {
            // 格式不对,换个格式尝试匹配就好
//                LOG.warn(e.getMessage() + "\nDateUtil error dateStr: " + dateStr, e);
        }
        return null;
    }

    public static Date guessDate(String dateStr) {
        if (StringUtil.isNullOrEmpty(dateStr)) {
            return null;
        }
        if (dateStr.length() == 13 && StringUtil.isNumeric(dateStr)) {
            return new Date(Long.valueOf(dateStr));
        }
        if (dateStr.equals("刚刚")) {
            return new Date();
        }
        String backup = dateStr;
        dateStr = prepareDateStr(dateStr);
        // xx分钟前
        boolean match = false;
        long time = 0;
        if (dateStr.endsWith("前")) {
            dateStr = dateStr.replace("前", "").replace(" ", "").trim();
            dateStr = dateStr.replace("半小时", "30分钟");
            List<Pair<String, Long>> timeList = new LinkedList<>();
            timeList.add(new Pair<>("秒", 1000L));
            timeList.add(new Pair<>("分钟", 1000L * 60));
            timeList.add(new Pair<>("小时", timeUnitHour));
            timeList.add(new Pair<>("天", timeUnitDay));
            timeList.add(new Pair<>("个月", timeUnitMonth));
            timeList.add(new Pair<>("月", timeUnitMonth));
            timeList.add(new Pair<>("年", timeUnitYear));
            timeList.add(new Pair<>("周", timeUnitWeek));
            timeList.add(new Pair<>("星期", timeUnitWeek));
            time = System.currentTimeMillis();
            for (Pair<String, Long> pair : timeList) {
                if (dateStr.contains(pair.getKey())) {
                    Pattern pattern = Pattern.compile("(\\d+)" + pair.getKey());
                    String formatDateStr = TextUtil.extractFirstWord(dateStr, pattern);
                    if (StringUtil.isNullOrEmpty(formatDateStr)) {
                        continue;
                    }
                    long value = Long.valueOf(formatDateStr);
                    time -= value * pair.getValue();
                    dateStr = dateStr.replaceAll("(\\d+)" + pair.getKey(), "");
                    match = true;
                }
            }
        }
        if (match) {
            return new Date(time);
        }
        List<Pair<String, Integer>> dayList = new LinkedList<>();
        dayList.add(new Pair<>("前天", -2));
        dayList.add(new Pair<>("昨天", -1));
        dayList.add(new Pair<>("今天", 0));
        dayList.add(new Pair<>("明天", 1));
        dayList.add(new Pair<>("后天", 2));
        for (Pair<String, Integer> kv : dayList) {
            if (dateStr.contains(kv.first)) {
                time = System.currentTimeMillis() + kv.second * timeUnitDay;
                String str = new SimpleDateFormat("yyyy-MM-dd ").format(new Date(time));
                dateStr = dateStr.replace(kv.first, str).replaceAll("\\s+", " ").trim();
                break;
            }
        }
        try {
            return DateUtil.smartParse(dateStr);
        } catch (Exception e) {
            LOG.error("Guess date fail. origin date str:" + backup);
            return null;
        }
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String toMySQLDateStr(Date date) {
        return toDateStr(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Calendar getCalendar(String timeZoneId) {
        return getCalendar(new Date().getTime(), timeZoneId);
    }

    //返回当前时区id的当前日期对象
    public static Calendar getCalendar(long times, String timeZoneId) {
        Date date = new Date(times);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        // 或者可以 Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return calendar2;
    }


    /**
     * 假定时间分为4部分: 年份(2016), 月份(外文), 日(6), 小时分钟秒(13:10)
     * 0. 初始化日期字符串, 干掉-,/这些符号
     * 1. 抽取并删除 月份 - 外文
     * 2. 抽取并删除 小时分钟秒 - \d{1,2}:\d{1,2}:\d{1,2} OR \d{1,2}:\d{1,2} OR 无
     * 3. 抽取并删除 年份 - \d{4}
     * 4. 抽取并删除 日 - \d{1,2}
     * 5. 删除可能剩下的星期X
     * 6. 断言 1234步骤抽取成功 && s.trim().length() == 0, 若为真则返回
     * 7. 还是抽取失败就抛异常
     *
     * @param dateStr   日期字符串
     * @param monthList 月份列表,外文映射英文
     * @param weekList  星期列表,星期X
     * @return
     * @throws ParseException
     */
    public static String commonParse(String dateStr, List<Tuple2<String, String>> monthList, List<String> weekList) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            throw new ParseException("required dateStr and timeZoneId", -1);
        }
        String backup = dateStr;
        String all = "[\\s\\S]*";
        // 0. 初始化日期字符串, 干掉-,./这些符号, 将星期X干掉
        dateStr = dateStr.replace("-", " ").replace(",", " ").replace("/", " ");
        // 1. 抽取并删除 月份 - 外文
        String month = null;
        for (Tuple2<String, String> tuple2 : monthList) {
            if (dateStr.contains(tuple2._1()) && dateStr.indexOf(tuple2._1()) == dateStr.lastIndexOf(tuple2._1())) {
                month = tuple2._2();
                dateStr = dateStr.replace(tuple2._1(), "");
                break;
            }
        }
        // 2. 小时分钟秒
        String hourMin = "000000";
        String p1 = "(\\d{1,2}:\\d{1,2}:\\d{1,2})";
        String p2 = "(\\d{1,2}:\\d{1,2})";
        if (dateStr.matches(all + p1 + all)) {
            hourMin = TextUtil.extractFirstWord(dateStr, Pattern.compile(p1)).replace(":", "");
            dateStr = dateStr.replaceFirst(p1, "");
        } else if (dateStr.matches(all + p2 + all)) {
            hourMin = TextUtil.extractFirstWord(dateStr, Pattern.compile(p2)).replace(":", "") + "00";
            dateStr = dateStr.replaceFirst(p2, "");
        }
        // 3. 年份
        String year = null;
        String pYear = "(\\d{4})";
        if (dateStr.matches(all + pYear + all)) {
            year = TextUtil.extractFirstWord(dateStr, Pattern.compile(pYear));
            dateStr = dateStr.replaceFirst(pYear, "");
        }
        // 4. 日
        String day = null;
        String pDay = "(\\d{1,2})";
        if (dateStr.matches(all + pDay + all)) {
            day = TextUtil.extractFirstWord(dateStr, Pattern.compile(pDay));
            dateStr = dateStr.replaceFirst(pDay, "");
            if (day.length() == 1) {
                day = "0" + day;
            }
        }
        // 5. 删除可能剩下的星期X
        for (String week : weekList) {
            if (dateStr.trim().equals(week)) {
                dateStr = dateStr.replace(week, " ");
                break;
            }
        }
        // 6. 断言全都抽取成功且剩下的是空字符串
        if (month != null && hourMin != null && year != null && day != null && dateStr.trim().length() == 0) {
            return year + month + day + hourMin;
        }
        // 7. 用尽办法都抽取失败,抛异常
        LOG.warn(String.format("parse error, year: %s, month: %s, day: %s, hourMin: %s, other: %s, origin dateStr: %s"
                , year, month, day, hourMin, dateStr.trim(), backup));
        throw new ParseException("common parse error", -1);
    }

    public static Date parse(String text, TimeZone timeZone, String lang, List<String> patterns) throws ParseException {
        DateParser parser;
        switch (lang) {
            case "ar":
                parser = new ArabicDateParser();
                break;
            case "fr":
                parser = new FrenchDateParser();
                break;
            case "en":
                parser = new EnglishDateParser();
                break;
            case "zh":
                parser = new ChineseDateParser();
                break;
            default:
                parser = new ChineseDateParser();
        }
        try {
            return parser.parse(text, timeZone, patterns);
        } catch (ParseException e) {
            try {
                return new EnglishDateParser().parse(text, timeZone, patterns);
            } catch (ParseException ignored) {
            }
            throw e;
        }
    }

    /**
     * 获取当天凌晨时间
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
