package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.translator.Helper.BitmapHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraViewActivity extends AppCompatActivity {

    CameraView cameraView;
    ImageView caputreImage, gallery;
    Button inputLanguage,outputLanguage;
    int galleryCode,cameraCode;


    private ArrayList<String> allLanguagesCode=new ArrayList<>(Arrays.asList("en","ur","zh","ar","tr","ru","id","ja","it","hi","fa","es","af","de","fr"));
    private  ArrayList<String> allLanguagesAbbrevations=new ArrayList<>(Arrays.asList("English","Urdu","Chineese","Arabic","Turkish","Russian","Indonesian","Japanese","Italian"
            ,"Hindi","Persian","Spanish","Afrikaans","German","French"));





    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);


        cameraView=(CameraView) findViewById(R.id.cameraView);


        gallery=  findViewById(R.id.gallery);
        caputreImage=  findViewById(R.id.capture);
        inputLanguage=(Button) findViewById(R.id.inputLanguage);
        outputLanguage=(Button) findViewById(R.id.outputLanguage);

        setInputLanguageAndOutputLanguage();

        Intent intent=getIntent();
        int temp=intent.getIntExtra("request",0);
        if (temp==1){cameraCode=1;}
        else {galleryCode=2;}

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,2);
            }
        });

        caputreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();


            }
        });





        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);

                BitmapHelper.getInstance().setBitmap(bitmap);
                cameraView.stop();


                Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(intent);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap;
        if (requestCode == galleryCode && data != null) {

            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            //this.startActivity(intent);

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                // ByteArrayOutputStream bs=new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG,50,bs);
                // intent.putExtra("byteArray",bs.toByteArray());

                BitmapHelper.getInstance().setBitmap(bitmap);
                this.startActivity(intent);
                // Toast.makeText(getApplicationContext(), "Bitmap Send", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }





    public void OnClickMethod(View view) {
        if (view == inputLanguage) {

            Intent intent = new Intent(getApplicationContext(), LanguageInputActivity.class);
            intent.putExtra("requestActivity","B");
            this.startActivity(intent);
            finish();
            // Toast.makeText(this,"Input Language",Toast.LENGTH_LONG).show();
        }

        if (view == outputLanguage) {
            Intent intent=new Intent(getApplicationContext(),LanguageOutputActivity.class);
            intent.putExtra("requestActivity","B");
            this.startActivity(intent);
            finish();
            // Toast.makeText(this,"Output Language",Toast.LENGTH_LONG).show();

        }

    }


    public void setInputLanguageAndOutputLanguage(){
        String input= LanguageRemindHelper.getInstance().getInputLanguage();
        String output=LanguageRemindHelper.getInstance().getOutputLanguage();
        if (input!=null){

            if (input.equalsIgnoreCase("Detect Language")){
                inputLanguage.setText("Detect Language");
            }
            if ( allLanguagesCode.contains(input)){
                int index=allLanguagesCode.indexOf(input);
                inputLanguage.setText(allLanguagesAbbrevations.get(index));
            }
        }
        else {
            LanguageRemindHelper.getInstance().setInputLanguage("en");
        }



        if (output!=null){

            if ( allLanguagesCode.contains(output)){
                int index=allLanguagesCode.indexOf(output);
                outputLanguage.setText(allLanguagesAbbrevations.get(index));
            }
        }
        else {LanguageRemindHelper.getInstance().setOutputLanguage("ur");}
    }






}
