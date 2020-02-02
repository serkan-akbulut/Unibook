package com.fikretserkan.unibook.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Book {
    public String userId;
    public String bookId;
    public String name;
    public String writer;
    public String details;
    public Double price;
    public String url;
    public Bitmap profile_image;

    public Book(String userId, String bookId, String name, String writer, String details, Double price, String url) {
        this.userId = userId;
        this.bookId = bookId;
        this.name = name;
        this.writer = writer;
        this.details = details;
        this.price = price;
        this.url = url;
    }

    public Book(String userId, String bookId, String name, String writer, String details, Double price, Bitmap profile_image) {
        this.userId = userId;
        this.bookId = bookId;
        this.name = name;
        this.writer = writer;
        this.details = details;
        this.price = price;
        this.profile_image = profile_image;
    }

    public Bitmap getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(Bitmap profile_image) {
        this.profile_image = profile_image;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
