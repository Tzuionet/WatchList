package de.hs_kl.eae.watchlist;

/**
 * Created by daniel on 17.12.2016.
 * Diese Klasse ermöglicht die Datenbankzugriffe.
 * Datensätze können in die Datenbank geschrieben werden.
 * Auch das Lesen aus der Datenbank wird hier realisiert.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

//Stellt Funktionen bzw. Methoden bereit um mit den Daten der Tabelle innerhalb der Datenbank zu arbeiten
public class MovieListDataSource {

    private static final String LOG_TAG = MovieListDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private MovieListDbHelper dbHelper;

    //Spalten-Array wird angelegt
    //Benötigt man für die Suche
    private String[] columns = {
            MovieListDbHelper.COLUMN_ID,
            MovieListDbHelper.COLUMN_TITLE,
            MovieListDbHelper.COLUMN_YEAR,
            MovieListDbHelper.COLUMN_GENRE,
            MovieListDbHelper.COLUMN_PLOT,
            MovieListDbHelper.COLUMN_POSTER,
            MovieListDbHelper.COLUMN_STATUS
    };

    //Konstruktor
    public MovieListDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new MovieListDbHelper(context);
    }

    //Öffnen der Datenbankverbindung
    public void open() {
//        if (!database.isOpen()) {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
        //       }
    }

    //Schließen der Datenbankverbindung
    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    //Liest id vom Titel aus
    public int getID(String title) {
        Log.d("Select ID Befehl", "SELECT * FROM " + MovieListDbHelper.TABLE_MOVIE_LIST + " WHERE " + MovieListDbHelper.COLUMN_TITLE + "=" + title);
        Cursor cursor = database.rawQuery("SELECT * FROM " + MovieListDbHelper.TABLE_MOVIE_LIST + " WHERE " + MovieListDbHelper.COLUMN_TITLE + "=" + "'" + title + "'", null);

        cursor.moveToFirst();
        MovieList datenbank;

        datenbank = cursorToMovieList(cursor);
        Log.d(LOG_TAG, "ID: " + datenbank.getId() + ", Inhalt: " + datenbank.toString());
        cursor.close();
        Log.d("MovieListDataSource", "getID: " + datenbank.getId());
        return datenbank.getId();
    }

    //Updated das Poster Anhand der id in der Datenbank
    public void updatePoster(int id, String posterPath) {
        ContentValues values = new ContentValues();
        values.put(MovieListDbHelper.COLUMN_POSTER, posterPath);

        String where = MovieListDbHelper.COLUMN_ID + "=?";
        String[] whereArg = new String[]{Integer.toString(id)};
        database.update(MovieListDbHelper.TABLE_MOVIE_LIST, values, where, whereArg);
    }

    //Updated oder löscht den Status eines Films
    public void updateStatus(int id, String status){

        if(!status.equals("6")) {
            ContentValues values = new ContentValues();
            values.put(MovieListDbHelper.COLUMN_STATUS, status);
            String where = MovieListDbHelper.COLUMN_ID + "=?";
            String[] whereArg = new String[]{Integer.toString(id)};
            database.update(MovieListDbHelper.TABLE_MOVIE_LIST, values, where, whereArg);
        } else {
            String where = MovieListDbHelper.COLUMN_ID + "=?";
            String[] whereArg = new String[]{Integer.toString(id)};
            database.delete(MovieListDbHelper.TABLE_MOVIE_LIST,where,whereArg);
        }
    }

    //Ueberprueft ob ein Eintrag mit den Titel bereits existiert, true wenn ein Eintrag existiert
    public boolean checkIsMovieInDBorNot(String title) {
        Log.d("checkIsMovieInDBorNot", "Prüfe ob Movie bereits in DB existiert");
        title = title.replace("'", "");
        String Query = "SELECT * FROM " + MovieListDbHelper.TABLE_MOVIE_LIST + " WHERE " + MovieListDbHelper.COLUMN_TITLE + "=" + "'" + title + "'";
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    //Datensätze in Datenbak einfügen
    //ContentValues-Objekt wird erzeugt
    public MovieList createMovieList(String title, String year, String genre, String plot, String poster, String status) {
        ContentValues values = new ContentValues();
        title = title.replace("'", "");
        values.put(MovieListDbHelper.COLUMN_TITLE, title);
        values.put(MovieListDbHelper.COLUMN_YEAR, year);
        values.put(MovieListDbHelper.COLUMN_GENRE, genre);
        values.put(MovieListDbHelper.COLUMN_PLOT, plot);
        values.put(MovieListDbHelper.COLUMN_POSTER, poster);
        values.put(MovieListDbHelper.COLUMN_STATUS, status);

        //Die Werte werden mit dem ContentValues-Objekt in die Datenbank eingetragen.
        long insertId = database.insert(MovieListDbHelper.TABLE_MOVIE_LIST, null, values);

        //Werte werden zur Kontrolle direkt wieder ausgelesen
        Cursor cursor = database.query(MovieListDbHelper.TABLE_MOVIE_LIST,
                columns, MovieListDbHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        //Erster Eintrag wird aufgerufen
        cursor.moveToFirst();
        //Cursor-Objekt wird in ein MovieList-Objekt umgewandelt
        MovieList movieList = cursorToMovieList(cursor);
        //Cursor-Objekt schließen !
        cursor.close();

        //MovieList-Objekt wird zurückgegeben.
        return movieList;
    }

    //speichert die in einem Cursor enthaltenen Informationen in ein MovieList-Objekt und gibts sie zurück.
    private MovieList cursorToMovieList(Cursor cursor) {
        int idId = cursor.getColumnIndex(MovieListDbHelper.COLUMN_ID);
        int idTitle = cursor.getColumnIndex(MovieListDbHelper.COLUMN_TITLE);
        int idYear = cursor.getColumnIndex(MovieListDbHelper.COLUMN_YEAR);
        int idGenre = cursor.getColumnIndex(MovieListDbHelper.COLUMN_GENRE);
        int idPlot = cursor.getColumnIndex(MovieListDbHelper.COLUMN_PLOT);
        int idPoster = cursor.getColumnIndex(MovieListDbHelper.COLUMN_POSTER);
        int idStatus = cursor.getColumnIndex(MovieListDbHelper.COLUMN_STATUS);

        String title = cursor.getString(idTitle);
        String year = cursor.getString(idYear);
        String genre = cursor.getString(idGenre);
        String plot = cursor.getString(idPlot);
        String poster = cursor.getString(idPoster);
        String status = cursor.getString(idStatus);
        int id = cursor.getInt(idId);

        MovieList movieList = new MovieList(title, year, genre, plot, poster, status, id);

        return movieList;
    }

    //Alle vorhanden Daten werden ausgelesen
    public List<MovieList> getAllMovies() {
        //Eine Liste wirde erzeugt, welche die MovieList-Objekte speichert
        List<MovieList> arrayList = new ArrayList<>();

        //Suche
        Cursor cursor = database.query(MovieListDbHelper.TABLE_MOVIE_LIST,
                columns, null, null, null, null, null);

        //Cursor wieder an den Anfang
        cursor.moveToFirst();

        //Variable (Typ MovieList)
        MovieList movieList;

        //while-Schleife
        //Alle Datensätze werden ausgelesen, in MovieList-Objekte umgewandelt und in die arrayList eingetragen
        while (!cursor.isAfterLast()) {
            movieList = cursorToMovieList(cursor);
            arrayList.add(movieList);
            Log.d(LOG_TAG, "ID: " + movieList.getId() + ", Inhalt: " + movieList.toString());
            cursor.moveToNext();
        }

        //cursor schließen !
        cursor.close();

        //erzeugte Liste  wird an die aufrufende Liste zurückgegeben.
        return arrayList;
    }

    public Cursor getAllMoviesCursor(int args) {
        //Eine Liste wird erzeugt, welche die MovieList-Objekte speichert

        //Suche
        String Query = "SELECT * FROM " + MovieListDbHelper.TABLE_MOVIE_LIST + " WHERE " + MovieListDbHelper.COLUMN_STATUS + "=" + "'" + Integer.toString(args) + "'";
        Cursor cursor = database.rawQuery(Query, null);

        //Cursor wieder an den Anfang
        cursor.moveToFirst();

        //erzeugte Liste  wird an die aufrufende Liste zurückgegeben.
        return cursor;
    }

    public Cursor getItem(String args) {
        //Eine Liste wirde erzeugt, welche die MovieList-Objekte speichert

        //Suche
        String Query = "SELECT * FROM " + MovieListDbHelper.TABLE_MOVIE_LIST + " WHERE " + MovieListDbHelper.COLUMN_ID + "=" + "'" + args + "'";
        Cursor cursor = database.rawQuery(Query, null);

        //Cursor wieder an den Anfang
        cursor.moveToFirst();

        //cursor schließen !


        //erzeugte Liste  wird an die aufrufende Liste zurückgegeben.
        return cursor;
    }



}