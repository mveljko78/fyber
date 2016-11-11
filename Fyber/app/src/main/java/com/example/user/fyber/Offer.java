package com.example.user.fyber;


public class Offer  {

    private String title;
    private String payout;
    private String teaser;
    private String hires;


    public Offer(String title, String teaser, String payout, String hires) {

        this.title = title;
        this.teaser = teaser;
        this.payout = payout;
        this.hires = hires;
    }

    public String getTitle() {
        return title;
    }

    public String getTeaser() {
        return teaser;
    }

    public String getPayout() {
        return payout;
    }


    public String getHires() {
        return hires;
    }


    @Override
    public String toString() {
        return title;
    }

}
