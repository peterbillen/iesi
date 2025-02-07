package io.metadew.iesi.connection.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Object used to manage the http response resulting from the http connection object.
 *
 */
public class HttpResponse {

    private final CloseableHttpResponse response;
    private final StatusLine statusLine;
    private final HttpEntity entity;
    private final String entityString;

    private final static Logger LOGGER = LogManager.getLogger();


    public HttpResponse(CloseableHttpResponse response) throws IOException {
        this.response = response;
        this.statusLine = response.getStatusLine();
        this.entity = response.getEntity();
        if (this.entity != null) {
            this.entityString = EntityUtils.toString(response.getEntity());
        } else {
            this.entityString = null;
        }
        EntityUtils.consume(response.getEntity());
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public Optional<String> getEntityString() {
        return Optional.ofNullable(entityString);
    }

    public List<Header> getHeaders() {
        return Arrays.asList(response.getAllHeaders());
    }

}