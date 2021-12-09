package com.taylan.hideyourphotos;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class MenuActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.right_upper_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        firebaseAuth = FirebaseAuth.getInstance();
        if(item.getItemId() == R.id.signOut){
            Paper.book().destroy();
            firebaseAuth.signOut();
            Intent signOutIntent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(signOutIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
