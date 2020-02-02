package com.fikretserkan.unibook.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fikretserkan.unibook.MainActivity;
import com.fikretserkan.unibook.R;
import com.fikretserkan.unibook.Storage;
import com.fikretserkan.unibook.classes.Book;
import com.fikretserkan.unibook.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainRecyclerView extends RecyclerView.Adapter<MainRecyclerView.MyViewHolder> {

    Context context;
    ArrayList<Book> data;
    String username = "";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    public MainRecyclerView(Context context, ArrayList<Book> data) {
        this.context = context;
        this.data = data;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.book_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Book book = data.get(position);

        if (book.getUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
            holder.username.setText(Storage.currentUser.username);
        } else {
            myRef.child(book.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    username = dataSnapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    holder.username.setText(username);
                }
            });
        }

        holder.book_image.setImageBitmap(book.getProfile_image());
        holder.book_price.setText(book.getPrice().toString()+" TL");
        holder.book_title.setText(book.getWriter()+", "+book.getName());
        holder.book_details.setText(book.getDetails());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView book_image, profile_image;
        TextView username, book_price, book_title, book_details;

        MyViewHolder(View viewItem) {
            super(viewItem);

            book_image = viewItem.findViewById(R.id.book_image);
            profile_image = viewItem.findViewById(R.id.profile_image);
            username = viewItem.findViewById(R.id.username);
            book_price = viewItem.findViewById(R.id.book_price);
            book_title = viewItem.findViewById(R.id.book_title);
            book_details = viewItem.findViewById(R.id.book_details);
        }
    }
}