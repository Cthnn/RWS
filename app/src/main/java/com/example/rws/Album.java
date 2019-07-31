package com.example.rws;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    protected String name;
    protected String uriString;
    protected ArrayList<String> images = new ArrayList<>();
    protected boolean selected = false;
    protected boolean visible = false;

    public Album(String name){
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setImage(String image){
        this.uriString = image;
    }
    public void setSelect(Boolean selected){
        this.selected = selected;
    }
    public void setVisible(Boolean visible){
        this.visible = visible;
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
    public Boolean getSelect(){
        return selected;
    }
    public Boolean getVisible(){
        return visible;
    }



}
