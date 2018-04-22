package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ManageCardsActivity extends AppCompatActivity {

    private boolean titleChanged = false;
    protected ArrayList<String> mCards;
    protected String newCardSetName;
    protected String cardSet;
    protected CardAdapter adapter;
    protected ArrayList<String> startSet;
    protected EditText title;
    protected RecyclerView rvCards;

    private TextWatcher titleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //no ops
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            titleChanged = true;
            newCardSetName = title.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //no ops
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intentBundle = getIntent();
        this.cardSet = intentBundle.getStringExtra("cardSet");
        actionBar.setTitle(cardSet);

        if(savedInstanceState != null) {
            mCards = (ArrayList<String>) savedInstanceState.get("mCards");
            startSet = (ArrayList<String>) savedInstanceState.get("startSet");
        }
        else {
            mCards = new ArrayList<>();
            initCards();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.EDIT_CARD);

        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

        title = (EditText) findViewById(R.id.edit_card_name);
        title.addTextChangedListener(titleTextWatcher);

        ImageView deleteTitle = (ImageView) findViewById(R.id.deleteButton);
        deleteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(android.R.id.content).getContext());
                builder.setTitle("Delete " + cardSet + "?");
                builder.setMessage(getApplicationContext().getString(R.string.delete_card_set, cardSet));
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCardSet();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                doKeepDialog(alert);
            }
        });
        title.setText(cardSet);

        if(mCards.isEmpty()) {
            rvCards.setVisibility(View.GONE);
            findViewById(R.id.manageCardsEmtpy).setVisibility(View.VISIBLE);
        }
    }
/*
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("Backpressed", "T'was Pressed");
        onBackPressed();
        return true;
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_manage_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
       if (id == R.id.save_button){
           if(titleChanged)
               titleChange();

           //Remove duplicate entries to play nice and handle user errors without them even knowing
           //Maybe that's bad?
           //Set<String> removeDuplicates = new LinkedHashSet<>(mCards);
           ArrayList<String> removeDuplicates = new ArrayList<>();
           while(!mCards.isEmpty()) {
               removeDuplicates.add(mCards.get(0));
               mCards.removeAll(Collections.singleton(mCards.get(0)));
           }
           mCards.addAll(removeDuplicates);

           CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
           SQLiteDatabase db = dbHelper.getWritableDatabase();
           ContentValues values = new ContentValues();
           //for (String str : mCards){
           int length = mCards.size();
           int orgLength = startSet.size();
           int max = Math.max(length, orgLength);
           for (int i =0;i<max;i++){
               if (i < orgLength && i < length && !startSet.get(i).equals(mCards.get(i))) {
                   //UPDATE
                   values.put("cardText",mCards.get(i));
                   String whereArgs[] = {startSet.get(i), cardSet};

                   int rowsaffected = db.update("cards", values, "cardText=? AND cardSet=?", whereArgs);
               }else if (i < orgLength && i >= length){
                   //DELETE
                   Log.e("Delete", startSet.get(i));
                   String whereArgs[] = {startSet.get(i), cardSet};
                   db.delete("cards", "cardText=? AND cardSet=?", whereArgs);

                   String whereArgs2[] = {cardSet};
                   db.execSQL("UPDATE cardsets SET count = count-1 WHERE title=? AND count > 0", whereArgs2);
               }else if (i >= orgLength && i < length ) {
                   //ADD
                   if (!mCards.get(i).equals("")) {
                       values.put("cardText", mCards.get(i));
                       values.put("cardSet", cardSet);
                       db.insert("cards", null, values);

                       String whereArgs[] = {cardSet};
                       db.execSQL("UPDATE cardsets SET count = count+1 WHERE title=?", whereArgs);
                   }
               }

           }
           Snackbar.make(findViewById(R.id.coordlayout), "Saved Changes", Snackbar.LENGTH_SHORT).show();
       }
       else if(id == android.R.id.home){
           Log.d("Backpressed", "T'was Pressed");
           onBackPressed();
       }

       return true;
    }
    private void initCards(){
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] selectionArgs = {cardSet};
        String query = "SELECT cardText FROM cards WHERE cardSet=?";
        //Cursor cursor = db.query("cards", projection, null, null, null, null, null);
        Cursor cursor = db.rawQuery(query, selectionArgs);
        while(cursor.moveToNext()){
            String item = cursor.getString(cursor.getColumnIndexOrThrow("cardText"));
            mCards.add(item);
        }
        this.startSet = new ArrayList<>(mCards);
        cursor.close();

    }
    public void newLine(View view){
        /*
        Linear layout method. Less efficient
        EditText newCard = new EditText(this);
        LinearLayout lineup = (LinearLayout) findViewById(R.id.lineup);
        LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newCard.setLayoutParams(parameters);
        newCard.setHint(R.string.addCardText);
        lineup.addView(newCard);
        this.numLines++;
        newCard.setId(numLines);
        */
        this.mCards.add("");
        this.adapter.notifyDataSetChanged();
        //Make the app focus on the newly added card
        this.rvCards.smoothScrollToPosition(mCards.size()-1);

        rvCards.setVisibility(View.VISIBLE);
        findViewById(R.id.manageCardsEmtpy).setVisibility(View.GONE);
    }
    public void delete(View view){
        EditText edit = (EditText) findViewById(R.id.edit_card_name);
        String str = edit.getText().toString();
        this.mCards.remove(str);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mCards", mCards);
        outState.putSerializable("startSet", startSet);
    }

    private void deleteCardSet() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereArgs[] = {cardSet};
        db.delete("cards", "cardSet=?", whereArgs);
        db.delete("cardsets", "title=?", whereArgs);
        finish();
    }

    private void titleChange() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cardsValues = new ContentValues();
        ContentValues titlesValues = new ContentValues();
        titlesValues.put("title", newCardSetName);
        cardsValues.put("cardSet", newCardSetName);
        String whereArgs[] = {cardSet};
        db.update("cardsets", titlesValues, "title=?", whereArgs);
        db.update("cards", cardsValues, "cardSet=?", whereArgs);
        cardSet = newCardSetName;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(cardSet);
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
