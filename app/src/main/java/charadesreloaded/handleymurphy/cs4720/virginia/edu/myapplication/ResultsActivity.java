package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
    }
    @Override
    public void onBackPressed() {
        Intent redirectIntent = new Intent(this, SelectCardSetToPlayActivity.class);
        startActivity(redirectIntent);
    }
}
