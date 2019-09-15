package com.example.rws;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends Activity {
    GridView grid;
    Button menuButton;
    ImageView aButton;
    ImageView rButton;
    ImageView tButton;
    ImageView eButton;
    ArrayList<String> albumList = new ArrayList<>();
    HashMap<String,Album> albumMap = new HashMap<>();
    int pixel;
    float [] colorMatrix = {
            1,0,0,0,0,
            0,1,0,0,0,
            1,0,1,0,0,
            0,0,0,1,1};
    final ColorFilter highlight = new ColorMatrixColorFilter(colorMatrix);
    private static final int PICK_IMAGE = 100;
    private static final int ALBUM_BACK_OUT = 101;
    private float[] lastTouchDownXY = new float[2];
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ran");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);
        grid = (GridView) findViewById(R.id.albumDisplay);
        menuButton = (Button)findViewById(R.id.button);
        aButton = (ImageView)findViewById(R.id.add);
        rButton = (ImageView) findViewById(R.id.random);
        tButton = (ImageView)findViewById(R.id.trash);
        eButton = (ImageView)findViewById(R.id.edit);
        tButton.setEnabled(false);
        eButton.setEnabled(false);
        aButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Bitmap button = makeBitmap((VectorDrawable) ((ImageView)view).getDrawable());
                    double ratio = (button.getHeight()/view.getHeight());
                    int x = ((int)(motionEvent.getX()*(ratio)));
                    int y =((int)(motionEvent.getY()*(ratio)));
                    pixel = button.getPixel(x,y);
                }
                return false;
            }
        });
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pixel == Color.TRANSPARENT){
                    System.out.println("ran");
                    return;
                }else {
                    albumCreation();
                }
            }
        });

        menuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    lastTouchDownXY[0] = motionEvent.getX();
                    lastTouchDownXY[1] = motionEvent.getY();
                }
                return false;
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float x = Math.abs(lastTouchDownXY[0] - (menuButton.getHeight() / 2));
                float y = Math.abs(lastTouchDownXY[1] - (menuButton.getWidth() / 2));
                if (Math.sqrt((x * x) + (y * y)) > (menuButton.getHeight() / 2)) {
                    return;
                } else {
                    menuClick();
                }
            }
        });

        rButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Bitmap button = makeBitmap((VectorDrawable)((ImageView)view).getDrawable());
                    double ratio = (button.getHeight()/view.getHeight());
                    int x = ((int)(motionEvent.getX()*(ratio)));
                    int y =((int)(motionEvent.getY()*(ratio)));
                    pixel = button.getPixel(x,y);
                }
                return false;
            }
        });
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pixel == Color.TRANSPARENT){
                    return;
                }else {
                    Random randNum = new Random();
                    int randomAlb = randNum.nextInt(albumList.size());
                    Album randAlb = albumMap.get(albumList.get(randomAlb));
                    Uri randIm = Uri.parse(randAlb.getImages().get(randNum.nextInt(randAlb.getImages().size())));
                    Bitmap newBm = loadBitmap(randIm);
                    reloadWallpaper(newBm);
                }

            }
        });

        tButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Bitmap button = makeBitmap((VectorDrawable) ((ImageView)view).getDrawable());
                    double ratio = (button.getHeight()/view.getHeight());
                    int x = ((int)(motionEvent.getX()*(ratio)));
                    int y =((int)(motionEvent.getY()*(ratio)));
                    pixel = button.getPixel(x,y);
                }
                return false;
            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pixel == Color.TRANSPARENT){
                    return;
                }else {
                    deleteAlbums();
                }
            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pixel == Color.TRANSPARENT){
                    return;
                }else {
                    editAlbumName();
                }
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (viewSelected()) {
                    if (((AlbumView)((CardView) view).getChildAt(0)).getSelect()) {
                        ((AlbumView)((CardView) view).getChildAt(0)).setSelect(false);
                        findAlbum(((AlbumView)((CardView) view).getChildAt(0)).getAlbumName()).setSelect(false);
                        ((AlbumView)((CardView) view).getChildAt(0)).clearColorFilter();
                        updateTrashButton();
                    } else {
                        ((AlbumView)((CardView) view).getChildAt(0)).setSelect(true);
                        findAlbum(((AlbumView)((CardView) view).getChildAt(0)).getAlbumName()).setSelect(true);
                        ((AlbumView)((CardView) view).getChildAt(0)).setColorFilter(highlight);
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
                ((AlbumView)((CardView) view).getChildAt(0)).setSelect(true);
                findAlbum(((AlbumView)((CardView) view).getChildAt(0)).getAlbumName()).setSelect(true);
                updateTrashButton();
                ((AlbumView)((CardView) view).getChildAt(0)).setColorFilter(highlight);
                return true;
            }
        });
    }
    private void menuClick(){
        if(aButton.getVisibility() == View.VISIBLE){
            aButton.setVisibility(View.INVISIBLE);
            tButton.setVisibility(View.INVISIBLE);
            rButton.setVisibility(View.INVISIBLE);
            eButton.setVisibility(View.INVISIBLE);
        }else{
            aButton.setVisibility(View.VISIBLE);
            tButton.setVisibility(View.VISIBLE);
            rButton.setVisibility(View.VISIBLE);
            eButton.setVisibility(View.VISIBLE);
        }


    }
    private void editAlbumName(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Album Name")
                .setView(R.layout.album_create);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Dialog dialogObj = Dialog.class.cast(dialog);
                EditText nameField = (EditText)dialogObj.findViewById(R.id.nameField);
                String oldName = onlyOneSelected();
                String newName = nameField.getText().toString();
                for(int i = 0;i<grid.getChildCount();i++){
                    if(((AlbumView)grid.getChildAt(i)).getAlbumName().equals(oldName)){
                        ((AlbumView)grid.getChildAt(i)).setAlbumName(newName);
                    }
                }
                albumList.set(albumList.indexOf(oldName),newName);
                Album alb =albumMap.get(oldName);
                albumMap.remove(oldName);
                albumMap.put(newName,alb);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        EditText nameField = (EditText)dialog.findViewById(R.id.nameField);
        nameField.setText(onlyOneSelected());
        dialog.show();
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
        if(numSelected() == 1){
            eButton.setEnabled(true);
        }else{
            eButton.setEnabled(false);
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
    private Bitmap drawableConverter(VectorDrawable vd){
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(), vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            vd.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vd.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }
    private Album findAlbum(String aName){
        for (String album:albumList){
            if(albumMap.get(album).getName().equals(aName)){
                return albumMap.get(album);
            }
        }
        return null;
    }
    private int numSelected(){
        int count = 0;
        for (String album:albumList){
            if(albumMap.get(album).getSelect()){
                count++;
            }
        }
        return count;
    }
    private String onlyOneSelected(){
        for(String album:albumList){
            if(albumMap.get(album).getSelect()){
                return album;
            }
        }
        return "";
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
            CardView actualView = new CardView(mContext);
            AlbumView imageView = new AlbumView(mContext,aList.get(i));
            imageView.setImageURI(Uri.parse(aMap.get(aList.get(i)).getImage()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            actualView.addView(imageView);
            actualView.setRadius(20);
            actualView.setLayoutParams(new GridView.LayoutParams(width/4, height/4));
            return actualView;
        }
    }
    private Bitmap makeBitmap(VectorDrawable vd){
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(), vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            vd.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vd.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
