package com.test.firemomo.firemomo.MomoGen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.firemomo.firemomo.ImageEdit;
import com.test.firemomo.firemomo.R;
import com.test.firemomo.firemomo.util.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MomoCam extends AppCompatActivity {

    private static final String TAG = "MomoCam";

    private static final int RC_TAKE_PICTURE = 100;
    private static final int RC_EDIT_PICTURE = 101;

    private static final String KEY_TEMP_URI = "key_temp_uri";

    private Button takePictureButton, uploadPictureButton;
    private ImageView picTaken;
    private Uri filePath;
    private Uri file;

    private Uri tempImageUri;

    // Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.momo_cam);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        takePictureButton = (Button) findViewById(R.id.button_image);
        uploadPictureButton = (Button) findViewById(R.id.button_upload);
        picTaken = (ImageView) findViewById(R.id.captured_photo);

        if (savedInstanceState != null) {
            tempImageUri = savedInstanceState.getParcelable(KEY_TEMP_URI);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //   takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        takePictureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        try {
                            photoFile = Files.createImageFile();
                        } catch (IOException ex) {
                            // TODO
                        }

                        if (photoFile != null) {
                            tempImageUri = FileProvider.getUriForFile(MomoCam.this,
                                    "com.test.firemomo.firemomo.provider",
                                    photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
                            startActivityForResult(intent, RC_TAKE_PICTURE);
                        } else {
                            Log.d(TAG, "null photoUri");
                        }
                    }
                });
        uploadPictureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (filePath != null) {
                            uploadImage();
                        } else {
                            Toast.makeText(getApplicationContext(), "No file attached", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_TEMP_URI, tempImageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_TAKE_PICTURE && resultCode == RESULT_OK) {

            // TODO: Support for pictures chosen, not just camera
            Intent intent = new Intent(this, ImageEdit.class);
            intent.putExtra(ImageEdit.EXTRA_URI, tempImageUri);
            startActivityForResult(intent, RC_EDIT_PICTURE);
        }

        if (requestCode == RC_EDIT_PICTURE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(ImageEdit.EXTRA_URI);

            try {
                InputStream is = getContentResolver().openInputStream(uri);
                picTaken.setImageBitmap(BitmapFactory.decodeStream(is));

                filePath = uri;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFound", e);
            }
        }
    }


    private void uploadImage() {
        if (filePath == null) {
            filePath = getImageUri(this, bitmap);
        }

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            storageReference.getDownloadUrl();
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(
                                            MomoCam.this,
                                            "Uploaded " + storageReference.getDownloadUrl(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MomoCam.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress =
                                            (100.0
                                                    * taskSnapshot.getBytesTransferred()
                                                    / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path =
                MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "nombre", null);
        return Uri.parse(path);
    }
}
