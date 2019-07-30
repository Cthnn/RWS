package com.example.rws;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class AlbumView extends AppCompatImageView {
    protected String albumName;
    protected boolean selected = false;

    public AlbumView(Context context,String name) {
        super(context);
        albumName = name;
    }

    public AlbumView(Context context, AttributeSet attrs,String name) {
        super(context, attrs);
        albumName = name;
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr,String name) {
        super(context, attrs, defStyleAttr);
        albumName = name;
    }
    public void setSelect(Boolean selected){
        this.selected = selected;
    }
    public void setAlbumName(String name){
        albumName = name;
    }
    public String getAlbumName(){
        return albumName;
    }
    public Boolean getSelect(){
        return selected;
    }
}
