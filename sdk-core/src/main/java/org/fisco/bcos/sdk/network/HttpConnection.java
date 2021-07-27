package org.fisco.bcos.sdk.network;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.fisco.bcos.sdk.channel.ResponseCallback;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.model.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HttpConnection implements Connection {

    private final String uri;

    public HttpConnection(ConfigOption config) {
        if (!config.getNetworkConfig().getPeers().get(0).startsWith("http://")) {
            this.uri = "http://" + config.getNetworkConfig().getPeers().get(0);
        } else {
            this.uri = config.getNetworkConfig().getPeers().get(0);
        }
    }

    /**
     * close connection
     */
    @Override
    public void close() {
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    /**
     * connect to node
     *
     * @return true if connected
     */
    @Override
    public Boolean connect() {
        return true;
    }

    @Override
    public Boolean reConnect() {
        return true;
    }

    @Override
    public String callMethod(String request) throws IOException {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {

            final HttpPost httppost = new HttpPost(this.uri);
            final InputStreamEntity reqEntity =
                    new InputStreamEntity(
                            new ByteArrayInputStream(request.getBytes()), -1, ContentType.APPLICATION_JSON);
            httppost.setEntity(reqEntity);
//            System.out.println("Executing request: " + request);
            try (final CloseableHttpResponse response = httpclient.execute(httppost)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    @Override
    public void asyncCallMethod(String request, ResponseCallback callback) throws IOException {
        String response = this.callMethod(request);
        Response resp = new Response();
        resp.setContent(response);
        callback.onResponse(resp);
    }
}
