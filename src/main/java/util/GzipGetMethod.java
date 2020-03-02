package util;

/**
 * 自定义http Method， 支持gzip压缩
 */


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


/**
 *
 */
public class GzipGetMethod extends org.apache.commons.httpclient.methods.GetMethod {
    private static final Logger LOG = Logger.getLogger(GzipGetMethod.class);

    public GzipGetMethod(String uri) {
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
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
