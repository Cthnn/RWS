package com.example.rws;

import android.net.Uri;

import java.util.ArrayList;

public class Album {
    protected String name;
    protected Uri image;
    protected ArrayList<Uri> images;

    public Album(ArrayList<Uri> images){
        this.images = images;
        image = this.images.get(0);
    }
    public void setName(String name){
        this.name = name;
    }
    public void setImage(Uri image){
        this.image = image;
    }
    public String getName(){
        return name;
    }
    public Uri getImage(){
        return image;
    }
    public ArrayList<Uri> getImages(){
        return images;
    }
}
