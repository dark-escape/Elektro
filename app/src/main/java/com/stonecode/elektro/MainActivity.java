package com.stonecode.elektro;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MediaPlayer mediaPlayer;
    int state=0;
    ArrayList<AudioInfo> musicData =new ArrayList<>();
    ArrayList<String> musicTitleList=new ArrayList<>();

    ListView songsLV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        songsLV= (ListView) findViewById(R.id.songsLV);

        mediaPlayer=new MediaPlayer();
       mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                fab.setImageResource(android.R.drawable.ic_media_play);
                Log.d(TAG, "onPrepared: ");
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        if(state==0) {
                            fab.setImageResource(android.R.drawable.ic_media_pause);
                            mediaPlayer.start();
                            state=1;
                        }
                        else{
                            fab.setImageResource(android.R.drawable.ic_media_play);
                            state=0;
                            mediaPlayer.pause();
                        }
                    }
                });
            }
        });




        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        int x=1;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title=cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));

                    musicData.add(new AudioInfo(title,path));
                    musicTitleList.add(title);
                    // Add code to get more column here
                    //Log.d(TAG, "onCreate: sjAudio"+data);
                    // Save to your list here

                    if(x==1){
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(this,Uri.parse(path));
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    x++;
                }

            }
        }

        songsLV.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,musicTitleList));
        songsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(getApplicationContext(),Uri.parse(musicData.get(i).uri));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cur.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
