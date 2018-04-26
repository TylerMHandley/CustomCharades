package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * This is the activity that shows you your card **sets**, not the cards within a set
 */
public class ManageCardSetsActivity extends AppCompatActivity {
    protected ArrayList<String> mCards;
    protected CardAdapter adapter;
    protected LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_card_sets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Card Sets");
        mCards = new ArrayList<>();

        initCards();

        FloatingActionButton addSet = (FloatingActionButton) findViewById(R.id.addSet);
        addSet.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(final View view) {
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
                                          //This seems dumb, but I have to set the on click listener after the button is created in order to show the error in the alert dialog
                                          alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  EditText textField = dialogView.findViewById(R.id.cardSetName);
                                                  if (!textField.getText().toString().equals("")) {
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
                                          doKeepDialog(alert);
                                      }
                                  });

        RecyclerView rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.VIEW_CARDSET);

        rvCards.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        rvCards.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvCards.getContext(), linearLayoutManager.getOrientation());
        rvCards.addItemDecoration(dividerItemDecoration);
        adapter.notifyDataSetChanged();

        if(mCards.isEmpty()) {
            rvCards.setVisibility(View.GONE);
            findViewById(R.id.manageCardSetsEmtpy).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCards.clear();
        initCards();
        adapter.notifyDataSetChanged();

        RecyclerView rvCards = findViewById(R.id.rvCards);
        if(mCards.isEmpty()) {
            rvCards.setVisibility(View.GONE);
            findViewById(R.id.manageCardSetsEmtpy).setVisibility(View.VISIBLE);
            }
        else {
            rvCards.setVisibility(View.VISIBLE);
            findViewById(R.id.manageCardSetsEmtpy).setVisibility(View.GONE);
            }
    }

    private void initCards() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] projection = {"title"};
        Cursor cursor = db.query("cardsets", projection, null, null, null, null, "title ASC");
        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            mCards.add(item);
        }
        cursor.close();
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
