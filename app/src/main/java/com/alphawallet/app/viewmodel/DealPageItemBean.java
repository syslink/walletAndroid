package com.alphawallet.app.viewmodel;


public class DealPageItemBean {
    private String mName;
    private String mAddress;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    @Override
    public String toString() {
        return "DealPageItemBean{" +
                "mName='" + mName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                '}';
    }


}
