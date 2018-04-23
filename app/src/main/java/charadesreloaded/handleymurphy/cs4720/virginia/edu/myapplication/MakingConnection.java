package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class MakingConnection extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private Uri[] mFileUris;
    private FileUriCallback mFileUriCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sending a Card");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mFileUriCallback = new FileUriCallback();
        mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, fileName);
        requestFile.setReadable(true, false);
        Uri fileUri = Uri.fromFile(requestFile);
        if (fileUri != null){
            mFileUris[0] = fileUri;
        }else{
            Log.e("NFC", "No file URI");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, MainActivity.class);
        startActivity(backIntent);
    }
    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback{
        public FileUriCallback(){
        }
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

}
