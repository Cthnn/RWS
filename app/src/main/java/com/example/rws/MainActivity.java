package com.example.rws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    Button menuButton;
    Button aButton;
    Button rButton;
    Button tButton;
    ArrayList<Album> albumList = new ArrayList<>();

    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);
        menuButton = (Button)findViewById(R.id.button);
        aButton = (Button)findViewById(R.id.add);
        rButton = (Button)findViewById(R.id.random);
        tButton = (Button)findViewById(R.id.trash);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumCreation();
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClick();
            }
        });
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void menuClick(){
        if(aButton.getVisibility() == View.VISIBLE){
            aButton.setVisibility(View.INVISIBLE);
            tButton.setVisibility(View.INVISIBLE);
            rButton.setVisibility(View.INVISIBLE);
        }else{
            aButton.setVisibility(View.VISIBLE);
            tButton.setVisibility(View.VISIBLE);
            rButton.setVisibility(View.VISIBLE);
        }


    }
    private void albumCreation(){
        final LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
        final View infView =inflater.inflate(R.layout.activity_album_page,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Set An Album Name")
                .setView(inflater.inflate(R.layout.album_create,null));
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(MainActivity.this, AlbumPage.class);
                MainActivity.this.startActivity(myIntent);
                openGallery();
                Album last = albumList.get(albumList.size()-1);
                EditText nameField = infView.findViewById(R.id.nameField);
                last.setName(nameField.getText().toString());
                albumCreated();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("*/*");
        startActivityForResult(gallery,PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ArrayList<Uri> images = new ArrayList<>();
            Uri imageUri = data.getData();
            images.add(imageUri);
        }
    }
    private void albumCreated(){
        for(Album album:albumList){
            ImageView imageView = new ImageView(MainActivity.this);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.container);
            imageView.setImageURI(album.getImage());
            layout.addView(imageView);
        }
    }
}
