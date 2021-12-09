package com.taylan.hideyourphotos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taylan.hideyourphotos.databinding.ActivityUploadImageBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadImage extends MenuActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> askPermission;
    Uri uri;
    private ActivityUploadImageBinding activityUploadImageBinding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUploadImageBinding = ActivityUploadImageBinding.inflate(getLayoutInflater());
        View view = activityUploadImageBinding.getRoot();
        setContentView(view);
        activityLauncherDefine();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void activityLauncherDefine(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                if(activityResult.getResultCode() == RESULT_OK){
                    Intent resultCode = activityResult.getData();
                    if(resultCode != null){
                        uri = resultCode.getData();
                        activityUploadImageBinding.selectImage.setImageURI(uri);
                    }
                }
            }
        });
        askPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent switchToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(switchToGallery);
                }else{
                    Toast.makeText(UploadImage.this, "Please give permission first.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void selectImageClicked(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "You need to give permission to access galery.", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
             Intent switchIntoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             activityResultLauncher.launch(switchIntoGallery);
        }
    }
    public void uploadButtonClicked(View view){
        UUID uuid = UUID.randomUUID();
        String uniqueImageName = "photos/" + uuid + ".jpg";
        if(uri != null){
            storageReference.child(uniqueImageName).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference fetchImage = firebaseStorage.getReference(uniqueImageName);
                    fetchImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fetchedUrl = uri.toString();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String mailAddress = firebaseUser.getEmail();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("mailAddress", mailAddress);
                            hashMap.put("urlAddress", fetchedUrl);
                            firebaseFirestore.collection("Photos").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                        Intent newIntent = new Intent(UploadImage.this, InnerActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(newIntent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(UploadImage.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(UploadImage.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(UploadImage.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}