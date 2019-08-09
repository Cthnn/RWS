package com.example.rws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
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
        reloadGrid();
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
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
    private void deleteImages(){
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
    private void removeImages(){
        Iterator listerator = album.getImages().iterator();
        while(listerator.hasNext()){
            String album = (String)listerator.next();
            for(int i = 0;i<grid.getChildCount();i++){
                if(((AlbumView)grid.getChildAt(i)).getSelect()){
                    listerator.remove();
                }
            }
        }
        reloadGrid();
        updateTrashButton();
    }
    private void updateTrashButton(){
        if(viewSelected()){
            tButton.setEnabled(true);

        }else{
            tButton.setEnabled(false);
        }
    }
    private Boolean viewSelected(){
        for(int i = 0;i<grid.getChildCount();i++){
            if(((AlbumView)grid.getChildAt(i)).getSelect()){
                return true;
            }
        }
        return false;
    }
    private Bitmap loadBitmap(Uri src) {

        Bitmap bm = null;

        try {
            bm = BitmapFactory.decodeStream(
                    getBaseContext().getContentResolver().openInputStream(src));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bm;
    }
    private void reloadWallpaper(Bitmap bm){
        if(bm != null){

            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Bitmap newBm = centerCropWallpaper(bm, displayMetrics.widthPixels, displayMetrics.heightPixels);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(myWallpaperManager.isWallpaperSupported()){
                    try {
                        myWallpaperManager.setBitmap(newBm);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(AlbumPage.this,
                            "isWallpaperSupported() NOT SUPPORTED",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                try {
                    myWallpaperManager.setBitmap(newBm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            Toast.makeText(AlbumPage.this, "bm == null", Toast.LENGTH_LONG).show();
        }
    }
    private Bitmap centerCropWallpaper(Bitmap wallpaper, int desiredWidth, int desiredHeight){
        float scale = (float) desiredHeight / wallpaper.getHeight();
        int scaledWidth = (int) (scale * wallpaper.getWidth());
        Bitmap scaledWallpaper = Bitmap.createScaledBitmap(wallpaper, scaledWidth, desiredHeight, false);
        Bitmap croppedWallpaper = Bitmap.createBitmap(scaledWallpaper,(scaledWallpaper.getWidth()-desiredWidth)/2, 0,desiredWidth, desiredHeight);
        return croppedWallpaper;
    }
    public void onBackPressed() {
        Intent myIntent = new Intent();
        myIntent.putExtra("ALBUM", album);
        myIntent.putExtra("NAME",name);
        setResult(RESULT_OK, myIntent);
        finish();
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
            System.out.println(album.getImages().get(i));
            System.out.println(images.get(i));
            AlbumView imageView = new AlbumView(mContext,album.getImages().get(i));
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
