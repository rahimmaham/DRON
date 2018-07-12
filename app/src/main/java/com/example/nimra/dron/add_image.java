package com.example.nimra.dron;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class add_image extends AppCompatActivity {

    private EditText names;
    private static int PICK_IMAGE = 100;
    private ImageView newsimg;
    private Uri mImageUri;
    private Button add;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            newsimg.setImageURI(mImageUri);

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        newsimg = (ImageView) findViewById(R.id.newsimg);
        newsimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("photos");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        names =(EditText) findViewById(R.id.name);
        newsimg = findViewById(R.id.newsimg);
        add=findViewById(R.id.add);
        progressDialog = new ProgressDialog(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        final String name =  names.getText().toString().trim();
        if(mImageUri != null){

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));


            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            Toast.makeText(add_image.this, "Image Added", Toast.LENGTH_SHORT).show();


                            Upload news = new Upload(name,taskSnapshot.getDownloadUrl().toString());
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child("photos").child(uploadId).setValue(news);
                            finish();
                            final Intent i = new Intent(getApplicationContext(), gallery.class);
                            startActivity(i);

                            progressDialog.dismiss();




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(add_image.this, e.getMessage(), Toast.LENGTH_LONG).show();



                        }
                    });
        }
        else{
            Toast.makeText(this,"Please Select Any Image ", Toast.LENGTH_LONG).show();
        }

    }
}
