package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class SelectCardSetToShare extends AppCompatActivity {
    protected ArrayList<String> mCards;
    protected CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_card_set_to_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Play With Which Card Set?");
        mCards = new ArrayList<>();
        initCards();
        RecyclerView rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.SHARE_CARDSET);

        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
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

    private void initCards() {
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] projection = {"title"};
        String whereClause = "count > 0";
        Cursor cursor = db.query("cardsets", projection, whereClause, null, null, null, "title ASC");
        while(cursor.moveToNext()) {
            String item = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            mCards.add(item);
        }
        cursor.close();
    }
}
