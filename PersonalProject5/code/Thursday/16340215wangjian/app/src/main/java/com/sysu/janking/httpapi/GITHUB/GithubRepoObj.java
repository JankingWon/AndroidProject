package com.sysu.janking.httpapi.GITHUB;
import com.google.gson.annotations.SerializedName;
public class GithubRepoObj {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("description")
        private String description;
        @SerializedName("open_issues_count")
        private int open_issues_count;
        @SerializedName("has_issues")
        private boolean has_issues;

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public int getOpen_issues_count() {
            return open_issues_count;
        }

        public String getDescription() {
            return description;
        }

    public boolean isHas_issues() {
        return has_issues;
    }
}
