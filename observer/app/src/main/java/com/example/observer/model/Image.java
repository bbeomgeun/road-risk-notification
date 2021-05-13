package com.example.observer.model;

import java.util.Date;

public class Image {
    int[] image;
    int width;
    int height;
    Date date;

    public Image(int[] image, int width, int height, Date date) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.date = date;
    }

    public int[] getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Date getDate() {
        return date;
    }

    public void setImage(int[] image) {
        this.image = image;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
