package com.example.firstapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ImageView imageView;
    public static final int req_code=1;
    public static final int req_camera=2;
    public static final int req_gallery=3;
    Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        floatingActionButton=findViewById(R.id.floatingButton);
        imageView=findViewById(R.id.image);
        clear=findViewById(R.id.clear);

        permissionCheck();


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageView.getDrawable().getConstantState()!=getResources().getDrawable(R.mipmap.ic_launcher).getConstantState())
                {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageCapture();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==req_code)
        {
            if(grantResults.length>0 )
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)

                {
                    return;
                }
                else
                {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void permissionCheck()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, req_code);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case req_camera:
            case req_gallery:
                if(resultCode==RESULT_OK)
                {
                    Bundle bundle=data.getExtras();

                    if(bundle!=null)
                    {
                        Bitmap bitmap=bundle.getParcelable("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
        }
    }

    public void ImageCapture()
    {

        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_burst_mode);
        builder.setTitle("Choose Image");
        final String[] items={"Camera","Gallery","Cancel"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Camera"))
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                    {
                        permissionCheck();
                    }
                    else
                    {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,req_camera);
                    }

                }
                else if(items[which].equals("Gallery"))
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        permissionCheck();

                    }
                    else
                    {
                        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.putExtra("crop","true");
                        intent.putExtra("scale",true);
                        intent.putExtra("outputX",256);
                        intent.putExtra("outputY",256);
                        intent.putExtra("aspectX",1);
                        intent.putExtra("aspectY",1);
                        intent.putExtra("return-data",true);
                        startActivityForResult(intent,req_gallery);

                    }

                }
                else
                {
                    dialog.cancel();
                }
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}
