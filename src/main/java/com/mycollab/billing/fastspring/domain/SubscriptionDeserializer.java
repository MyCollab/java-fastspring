package com.mycollab.billing.fastspring.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author MyCollab Ltd
 * @since 1.0.0
 */
public class SubscriptionDeserializer extends JsonDeserializer<Subscription> {

    private static SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'Z'");

    public Subscription deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            Subscription subscription = new Subscription();
            subscription.setStatus(node.get("status").asText());
            subscription.setStatusChanged(datetimeFormatter.parse(node.get("statusChanged").asText()));
            subscription.setCancelable(node.get("cancelable").asBoolean());
            subscription.setTest(node.get("test").asBoolean());
            subscription.setReference(node.get("reference").asText());
            subscription.setReferrer(node.get("referrer").asText());
            subscription.setNextPeriodDate((node.get("nextPeriodDate") != null) ? dateFormatter.parse(node.get("nextPeriodDate").asText()) : null);
            subscription.setCustomerUrl(node.get("customerUrl").asText());
            subscription.setProductName(node.get("productName").asText());
            subscription.setQuantity(node.get("quantity").asInt());

            Customer customer = new Customer();
            customer.setFirstName(node.get("customer").get("firstName").asText());
            customer.setLastName(node.get("customer").get("lastName").asText());
            customer.setEmail(node.get("customer").get("email").asText());
            customer.setPhoneNumber(node.get("customer").get("phoneNumber").asText());
            subscription.setCustomer(customer);
            return subscription;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
