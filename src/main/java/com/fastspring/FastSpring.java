package com.fastspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fastspring.domain.Subscription;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Hai Phuc Nguyen
 * @since 1.0.0
 */
public class FastSpring {
    private static Logger LOG = LoggerFactory.getLogger(FastSpring.class);

    private String company, username, password;

    public FastSpring(String company, String username, String password) {
        this.company = company;
        this.username = username;
        this.password = password;
    }

    private Result execute(HttpRequestBase request) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        rd.lines().forEach(line -> result.append(line));
        return new Result(response.getStatusLine().getStatusCode(), result.toString());
    }

    private HttpGet get(String url) {
        return insertAuthHeader(new HttpGet(url));
    }

    private HttpDelete delete(String url) {
        return insertAuthHeader(new HttpDelete(url));
    }

    private HttpPost post(String url) {
        return insertAuthHeader(new HttpPost(url));
    }

    private HttpPut put(String url) {
        return insertAuthHeader(new HttpPut(url));
    }

    private <T extends HttpMessage> T insertAuthHeader(T message) {
        String auth = String.format("%s:%s", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        message.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        message.setHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
        return message;
    }

    private static <T> T deserialize(String xmlValue, Class<T> cls) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlValue, cls);
    }

    private static String serialize(Object value) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(value);
    }

    /**
     * @param subscriptionId
     * @return
     * @throws IOException
     */
    public Subscription getSubscription(String subscriptionId) throws IOException {
        HttpGet request = get(String.format("https://api.fastspring.com/company/%s/subscription/%s", company, subscriptionId));
        Result result = execute(request);
        if (result.ok()) {
            return deserialize(result.body, Subscription.class);
        } else {
            LOG.error(result.code + " " + result.body);
            throw new OperationException(result.code, result.body);
        }
    }

    public Subscription updateSubscription(Subscription subscription) throws IOException {
        HttpPut request = put(String.format("https://api.fastspring.com/company/%s/subscription/%s", company,
                subscription.getReference()));
        String value = serialize(subscription);
        request.setEntity(new StringEntity(value));
        Result result = execute(request);
        if (result.ok()) {
            return deserialize(result.body, Subscription.class);
        } else {
            LOG.error(result.code + " " + result.body);
            throw new OperationException(result.code, result.body);
        }
    }

    /**
     * @param subscriptionId
     */
    public Subscription cancelSubscription(String subscriptionId) throws IOException {
        HttpDelete request = delete(String.format("https://api.fastspring.com/company/%s/subscription/%s", company, subscriptionId));
        Result result = execute(request);
        if (result.ok()) {
            return deserialize(result.body, Subscription.class);
        } else {
            LOG.error(result.code + " " + result.body);
            throw new OperationException(result.code, result.body);
        }
    }


    public void renewSubscription(String subscriptionId) throws IOException {
        HttpPost request = post(String.format("https://api.fastspring.com/company/%s/subscription/%s/renew", company,
                subscriptionId));
        Result result = execute(request);
        if (result.notFound()) {
            throw new IOException("Subscription " + subscriptionId + " not found");
        }
    }

    private static class Result {
        private int code;
        private String body;

        Result(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public boolean notFound() {
            return code == HttpStatus.SC_NOT_FOUND;
        }

        public boolean ok() {
            return code == HttpStatus.SC_OK;
        }
    }
}
