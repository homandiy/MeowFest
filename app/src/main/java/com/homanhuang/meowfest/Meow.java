package com.homanhuang.meowfest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Homan on 3/5/2018.
 */

public class Meow {
    /*
    title: "Space Keybaord Cat",
timestamp: "2018-03-06T06:14:23Z",
image_url: "https://triplebyte-cats.s3.amazonaws.com/space.jpg",
description: "In space, no one can hear you purr."
     */
    @SerializedName("title")
    private String title;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("description")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
