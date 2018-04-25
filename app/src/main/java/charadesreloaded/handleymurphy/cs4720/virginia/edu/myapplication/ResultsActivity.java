package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

//My lifeline: https://stackoverflow.com/questions/17693578/android-how-to-display-2-listviews-in-one-activity-one-after-the-other?

public class ResultsActivity extends AppCompatActivity {

    private ListView correct, incorrect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.results);
        setContentView(R.layout.activity_results);
        ArrayList<String> correct_cards = (ArrayList<String>) getIntent().getSerializableExtra("correct");
        ArrayList<String> incorrect_cards = (ArrayList<String>) getIntent().getSerializableExtra("incorrect");
        correct = (ListView) findViewById(R.id.correct);
        incorrect = (ListView) findViewById(R.id.incorrect);
        if(correct_cards.size() > 0)
            correct.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, correct_cards));
        else {
            findViewById(R.id.no_cards_right).setVisibility(View.VISIBLE);
            correct.setVisibility(View.GONE);
        }
        if(incorrect_cards.size() > 0)
            incorrect.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, incorrect_cards));
        else {
            findViewById(R.id.no_cards_wrong).setVisibility(View.VISIBLE);
            incorrect.setVisibility(View.GONE);
        }
        ListUtils.setDynamicHeight(correct);
        ListUtils.setDynamicHeight(incorrect);
    }
    @Override
    public void onBackPressed() {
        Intent redirectIntent = new Intent(this, SelectCardSetToPlayActivity.class);
        startActivity(redirectIntent);
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    public void returnToMain(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void returnToPlay(View view) {
        Intent playIntent = new Intent(this, SelectCardSetToPlayActivity.class);
        startActivity(playIntent);
    }
}
