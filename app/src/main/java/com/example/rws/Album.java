package com.example.rws;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    protected String name;
    protected String uriString;
    protected ArrayList<String> images = new ArrayList<>();

    public Album(String name){
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setImage(String image){
        this.uriString = image;
    }

    public String getName(){
        return name;
    }
    public String getImage(){
        return uriString;
    }
    public ArrayList<String> getImages(){
        return images;
    }


}
