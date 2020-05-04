package com.example.mymovie.Modal;

public class Reviews {

    private String reviewAuthor_name;
    private String reviewDescription;
    private String url;

    public Reviews(String reviewAuthor_name, String reviewDescription, String url) {
        this.reviewAuthor_name = reviewAuthor_name;
        this.reviewDescription = reviewDescription;
        this.url = url;
    }

    public String getReviewAuthor_name() {
        return reviewAuthor_name;
    }

    public void setReviewAuthor_name(String reviewAuthor_name) {
        this.reviewAuthor_name = reviewAuthor_name;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public String getReviewId() {
        return url;
    }

    public void setReviewId(String url) {
        this.url = url;
    }
}
