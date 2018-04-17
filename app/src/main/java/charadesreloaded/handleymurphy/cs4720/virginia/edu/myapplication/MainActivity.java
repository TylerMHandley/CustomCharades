package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "CharadesPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        builder.setCancelable(true).setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent addSetIntent = new Intent(thisView.getContext(), ManageCardsActivity.class);
                EditText textField = dialogView.findViewById(R.id.cardSetName);
                String setName = textField.getText().toString();
                addSetIntent.putExtra("cardSet", setName);
                CardDatabaseHelper dbHelper = new CardDatabaseHelper(thisView.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("title", setName);
                values.put("count", 0);
                db.insert("cardsets", null,values);
                startActivity(addSetIntent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //onClick method to go to play selection screen
    public void goToPlay(View view) {
        final Intent playIntent = new Intent(this, SelectCardSetToPlayActivity.class);
        startActivity(playIntent);
    }

}
