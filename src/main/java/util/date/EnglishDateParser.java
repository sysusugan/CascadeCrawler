package util.date;

import org.apache.commons.lang3.StringUtils;
import util.DateUtil;
import util.Pair;
import util.TextUtil;
import util.Tuple2;

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
public class EnglishDateParser extends DateParser {

    @Override
    protected String initDateStr(String dateStr, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(timeZone);
        if (dateStr.contains("Today") || dateStr.contains("today")) {
            String today = sdf.format(new Date());
            dateStr = dateStr.replace("Today", today).replace("today", today);
        } else if (dateStr.contains("Yesterday") || dateStr.contains("yesterday")) {
            String yesterday = sdf.format(new Date(System.currentTimeMillis() - DateUtil.timeUnitDay));
            dateStr = dateStr.replace("Yesterday", yesterday).replace("yesterday", yesterday);
        }
        boolean match = false;
        long time = 0;
        if (dateStr.endsWith("ago")) {
            dateStr = dateStr.replace("ago", "").trim();
            List<Pair<String, Long>> timeList = new LinkedList<>();
            timeList.add(new Pair<>("seconds", 1000L));
            timeList.add(new Pair<>("second", 1000L));
            timeList.add(new Pair<>("minutes", 1000L * 60));
            timeList.add(new Pair<>("minute", 1000L * 60));
            timeList.add(new Pair<>("hours", DateUtil.timeUnitHour));
            timeList.add(new Pair<>("hour", DateUtil.timeUnitHour));
            timeList.add(new Pair<>("days", DateUtil.timeUnitDay));
            timeList.add(new Pair<>("day", DateUtil.timeUnitDay));
            timeList.add(new Pair<>("months", DateUtil.timeUnitMonth));
            timeList.add(new Pair<>("month", DateUtil.timeUnitMonth));
            timeList.add(new Pair<>("years", DateUtil.timeUnitYear));
            timeList.add(new Pair<>("year", DateUtil.timeUnitYear));
            timeList.add(new Pair<>("weeks", DateUtil.timeUnitWeek));
            timeList.add(new Pair<>("week", DateUtil.timeUnitWeek));
            time = System.currentTimeMillis();
            for (Pair<String, Long> pair : timeList) {
                if (dateStr.contains(pair.getKey())) {
                    Pattern pattern = Pattern.compile("(\\d+)\\s+" + pair.getKey());
                    String formatDateStr = TextUtil.extractFirstWord(dateStr, pattern);
                    if (!StringUtils.isEmpty(formatDateStr)) {
                        long value = Long.valueOf(formatDateStr);
                        time -= value * pair.getValue();
                        dateStr = dateStr.replaceAll("(\\d+)\\s+" + pair.getKey(), "");
                        match = true;
                    }
                }
            }
        }
        if (match) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(timeZone);
            return simpleDateFormat.format(new Date(time));
        }
        return dateStr.replace(",", " ").replaceAll("\\/", "-").replace("@", "").replaceAll("\\s+", " ");
    }

    @Override
    protected String getLang() {
        return "en";
    }

    @Override
    protected Tuple2<String, Boolean> replacePm(String dateStr) {
        if (dateStr.contains("AM")) {
            return new Tuple2<>(dateStr.replace("AM", " ").trim(), false);
        } else if (dateStr.contains("PM")) {
            return new Tuple2<>(dateStr.replace("PM", " ").trim(), true);
        }
        return new Tuple2<>(dateStr, false);
    }
}
