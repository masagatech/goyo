package com.crest.goyo.ModelClasses;

/**
 * Created by brittany on 7/6/17.
 */

public class PaymentModel {
    String id, v_name, v_type, v_mode, v_image, key, fUrl, sUrl, email, phone, firstName, merchantId, productName;

    public PaymentModel(String id, String v_name, String v_type, String v_mode, String v_image, String key, String fUrl, String sUrl, String email, String phone, String firstName, String merchantId, String productName) {
        this.id = id;
        this.v_name = v_name;
        this.v_type = v_type;
        this.v_mode = v_mode;
        this.v_image = v_image;
        this.key = key;
        this.fUrl = fUrl;
        this.sUrl = sUrl;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.merchantId = merchantId;
        this.productName = productName;
    }

    public PaymentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public String getV_type() {
        return v_type;
    }

    public void setV_type(String v_type) {
        this.v_type = v_type;
    }

    public String getV_mode() {
        return v_mode;
    }

    public void setV_mode(String v_mode) {
        this.v_mode = v_mode;
    }

    public String getV_image() {
        return v_image;
    }

    public void setV_image(String v_image) {
        this.v_image = v_image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getfUrl() {
        return fUrl;
    }

    public void setfUrl(String fUrl) {
        this.fUrl = fUrl;
    }

    public String getsUrl() {
        return sUrl;
    }

    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
