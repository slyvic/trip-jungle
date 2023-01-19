package com.usa.tripjungle.carousel;

public class The_Slide_Items_Model_Class {

    private String featured_image;
    private String the_caption_Title;

    public The_Slide_Items_Model_Class(String hero, String title) {
        this.featured_image = hero;
        this.the_caption_Title = title;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public String getThe_caption_Title() {
        return the_caption_Title;
    }

    public void setFeatured_image(String featured_image) {
        this.featured_image = featured_image;
    }

    public void setThe_caption_Title(String the_caption_Title) {
        this.the_caption_Title = the_caption_Title;
    }
}
