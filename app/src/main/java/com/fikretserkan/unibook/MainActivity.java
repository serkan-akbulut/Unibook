package com.fikretserkan.unibook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fikretserkan.unibook.classes.Book;
import com.fikretserkan.unibook.fragments.AddBookFragment;
import com.fikretserkan.unibook.fragments.HomeFragment;
import com.fikretserkan.unibook.fragments.ProfileFragment;
import com.fikretserkan.unibook.services.ReadJsonDataService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    IntentFilter mIntentFilter;
    IntentFilter mIntentFilter2;

    private Menu menu;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("books");

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("BOOKS_JSON_PARSE_ACTION");

        mIntentFilter2 = new IntentFilter();
        mIntentFilter2.addAction("GO_BACK");

        registerReceiver(mIntentReceiver, mIntentFilter);
        registerReceiver(mIntentReceiver, mIntentFilter2);

        Intent intent = new Intent(this, ReadJsonDataService.class);
        intent.putExtra("filename","books.json");
        startService(intent);




        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //This is the if statement to keep the selected fragment when rotating the device.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        DatabaseReference ref = Storage.mDatabase.getReference("books");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new GetPosts().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class GetPosts extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            for(DataSnapshot item_snapshot:dataSnapshots[0].getChildren()) {
                for(DataSnapshot child_snapshot:item_snapshot.getChildren()) {

                    Bitmap book_photo = null;

                    try {
                        book_photo = Picasso.get().load(child_snapshot.child("url").getValue().toString()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Book book = new Book(
                            child_snapshot.child("userId").getValue().toString(),
                            child_snapshot.child("bookId").getValue().toString(),
                            child_snapshot.child("name").getValue().toString(),
                            child_snapshot.child("writer").getValue().toString(),
                            child_snapshot.child("details").getValue().toString(),
                            Double.parseDouble(child_snapshot.child("price").getValue().toString()),
                            book_photo);

                    if (book.getUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid().toString())) {
                        Storage.mybookList.add(book);
                        Storage.bookList.add(book);
                    } else {
                        Storage.bookList.add(book);
                    }
                }
            }

            Intent broascastIntent = new Intent();
            broascastIntent.setAction("UPDATE_RV");
            getBaseContext().sendBroadcast(broascastIntent);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().toString().equalsIgnoreCase("BOOKS_JSON_PARSE_ACTION")) {
                for(int i = 0; i< Storage.jsonBookList.size(); i++) {
                    Book book = Storage.jsonBookList.get(i);
                    myRef.child(mAuth.getCurrentUser().getUid()).child(book.getBookId()).setValue(book);
                }
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                changeMenuItemCheckedStateColor(bottomNav, "#FFFFFF", "#303550");
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            changeMenuItemCheckedStateColor(bottomNav, "#FFFFFF", "#303550");
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            changeMenuItemCheckedStateColor(bottomNav, "#FFFFFF", "#303550");
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    private void changeMenuItemCheckedStateColor(BottomNavigationView bottomNavigationView, String checkedColorHex, String uncheckedColorHex) {
        int checkedColor = Color.parseColor(checkedColorHex);
        int uncheckedColor = Color.parseColor(uncheckedColorHex);

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] {android.R.attr.state_checked}, // checked

        };

        int[] colors = new int[] {
                uncheckedColor,
                checkedColor
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        bottomNavigationView.setItemTextColor(colorStateList);
        bottomNavigationView.setItemIconTintList(colorStateList);

    }

    //Add Create Button to the Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        this.menu = menu;
        return true;
    }

    //Catch the Add Button Click Action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_news) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddBookFragment()).commit();
            changeMenuItemCheckedStateColor(bottomNav, "#303550", "#303550");
        }
        return super.onOptionsItemSelected(item);
    }
}
