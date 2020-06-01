package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TextTranslation extends AppCompatActivity {

    TextView textExtracted,textTranslated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_translation);

        Intent intent=getIntent();

        textExtracted=findViewById(R.id.textExtract);
        textTranslated=findViewById(R.id.textTranslate);

        textExtracted.setText(intent.getStringExtra("textExtracted"));
//        textTranslated.setText(intent.getStringExtra("textTranslation"));


        if (intent.getStringExtra("textTranslation").equals("0")) {
            textTranslated.setText("No Text Translated");
            textTranslated.setVisibility(textTranslated.INVISIBLE);
        }
        else {
            textTranslated.setText(intent.getStringExtra("textTranslation"));
        }


    }
}
