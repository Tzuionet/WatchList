package de.hs_kl.eae.watchlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Eigens erstellter Adapter
public class MyAdapter extends CursorAdapter {

    LayoutInflater myLayoutInflater;
    int itemLayout;
    String[] from;
    int[] to;

    //Konstruktor füllt die Variablen
    public MyAdapter(Context ctx, int itemLayout, Cursor c, String[] from, int[] to, int flags) {
        super(ctx, c, flags);
        myLayoutInflater = LayoutInflater.from(ctx);
        this.itemLayout = itemLayout;
        this.from = from;
        this.to = to;

    }

    //Erzeugt neues view Element mit der eigenen AdapterView Ressource
    @Override
    public View newView(Context ctx, Cursor c, ViewGroup viewGroup) {
        View v = myLayoutInflater.inflate(itemLayout, viewGroup, false);
        return v;
    }

    //Stellt für jedes Listenelement die Verbindung zwischen den View Elementen und der Datenquelle her
    @Override
    public void bindView(View v, Context ctx, Cursor c) {
        String image = c.getString(c.getColumnIndexOrThrow(from[0]));
        ImageView imageView = (ImageView) v.findViewById(to[0]);

        if (!image.equals("noposter")){
            imageView.setImageDrawable(Drawable.createFromPath(image));
        }else{
            imageView.setImageResource(R.drawable.noposter);
        }


        String text1 = c.getString(c.getColumnIndexOrThrow(from[1]));
        TextView textView1 = (TextView) v.findViewById(to[1]);
        textView1.setText(text1);

        String text2 = c.getString(c.getColumnIndexOrThrow(from[2]));
        TextView textView2 = (TextView) v.findViewById(to[2]);
        textView2.setText(text2);
    }
}
