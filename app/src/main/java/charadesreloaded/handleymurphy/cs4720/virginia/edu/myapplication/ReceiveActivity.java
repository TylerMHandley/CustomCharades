package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ReceiveActivity extends AppCompatActivity {
    private File mParentPath;
    private Intent mIntent;
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        getSupportActionBar().setTitle("Upload a file");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fileSearch();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void fileSearch(){
        Intent openFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openFile.addCategory(Intent.CATEGORY_OPENABLE);
        openFile.setType("*/*");
        startActivityForResult(openFile, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (resultData != null){
                uri = resultData.getData();
                Log.i("Receive", "Uri: " + uri.toString());
                try{
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    String cardSet;
                    CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    cardSet = reader.readLine();
                    values.put("title", cardSet);
                    values.put("count", 0);
                    db.insert("cards", null, values);
                    while ((line = reader.readLine()) != null){
                        values.put("cardText", line);
                        values.put("cardSet", cardSet);
                        db.insert("cards", null, values);
                    }
                }catch (Exception e){
                    Log.e("Receive", e.toString());
                }
            }
        }
    }

    /*
    private void handleViewIntent() {
        mIntent = getIntent();
        String action = mIntent.getAction();
        if (TextUtils.equals(action, Intent.ACTION_VIEW)){
            Uri beamUri = mIntent.getData();
            if (TextUtils.equals(beamUri.getScheme(), "file")){
                //mParentPath = handleFileUri(beamUri);
            }else if (TextUtils.equals(beamUri.getScheme(), "content")){
                //mParentPath = handleContentUri(beamUri);
            }
        }
    }
    public String handleFileUri(Uri beamUri){
        String fileName = beamUri.getPath();
        File copiedFile = new File(fileName);
        return copiedFile.getParent();
    }
    public String handleContentUri(Uri beamUri){
        return "temp";
    }
    */
}
