package com.example.firebase_crud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTrackActivity extends AppCompatActivity {

    TextView textViewartistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    ListView listViewTracks;
    Button buttonAddTrack;

    DatabaseReference databaseTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        textViewartistName = findViewById(R.id.textViewArtistTrack);
        editTextTrackName = findViewById(R.id.id_edit_textTrackName);
        seekBarRating = findViewById(R.id.SeekBarRating);
        listViewTracks = findViewById(R.id.listViewTracks);
        buttonAddTrack = findViewById(R.id.btnAddTrack);

        Intent intent = getIntent();

        String id = intent.getStringExtra(MainActivity.Artist_ID);
        String name = intent.getStringExtra(MainActivity.Artist_Name);

        textViewartistName.setText(name);

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveTrack();
            }
        });

    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString();
        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName))
        {
            String id = databaseTracks.push().getKey();
            Track track = new Track(id , trackName , rating );
            databaseTracks.child(id).setValue(track);

            Toast.makeText(this, "Saved Track Sucessfully", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
        }

    }
}
