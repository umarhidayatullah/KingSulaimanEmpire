package com.example.indonesiatangguhindonesiatumbuh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.indonesiatangguhindonesiatumbuh.fileprovider";
    private String tempPhoto;
    private Bitmap resultBitmap;

    /*@BindView(R.id.image_view) ImageView imageView;
    @BindView(R.id.emojify_button) Button emojifyButton;
    @BindView(R.id.share_button) FloatingActionButton shareBtn;
    @BindView(R.id.save_button) FloatingActionButton saveBtn;
    @BindView(R.id.clear_button) FloatingActionButton clearBtn;
    @BindView(R.id.title_text_view) TextView titleText;*/

    private ImageView imageView;
    private Button emojifyButton;
    private FloatingActionButton shareBtn, saveBtn, clearBtn;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       imageView = findViewById(R.id.image_view);
       emojifyButton = findViewById(R.id.emojify_button);
       shareBtn = findViewById(R.id.share_button);
       saveBtn = findViewById(R.id.save_button);
       clearBtn = findViewById(R.id.clear_button);
       titleText = findViewById(R.id.title_text_view);
    }

    public void emojifyMe(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void launchCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null) {
                tempPhoto = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processAndSetImage();
        } else {
            BitmapUtils.deleteImageFile(this, tempPhoto);
        }
    }

    private void processAndSetImage() {
        emojifyButton.setVisibility(View.GONE);
        titleText.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);
        shareBtn.setVisibility(View.VISIBLE);
        clearBtn.setVisibility(View.VISIBLE);

        resultBitmap = BitmapUtils.resamplePic(this, tempPhoto);
        resultBitmap = Emojifier.detectFacesandOverlayEmoji(this, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }
    public void saveMe(View view) {
        BitmapUtils.deleteImageFile(this, tempPhoto);
        BitmapUtils.saveImage(this, resultBitmap);
    }
    public void shareMe(View view) {
        BitmapUtils.deleteImageFile(this, tempPhoto);
        BitmapUtils.saveImage(this, resultBitmap);
        BitmapUtils.shareImage(this, tempPhoto);
    }
    public void  clearMe(View view) {
        imageView.setImageResource(0);
        emojifyButton.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        shareBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        clearBtn.setVisibility(View.GONE);
        BitmapUtils.deleteImageFile(this, tempPhoto);
    }


}