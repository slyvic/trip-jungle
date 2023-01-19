package com.usa.tripjungle.model;

import org.json.JSONArray;

public class MainModel {
    private final String title;
    private final String description;
    private final String ranking, uranking;
    private final JSONArray images;
    private final String image;
    private final String id;
    private final int mark;
    private final Boolean like;

    public MainModel(String id, String title, String description, String ranking, JSONArray images, String image, boolean like, int mark, String uranking){
        this.id = id;
        this.title = title;
        this.description = description;
        this.ranking = ranking;
        this.uranking = uranking;
        this.images = images;
        this.image = image;
        this.mark = mark;
        this.like = like;
    }

    public String getUranking() {
        return uranking;
    }

    public String getId() {
        return id;
    }

    public Boolean getLike() {
        return like;
    }

    public String getImage() {
        return image;
    }
    public JSONArray getImages() {
        return images;
    }

    public int getMark() {
        return mark;
    }

    public String getDescription() {
        return description;
    }

    public String getRanking() {
        return ranking;
    }

    public String getTitle() {
        return title;
    }
}
