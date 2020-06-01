package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.example.translator.RecyclerViewAdapterr.LanguageOutputAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;

public class LanguageOutputActivity extends AppCompatActivity implements LanguageOutputAdapter.IMyItemClickListener{

    LanguageOutputAdapter adapter;
    ArrayList<Integer> primaryKey ;
    ArrayList<String> language ;
    ArrayList<String> languageCode ;
    ArrayList<Integer> download;
    String intentString;
    boolean wifi=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_output);

        Intent intent=getIntent();
        intentString=intent.getStringExtra("requestActivity");

        DatabaseHelper obj=new DatabaseHelper(this);
        Cursor cursor= obj.getLanguageDetail();

        primaryKey = new ArrayList<>();
        language = new ArrayList<>();
        languageCode = new ArrayList<>();
        download = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                primaryKey.add(cursor.getInt(0));
                language.add(cursor.getString(1));
                languageCode.add(cursor.getString(2));
                download.add(cursor.getInt(3));

            }
            while (cursor.moveToNext());
        }




        RecyclerView recyclerView=findViewById(R.id.recyclerView) ;
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new LanguageOutputAdapter(this,language,download,   this);

        recyclerView.setAdapter(adapter);


    }


    @Override
    public void onBackPressed() {
        //goTobackActivity();
        if (intentString.equalsIgnoreCase("B")){
            Intent intent=new Intent(this,CameraViewActivity.class);
            this.startActivity(intent);
            finish();
        }
        else if (intentString.equalsIgnoreCase("C")){
            Intent intent=new Intent(this,LiveTextTranslation.class);
            this.startActivity(intent);
            finish();
        }else {
            super.onBackPressed();

        }

    }




    @Override
    public void onMyItemClick(int position) {
        String outputLang=LanguageRemindHelper.getInstance().getOutputLanguage().toLowerCase();


//        Toast.makeText(getApplicationContext()," SourceLanguageCode: "+languageCode.get(position)+" targetLanguageCode: "+,Toast.LENGTH_SHORT).show();

        if (download.get(position)==1){
            LanguageRemindHelper.getInstance().setOutputLanguage(languageCode.get(position));
            goTobackActivity();
            finish();
        }

        else {

            if (outputLang.equalsIgnoreCase("Detect Language")) {
                alertDialog("en", languageCode.get(position), position);
            } else {
                alertDialog(outputLang, languageCode.get(position), position);
            }

        }

    }




    public void alertDialog(final String sourceLanguage, final String targetLanguage, final int position){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download offline Languages");
        builder.setMessage("Tranlsate this language even when you are offline by downloading an offline Transaltion File");

// Add a checkbox list
        String[] optionMessage = {"Download using Wi-Fi only(doesnot include mobile hotspots"};
        boolean[] checkedItems = {true};




        builder.setMultiChoiceItems(optionMessage, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // The user checked or unchecked a box

                if (wifi){
                    setWifi(false);
                }else { setWifi(true);}
                // Toast.makeText(getApplicationContext(),"Chkkkkk: "+wifi,Toast.LENGTH_SHORT).show();
            }
        });



// Add OK and Cancel buttons
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(),"Download positive btn: ",Toast.LENGTH_SHORT).show();

                int sourceLanuageCode = FirebaseTranslateLanguage.languageForLanguageCode(sourceLanguage);
                int targetLanguageCode = FirebaseTranslateLanguage.languageForLanguageCode(targetLanguage);


                downloadModel(sourceLanuageCode,targetLanguageCode,position);
            }
        });
        builder.setNegativeButton("Cancel", null);

// Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    public void downloadModel(int sourceLanguageCode, int targetLanguageCode, final int position){




        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(sourceLanguageCode)
                        .setTargetLanguage(targetLanguageCode)
                        .build();
        final FirebaseTranslator translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);



        DatabaseHelper obj=new DatabaseHelper(getApplicationContext());
        boolean status=obj.updateLanguageDetail(primaryKey.get(position),2 );

        Toast.makeText(getApplicationContext(),"Status: "+status,Toast.LENGTH_SHORT).show();


        if (wifi){
            final FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                    .requireWifi()
                    .build();


            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            DatabaseHelper obj=new DatabaseHelper(getApplicationContext());
                            boolean status=obj.updateLanguageDetail(primaryKey.get(position),1 );

                            Toast.makeText(getApplicationContext(),"Model Is Downloaded using wifi: "+status,Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error Occured While downloading ModeL: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            Toast.makeText(getApplicationContext(), "Else Available data: ", Toast.LENGTH_SHORT).show();

            translator.downloadModelIfNeeded()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Model Is Downloaded ", Toast.LENGTH_SHORT).show();


                            DatabaseHelper obj=new DatabaseHelper(getApplicationContext());
                            boolean status=obj.updateLanguageDetail(primaryKey.get(position),1 );


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Error Occured While downloading ModeL: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }


    }





    public void goTobackActivity(){
        if (intentString.equalsIgnoreCase("A")){
            Intent intent=new Intent(this,TextAugmenter.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);

        }
        else if (intentString.equalsIgnoreCase("B")){
            Intent intent=new Intent(this,CameraViewActivity.class);
             this.startActivity(intent);
        }
        else if (intentString.equalsIgnoreCase("C")){
            Intent intent=new Intent(this,LiveTextTranslation.class);
             this.startActivity(intent);
        }
        else {

        }

    }

    public void setWifi(boolean a){
        wifi=a;
    }


}
