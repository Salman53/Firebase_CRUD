package com.example.firebase_crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button buttonAdd;
    Spinner spinner;
    DatabaseReference databaseReference;
    ListView listViewArtist;
    List<Artist> artistList;
    Toast toast;

    public static final String Artist_Name = "artistname";
    public static final String Artist_ID = "artistid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("artists");

        editText = findViewById(R.id.id_edit_text);
        buttonAdd = findViewById(R.id.btnAdd);
        spinner = findViewById(R.id.spinner);
        listViewArtist = findViewById(R.id.listView);

        artistList = new ArrayList<>();


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();

            }
        });

      listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Artist artist = artistList.get(position);
              Intent intent = new Intent(getApplicationContext() , AddTrackActivity.class);
              intent.putExtra(Artist_ID , artist.getArtistId());
              intent.putExtra(Artist_Name , artist.getArtistName());
              startActivity(intent);
          }
      });

        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = artistList.get(position);
                showUpdateDialog(artist.getArtistId(), artist.getArtistName());

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                artistList.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren())
                {
                    Artist artist = artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }

                ArtistList adapter = new ArtistList(MainActivity.this , artistList);
                listViewArtist.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addArtist()
    {
        String name = editText.getText().toString();
        String genre = spinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name))
        {

            String id = databaseReference.push().getKey();
            Artist artist = new Artist(id , name , genre);
            databaseReference.child(id).setValue(artist);

            Toast.makeText(MainActivity.this, "Artist Added", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(MainActivity.this, "Please Enter a name", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog(final String artistID, String artistName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = dialogView.findViewById(R.id.btnUpdate);
        final Spinner spinnerGenres = dialogView.findViewById(R.id.spinnerGenres);
        final Button buttondelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Updating Artist " + artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if (TextUtils.isEmpty(name))
                {
                    editTextName.setError("Name Required!");
                    return;
                }
                else
                {
                    updateArtist(artistID, name, genre);
                    alertDialog.dismiss();
                }
            }
        });

        buttondelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistID);
            }
        });

    }

    private void deleteArtist(String artistID) {
        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artists").child(artistID);
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistID);

        drArtist.removeValue();
        drTracks.removeValue();

        Toast.makeText(this, "Artist Deleted Successfully", Toast.LENGTH_SHORT).show();


    }

    private boolean updateArtist(String id, String name, String genre)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);
        Artist artist = new Artist(id,name,genre);
        databaseReference.setValue(artist);
        toast = Toast.makeText(this, "Artist Updated As " +name, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        return true;
    }
}
