package com.example.learningapp.intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learningapp.R;

import java.io.File;

public class StartActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static final int REQUEST_IMAGE_CAPTURE_CODE = 1;
    private static final int REQUEST_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.et_start_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void imageCapture(View view){
        if(!checkPermissions()){
            requestPermissions();
            return;
        }
        takePhoto();
    }

    public void takePhoto(){
        String path = getFilesDir()+File.separator+"images"+File.separator;
        Log.i("StartActivity", "image path: "+path);
        File file = new File(path, "test.jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        Cursor cursor;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            uri = FileProvider.getUriForFile(this, "com.example.learningapp.fileauthor", file);
        }else{
            uri = Uri.fromFile(file);
        }
        Log.i("StartActivity", "image save uri: " + uri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                System.out.println(data.getStringExtra("name"));
                startActivity(new Intent(Intent.ACTION_VIEW, data.getData()));
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE_CODE){
            if (resultCode == RESULT_OK){
                Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir()+"/images/test.jpg");
                ImageView imageView = (ImageView)findViewById(R.id.iv_intent_image_capture);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public boolean checkPermissions(){
        boolean haveCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean haveWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return haveCameraPermission && haveWritePermission;
    }

    public void requestPermissions(){
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                boolean allAllowed = true;
                for(int i=0; i<grantResults.length; ++i){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        allAllowed = false;
                        break;
                    }
                }
                if(allAllowed){
                    takePhoto();
                }else{
                    Toast.makeText(this, "请授权方可使用哦", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("state", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("state", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("state", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("state", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("state", "onDestroy");
    }
}
