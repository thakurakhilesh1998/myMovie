package com.example.mymovie.Modal;

public class Trailers {

    private String trailer_id;
    private String trailer_website;
    private String trialer_title;

    public Trailers(String trailer_id, String trailer_website, String trialer_title) {
        this.trailer_id = trailer_id;
        this.trailer_website = trailer_website;
        this.trialer_title = trialer_title;
    }

    public String getTrailer_id() {
        return trailer_id;
    }

    public void setTrailer_id(String trailer_id) {
        this.trailer_id = trailer_id;
    }

    public String getTrailer_website() {
        return trailer_website;
    }

    public void setTrailer_website(String trailer_website) {
        this.trailer_website = trailer_website;
    }

    public String getTrialer_title() {
        return trialer_title;
    }

    public void setTrialer_title(String trialer_title) {
        this.trialer_title = trialer_title;
    }
}


