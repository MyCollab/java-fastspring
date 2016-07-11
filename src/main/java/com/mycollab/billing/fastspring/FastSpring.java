package com.mycollab.billing.fastspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mycollab.billing.fastspring.domain.Subscription;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author MyCollab Ltd
 * @since 5.3.5
 */
public class FastSpring {

    private String company, username, password;

    public FastSpring(String company, String username, String password) {
        this.company = company;
        this.username = username;
        this.password = password;
    }

    private Result execute(HttpRequestBase request) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

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

    private <T extends HttpMessage> T insertAuthHeader(T message) {
        String auth = String.format("%s:%s", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        message.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        return message;
    }

    private static <T> T deserialize(String xmlValue, Class<T> cls) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlValue, cls);
    }

    /**
     * @param subscriptionId
     * @return
     * @throws IOException
     */
    public Subscription getSubscription(String subscriptionId) throws IOException {
        HttpGet request = get(String.format("https://api.fastspring.com/company/%s/subscription/%s", company, subscriptionId));
        Result result = execute(request);
        return deserialize(result.body, Subscription.class);
    }

    /**
     * @param subscriptionId
     */
    public Subscription cancelSubscription(String subscriptionId) throws IOException {
        HttpDelete request = delete(String.format("https://api.fastspring.com/company/%s/subscription/%s", company, subscriptionId));
        Result result = execute(request);
        return deserialize(result.body, Subscription.class);
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

    public static void main(String[] args) throws IOException {
        FastSpring fastSpring = new FastSpring("mycollab", "linhduong@esofthead.com", "24pIlObiL14A");
        Subscription subscription = fastSpring.getSubscription("MYC160711-4943-51159S");
        System.out.println("Sub: " + subscription.getStatus() + "---" + subscription.getNextPeriodDate());

        fastSpring.renewSubscription("MYC160711-4943-51159S");
    }
}
