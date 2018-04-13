package charadesreloaded.handleymurphy.cs4720.virginia.edu.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huntermurphy on 4/12/18.
 */

public class CardDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cards.db";

    public CardDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cardsets (title varchar(50), PRIMARY KEY (title))");
        db.execSQL("create table cards (cardText varchar(200), cardSet varchar(50), FOREIGN KEY (cardSet) REFERENCES cardsets(title))");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("drop table cardsets");
        db.execSQL("drop table cards");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
