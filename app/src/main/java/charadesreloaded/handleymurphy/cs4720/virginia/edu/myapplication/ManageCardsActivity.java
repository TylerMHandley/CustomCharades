package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.sql.SQLData;
import java.util.ArrayList;

public class ManageCardsActivity extends AppCompatActivity {
    private int numLines = -1;
    protected ArrayList<String> mCards;
    protected String cardSet;
    protected CardAdapter adapter;

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
        mCards = new ArrayList<>();
        initCards();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RecyclerView rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.EDIT_CARD);

        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
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
           CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
           SQLiteDatabase db = dbHelper.getWritableDatabase();
           ContentValues values = new ContentValues();
           for (String str : mCards){
               values.put("cardText",str);
               values.put("cardSet", cardSet);
               db.insert("cards", null, values);
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
        String [] projection = {"cardText"};
        String query = "SELECT cardText FROM cards WHERE cardSet ='" + cardSet + "'";
        //Cursor cursor = db.query("cards", projection, null, null, null, null, null);
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            String item = cursor.getString(cursor.getColumnIndexOrThrow("cardText"));
            mCards.add(item);
        }

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
    }

}
