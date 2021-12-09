package com.taylan.hideyourphotos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.taylan.hideyourphotos.databinding.ActivityInnerBinding;
import com.taylan.hideyourphotos.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Map;

public class InnerActivity extends AppCompatActivity {
    private ActivityInnerBinding activityInnerBinding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<Image> arrayList;
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInnerBinding = ActivityInnerBinding.inflate(getLayoutInflater());
        View view = activityInnerBinding.getRoot();
        setContentView(view);
        arrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fetchImage();
        activityInnerBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageAdapter(arrayList);
        activityInnerBinding.recyclerView.setAdapter(imageAdapter);
    }
    private void fetchImage(){
        firebaseFirestore.collection("Photos").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(InnerActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        Map<String, Object> image = documentSnapshot.getData();
                        String mailAddress = (String) image.get("mailAddress");
                        String urlAddress = (String) image.get("urlAddress");
                        Image imageObject = new Image(mailAddress, urlAddress);
                        arrayList.add(imageObject);
                    }
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}