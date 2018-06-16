package de.hs_kl.eae.watchlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int actualPage;
    //Zeigt die ausgewählten Inhalte
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Erstellt den Adapter, welches ein Fragment für jedes der fünf Sektionen der Activity zurückgibt
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Setzt den Viewpager mit dem Sektions Adapter auf
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Erstellt die Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //Beim Drücken auf dem Floating Action Button wird die MediaInfo_Activity gestartet.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MediaInfo_Activity.class);
                startActivity(intent);

            }
        });

    }

    //Speichert den aktuellen Tab der gerade angezeigt wird, bevor der Focus der Activity verloren geht.
    @Override
    public void onPause() {
        super.onPause();
        actualPage = mViewPager.getCurrentItem();
    }

    //Ist die Activity wieder zurück im Focus wird der zuletzt angezeigte Tab angezeigt und upgedatet.
    @Override
    public void onResume() {
        super.onResume();
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(actualPage);
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);
    }


    //Ein Placeholder Fragment enthält die ListViews.
    public static class PlaceholderFragment extends Fragment {

        //Section Number sind hier in diesem Fall die verschiedenen WatchStatuse
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView list;
        private MovieListDataSource dataSource;
        private MyAdapter myAdapter;

        public PlaceholderFragment() {
        }
        //Gibt eine neue Instanz von diesem Fragment für die gegebene Sections Nummer zurück
        public static MainActivity.PlaceholderFragment newInstance(int sectionNumber) {
            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        //Erstellt eine View mit den für die Sektions Nummer zugehörigem Inhalt
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            list = (ListView) rootView.findViewById(R.id.MovieListView);
            dataSource = new MovieListDataSource(rootView.getContext());
            dataSource.open();
            Context ctx = rootView.getContext();
            int itemLayout = R.layout.simple_list_image_item_2;
            //Lädt aus der Datenbank die für die Sektion zugehörigen Medien
            Cursor cursor = dataSource.getAllMoviesCursor(getArguments().getInt(ARG_SECTION_NUMBER));
            String[] from = new String[]{MovieListDbHelper.COLUMN_POSTER, MovieListDbHelper.COLUMN_TITLE, MovieListDbHelper.COLUMN_YEAR};
            int[] to = new int[]{R.id.POSTER, R.id.MOVIETITEL, R.id.MOVIEYEAR};
            myAdapter = new MyAdapter(ctx, itemLayout, cursor, from, to, 0);
            //Setzt die ListView mit den Medien auf
            list.setAdapter(myAdapter);
            //Beim Klick auf ein Medium der Liste wird eine neue Acticity gestartet mit den Informationen über das Medium
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Log.d("+++MAIN_ITEM i+++", Integer.toString(i));
                    //Log.d("+++MAIN_ITEM l+++", Long.toString(l));
                    Object listItem = list.getItemAtPosition(i);
                    Cursor cur = (Cursor) myAdapter.getItem(i);
                    cur.moveToPosition(i);
                    String id = cur.getString(cur.getColumnIndexOrThrow(MovieListDbHelper.COLUMN_ID));
                    //Log.d("+++MAIN_ITEM++++", id);
                    Intent intent = new Intent(getContext(), ShowMovieActivity.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);

                }
            });
            dataSource.close();


            return rootView;
        }


    }


    //Gibt ein Fragment zu der zugehörigen Sektion/Tab/Seite zurück
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        //getItem wird aufgerufen um eine neue Instanz eines Fragments zur zugehörigen Seite zu erstellen.
        @Override
        public Fragment getItem(int position) {

            //Gibt ein Placeholderfragment zurück
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Currently Watching";
                case 1:
                    return "Plan to Watch";
                case 2:
                    return "On Pause";
                case 3:
                    return "Completed";
                case 4:
                    return "Dropped";
            }
            return null;
        }
    }
}


