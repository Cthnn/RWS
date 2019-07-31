package com.example.rws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends Activity {

    Button menuButton;
    Button aButton;
    Button rButton;
    Button tButton;
    ArrayList<String> albumList = new ArrayList<>();
    HashMap<String,Album> albumMap = new HashMap<>();
    HashMap<String,AlbumView> albumViewMap = new HashMap<>();
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
        tButton.setEnabled(false);
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
                deleteAlbums();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set An Album Name")
                .setView(R.layout.album_create);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Dialog dialogObj = Dialog.class.cast(dialog);
                EditText nameField = (EditText)dialogObj.findViewById(R.id.nameField);
                Album newAlbum = new Album(nameField.getText().toString());
                albumList.add(nameField.getText().toString());
                albumMap.put(nameField.getText().toString(),newAlbum);
                openGallery();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
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
            Uri imageUri = data.getData();
            albumMap.get(albumList.get(albumList.size()-1)).getImages().add(imageUri.toString());
            albumMap.get(albumList.get(albumList.size()-1)).setImage(imageUri.toString());
            albumCreated();
        }
    }
    private void albumCreated(){
        for(String album:albumList){
            AlbumView imageView = new AlbumView(MainActivity.this,album);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.container);
            imageView.setImageURI(Uri.parse(albumMap.get(album).getImage()));
            layout.addView(imageView);
            albumViewMap.put(imageView.getAlbumName(),imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(viewSelected()){
                        if(((AlbumView)view).getSelect()){
                            ((AlbumView)view).setSelect(false);
                            findAlbum(((AlbumView)view).getAlbumName()).setSelect(false);
                            updateTrashButton();
                        }else{
                            ((AlbumView)view).setSelect(true);
                            findAlbum(((AlbumView)view).getAlbumName()).setSelect(true);
                            updateTrashButton();
                        }
                    }else{
                        Intent myIntent = new Intent(MainActivity.this, AlbumPage.class);
                        myIntent.putExtra("ALBUM", albumMap.get(((AlbumView)view).getAlbumName()));
                        myIntent.putExtra("NAME",((AlbumView)view).getAlbumName());
                        MainActivity.this.startActivity(myIntent);
                    }
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AlbumView)view).setSelect(true);
                    findAlbum(((AlbumView)view).getAlbumName()).setSelect(true);
                    updateTrashButton();
                    return true;
                }
            });
        }
        Intent myIntent = new Intent(MainActivity.this, AlbumPage.class);
        myIntent.putExtra("ALBUMS", albumMap.get(albumList.get(albumList.size()-1)));
        myIntent.putExtra("NAME",albumList.get(albumList.size()-1));
        MainActivity.this.startActivity(myIntent);
    }
    private void updateTrashButton(){
        if(viewSelected()){
            tButton.setEnabled(true);

        }else{
            tButton.setEnabled(false);
        }
    }
    private Boolean viewSelected(){
        for (String album:albumList){
            if(albumMap.get(album).getSelect()){
                return true;
            }
        }
        return false;
    }
    private Album findAlbum(String aName){
        for (String album:albumList){
            if(albumMap.get(album).getName().equals(aName)){
                return albumMap.get(album);
            }
        }
        return null;
    }
    private void deleteAlbums(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pressing delete will delete all selected albums. Do you want to delete these albums?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeAlbums();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void removeAlbums(){
        Iterator listerator = albumList.iterator();
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.container);
        for(String albums:albumList){
            if(albumMap.get(albums).getSelect()){
                albumMap.remove(albums);
                layout.removeView(albumViewMap.get(albums));
                albumViewMap.remove(albums);
                //listerator.remove();
            }
        }
    }

}
