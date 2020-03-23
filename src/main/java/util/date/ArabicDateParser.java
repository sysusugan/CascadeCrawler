package util.date;

import org.apache.commons.lang3.StringUtils;
import util.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @author zebin
 * @since 2017-01-05.
 */
public class ArabicDateParser extends DateParser {

    @Override
    protected String initDateStr(String dateStr, TimeZone timeZone) {
        String backup = dateStr;
        boolean match = false;
        // xx分钟前
        if (dateStr.contains("مضت") || dateStr.contains("منذ")) {
            dateStr = dateStr.replace("مضت", "").replace("منذ", "").replace(" ", "").trim();
            List<Pair<String, Long>> timeList = new LinkedList<>();
            timeList.add(new Pair<>("دقيقة", 1000L * 60));  //分钟
            timeList.add(new Pair<>("ساعة", DateUtil.timeUnitHour)); //小时
            timeList.add(new Pair<>("ساعات", DateUtil.timeUnitHour)); //小时
            timeList.add(new Pair<>("أشهر", DateUtil.timeUnitMonth));//月
            timeList.add(new Pair<>("سنوات", DateUtil.timeUnitYear));//年
            long time = System.currentTimeMillis();
            for (Pair<String, Long> pair : timeList) {
                if (dateStr.contains(pair.getKey())) {
                    Pattern pattern = Pattern.compile("(\\d+)" + pair.getKey());
                    String formatDateStr = TextUtil.extractFirstWord(dateStr, pattern);
                    if (StringUtils.isNumeric(formatDateStr)) {
                        time -= Long.valueOf(formatDateStr) * pair.getValue();
                        dateStr = dateStr.replaceAll("(\\d+)" + pair.getKey(), "");
                        match = true;
                    }
                }
            }
            if (match) {
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.RHINO_STD_DATE_FORMAT);
                sdf.setTimeZone(timeZone);
                return sdf.format(new Date(time));
            }
        }
        dateStr = backup;
        // 将"今天","昨天"替换成数字时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(timeZone);
        if (dateStr.contains("يوم أمس")) {      //昨天
            Date yesterday = new Date(System.currentTimeMillis() - DateUtil.timeUnitDay);
            dateStr = dateStr.replace("يوم أمس", sdf.format(yesterday));
        } else if (dateStr.contains("اليوم")) {   //今天
            dateStr = dateStr.replace("اليوم", sdf.format(new Date()));
        }
        dateStr = dateStr.replace(",", " ").replaceAll("\\/", "-").replaceAll("\\s+", " ");
        if (dateStr.matches(".*\\d{4}-\\d{1,2}-\\d{1,2} - \\d{1,2}:\\d{1,2}.*")) {
            dateStr = dateStr.replace(" - ", " ");
        }
        return dateStr;
    }

    @Override
    protected Tuple2<String, Boolean> replacePm(String dateStr) {
        if (dateStr.contains("م") && !dateStr.contains("ماي")) {
            // PM
            return new Tuple2<>(dateStr.replace("م", ""), true);
        }
        return new Tuple2<>(dateStr, false);
    }

    @Override
    protected List<Tuple2<String, String>> monthList(String lang) {
        List<Tuple2<String, String>> ret = super.monthList(lang);
        ret.add(new Tuple2<>("جانفي", "01"));
        ret.add(new Tuple2<>("فيفري", "02"));
        ret.add(new Tuple2<>("ابريل", "04"));
        ret.add(new Tuple2<>("أفريل", "04"));
        ret.add(new Tuple2<>("جوان", "06"));
        ret.add(new Tuple2<>("جويلية", "07"));
        ret.add(new Tuple2<>("أوت", "08"));
        ret.add(new Tuple2<>("سبتمبر", "09"));
        ret.add(new Tuple2<>("كانون", "12"));
        ret.add(new Tuple2<>("ديسمبر", "12"));
        ret.add(new Tuple2<>("مايو", "05"));
        ret.add(new Tuple2<>("ماي ", "05"));
        ret.add(new Tuple2<>("نوفمبر ", "11"));
        return ret;
    }

    @Override
    protected List<String> weekList(String lang) {
        List<String> ret = super.weekList(lang);
        ret.add("الإثنين");
        ret.add("الاثنين");
        ret.add("الثلاثاء");
        ret.add("الأربعاء");
        ret.add("الخميس");
        ret.add("الخميس");
        ret.add("الجمعة");
        ret.add("السبت");
        ret.add("الأحد");
        return ret;
    }

    @Override
    protected String getLang() {
        return "ar";
    }
}
