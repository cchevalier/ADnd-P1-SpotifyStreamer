package net.cchevalier.adnd.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    EditText artist;
    ListView listArtist;
    ArrayAdapter<String> mArtistAdapter;


    public ArtistFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Artist Search
        artist = (EditText) rootView.findViewById(R.id.artist_name);

        // Launching search
        artist.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_DONE){

                    String search = artist.getText().toString();
                    SearchSpotifyForArtist task = new SearchSpotifyForArtist();
                    task.execute(search);

                    //handled = true;
                }
                return handled;
            }
        });

        // ListArtist Handling
        listArtist = (ListView) rootView.findViewById(R.id.listview_artist);
        List<String> emptyList = new ArrayList<>();
        mArtistAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                emptyList
        );
        listArtist.setAdapter(mArtistAdapter);

        // Click handling on item artist
        listArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedArtist = mArtistAdapter.getItem(position);

                // Toast version
                Toast.makeText(getActivity(), selectedArtist, Toast.LENGTH_LONG).show();

                // Start TracksActivity
                Intent intent = new Intent(getActivity(), TracksActivity.class).
                        putExtra(Intent.EXTRA_TEXT, selectedArtist);
                startActivity(intent);
            }
        });

        return rootView;
    }


    private void displayDummyListArtist(String search) {

        mArtistAdapter.clear();

        int count = 10;
        for (Integer i = 0; i < count; i++) {
            mArtistAdapter.add(search + " " + i.toString());
        }
    }


    public class SearchSpotifyForArtist extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected List<Artist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // A basic search on artists named params[0]
            ArtistsPager resultsArtists = service.searchArtists(params[0]);
            List<Artist> artists = resultsArtists.artists.items;

            // logcat:
            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                Log.i("SAPI", i + " " + artist.name);
                Log.i("SAPI", i + "   id: " + artist.id);
                Log.i("SAPI", i + "  uri: " + artist.uri);
            }

            // logcat: Top Ten Tracks for most famous artist named on previous search
            TracksPager resultsTracks = service.searchTracks(artists.get(0).name);
            List<Track> tracks = resultsTracks.tracks.items;
            for (int i = 0; i < 10; i++) {
                Track track = tracks.get(i);
                Log.i("SAPI", i + " - " + track.name );
            }
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            //super.onPostExecute(aVoid);

            if (artists != null) {
                mArtistAdapter.clear();
                for (int i = 0; i < artists.size(); i++) {
                    mArtistAdapter.add(artists.get(i).name);
                }
            }
        }


    }
}
