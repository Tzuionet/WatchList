package de.hs_kl.eae.watchlist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

//Wird aufgerufen wenn der FloatActionButton gedrückt wurde, hier kann man nach Filmen oder Serien suchen
public class MediaInfo_Activity extends AppCompatActivity {
    private TextView title;
    private TextView story;
    private EditText searchTerm;
    private ImageView poster;
    private Button searchButton;
    private Button btnAdd;
    private Bitmap bmp;
    private ScrollView scrollView;
    private TextView released;
    private TextView genre;
    private MovieListDataSource dataSource;
    private String movie[] = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MediaInfo:", "onCreate wurde aufgerufen");
        setContentView(R.layout.activity_media_info_);

        //Zeigt die Tastatur an, wenn die Activity gestartet wurd
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Initialisiert die View-Elmente
        title = (TextView) findViewById(R.id.textView_Titel);
        story = (TextView) findViewById(R.id.textView_Story);
        poster = (ImageView) findViewById(R.id.imageView_poster);
        searchTerm = (EditText) findViewById(R.id.edt_searchTerm);
        searchButton = (Button) findViewById(R.id.btn_Suche);
        scrollView = (ScrollView) findViewById(R.id.Scroll);
        btnAdd = (Button) findViewById(R.id.btn_add);
        scrollView.setVisibility(View.GONE);
        released = (TextView) findViewById(R.id.textView_Released);
        genre = (TextView) findViewById(R.id.textView_Genre);

