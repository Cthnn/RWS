package com.example.rws;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends Activity {
    GridView grid;
    Button menuButton;
    Button aButton;
    Button rButton;
    Button tButton;
    ArrayList<String> albumList = new ArrayList<>();
    HashMap<String,Album> albumMap = new HashMap<>();
    private static final int PICK_IMAGE = 100;
    private static final int ALBUM_BACK_OUT = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ran");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);
        grid = (GridView) findViewById(R.id.albumDisplay);
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
                Random randNum = new Random();
                int randomAlb = randNum.nextInt(albumList.size());
                System.out.println(randomAlb);
                Album randAlb = albumMap.get(albumList.get(randomAlb));
                Uri randIm = Uri.parse(randAlb.getImages().get(randNum.nextInt(randAlb.getImages().size())));
                Bitmap newBm = loadBitmap(randIm);
                reloadWallpaper(newBm);

            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlbums();
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (viewSelected()) {
                    if (((AlbumView) view).getSelect()) {
                        ((AlbumView) view).setSelect(false);
                        findAlbum(((AlbumView) view).getAlbumName()).setSelect(false);
                        updateTrashButton();
                    } else {
                        ((AlbumView) view).setSelect(true);
                        findAlbum(((AlbumView) view).getAlbumName()).setSelect(true);
                        updateTrashButton();
                    }
                } else {
                    Intent myIntent = new Intent(MainActivity.this, AlbumPage.class);
                    myIntent.putExtra("ALBUM", albumMap.get(((AlbumView) view).getAlbumName()));
                    myIntent.putExtra("NAME", ((AlbumView) view).getAlbumName());
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((AlbumView) view).setSelect(true);
                findAlbum(((AlbumView) view).getAlbumName()).setSelect(true);
                updateTrashButton();
                return true;
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
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
        }else if(resultCode == RESULT_OK && requestCode == ALBUM_BACK_OUT){
                Album updatedAlbum = (Album)data.getSerializableExtra("ALBUM");
                System.out.println(updatedAlbum.getImages().size());
                String name = data.getStringExtra("NAME");
                albumMap.remove(name);
                albumMap.put(name,updatedAlbum);
        }
    }
    private void albumCreated() {
        for (String album : albumList) {
            if (!albumMap.get(album).getVisible()) {
                albumMap.get(album).setVisible(true);
            }
        }
        reloadGrid();
        Intent myIntent = new Intent(MainActivity.this, AlbumPage.class);
        myIntent.putExtra("ALBUM", albumMap.get(albumList.get(albumList.size()-1)));
        myIntent.putExtra("NAME",albumList.get(albumList.size()-1));
        MainActivity.this.startActivityForResult(myIntent,ALBUM_BACK_OUT);
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
                    Toast.makeText(MainActivity.this,
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
            Toast.makeText(MainActivity.this, "bm == null", Toast.LENGTH_LONG).show();
        }
    }
    private Bitmap centerCropWallpaper(Bitmap wallpaper, int desiredWidth, int desiredHeight){
        float scale = (float) desiredHeight / wallpaper.getHeight();
        int scaledWidth = (int) (scale * wallpaper.getWidth());
        Bitmap scaledWallpaper = Bitmap.createScaledBitmap(wallpaper, scaledWidth, desiredHeight, false);
        Bitmap croppedWallpaper = Bitmap.createBitmap(scaledWallpaper,(scaledWallpaper.getWidth()-desiredWidth)/2, 0,desiredWidth, desiredHeight);
        return croppedWallpaper;
    }
    private Bitmap centerCrop(Bitmap bm, int desiredWidth, int desiredHeight){
        float scale = (float) desiredHeight / bm.getHeight();
        int scaledWidth = (int) (scale * bm.getWidth());
        Bitmap scaledWallpaper = Bitmap.createScaledBitmap(bm, scaledWidth, desiredHeight, false);
        Bitmap croppedWallpaper = Bitmap.createBitmap(scaledWallpaper, (scaledWallpaper.getWidth()-desiredWidth)/2, 0, desiredWidth, desiredHeight);
        return croppedWallpaper;
    }
    private void reloadGrid(){
        grid.setAdapter(new ImageAdapter(this,albumMap,albumList));
    }
    public class ImageAdapter extends BaseAdapter{
        private Context mContext;
        protected HashMap<String,Album> aMap;
        ArrayList<String> aList;
        public ImageAdapter(Context mainActivity,HashMap<String,Album> aMap, ArrayList<String> aList){
            this.mContext = mainActivity;
            this.aMap = aMap;
            this.aList = aList;
        }

        @Override
        public int getCount() {
            return aList.size();
        }

        @Override
        public Object getItem(int i) {
            return aMap.get(aList.get(i));
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            AlbumView imageView = new AlbumView(mContext,aList.get(i));
            imageView.setImageURI(Uri.parse(aMap.get(aList.get(i)).getImage()));
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
