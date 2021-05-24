package com.example.readsongsfromsdcard;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Player extends AppCompatActivity {
    ArrayList<File> mySongs;
    TextView txtNameSong, txtSecondsPresent, txtSecondsEnd;
    static MediaPlayer mediaPlayer;
    ImageButton btnPrev, btnPlay, btnNext, btnReplay, btnFF, btnRew;
    int position;
    SeekBar seekBarkSong;
    Uri uri;
    boolean isReplayAll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");

        btnNext = findViewById(R.id.btnnext);
        btnPlay = findViewById(R.id.btnplay);
        btnPrev = findViewById(R.id.btnprevious);
        txtNameSong = findViewById(R.id.txtNameSong);
        txtSecondsPresent = findViewById(R.id.secondsPresent);
        txtSecondsEnd = findViewById(R.id.secondsEnd);
        seekBarkSong = findViewById(R.id.seekbarSong);
        btnReplay = findViewById(R.id.btnreplay);
        btnRew = findViewById(R.id.btnrew);
        btnFF = findViewById(R.id.btnff);

        //when playing the a song, click the b song, kill progress the a song, play the b song
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songslist");
        position = bundle.getInt("pos", 0);
        uri = Uri.parse(mySongs.get(position).toString());
        txtNameSong.setText(mySongs.get(position).getName().replace(".mp3", ""));

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        setSecondsEnd();
        updateSecondsPresent();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position + 1) % mySongs.size();

                Uri uri = Uri.parse(mySongs.get(position).toString());
                txtNameSong.clearFocus();
                txtNameSong.setText(mySongs.get(position).getName().replace(".mp3", ""));

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                setSecondsEnd();
                updateSecondsPresent();

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;

                Uri uri = Uri.parse(mySongs.get(position).toString());
                txtNameSong.clearFocus();
                txtNameSong.setText(mySongs.get(position).getName().replace(".mp3", ""));

                //txtNameSong.setText(NAMESONGS[position - 1]);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                setSecondsEnd();
                updateSecondsPresent();
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtNameSong.clearFocus();
                txtNameSong.setText(mySongs.get(position).getName().replace(".mp3", ""));

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);

                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
                setSecondsEnd();
                updateSecondsPresent();

            }
        });

        btnFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        btnRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        seekBarkSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBarkSong.getProgress());


            }
        });

        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isReplayAll == true) {
                    isReplayAll = false;
                    btnReplay.setImageResource(R.drawable.repeat_one);
                } else {
                    isReplayAll = true;
                    btnReplay.setImageResource(R.drawable.repeat);
                }

            }
        });

    }

    private void updateSecondsPresent() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                txtSecondsPresent.setText(formatTime.format(mediaPlayer.getCurrentPosition()));
                //update progress time of seekbar
                seekBarkSong.setProgress(mediaPlayer.getCurrentPosition());

                //when the song finishes -> next song
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (isReplayAll == true) {
                            position = (position + 1) % mySongs.size();
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                            }
                            txtNameSong.clearFocus();
                            txtNameSong.setText(mySongs.get(position).getName().replace(".mp3", ""));

                            //txtNameSong.setText(NAMESONGS[position - 1]);
                        }

                        Uri uri = Uri.parse(mySongs.get(position).toString());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                        mediaPlayer.start();
                        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        setSecondsEnd();
                        updateSecondsPresent();

                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);


    }

    private void setSecondsEnd() {
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        txtSecondsEnd.setText(formatTime.format(mediaPlayer.getDuration()));
        seekBarkSong.setMax(mediaPlayer.getDuration());

    }

}