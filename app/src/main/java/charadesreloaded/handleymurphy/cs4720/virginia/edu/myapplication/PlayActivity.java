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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManagerFix;
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
    protected SharedPreferences loadPrefs;
    protected int playTime;
    protected int totalCardsLimit;
    protected int correctCardsLimit;
    protected boolean finished = false;
    protected boolean gameBegun = false;
    protected boolean gamePaused = false;
    protected boolean timerEnabled;
    protected boolean correctLimited;
    protected boolean totalLimited;
    protected int mCardsPos;
    protected TextView card;
    SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float[] orientation;
    private double lastTime;
    private CountDownTimer gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Make it ~~immersive~~
        //Get rid of the navigation bar. User can bring it back up by tapping the screen
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //This is where we will put the counter
        final TextView title = toolbar.findViewById(R.id.toolbar_title);
        //We are only concerned about orientation[2], which is the roll angle
        orientation = new float[3];

        //Get player preferences
        loadPrefs = PreferenceManagerFix.getDefaultSharedPreferences(this);
        try {
            playTime = Integer.parseInt(loadPrefs.getString("game_time", "60"));
        }
        catch (Exception e) {
            //Just use the default play time
            playTime = 60;
        }
        timerEnabled = loadPrefs.getBoolean("show_timer", true);
        totalLimited = loadPrefs.getBoolean("limit_total_cards", false);
        correctLimited = loadPrefs.getBoolean("limit_correct_cards", false);

        //Set up game cards
        this.cardSet = getIntent().getStringExtra("cardSet");
        initCards();

        /*String mCardsSize = String.valueOf(mCards.size());
        String test = loadPrefs.getString("limit_total_cards_number", mCardsSize);
        Log.d("debuggin'", test);
        totalCardsLimit = Integer.parseInt(test);
        System.out.println(totalCardsLimit);*/


        mCardsPos = 0;
        //This is where we put the current game card
        this.card = findViewById(R.id.displayedCard);
        //Set the title to the name of the card set before the countdown timer starts
        title.setText(cardSet);

        //Set up sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Set up our game
        initializeGame();
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
        sensorManager.unregisterListener(this, mMagnetometer);
        sensorManager.unregisterListener(this, mAccelerometer);
        gameTimer.cancel();
        gamePaused = true;
    }

    private void initializeGame() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Our title is where we show the countdown timer, if the user wants it
        final TextView title = toolbar.findViewById(R.id.toolbar_title);

        /*
        * I know this is going to seem out of order, so bear with me. I had to do it for execution reasons.
        *
        * This is the main timer for the game. The onTick method will be called every 1000ms, and onFinish
        * will be called when the timer has reached 0 seconds */
        gameTimer = new CountDownTimer((long) playTime* 1000, 1000) {
            @Override
            public void onTick(long l) {
                if(timerEnabled) {
                    //Every time the clock ticks (every second), show the user the new time remaining.
                    String titleText = getString(R.string.countdown_timer, l / 1000);
                    title.setText(titleText);
                }
            }
            @Override
            public void onFinish() {
                //Let playGame know the game timer is finished and the game is over
                finished = true;
            }
        };

        /*This is the timer that displays the following things in order at the start of the game:
            - Get Ready!
            - 3
            - 2
            - 1
            - Go!
            This timer then finishes, and starts the game timer.
         */
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
                    //Show the first card to the user
                    card.setText(mCards.get(mCardsPos));
                    //Start the game timer (this is why timer is declared above)
                    gameTimer.start();
                    //The game has now begun, the while loop in playGame can actually do something
                    gameBegun = true;
                    //This lets us establish a delay between signaling an incorrect or correct card
                    lastTime = System.currentTimeMillis();
                    //Actually play the game
                    playGame();
            }
        }.start();
    }

    private void playGame() {
        //Define a new thread to run the actual game while the main timer and stuff runs on the main thread
        Thread game = new Thread(new Runnable() {
            @Override
            public void run() {
                //Since we don't want our thread to just terminate immediately, I put it in a while(true) loop
                while(!gamePaused) {
                    //We only want to do anything if our game has begun
                    if(gameBegun) {
                        /* If we've received the "finished" flag from the game timer or we've gone
                           past the end of our card set, we break, which will call gameOver (out of
                           the while loop)
                        */
                        if(finished || mCardsPos >= mCards.size()) {
                            gameTimer.cancel();
                            break;
                        }
                        //Only check the orientation if it's been 1.5s since the last tilt. We can change this
                        else if(System.currentTimeMillis() - lastTime > 1500){
                            /* 0.0f is the default value. I'm checking this because I don't want to
                               proceed if we don't have an orienation array yet and because Java
                               hates me and won't let me do float != null
                             */
                            if(orientation[2] != 0.0f) {
                                //Log.d("orientation", String.valueOf(orientation[2]));
                                //Got it incorrect -> these angles need to be changed. They were merely spot-checked and I have no idea what they actually equal
                                //between -50 and -70 degrees
                                if (orientation[2] < -.698 && orientation[2] > -1.042) {
                                    /* We will return this list to the ResultsActivity to show the
                                       player what they got wrong */
                                    incorrectCards.add(card.getText().toString());
                                    //This is our position in mCards; we're going on to the next card now
                                    mCardsPos++;
                                    /*Set the card text shown to the user to the next card in the list
                                      We have to do this weird runOnUiThread thing because Android does
                                      not like when a subthread tries to mess with the UI--an exception will
                                      be thrown
                                     */
                                    playSound(R.raw.incorrect);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    card.setText(mCards.get(mCardsPos));
                                                }
                                                catch(IndexOutOfBoundsException e) {

                                                }
                                            }
                                        });
                                    //Set the previous time so we can appropriately calculate the delay for the next tilt
                                    lastTime = System.currentTimeMillis();
                                }
                                //Got it correct - > these angles should also be changed
                                //Between -120 and -130 degrees
                                else if (orientation[2] < -2.0943951 && orientation[2] > -2.44346095) {
                                    correctCards.add(card.getText().toString());
                                    if(correctLimited && correctCards.size() == correctCardsLimit)
                                        break;
                                    mCardsPos++;
                                    playSound(R.raw.correct);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    card.setText(mCards.get(mCardsPos));
                                                }
                                                catch(IndexOutOfBoundsException e) {

                                                }
                                            }
                                        });
                                    lastTime = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                }
                //***THIS IS THE END OF THE GAME (end of the while loop)***

                /* Let's say the timer ended before we reached the last card. Add the last card shown
                   to the 'incorrect' list */
                try {
                    if(finished)
                        incorrectCards.add(mCards.get(mCardsPos));
                }
                catch (IndexOutOfBoundsException e) {
                    //Meh, don't really need to do anything, they actually got through all the cards
                }
                //End the game
                if(!gamePaused)
                    gameOver();
            }
        });
        //This runs the above thread definition
        game.start();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No edits needed here.
    }

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
                    SensorManager.getOrientation(R, orientation);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void playSound(int soundFile) {
        MediaPlayer mp;
        mp = MediaPlayer.create(this, soundFile);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
            }

        });
        mp.start();
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
        String [] selectionArgs = {cardSet};
        String query = "SELECT cardText FROM cards WHERE cardSet=?";
        Cursor cursor = db.rawQuery(query, selectionArgs);
        while(cursor.moveToNext()){
            String item = cursor.getString(cursor.getColumnIndexOrThrow("cardText"));
            mCards.add(item);
        }
        cursor.close();
        Collections.shuffle(mCards);

        //Truncate the list to the user preferences
        try {
            totalCardsLimit = Integer.parseInt(loadPrefs.getString("limit_total_cards_number", String.valueOf(mCards.size())));
        }
        catch(Exception e) {
            totalCardsLimit = mCards.size();
        }
        try {
            correctCardsLimit = Integer.parseInt(loadPrefs.getString("limit_correct_cards_number", String.valueOf(mCards.size())));
        }
        catch (Exception e) {
            correctCardsLimit = mCards.size();
        }

        if(totalLimited && totalCardsLimit < mCards.size())
            mCards.subList(totalCardsLimit, mCards.size()).clear();
    }
}
