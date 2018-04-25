package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReceiveActivity extends AppCompatActivity {
    private File mParentPath;
    private Intent mIntent;
    private static final int READ_REQUEST_CODE = 42;
    private String cardSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        getSupportActionBar().setTitle("Import a Card Set");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //There is a much better way to do this but I am so tired
        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("finishCalledAlready")) {

            } else {
                fileSearch();
            }
        }
        else
            fileSearch();

        findViewById(R.id.findFileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileSearch();
            }
        });
        TextView text = findViewById(R.id.receiveText);
        //text.setText("Complete :-)");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("finishCalledAlready", true);
    }

    public void fileSearch(){
        Intent openFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openFile.addCategory(Intent.CATEGORY_OPENABLE);
        openFile.setType("text/plain");
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
                    ContentValues values2 = new ContentValues();
                    cardSet = reader.readLine();
                    this.cardSet = cardSet;
                    try{
                        values.put("title", cardSet);
                        values.put("count", 0);
                        db.insertOrThrow("cardSets", null, values);

                    while ((line = reader.readLine()) != null){
                        values2.put("cardText", line);
                        values2.put("cardSet", cardSet);
                        db.insert("cards", null, values2);
                    }
                    }catch (SQLiteConstraintException sqle){
                        Log.e("Receive", sqle.toString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getApplicationContext().getString(R.string.set_already_exists_title, this.cardSet));
                        builder.setMessage(getApplicationContext().getString(R.string.set_already_exists, this.cardSet));

                        builder.setCancelable(true);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        doKeepDialog(alert);
                        this.cardSet = null;
                    }
                }catch (Exception e){
                    Log.e("Receive", e.toString());
                }
                if(this.cardSet != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getApplicationContext().getString(R.string.set_imported_title, this.cardSet));
                    builder.setMessage(getApplicationContext().getString(R.string.set_imported, this.cardSet));

                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.view_imported_set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent editIntent = new Intent(getBaseContext(), ManageCardsActivity.class);
                            editIntent.putExtra("cardSet", cardSet);
                            startActivity(editIntent);
                        }
                    });
                    builder.setNegativeButton(R.string.do_not_view_imported_set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    doKeepDialog(alert);
                }
            }
        }
    }

    //Refer to: https://stackoverflow.com/a/27311231
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}
