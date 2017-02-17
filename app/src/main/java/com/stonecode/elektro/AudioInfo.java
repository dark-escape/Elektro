package com.stonecode.elektro;

/**
 * Created by Sushant on 18-02-2017.
 */

public class AudioInfo {
    String title;
    String uri;

    public AudioInfo(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }
}
