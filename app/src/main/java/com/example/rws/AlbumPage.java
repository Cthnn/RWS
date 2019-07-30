package com.example.rws;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class AlbumPage extends Activity {
    ArrayList<Album> albumList;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        albumList= (ArrayList<Album>)intent.getSerializableExtra("ALBUMS");
        ImageView imageView = new ImageView(this);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.container);
        imageView.setImageURI(Uri.parse(findAlbum().getImage()));
        layout.addView(imageView);
    }
    private Album findAlbum(){
        for (Album album:albumList){
            if(album.getName().equals(name)){
                return album;
            }
        }
        return null;
    }
}
