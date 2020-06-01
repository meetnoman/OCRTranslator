package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.LanguageRemindHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class TextAugmenter extends AppCompatActivity {

    Button inputLanguage,outputLanguage,camera,textExtractor,history;

    private ArrayList<String> allLanguagesCode=new ArrayList<>(Arrays.asList("en","ur","zh","ar","tr","ru","id","ja","it","hi","fa","es","af","de","fr"));
    private  ArrayList<String> allLanguagesAbbrevations=new ArrayList<>(Arrays.asList("English","Urdu","Chineese","Arabic","Turkish","Russian","Indonesian","Japanese","Italian"
            ,"Hindi","Persian","Spanish","Afrikaans","German","French"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_augmenter);


        inputLanguage=(Button)findViewById(R.id.inputLanguage);
        outputLanguage=(Button)findViewById(R.id.outputLanguage);
        camera=(Button)findViewById(R.id.camera);
        textExtractor=(Button)findViewById(R.id.textExtractor);
        history=(Button)findViewById(R.id.history);

        setInputLanguageAndOutputLanguage();
      //  Toast.makeText(getApplicationContext(),"Language Selected: "+LanguageRemindHelper.getInstance().getOutputLanguage(),Toast.LENGTH_SHORT).show();


    }


    public void OnClickMethod(View view){
        if(view==inputLanguage){
            Intent intent=new Intent(getApplicationContext(),LanguageInputActivity.class);
            intent.putExtra("requestActivity","A");
            this.startActivity(intent);

            // Toast.makeText(this,"Input Language",Toast.LENGTH_LONG).show();
        }

        if(view==outputLanguage){
            Intent intent=new Intent(getApplicationContext(),LanguageOutputActivity.class);
            intent.putExtra("requestActivity","A");
            this.startActivity(intent);

            // Toast.makeText(this,"Output Language",Toast.LENGTH_LONG).show();

        }
        if(view==camera){
            Intent intent=new Intent(getApplicationContext(),LiveTextTranslation.class);
            //intent.putExtra("request",1);
             this.startActivity(intent);
            //Toast.makeText(this,"  Camera",Toast.LENGTH_LONG).show();

        }
        if(view==textExtractor){
            Intent intent=new Intent(getApplicationContext(),CameraViewActivity.class);
            intent.putExtra("request",2);
            this.startActivity(intent);
            //  Toast.makeText(this,"Text Extracor",Toast.LENGTH_LONG).show();
        }
        if(view==history){
            // Toast.makeText(this,"History",Toast.LENGTH_LONG).show();
            this.startActivity(new Intent(this,historyActivity.class));
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



    public String getAbbrevation(String languagecode){
        if (allLanguagesCode.contains(languagecode)){
            int index=allLanguagesCode.indexOf(languagecode);
            return allLanguagesAbbrevations.get(index);
        }
        return null;
    }


}
