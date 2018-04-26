package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button createSetButton;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        createSetButton = findViewById(R.id.create_text);
        /*if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            //we do not have nfc
            findViewById(R.id.share).setVisibility(View.GONE);
        }
        else {
            //we have the nfc we need

        }*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(android.R.id.content).getContext());
                builder.setTitle(R.string.share_title);
                builder.setMessage(R.string.share_description);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.receive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent beginReceive = new Intent(mContext, ReceiveActivity.class);
                        startActivity(beginReceive);
                    }
                });
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent beginNfc = new Intent(mContext, SelectCardSetToShare.class);
                        startActivity(beginNfc);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                doKeepDialog(alert);
                break;
            case R.id.settings:
                Intent shareIntent = new Intent(this, SettingsActivity.class);
                startActivity(shareIntent);
                break;
        }
        return true;
    }

    //onClick method to go to manage cards screen
    public void goToManage(View view) {
        final Intent manageIntent = new Intent(this, ManageCardSetsActivity.class);
        startActivity(manageIntent);
    }
    public void createSet(View view){
        final View thisView = view;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(R.string.dialog);
        builder.setTitle(R.string.dialog_title);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(true).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton(R.string.accept, null);
        final AlertDialog alert = builder.create();
        alert.show();
        doKeepDialog(alert);

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textField = dialogView.findViewById(R.id.cardSetName);
                if(!textField.getText().toString().equals("")) {
                    Intent addSetIntent = new Intent(view.getContext(), ManageCardsActivity.class);
                    String setName = textField.getText().toString();
                    addSetIntent.putExtra("cardSet", setName);
                    CardDatabaseHelper dbHelper = new CardDatabaseHelper(view.getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("title", setName);
                    values.put("count", 0);
                    try {
                        db.insertOrThrow("cardsets", null, values);
                        startActivity(addSetIntent);
                        alert.dismiss();
                    } catch (android.database.sqlite.SQLiteConstraintException e) {
                        textField.setError("There's already a card set called " + setName);
                    }
                }
                else
                    textField.setError("Please enter a name");
            }
        });

    }
    //onClick method to go to play selection screen
    public void goToPlay(View view) {
        int numCardSets = getNumCardSets();
        if(numCardSets > 0) {
            final Intent playIntent = new Intent(this, SelectCardSetToPlayActivity.class);
            startActivity(playIntent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(R.string.no_playable_sets);
            builder.setTitle(R.string.no_playable_sets_title);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setPositiveButton(R.string.create_cards_dialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    createSetButton.callOnClick();
                }
            });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private int getNumCardSets() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] projection = {"title"};
        String whereClause = "count > 0";
        Cursor cursor = db.query("cardsets", projection, whereClause, null, null, null, "title ASC");
        return cursor.getCount();
    }


    //Refer to: https://stackoverflow.com/a/27311231
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("reading it", "hopefully");
        if(intent!=null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.d("it works?", "yeah");
        }
    }
}
