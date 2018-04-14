package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Intent;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(cardSet);
        mCards = new ArrayList<>();

        Intent intentBundle = getIntent();
        this.cardSet = intentBundle.getStringExtra("cardSet");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RecyclerView rvCards = findViewById(R.id.rvCards);
        adapter = new CardAdapter(mCards, this, CardAdapter.EDIT_CARD);

        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void newLine(View view){
        EditText newCard = new EditText(this);
        LinearLayout lineup = (LinearLayout) findViewById(R.id.lineup);
        LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newCard.setLayoutParams(parameters);
        newCard.setHint(R.string.addCardText);
        lineup.addView(newCard);
        this.numLines++;
        newCard.setId(numLines);
    }

}
