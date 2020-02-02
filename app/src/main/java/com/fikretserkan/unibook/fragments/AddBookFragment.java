package com.fikretserkan.unibook.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fikretserkan.unibook.R;
import com.fikretserkan.unibook.Storage;
import com.fikretserkan.unibook.classes.Book;
import com.fikretserkan.unibook.views.MainRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBookFragment extends Fragment {

    EditText url, writer, name, price, description;
    Button add_btn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    Dialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("books");

        url = view.findViewById(R.id.url);
        writer = view.findViewById(R.id.writer);
        name = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        description = view.findViewById(R.id.description);
        add_btn = view.findViewById(R.id.add_btn);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                Book book = new Book(
                        mAuth.getUid(),
                        Storage.mDatabase.getReference("books").push().getKey(),
                        name.getText().toString(),
                        writer.getText().toString(),
                        description.getText().toString(),
                        Double.parseDouble(price.getText().toString()),
                        url.getText().toString()
                );

                new AddToDB().execute(book);
            }
        });

        progressDialog = new Dialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.setCancelable(false);

        if(progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return view;
    }

    public class AddToDB extends AsyncTask<Book, Void, Void> {
        @Override
        protected Void doInBackground(Book... books) {
            myRef.child(mAuth.getCurrentUser().getUid()).child(books[0].getBookId()).setValue(books[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            Intent broascastIntent = new Intent();
            broascastIntent.setAction("GO_BACK");
            getActivity().sendBroadcast(broascastIntent);
        }
    }
}