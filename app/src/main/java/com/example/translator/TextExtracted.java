package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.BitmapHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TextExtracted extends AppCompatActivity {


    EditText showLanguange,showText,showTranslation;
    Button cancel,save;
    ArrayList<String> recognizedLanguages;
    String allLanguageAbbreviations="";
    private String textDetect;
    private String textTranslation;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_extracted);
        showLanguange=(EditText)findViewById(R.id.showLanguages);
        showText=(EditText)findViewById(R.id.showText);
        showTranslation=findViewById(R.id.showTranslation);
        cancel=(Button)findViewById(R.id.cancel);
        bitmap= BitmapHelper.getInstance().getBitmap();


        Intent intent=getIntent();
        textDetect=intent.getStringExtra("Text");

        Bundle bundle=intent.getExtras();
        if (bundle!=null) {
            recognizedLanguages = (ArrayList<String>) bundle.getSerializable("languages");
            TextAugmenter obj=new TextAugmenter();
            for (String a:recognizedLanguages) {
                if (obj.getAbbrevation(a)!=null){
                    allLanguageAbbreviations+=obj.getAbbrevation(a);

                    allLanguageAbbreviations+=" ";

                }
            }


        }else {
            recognizedLanguages.add("Could Not Recognized Any language ");
        }


        // Toast.makeText(getApplicationContext(),"Languages RecO: "+allLanguageAbbreviations,Toast.LENGTH_LONG).show();

        showLanguange.setText(String.valueOf(allLanguageAbbreviations));

        showText.setText(intent.getStringExtra("Text"));

        //////////////////////////////
        startTranslation(textDetect);



    }

    public void OnCLickMethod(View view){
        if (view==cancel){
            Intent intent=new Intent(this,TextAugmenter.class);
            this.startActivity(intent);
            finish();
        }

    }





    public void startTranslation(String text) {

        getLanguageCode(text);
    }




    private void getLanguageCode(final String text) {
        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        identifier.identifyLanguage(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                if (s.equals("und")) {
                    //  Toast.makeText(getApplicationContext(),"Language Not Identified",Toast.LENGTH_SHORT).show();

                } else {
                    //   Toast.makeText(getApplicationContext(),"Identified lang: "+s,Toast.LENGTH_SHORT).show();
                    translateText(text, s);
                }
            }
        });
    }





    private void translateText(final String text, final String lang) {


        int inputLanguageCode = FirebaseTranslateLanguage.languageForLanguageCode(LanguageRemindHelper.getInstance().getInputLanguage());

        //int targetL = targetLangSelector.getSelectedItemPosition();
       // int code = FirebaseTranslateLanguage.languageForLanguageCode(allLanguagesCode.get(targetL));
        int outputLanguageCode = FirebaseTranslateLanguage.languageForLanguageCode(LanguageRemindHelper.getInstance().getOutputLanguage());

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(inputLanguageCode)
                        .setTargetLanguage(outputLanguageCode)
                        .build();


        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                //Toast.makeText(getApplicationContext(),"Model download",Toast.LENGTH_SHORT).show();
                                translator.translate(text)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(@NonNull String translatedText) {
                                                        showTranslation.setText(translatedText);
                                                        textTranslation= translatedText;
                                                        addRecord(text,translatedText);


                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error.
                                                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                     }
                                                });


                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                Toast.makeText(getApplicationContext(), "Error downloading model ", Toast.LENGTH_SHORT).show();
                            }
                        });


    }


    private void addRecord(String text,String translation){
        DatabaseHelper db=new DatabaseHelper(this);

        //Bitmap image = BitmapFactory.decodeResource(getResources(),bitmap);
// convert bitmap to byte
        Bitmap image=bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[]  = stream.toByteArray();

        TextAugmenter obj=new TextAugmenter();
        String inputLangauge=obj.getAbbrevation( LanguageRemindHelper.getInstance().getInputLanguage());
        String outputLanguage=obj.getAbbrevation( LanguageRemindHelper.getInstance().getOutputLanguage());


        boolean temp=db.storeTextTranslation(text,translation,imageInByte,inputLangauge,outputLanguage);
        if (temp){
            Toast.makeText(this, "Recorded inserted...", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Recorded Not inserted...", Toast.LENGTH_SHORT).show();

        }

    }








}
