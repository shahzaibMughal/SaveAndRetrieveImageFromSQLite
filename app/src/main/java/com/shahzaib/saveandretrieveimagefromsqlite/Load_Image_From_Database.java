package com.shahzaib.saveandretrieveimagefromsqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.shahzaib.saveandretrieveimagefromsqlite.Database.DbHelper;

public class Load_Image_From_Database extends AppCompatActivity {

    public static final String INTENT_KEY_ITEM_ID = "itemID";
    String itemID;
    ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load__image__from__database);
        imageView = findViewById(R.id.imageView);

        itemID = getIntent().getStringExtra(INTENT_KEY_ITEM_ID);

        byte[] imageInByte = getImageInBytesFromDb(itemID);
        imageView.setImageBitmap(convertBytesIntoBitmap(imageInByte));
        Toast.makeText(this, "Image Loaded from Database", Toast.LENGTH_SHORT).show();

    }













    //**************** Helper methods
    private void SHOW_LOG(String message)
    {
        Log.i("123456",message);
    }
    private byte[] getImageInBytesFromDb(String itemID) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("PicturesTable",null,"_id="+itemID,null,null,null,null);
        if(cursor != null)
        {
            cursor.moveToFirst();
            byte[] imageInByte = cursor.getBlob(cursor.getColumnIndex("picture"));
            return imageInByte;
        }
        else
        {
            SHOW_LOG("Cursor is empty");
            return null;
        }
    }
    private Bitmap convertBytesIntoBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
}




