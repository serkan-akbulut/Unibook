package com.fikretserkan.unibook;

import com.fikretserkan.unibook.classes.Book;
import com.fikretserkan.unibook.classes.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Storage {
    public static ArrayList<Book> jsonBookList = new ArrayList<>();
    public static ArrayList<Book> bookList = new ArrayList<Book>();
    public static ArrayList<Book> mybookList = new ArrayList<Book>();
    public static User currentUser;
    public static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
}
