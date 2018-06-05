package com.shahzaib.saveandretrieveimagefromsqlite;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shahzaib.saveandretrieveimagefromsqlite.Database.DbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE_IMAGE_PIC = 100;

    ImageView imageView;
    Bitmap image = null;
    long insertedItemId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);



        // requesting for READ_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE_PIC)
        {
            if(data == null) return;
            Uri imageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageView.setImageBitmap(image);
                SHOW_LOG("Image Read Successfully");
                Toast.makeText(this, "Image Loaded from Gallery", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                SHOW_LOG("Error occur while reading Image");
            }
        }
    }








    public void loadImageFromGallery(View view) {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK);
        chooseImageIntent.setType("image/*");
        Intent chooser = Intent.createChooser(chooseImageIntent,"Complete action using");
        startActivityForResult(chooser,REQUEST_CODE_IMAGE_PIC);
    }
    public void saveImageIntoDatabase(View view) {
        if(image == null) return;

        byte[] imageInByte = convertBitmapIntoByteArray();
        insertedItemId = saveByteArrayIntoDb(imageInByte);
        image = null; // clear the image, it will prevent again & again insertion into Db when button will pressed
        Toast.makeText(this, "Image Saved into DB Successfully", Toast.LENGTH_SHORT).show();

    }
    public void loadImageFromDb(View view) {
        if(insertedItemId == -1) return;
        Intent intent = new Intent(this, Load_Image_From_Database.class);
        intent.putExtra(Load_Image_From_Database.INTENT_KEY_ITEM_ID,String.valueOf(insertedItemId));
        startActivity(intent);
    }












    //*********** Helper method
    private void SHOW_LOG(String message)
    {
        Log.i("123456",message);
    }
    private byte[] convertBitmapIntoByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[] = stream.toByteArray();
        return imageInByte;
    }
    private long saveByteArrayIntoDb(byte[] byteArray) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db =  dbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("picture",byteArray);
        long insertedItemId = db.insert("PicturesTable",null,value);
        db.close();
        return insertedItemId;
    }



}