        //Erstellt die Datenquelle
        dataSource = new MovieListDataSource(this);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //OnClickListener wird aufgerufen, wenn auf den "Suche" Button gedrückt wurde
            public void onClick(View view) {
                Log.d("MediaInfo:", "Suche wurde gedrueckt");
                //Überprüft ob im Suchfeld etwas eingetragen wurde
                if (searchTerm.getText().toString().equals("")) {
                    Toast.makeText(MediaInfo_Activity.this, "Please insert a title", Toast.LENGTH_LONG).show();
                } else {
                    //Schließt die Softkey Tastatur und führ einen Asynchronen Task aus der nach dem Medium sucht und die Informationen anzeigt
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchTerm.getWindowToken(), 0);
                    MyAsyncTask omdbLoader = new MyAsyncTask();
                    omdbLoader.execute(searchTerm.getText().toString());
                    Log.d("MediaInfo:", "Asynchroner Task zum Suchen des Filmes wird ausgefuehrt");
                }
            }
        });

        searchTerm.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                Log.d("MediaInfo:", "Suche wurde gedrueckt");
                //Überprüft ob im Suchfeld etwas eingetragen wurde
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    if (searchTerm.getText().toString().equals("")) {
                        Toast.makeText(MediaInfo_Activity.this, "Please insert a title", Toast.LENGTH_LONG).show();
                    } else {
                        //Führt einen Asynchronen Task aus, der nach dem Medium sucht und die Informationen anzeigt
                        Log.d("onKeyListener", "Enter wurde gedrückt");
                        MyAsyncTask omdbLoader = new MyAsyncTask();
                        omdbLoader.execute(searchTerm.getText().toString());
                        Log.d("MediaInfo:", "Asynchroner Task zum Suchen des Filmes wird ausgefuehrt");
                        return true;
                    }
                }
                return false;
            }
        });

        //Wurde der hinzufügen Button gedrückt und ein WatchStatus ausgewählt, wird er in die Datenbank eingetragen
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSource.open();
                Log.d("MediaInfo:", "Hinzufuegen Button wurde gedrückt");
                Log.d("MediaInfo:", movie[1]);


                //Eintrag in die Datnbank, wenn der Eintrag noch nicht existiert
                if (!dataSource.checkIsMovieInDBorNot(movie[1])) {
                    //Log.d("MediaInfo:", "Hat Permission");
                    //Baut einen ListDialog auf mit den verschiedenen WatchStatusen
                    String[] val = {"Currently Watching", "Plan to Watch", "On Pause", "Completed", "Dropped"};
                    final Dialog listDialog;
                    listDialog = new Dialog(MediaInfo_Activity.this);
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View v = li.inflate(R.layout.status_view, null, false);
                    listDialog.setContentView(v);
                    listDialog.setCancelable(true);
                    listDialog.setCanceledOnTouchOutside(true);
                    ListView list1 = (ListView) listDialog.findViewById(R.id.statuslistview);
                    list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        //Bei einem Klick auf ein Item im ListDialog Fenster
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Log.d("+++ITEMDIALOG i+++", Integer.toString(i));
                            //Log.d("+++ITEMDIALOG l+++", Long.toString(l));

                            //Schließe ListDialog
                            listDialog.cancel();

                            //Ruft einen DownloadAsyncTask aus, der die Informationen vom Medium in die Datenbank speichert
                            DownloadAsyncTask downloadTask = new DownloadAsyncTask();
                            downloadTask.execute(i + 1);


                        }
                    });
                    list1.setAdapter(new ArrayAdapter<String>(MediaInfo_Activity.this, android.R.layout.simple_list_item_1, val));
                    //Nach dem erzeugen des ListDialog wird es angezeigt
                    listDialog.show();

                } else {
                    //Eintrag vom Medium existiert bereits
                    Toast.makeText(MediaInfo_Activity.this, "It is already in your list", Toast.LENGTH_LONG).show();
                }


            }

        });


    }

    //Der Asynchrone Task holt sich mit Hilfe der Omdb API die gefordertn Informationen des Filmes/Serie
    class MyAsyncTask extends AsyncTask<String, Void, String[]> {

        private ProgressDialog progressDialog = new ProgressDialog(MediaInfo_Activity.this);
        InputStream inputStream = null;
        String result = "";

        //Bevor der Task ausgeführt wird, wird ein ProgressDialog angezeigt, welches Aktionen vom User verhindert, außer er drückt die Zurück-Taste
        @Override
        protected void onPreExecute() {


            Log.d("MyAsyncTask", "onPreExcecute");
            progressDialog.setMessage("Searching...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }

        //Haupttask der im Hintergrund ausgeführt wird und die Information zum Medium lädt
        @Override
        protected String[] doInBackground(String... params) {
            Log.d("MyAsyncTask", "doInBackground");

            //Lädt die Informationen mit Hilfe der Omdb API
            try {
                JSONObject json = Crawler.readJsonFromUrl(Crawler.searchTerm(params[0]));
                movie[0] = json.get("Response").toString();
                movie[1] = json.get("Title").toString();
                movie[2] = json.get("Released").toString();
                movie[3] = json.get("Genre").toString();
                movie[4] = json.get("Plot").toString();
                movie[5] = json.get("Poster").toString();
                movie[6] = "1";

                Log.d("MyAsyncTask Response", movie[0]);
                Log.d("MyAsyncTask Titel", movie[1]);
                Log.d("MyAsyncTask Released", movie[2]);
                Log.d("MyAsyncTask Genre", movie[3]);
                Log.d("MyAsyncTask Plot", movie[4]);
                Log.d("MyAsyncTask Poster", movie[5]);
                Log.d("MyAsyncTask WatchStatus", movie[6]);

                //Testet ob ein Poster zum Medium existiert
                if (movie[5].equals("N/A")) {
                    movie[5] = null;
                }

                URL url = null;
                try {
                    url = new URL(movie[5]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                bmp = null;
                //Speichert das Poster in eine Bitmap
                if (movie[5] != null) {
                    try {
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movie;

        }

        //Nach dem doInBackground() wird die View aufgebaut, welches die Informationen zum Medium anzeigt. Danach wird der ProgressDialog geschlossen
        @Override
        protected void onPostExecute(String[] movie) {
            Log.d("onPostExcecute", " onPostExccute");
            //Wenn kein Film gefunden wurde wird "Nicht gefunden" angezeigt
            if (movie[0].equals("True")) {
                //Baut die View auf, wenn ein Medium gefunden wurde
                title.setVisibility(View.VISIBLE);
                story.setVisibility(View.VISIBLE);
                poster.setVisibility(View.VISIBLE);
                genre.setVisibility(View.VISIBLE);
                released.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
                title.setText(movie[1]);
                released.setText("Released: " + movie[2]);
                genre.setText("Genre: " + movie[3]);
                story.setText(movie[4]);
                if (bmp != null) {
                    poster.setImageBitmap(bmp);
                } else {
                    poster.setImageResource(R.drawable.noposter);
                }
                scrollView.setVisibility(View.VISIBLE);
            } else {
                //Wenn kein Medium gefunden wurde
                Log.d("MyAsyncTask", "Kein Medium gefunden");
                scrollView.setVisibility(View.VISIBLE);
                title.setText("Nicht gefunden");
                title.setVisibility(View.VISIBLE);
                story.setVisibility(View.GONE);
                poster.setVisibility(View.GONE);
                btnAdd.setVisibility(View.GONE);
                genre.setVisibility(View.GONE);
                released.setVisibility(View.GONE);
            }
            //ProgressDialog wird gesclossen
            progressDialog.cancel();
        }
    }

    //Der Asynchrone Download Task speichert die Informationen des
    // Mediums in die Datenbank und downloaded und speichert das Poster ins Gerät
    class DownloadAsyncTask extends AsyncTask<Integer, Integer, String> {
        ProgressDialog progressDialog;

        //Zeigt vor dem Ausführen des Hauttasks einen ProgressDialog
        @Override
        protected void onPreExecute() {
            Log.d("DownloadAsyncTask:", "ProgressDialog wird erstellt");
            //Erzeuge und zeige ProgessDialog
            progressDialog = new ProgressDialog(MediaInfo_Activity.this);
            progressDialog.setTitle("Will be added");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        //Speichert die Informationen in die Datenbank und downloaded das Poster
        @Override
        protected String doInBackground(Integer... status) {
            movie[6] = Integer.toString(status[0]);
            Log.d("DownloadAsyncTask:", "doInBackground wurde aufgerufen");
            Log.d("DownloadAsyncTask", "Response " + movie[0]);
            Log.d("DownloadAsyncTask", "Titel " + movie[1]);
            Log.d("DownloadAsyncTask", "Released " + movie[2]);
            Log.d("DownloadAsyncTask", "Genre " + movie[3]);
            Log.d("DownloadAsyncTask", "Plot " + movie[4]);
            Log.d("DownloadAsyncTask", "Poster alt:" + movie[5]);
            Log.d("DownloadAsyncTask", "WatchStatus " + movie[6]);

            dataSource.open();

            if (movie[5] == null) {
                movie[5] = "noposter";
            }
            int id = 0;
            dataSource.createMovieList(movie[1], movie[2], movie[3], movie[4], movie[5], movie[6]);
            id = dataSource.getID(movie[1]);


            Log.d("DownloadAsyncTask:", "Daten wurden in die Datenbank eingetragen");

            //Downloaded Poster vom Medium
            if (movie[5] != "noposter") {
                int file_length = 0;
                try {
                    URL url = new URL(movie[5]);
                    try {
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        file_length = urlConnection.getContentLength();
                        // File newFolder = new File(Environment.getExternalStorageDirectory(), "WatchList");
                        File newFolder = new File(getApplicationContext().getFilesDir(), "WatchList");
                        //Überprüft ob ein WatchList Ordner bereits existiert
                        if (!newFolder.exists()) {
                            Log.d("DownloadAsyncTask:", "Folder existriert nicht und wird erstellt");
                            newFolder.mkdir();
                        } else {
                            Log.d("DownloadAsyncTask:", "Folder existiert");
                        }
                        File inputFile = new File(newFolder, id + ".jpg");
                        InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                        byte[] data = new byte[1024];
                        int total = 0;
                        int count = 0;
                        OutputStream outputStream = new FileOutputStream(inputFile);
                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            outputStream.write(data, 0, count);
                            //Updatet den ProgressDialog Fortschrifftsbalken
                            int progress = total * 100 / file_length;
                            publishProgress(progress);
                        }
                        inputStream.close();
                        outputStream.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                //Updatet Posterpfad in der Datenbank, weil davor stand nur ein Link
                //movie[5] = Environment.getExternalStorageDirectory() + "/WatchList/" + id + ".jpg";
                movie[5] = getApplicationContext().getFilesDir() + "/WatchList/" + id + ".jpg";
                Log.d("DownloadAsyncTask", "Poster neu " + movie[5]);
                dataSource.updatePoster(id, movie[5]);
            }


            dataSource.close();

            return "Added";
        }

        //wird bei publishProgress() aufgerufen und updatet den Fortschrittsbalken vom ProgressDialog
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }


        //Nachdem alles eingetragen wurde wird der ProgressDialog geschlossen und eine "Hinzugefügt" Nachricht angezigt
        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }


}
