package de.hs_kl.eae.watchlist;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.net.URL;
        import java.nio.charset.Charset;
        import java.util.Scanner;

        import org.json.JSONException;
        import org.json.JSONObject;

public class Crawler {



    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String searchTerm(String s){

        String key = "1d4133ae";
        s = s.replace(" ", "+");
        return ("http://www.omdbapi.com/?apikey=" + key + "&t=" + s +"&y=&plot=short&r=json");
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


}

