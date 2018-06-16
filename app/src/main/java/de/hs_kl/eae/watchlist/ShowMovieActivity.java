package de.hs_kl.eae.watchlist;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

//Diese Activity wird angezeigt, wenn ein Film oder eine Serie aus der MainActivity angeklickt wurde.
//Sie zeigt die zum Film dazugehörige Informationen, wie z.B. Poster, Release, Genre, Story an
//Außerdem ist ein "Bearbeiten" Button eingebaut mit dem man den Watch Status verändern kann.
public class ShowMovieActivity extends AppCompatActivity {

    private TextView title;
    private TextView story;
    private ImageView poster;
    private TextView released;
    private TextView genre;
    private MovieListDataSource dataSource;
    private Button editButton;
    private String movieId;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movie);

        //Button, Textfelder werden initialisiert
        title = (TextView) findViewById(R.id.textView_Titel);
        story = (TextView) findViewById(R.id.textView_Story);
        poster = (ImageView) findViewById(R.id.imageView_poster);
        released = (TextView) findViewById(R.id.textView_Released);
        genre = (TextView) findViewById(R.id.textView_Genre);
        editButton = (Button) findViewById(R.id.editButton);
        dataSource = new MovieListDataSource(this);

        //Intent enthält die ID des zugehörigen Film/Serie
        Intent intent = getIntent();
        movieId = intent.getStringExtra("ID");

        //Öffnet die Datenbank und holt sich die Informationen zum zugehörigen Medium. Danach wird die Datenbank geschlossen.
        dataSource.open();
        cursor = dataSource.getItem(movieId);
        dataSource.close();

        //Setzt die Informationen in die View Elemente ein
        String StrPoster = cursor.getString(cursor.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_POSTER));
        String StrTitle = cursor.getString(cursor.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_TITLE));
        String StrGenre = cursor.getString(cursor.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_GENRE));
        String StrReleased = cursor.getString(cursor.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_YEAR));
        String StrPlot = cursor.getString(cursor.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_PLOT));

        title.setText(StrTitle);
        genre.setText(StrGenre);
        story.setText(StrPlot);
        released.setText(StrReleased);
        if (StrPoster.equals("noposter")) {
            poster.setImageResource(R.drawable.noposter);
        } else {
            poster.setImageDrawable(Drawable.createFromPath(StrPoster));
        }

        //Beim drücken des "Bearbeiten" Button wird ein ListDialog angezeigt in der der Watch Status des Mediums veröndert werden kann
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Erstellt ListDialog
                String[] val = {"Currently Watching", "Plan to Watch", "On Pause", "Completed", "Dropped", "Delete"};
                final Dialog listDialog;
                listDialog = new Dialog(ShowMovieActivity.this);
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View v = li.inflate(R.layout.status_view, null, false);
                listDialog.setContentView(v);
                listDialog.setCancelable(true);
                listDialog.setCanceledOnTouchOutside(true);
                ListView list1 = (ListView) listDialog.findViewById(R.id.statuslistview);
                list1.setAdapter(new ArrayAdapter<String>(ShowMovieActivity.this, android.R.layout.simple_list_item_1, val));
                //Nach dem aufsetzen des Dialogs wird er angezeigt.
                listDialog.show();

                //Wird aufgerufen, wenn beim ListDialog eine Zeile angeklickt wurde
                list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //Updatet das Medium in der Datenbank bzw. löscht es
                        dataSource.open();
                        dataSource.updateStatus(Integer.parseInt(movieId), Integer.toString(i + 1));
                        dataSource.close();

                        //Löscht das zugehörige Poster vom Medium, wenn "Löschen" gedrückt wurde
                        if (i == 5) {
                            //File file = new File(Environment.getExternalStorageDirectory(), "Watchlist/"+movieId+".jpg");
                            File file = new File(getApplicationContext().getFilesDir(), "WatchList/" + movieId + ".jpg");
                            if (file.delete()) {
                                Log.d("SHOWMOVIEACTIVITY", "Gelöscht");
                            } else {
                                Log.d("SHOWMOVIEACTIVITY", "Nicht Gelöscht");
                            }
                            listDialog.cancel();
                            Toast.makeText(ShowMovieActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            listDialog.cancel();
                        }

                    }
                });
            }
        });


    }
}
