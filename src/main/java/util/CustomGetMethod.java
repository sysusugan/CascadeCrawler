package util;

/**
 * Created with IntelliJ IDEA.
 *
 * @author sugan
 * @since 2014/8/27
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


public class CustomGetMethod extends org.apache.commons.httpclient.methods.GetMethod {

    public CustomGetMethod(String uri) {
        super(uri);
    }

    /**
     * Get response as string whether response is GZipped or not
     *
     * @return
     * @throws java.io.IOException
     */
    @Override
    public String getResponseBodyAsString() {
        GZIPInputStream gzin;
        InputStream is = null;
        try {
            if (getResponseBody() != null || getResponseStream() != null) {
                if (getResponseHeader("Content-Encoding") != null
                        && getResponseHeader("Content-Encoding").getValue().toLowerCase().contains("gzip")) {
                    //For GZip response
                      is = getResponseBodyAsStream();
                    gzin = new GZIPInputStream(is);

                    InputStreamReader isr = new InputStreamReader(gzin, getResponseCharSet());
                    java.io.BufferedReader br = new java.io.BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String tempbf;
                    while ((tempbf = br.readLine()) != null) {
                        sb.append(tempbf);
                        sb.append("\r\n");
                    }
                    isr.close();
                    gzin.close();
                    return sb.toString();
                } else {
                    //For deflate response
                    return super.getResponseBodyAsString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
