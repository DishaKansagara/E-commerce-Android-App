package com.example.shopee;

public class AddressesModel {

    private String fullname;
    private String mobileNo;
    private String address;
    private String pincode;
    private Boolean Selected;



    public AddressesModel(String fullname, String address, String pincode, Boolean selected,String mobileNo) {
        this.fullname = fullname;
        this.address = address;
        this.pincode = pincode;
        this.Selected = selected;
        this.mobileNo = mobileNo;
    }

    public AddressesModel(String toString, String toString1, String toString2, boolean b) {
    }

    public Boolean getSelected() {
        return Selected;
    }

    public void setSelected(Boolean selected) {
        Selected = selected;
    }
    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
