package com.sysu.janking.httpapi.BILIBILI;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PreviewObj {
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private data idata;
    public static class data {
        @SerializedName("img_x_len")
        private int img_x_len;
        @SerializedName("img_y_len")
        private int img_y_len;
        @SerializedName("img_x_size")
        private int img_x_size;
        @SerializedName("img_y_size")
        private int img_y_size;
        @SerializedName("image")
        private ArrayList<String> image;
        @SerializedName("index")
        private ArrayList<Integer> index;

        public ArrayList<String> getImage() {
            return image;
        }

        public int getImg_x_len() {
            return img_x_len;
        }

        public int getImg_x_size() {
            return img_x_size;
        }

        public int getImg_y_len() {
            return img_y_len;
        }

        public int getImg_y_size() {
            return img_y_size;
        }

        public ArrayList<Integer> getIndex() {
            return index;
        }
    }

    public data getIdata() {
        return idata;
    }

    public int getCode() {
        return code;
    }
}

