package com.mycollab.billing.fastspring.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

/**
 * @author MyCollab Ltd
 * @since 1.0.0
 */
@JsonDeserialize(using = SubscriptionDeserializer.class)
public class Subscription {
    private String status;

    private Date statusChanged;

    private Boolean cancelable;

    private String reference;

    private Boolean test;

    private String referrer;

    private Date nextPeriodDate;

    private Customer customer;

    private String customerUrl;

    private String productName;

    private int quantity;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(Date statusChanged) {
        this.statusChanged = statusChanged;
    }

    public Boolean getCancelable() {
        return cancelable;
    }

    public void setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Date getNextPeriodDate() {
        return nextPeriodDate;
    }

    public void setNextPeriodDate(Date nextPeriodDate) {
        this.nextPeriodDate = nextPeriodDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCustomerUrl() {
        return customerUrl;
    }

    public void setCustomerUrl(String customerUrl) {
        this.customerUrl = customerUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
