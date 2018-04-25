package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

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

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class ReceiveActivity extends AppCompatActivity {
    private File mParentPath;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        getSupportActionBar().setTitle("Waiting for a card set...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
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

}
