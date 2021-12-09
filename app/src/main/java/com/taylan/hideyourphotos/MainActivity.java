package com.taylan.hideyourphotos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taylan.hideyourphotos.databinding.ActivityMainBinding;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private FirebaseAuth firebaseAuth;
    private String eMailAddress;
    private String password;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        Paper.init(this);
        String eMail = Paper.book().read(KeepSignStatus.mailAddress);
        String passw = Paper.book().read(KeepSignStatus.password);
        if( !("".equals(eMail)) && !("".equals(passw)) ){
            if( !TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(passw)){
                Intent intentAlreadyAuthenticated = new Intent(MainActivity.this, UploadImage.class);
                startActivity(intentAlreadyAuthenticated);
                finish();
            }
        }
    }
    public void signInClicked(View view){
        eMailAddress = activityMainBinding.eMailText.getText().toString();
        password = activityMainBinding.passwordText.getText().toString();
        if(checkBox.isChecked()){
            Paper.book().write(KeepSignStatus.mailAddress, eMailAddress);
            Paper.book().write(KeepSignStatus.password, password);
        }
        if( "".equals(eMailAddress) || "".equals(password) ){
            Toast.makeText(this, "E-mail or password can not be empty.", Toast.LENGTH_LONG).show();
        }else{
            firebaseAuth.signInWithEmailAndPassword(eMailAddress, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intentSignIn = new Intent(MainActivity.this, UploadImage.class);
                startActivity(intentSignIn);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        }
    }
    public void signUpClicked(View view){
        eMailAddress = activityMainBinding.eMailText.getText().toString();
        password = activityMainBinding.passwordText.getText().toString();
        if(checkBox.isChecked()){
            Paper.book().write(KeepSignStatus.mailAddress, eMailAddress);
            Paper.book().write(KeepSignStatus.password, password);
        }
        if( "".equals(eMailAddress) || "".equals(password) ){
            Toast.makeText(this, "E-mail or password can not be empty.", Toast.LENGTH_LONG).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(eMailAddress,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intentSignUp = new Intent(MainActivity.this,UploadImage.class);
                    startActivity(intentSignUp);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}