package util.date;

import org.apache.commons.lang3.StringUtils;
import util.DateUtil;
import util.Pair;
import util.TextUtil;

import java.text.ParseException;
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
public class ChineseDateParser extends DateParser {

    public Date parse(String dateStr) throws ParseException {
        return parse(dateStr, TimeZone.getTimeZone("GMT+8"), null);
    }

    @Override
    protected String initDateStr(String dateStr, TimeZone timeZone) {
        dateStr = DateUtil.prepareDateStr(dateStr);
        if (dateStr.trim().equals("刚刚")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(timeZone);
            return sdf.format(new Date());
        }
        // xx分钟前
        boolean match = false;
        long time = 0;
        if (dateStr.endsWith("前")) {
            dateStr = dateStr.replace("前", "").replace(" ", "").trim();
            dateStr = dateStr.replace("半小时", "30分钟");
            dateStr = dateStr.replace("個月", "个月").replace("分鐘", "分钟").replace("小時", "小时");
            List<Pair<String, Long>> timeList = new LinkedList<>();
            timeList.add(new Pair<>("秒", 1000L));
            timeList.add(new Pair<>("分钟", 1000L * 60));
            timeList.add(new Pair<>("小时", DateUtil.timeUnitHour));
            timeList.add(new Pair<>("天", DateUtil.timeUnitDay));
            timeList.add(new Pair<>("个月", DateUtil.timeUnitMonth));
            timeList.add(new Pair<>("月", DateUtil.timeUnitMonth));
            timeList.add(new Pair<>("年", DateUtil.timeUnitYear));
            timeList.add(new Pair<>("周", DateUtil.timeUnitWeek));
            timeList.add(new Pair<>("星期", DateUtil.timeUnitWeek));
            time = System.currentTimeMillis();
            for (Pair<String, Long> pair : timeList) {
                if (dateStr.contains(pair.getKey())) {
                    Pattern pattern = Pattern.compile("(\\d+)" + pair.getKey());
                    String formatDateStr = TextUtil.extractFirstWord(dateStr, pattern);
                    if (!StringUtils.isEmpty(formatDateStr)) {
                        long value = Long.valueOf(formatDateStr);
                        time -= value * pair.getValue();
                        dateStr = dateStr.replaceAll("(\\d+)" + pair.getKey(), "");
                        match = true;
                    }
                }
            }
        }
        if (match) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(timeZone);
            return sdf.format(new Date(time));
        }
        List<Pair<String, Integer>> dayList = new LinkedList<>();
        dayList.add(new Pair<>("前天", -2));
        dayList.add(new Pair<>("昨天", -1));
        dayList.add(new Pair<>("今天", 0));
        dayList.add(new Pair<>("明天", 1));
        dayList.add(new Pair<>("后天", 2));
        for (Pair<String, Integer> kv : dayList) {
            if (dateStr.contains(kv.first)) {
                time = System.currentTimeMillis() + kv.second * DateUtil.timeUnitDay;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
                sdf.setTimeZone(timeZone);
                String str = sdf.format(new Date(time));
                dateStr = dateStr.replace(kv.first, str).replaceAll("\\s+", " ").trim();
                break;
            }
        }

        dateStr = dateStr.replace(",", " ").replaceAll("\\/", "-").replaceAll("\\s+", " ");
        dateStr = dateStr.replace("年", "-").replace("月", "-").replace("日", " ")
                .replace("时", ":").replace("分", ":").replace("秒", " ").replaceAll("\\s+", " ").trim();
        return dateStr;
    }

    @Override
    protected String getLang() {
        return "zh";
    }
}
