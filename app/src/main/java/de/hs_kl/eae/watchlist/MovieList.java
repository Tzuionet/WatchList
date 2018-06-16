package de.hs_kl.eae.watchlist;

/**
 * Created by daniel on 17.12.2016.
 *
 * Diese Klasse gleicht den Datensätzen der SQLite Datenbank.
 * Jeder Datensatz besteht aus title, year, genre, plot, poster, status_view, id.
 * Die Datensätze werden mit Hilfe dieser Klasse in Java-Objekte gewandelt.
 * So können die Inhalte in der ListView ausgegeben werden.
 * Datensätze können auch geändert oder gelöscht werden.
 */

public class MovieList {

    private String title;
    private String year;
    private String genre;
    private String plot;
    private String poster;
    private String status;
    private int id;

    //Konstruktor erzeugt die FilmList Objekte.

    public MovieList(String title, String year, String genre, String plot, String poster, String status, int id) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.plot = plot;
        this.poster = poster;
        this.status = status;
        this.id = id;
    }

    //Übliche Getter und Setter Methoden

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //Ausgabe für die ListView

    @Override
    public String toString() {
        String output = title + " " + year + " " + genre + " " + plot + " " + poster + " " + status + " ID = " + id;
        return output;
    }
}