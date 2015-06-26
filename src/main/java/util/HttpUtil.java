package util;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.params.ClientPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author sugan
 * @since 2014/8/5
 */
public class HttpUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int SOCK_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;


    public static final String UA_MOBILE_IOS7 = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A4449d Safari/9537.53";
    public static final String UA_PC_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";


    public static Map<String, String> parseQueryString(String query)
            throws UnsupportedEncodingException {
        return parseQueryString(query, DEFAULT_CHARSET);
    }

    public static Map<String, String> parseQueryString(String query,
                                                       String charset) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<String, String>();
        String[] segs = query.split("\\?");
        String queryPart = query;
        if (segs.length == 2)
            queryPart = segs[1];

        String[] pairs = queryPart.split("&");
        for (String pair : pairs) {
            pair = URLDecoder.decode(pair, charset);
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), charset),
                    URLDecoder.decode(pair.substring(idx + 1), charset));
        }
        return queryPairs;
    }

    public static URI parsrUrl(String uri) throws URIException {
        return parsrUrl(uri, DEFAULT_CHARSET);
    }

    public static URI parsrUrl(String uriStr, String charset)
            throws URIException {
        return new URI(uriStr, true, charset);
    }

    public static HttpResult getMethodResult(String uri) throws Exception {
        return getMethodResult(uri, null);
    }

    public static HttpResult getMethodResult(String uri,
                                             Map<String, String> headers) throws IOException {
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(CONNECT_TIMEOUT);
        client.setTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);

        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);

        GetMethod get = new CustomGetMethod(uri);
        get.setRequestHeader("User-Agent", UA_PC_CHROME);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                get.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }

        int status = client.executeMethod(get);
        String ret = get.getResponseBodyAsString();
        get.abort();
        get.releaseConnection();

        if (ret.trim().equals(""))
            return null;
        if (status != 200) {
            System.err.println("[WARNING] status code\t" + status + "\t url:\t" + uri);
            return null;
        }
        return new HttpResult(get, ret, Jsoup.parse(ret));
    }

    public static HttpResult getPostMethodResult(String uri, Map<String, String> parames) throws Exception {
        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(CONNECT_TIMEOUT);
        client.getParams().setSoTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);

        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setParameter(HttpMethodParams.USER_AGENT, UA_PC_CHROME);

        PostMethod post = new PostMethod(uri);
        if (parames != null) {
            for (Map.Entry<String, String> entry : parames.entrySet()) {
                post.setParameter(entry.getKey(), entry.getValue());
            }
        }

        int status = client.executeMethod(post);

        String ret = post.getResponseBodyAsString();
        post.abort();
        post.releaseConnection();
        if (status != 200) {
            System.err.println("[WARNING] status code\t" + status + "\t url:\t"
                    + uri);
            return null;
        }
        return new HttpResult(post, ret, Jsoup.parse(ret));
    }

    public static String getDocumentString(String uri) throws IOException {
        HttpClient client = new HttpClient();
        client.setConnectionTimeout(CONNECT_TIMEOUT);
        client.setTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(SOCK_TIMEOUT);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);

        GetMethod get = new CustomGetMethod(uri);
        get.setRequestHeader("User-Agent", UA_PC_CHROME);
            get.setFollowRedirects(false);

        String ret = null;
        try {

            int status = client.executeMethod(get);
            ret = get.getResponseBodyAsString();
            get.abort();
            get.releaseConnection();
            if (status != 200) {
                System.err.println("[WARNING] status code\t" + status
                        + "\t url:\t" + uri);
            }
        } catch (ConnectException e) {
            e.printStackTrace();

        }
        return ret;
    }

    public static Document getDocument(String uri) throws IOException {
//        return Jsoup.parse(new URL(uri), SOCK_TIMEOUT);
        return Jsoup.parse(getDocumentString(uri));
    }

}
