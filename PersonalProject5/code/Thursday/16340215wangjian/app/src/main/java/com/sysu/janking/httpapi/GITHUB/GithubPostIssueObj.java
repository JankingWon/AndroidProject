package com.sysu.janking.httpapi.GITHUB;

import com.google.gson.annotations.SerializedName;

public class GithubPostIssueObj {
    @SerializedName("id")
    private int id;

    public int getTitle() {
        return id;
    }
}
