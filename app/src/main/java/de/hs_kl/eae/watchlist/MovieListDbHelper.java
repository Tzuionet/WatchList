package de.hs_kl.eae.watchlist;

/**
 * Created by daniel on 17.12.2016.
 * Hilfsklasse für das erstellen der Datenbank.
 * Informationen wie Tabellenname, Datenbankversion und Spaltennamen werden hier gespeichert.
 */

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

public class MovieListDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = MovieListDbHelper.class.getSimpleName();

    //Name und Version der Datenbank
    public static final String DB_NAME = "movie_db.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_MOVIE_LIST = "movie_db";

    //Name der Tabellenspalten
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_POSTER = "poster";
    public static final String COLUMN_STATUS = "status";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_MOVIE_LIST + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_YEAR + " INTEGER NOT NULL, " +
                    COLUMN_GENRE + " TEXT NOT NULL, " +
                    COLUMN_PLOT + " TEXT NOT NULL, " +
                    COLUMN_POSTER + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " INTEGER NOT NULL);"
            ;

    //Konstruktor
    public MovieListDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    //OnCreate Methode erstellt Tabelle
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}