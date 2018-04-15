package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    protected ArrayList<String> mCards;
    protected ArrayList<String> correctCards;
    protected ArrayList<String> incorrectCards;
    protected String cardSet;
    protected int playTime;
    protected boolean gameBegun = false;
    protected int mCardsPos;
    protected TextView card;
    SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private double lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Make it ~~immersive~~
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final TextView title = toolbar.findViewById(R.id.toolbar_title);

        //Get player preferences
        SharedPreferences loadPrefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        playTime = loadPrefs.getInt("gameTime", 60);

        //Set up game cards
        this.cardSet = getIntent().getStringExtra("cardSet");
        initCards();
        mCardsPos = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.card = findViewById(R.id.displayedCard);
        title.setText(cardSet);
        playGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add a line to register the Session Manager Listener
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void playGame() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView title = toolbar.findViewById(R.id.toolbar_title);

        final CountDownTimer timer = new CountDownTimer((long) playTime* 1000, 1000) {
            @Override
            public void onTick(long l) {
                String titleText = getString(R.string.countdown_timer, l/1000);
                title.setText(titleText);
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        };


        CountDownTimer beginTimer = new CountDownTimer((long) 7200, 1200) {
            int i = 4;
            @Override
            public void onTick(long l) {
                if(i == 4)
                    card.setText(getString(R.string.get_ready));
                else if(i > 0 && i < 4)
                    card.setText(String.valueOf(i));
                else
                    card.setText(getString(R.string.go));
                i--;
            }
            @Override
            public void onFinish() {
                card.setText(mCards.get(mCardsPos));
                timer.start();
                gameBegun = true;
                lastTime = System.currentTimeMillis();
            }
        }.start();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No edits needed here.
    }

    /*@Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        if(gameBegun) {
            if(Math.abs(y) > Math.abs(x)) {
                if(y > 0) {
                    mCardsPos++;
                    if (mCardsPos < mCards.size())
                        card.setText(mCards.get(mCardsPos));
                }
            }
        }
    }*/

    //Example taken basically directly from https://stackoverflow.com/a/20340147
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                if(gameBegun && orientation[2] < 0) {
                    //Got it right
                    if(orientation[2] < -1.1 && orientation[2] > -1.3 && (System.currentTimeMillis() - lastTime) > 1000) {
                        if(mCardsPos >= mCards.size()) {
                            gameOver();
                        }
                        else {
                            correctCards.add(card.getText().toString());
                            card.setText(mCards.get(mCardsPos));
                            mCardsPos++;
                            lastTime = System.currentTimeMillis();
                        }
                    }
                    //Got it wrong
                    else if(orientation[2] < -1.9 && orientation[2] > -2.1 && (System.currentTimeMillis() - lastTime) > 1000) {
                        if(mCardsPos >= mCards.size()) {
                            gameOver();
                        }
                        else {
                            incorrectCards.add(card.getText().toString());
                            card.setText(mCards.get(mCardsPos));
                            mCardsPos++;
                            lastTime = System.currentTimeMillis();
                            Log.d("position", String.valueOf(mCardsPos));
                        }
                    }
                }

            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void gameOver() {
        Intent gameOver = new Intent(this, ResultsActivity.class);
        gameOver.putExtra("incorrect", incorrectCards);
        gameOver.putExtra("correct", correctCards);
        startActivity(gameOver);
    }

    private void initCards(){
        mCards = new ArrayList<>();
        correctCards = new ArrayList<>();
        incorrectCards = new ArrayList<>();
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] projection = {"cardText"};
        String query = "SELECT cardText FROM cards WHERE cardSet ='" + cardSet + "'" ;
        //Cursor cursor = db.query("cards", projection, null, null, null, null, null);
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            String item = cursor.getString(cursor.getColumnIndexOrThrow("cardText"));
            mCards.add(item);
        }
        Collections.shuffle(mCards);
    }
}
