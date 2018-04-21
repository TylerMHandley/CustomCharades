package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "CharadesPrefsFile";
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            //we do not have nfc
            findViewById(R.id.share).setVisibility(View.GONE);
        }
        else {
            //we have the nfc we need

        }*/
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

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

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.settings:
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

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addSetIntent = new Intent(view.getContext(), ManageCardsActivity.class);
                EditText textField = dialogView.findViewById(R.id.cardSetName);
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
                }
                catch(android.database.sqlite.SQLiteConstraintException e) {
                    textField.setError("There's already a card set called " + setName);
                }
            }
        });

    }
    //onClick method to go to play selection screen
    public void goToPlay(View view) {
        final Intent playIntent = new Intent(this, SelectCardSetToPlayActivity.class);
        startActivity(playIntent);
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
