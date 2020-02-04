package com.example.shopee;

public class SliderModel {
    private String banner;
   private String getbackgroundcolor;



    public SliderModel(String banner, String getbackgroundcolor) {
        this.banner = banner;
        this.getbackgroundcolor = getbackgroundcolor;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }



    public String getGetbackgroundcolor() {
        return getbackgroundcolor;
    }

    public void setGetbackgroundcolor(String getbackgroundcolor) {
        this.getbackgroundcolor = getbackgroundcolor;
    }
}