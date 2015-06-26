package util;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jsoup.nodes.Document;

/**
 * Created with IntelliJ IDEA.
 *
 * @author sugan
 * @since 2014/8/11
 */
public class HttpResult {

    private HttpMethodBase method;
    private String responseString;
    private Document document;

    public HttpResult(HttpMethodBase method, String responseString, Document document) {
        this.method = method;
        this.responseString = responseString;
        this.document = document;
    }

    public HttpMethodBase getMethod() {
        return method;
    }

    public void setMethod(HttpMethodBase method) {
        this.method = method;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
