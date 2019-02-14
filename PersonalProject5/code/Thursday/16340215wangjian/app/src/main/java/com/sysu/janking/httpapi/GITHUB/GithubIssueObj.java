package com.sysu.janking.httpapi.GITHUB;

import com.google.gson.annotations.SerializedName;

public class GithubIssueObj {
    @SerializedName("title")
    private String title;
    @SerializedName("state")
    private String state;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("body")
    private String body;
    public String getTitle() {
        return title;
    }
    public String getBody() {
        return body;
    }
    public String getCreated_at() {
        return created_at;
    }
    public String getState() {
        return state;
    }
}
