package com.example.rws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class AlbumPage extends Activity {
    Album album;
    String name;
    GridView grid;
    Button menuButton;
    Button aButton;
    Button rButton;
    Button tButton;
    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_page);
        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        album = (Album) intent.getSerializableExtra("ALBUM");
        grid = (GridView) findViewById(R.id.albumDisplay);
        menuButton = (Button)findViewById(R.id.button);
        aButton = (Button)findViewById(R.id.add);
        rButton = (Button)findViewById(R.id.random);
        tButton = (Button)findViewById(R.id.trash);
        tButton.setEnabled(false);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
                Random randNum = new Random();
                int randomAlb = randNum.nextInt(album.getImages().size());
                System.out.println(randomAlb);
                Uri randIm = Uri.parse(album.getImages().get(randomAlb));
                Bitmap newBm = loadBitmap(randIm);
                reloadWallpaper(newBm);

            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImages();
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
            album.getImages().add(imageUri.toString());
            reloadGrid();
        }
    }
    private void deleteAlbums(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pressing delete will delete all selected images. Do you want to delete these images?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeImages();
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
    private void reloadGrid(){
        grid.setAdapter(new PicAdapter(this,album.getImages()));
    }
    private void removeImagesg(){
        Iterator listerator = album.getImages().iterator();
        while(listerator.hasNext()){
            String album = (String)listerator.next();
            if(albumMap.get(album).getSelect()){
                albumMap.remove(album);
                listerator.remove();
            }
        }
        reloadGrid();
        updateTrashButton();
    }
    public class PicAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<String> images;
        public PicAdapter(Context mainActivity,ArrayList<String> images){
            this.mContext = mainActivity;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int i) {
            return images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageURI(Uri.parse(images.get(i)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            imageView.setLayoutParams(new GridView.LayoutParams(width/5, height/5));
            return imageView;
        }
    }
}
