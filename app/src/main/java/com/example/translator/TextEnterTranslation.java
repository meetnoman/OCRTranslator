package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class TextEnterTranslation extends AppCompatActivity {

    EditText textExtract;
    TextView textTranslation;
    Button finish;
    TextAugmenter textAugmenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_enter_detection);

        textExtract=findViewById(R.id.textExtract);
        textTranslation=findViewById(R.id.textTranslate);
        finish=findViewById(R.id.finish);

          textAugmenter=new TextAugmenter();

        textExtract.setHint("Enter Text ("+textAugmenter.getAbbrevation( LanguageRemindHelper.getInstance().getInputLanguage())+")");
        textTranslation.setText("Translation ("+textAugmenter.getAbbrevation(LanguageRemindHelper.getInstance().getOutputLanguage())+")");

        textExtract.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(getApplicationContext(),textExtract.getText(),Toast.LENGTH_SHORT).show();
                startTranslation(textExtract.getText().toString());
            }
        });


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),TextTranslation.class);
                intent.putExtra("textExtracted",textExtract.getText().toString());
                intent.putExtra("textTranslation",textTranslation.getText().toString());
                startActivity(intent);

                saveData(textExtract.getText().toString(),textTranslation.getText().toString());

            }
        });


    }








    public void startTranslation(String text) {

        if(text.equalsIgnoreCase("")){
            textTranslation.setText("Translation ("+textAugmenter.getAbbrevation(LanguageRemindHelper.getInstance().getOutputLanguage())+")");

        }else{
        getLanguageCode(text);
    }

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

                                                        textTranslation.setText(translatedText);
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


    public  void saveData(String textExtracted,String textTranslation){
        DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());

        boolean flag=databaseHelper.storeEnterdTextTranslation(textExtracted,textTranslation,textAugmenter.getAbbrevation( LanguageRemindHelper.getInstance().getInputLanguage()),textAugmenter.getAbbrevation( LanguageRemindHelper.getInstance().getOutputLanguage()));

        if (flag){
            Toast.makeText(getApplicationContext(),"Text Stored"+flag,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"OOOPS No"+flag,Toast.LENGTH_SHORT).show();
        }
    }



}
