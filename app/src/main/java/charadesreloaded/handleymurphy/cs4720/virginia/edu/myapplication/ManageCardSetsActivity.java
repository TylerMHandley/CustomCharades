package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * This is the activity that shows you your card **sets**, not the cards within a set
 */
public class ManageCardSetsActivity extends AppCompatActivity {
    protected ArrayList<String> mCards;
    protected CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_card_sets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your Card Sets");
        mCards = new ArrayList<>();
        //TODO: Remove this method call
        fakeInitCards();
        
        initCards();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.VIEW_CARDSET);

        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //TODO: Remove this method, this is only for testing
    private void fakeInitCards() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", "Testing");
        db.insert("cardsets", null, values);
        values.put("title", "Woah");
        db.insert("cardsets", null, values);
        values.put("title", "Yep");
        db.insert("cardsets", null, values);
        values.put("title", "Yep");
        db.insert("cardsets", null, values);
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

}