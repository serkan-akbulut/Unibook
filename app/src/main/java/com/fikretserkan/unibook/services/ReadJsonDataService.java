package com.fikretserkan.unibook.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fikretserkan.unibook.Storage;
import com.fikretserkan.unibook.classes.Book;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReadJsonDataService extends IntentService {

    JSONArray books;
    FirebaseAuth mAuth;

    private static final String TAG_ARRAY_NAME = "books";
    private static final String TAG_NAME = "name";
    private static final String TAG_WRITER = "writer";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_PRICE = "price";
    private static final String TAG_URL = "url";

    public ReadJsonDataService(){
        super("MyBooksService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mAuth = FirebaseAuth.getInstance();

        String filename = intent.getStringExtra("filename");
        String jsonfileContent = loadFileFromAsset(filename);

        Log.d("Response: ", "> " + jsonfileContent);

        if (jsonfileContent != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonfileContent);

                // Getting JSON Array node
                books = jsonObj.getJSONArray(TAG_ARRAY_NAME);

                // looping through all Contacts
                for (int i = 0; i < books.length(); i++) {
                    JSONObject book = books.getJSONObject(i);

                    String userId = mAuth.getUid();
                    String bookId = Storage.mDatabase.getReference("books").push().getKey();
                    String name = book.getString(TAG_NAME);
                    String writer = book.getString(TAG_WRITER);
                    String details = book.getString(TAG_DETAILS);
                    Double price = book.getDouble(TAG_PRICE);
                    String url = book.getString(TAG_URL);

                    Book b = new Book(userId, bookId, name, writer, details, price, url);
                    Storage.jsonBookList.add(b);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent broascastIntent = new Intent();
        broascastIntent.setAction("BOOKS_JSON_PARSE_ACTION");

        getBaseContext().sendBroadcast(broascastIntent);
    }

    private String loadFileFromAsset(String fileName) {
        String jsonfileContent = null;
        try {

            InputStream is = getBaseContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            jsonfileContent = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonfileContent;
    }
}
