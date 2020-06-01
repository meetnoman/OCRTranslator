package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.translator.Helper.LanguageRemindHelper;

public class LanguageInputActivity extends AppCompatActivity {
    Button english,urdu,chinesse,detect;
    String intentString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_input);
        detect=(Button)findViewById(R.id.Detect);
        english=(Button)findViewById(R.id.English);
        urdu=(Button)findViewById(R.id.Urdu);
        chinesse=(Button)findViewById(R.id.Chinese);

        Intent intent=getIntent();
        intentString=intent.getStringExtra("requestActivity");


    }
    public void SelectInputLanguage(View view){
        if (view==detect){
            LanguageRemindHelper.getInstance().setInputLanguage("Detect Language");

        }
        if (view==english){
            LanguageRemindHelper.getInstance().setInputLanguage("en");
        }else if (view==urdu){
            LanguageRemindHelper.getInstance().setInputLanguage("ur");
        }
        else if (view==chinesse){
            LanguageRemindHelper.getInstance().setInputLanguage("zh");
        }
        goTobackActivity();
    }



    public void goTobackActivity(){
        if (intentString.equalsIgnoreCase("A")){
            Intent intent=new Intent(this,TextAugmenter.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);

        }
        else if (intentString.equalsIgnoreCase("B")){
            Intent intent=new Intent(this,CameraViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);

        }
        else if (intentString.equalsIgnoreCase("C")){
            Intent intent=new Intent(this,LiveTextTranslation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
        else {

        }
        finish();
    }
}
