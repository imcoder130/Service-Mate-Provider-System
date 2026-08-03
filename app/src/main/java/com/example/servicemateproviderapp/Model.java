package com.example.servicemateproviderapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class Model {

    Model()
    {

    }

    public Model(String providerName, String providerPhoneNo, String address,String providerId,String order) {
        ProviderName = providerName;
        ProviderPhoneNo = providerPhoneNo;
        Address = address;
        ProviderId=providerId;
        TotalOrder=order;


    }


    public String getProviderName() {
        return ProviderName;
    }

    public void setProviderName(String providerName) {
        ProviderName = providerName;
    }

    public String getProviderPhoneNo() {
        return ProviderPhoneNo;
    }

    public void setProviderPhoneNo(String providerPhoneNo) {
        ProviderPhoneNo = providerPhoneNo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getProviderId() {
        return ProviderId;
    }
    public void setProviderId(String providerId)
    {
        ProviderId=providerId;
    }
    public String getTotalOrder() {
        return TotalOrder;
    }
    public void setTotalOrder(String order) {
        TotalOrder =order;
    }



    String ProviderName,ProviderPhoneNo,Address,ProviderId,TotalOrder;



}
