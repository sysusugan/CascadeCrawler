package util.date;

import java.util.TimeZone;

/**
 * @author zebin
 * @since 2017-01-05.
 */
public class FrenchDateParser extends DateParser {

    @Override
    protected String initDateStr(String dateStr, TimeZone timeZone) {
        return dateStr.replace(",", " ").replaceAll("\\/", "-").replaceAll("\\s+", " ");
    }

    @Override
    protected String getLang() {
        return "fr";
    }
}
