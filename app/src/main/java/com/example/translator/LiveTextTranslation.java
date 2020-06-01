package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.BitmapHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.example.translator.camera.CameraSource;
import com.example.translator.camera.CameraSourcePreview;
import com.example.translator.others.GraphicOverlay;
import com.example.translator.text_detection.TextRecognitionProcessor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveTextTranslation extends AppCompatActivity   {

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    Button inputLanguage,outputLanguage;
    TextView srcText, translatedTextView;
    Spinner targetLangSelector;
    private static LiveTextTranslation instance;
    Bitmap globalImage;

    private  ArrayList<String> allLanguagesAbbrevations=new ArrayList<>(Arrays.asList("English","Urdu","Chineese","Arabic","Turkish","Russian","Indonesian","Japanese","Italian"
            ,"Hindi","Persian","Spanish","Afrikaans","German","French"));
    private ArrayList<String> allLanguagesCode = new ArrayList<>(Arrays.asList("en", "ur", "zh", "ar", "tr", "ru", "id", "ja", "it", "hi", "fa", "es", "af", "de", "fr"));

    private ArrayList<String> textDetect;
    private ArrayList<String> textTranslation;


    private static String TAG = LiveTextTranslation.class.getSimpleName().toString().trim();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        globalImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte imageInByte[] = stream.toByteArray();
        final DatabaseHelper databaseHelper = new DatabaseHelper(this);

        TextAugmenter obj=new TextAugmenter();
        String inputLangauge=obj.getAbbrevation( LanguageRemindHelper.getInstance().getInputLanguage());
        String outputLanguage=obj.getAbbrevation( LanguageRemindHelper.getInstance().getOutputLanguage());

        databaseHelper.storeTextTranslation(textDetect.toString(),textTranslation.toString(),imageInByte,inputLangauge,outputLanguage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_text_translation);

        textDetect=new ArrayList<>();
        textTranslation=new ArrayList<>();

        inputLanguage=(Button) findViewById(R.id.inputLanguage);
        outputLanguage=(Button) findViewById(R.id.outputLanguage);
        instance = this;
        srcText = findViewById(R.id.srcText);
        translatedTextView = findViewById(R.id.translatedText);

        setInputLanguageAndOutputLanguage();




        preview = (CameraSourcePreview) findViewById(R.id.camera_source_preview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = (GraphicOverlay) findViewById(R.id.graphics_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }


        createCameraSource();
        startCameraSource();


    }

    public void OnClickMethod(View view) {
        if (view == inputLanguage) {

            Intent intent = new Intent(getApplicationContext(), LanguageInputActivity.class);
            intent.putExtra("requestActivity","C");
            this.startActivity(intent);
            finish();
            // Toast.makeText(this,"Input Language",Toast.LENGTH_LONG).show();
        }

        if (view == outputLanguage) {
            Intent intent=new Intent(getApplicationContext(),LanguageOutputActivity.class);
            intent.putExtra("requestActivity","C");
            this.startActivity(intent);
            finish();
            // Toast.makeText(this,"Output Language",Toast.LENGTH_LONG).show();

        }

    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor());
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    public static LiveTextTranslation getInstance() {
        return instance;
    }




    public void myMethod(String text, Bitmap bitmap) {


        this.globalImage = bitmap;
       // recognizeText(bitmap);

         srcText.setText(text);
        textDetect.add(text);
        getLanguageCode(text);
    }



    public void recognizeText(Bitmap bitmap) {

        String inputLanguage= LanguageRemindHelper.getInstance().getInputLanguage();

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionCloudTextRecognizerOptions options;

        if (inputLanguage.equalsIgnoreCase("Detect Language")){

            options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList("en","ur,","zh")).build();
        }else{


            options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList(inputLanguage)).build();
        }

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options);


        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        processTextResult(firebaseVisionText);
                       // Toast.makeText(getApplicationContext(), "ON Success Found", Toast.LENGTH_LONG).show();

                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"ON Failure: "+e.getMessage(),Toast.LENGTH_LONG).show();


                Log.d("Error: ", e.getMessage());

            }
        });
    }



    private void processTextResult(FirebaseVisionText firebaseVisionText) {

        StringBuilder textRecognized = new StringBuilder();
        List<RecognizedLanguage> languages =null;
        ArrayList<String> uniqueRecognizedLanguage=new ArrayList<String>();

        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Load in BLock"+firebaseVisionText.getText(), Toast.LENGTH_LONG).show();

        srcText.setText(blocks.toString());
/*

        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

            for (FirebaseVisionText.Line line : block.getLines()) {

                List<RecognizedLanguage> languagesReco=line.getRecognizedLanguages();




                for (FirebaseVisionText.Element element: line.getElements()){
                    languages = line.getRecognizedLanguages();


                    String tempInputLanguage=LanguageRemindHelper.getInstance().getInputLanguage();
                    if (tempInputLanguage.equalsIgnoreCase("en") || tempInputLanguage.equalsIgnoreCase("zh") ){
                        if (languages.size()!=0 && languages.get(0).getLanguageCode().equalsIgnoreCase( tempInputLanguage)) {
                            textRecognized.append(element.getText()+" ");
                            if (!uniqueRecognizedLanguage.contains(languages.get(0).getLanguageCode())) {
                                uniqueRecognizedLanguage.add(languages.get(0).getLanguageCode());
                            }
                        }
                    }

                    else if (tempInputLanguage.equalsIgnoreCase("ur")){
                        if (languages.size()!=0    ) {
                            if (languages.get(0).getLanguageCode().equalsIgnoreCase( tempInputLanguage) || languages.get(0).getLanguageCode().equalsIgnoreCase( "fa")){
                                textRecognized.append(element.getText()+" ");
                                if (!uniqueRecognizedLanguage.contains(languages.get(0).getLanguageCode())) {
                                    uniqueRecognizedLanguage.add(languages.get(0).getLanguageCode());
                                }
                            }
                        }
                    }



                    else if(languages.size()!=0 && LanguageRemindHelper.getInstance().getInputLanguage().equalsIgnoreCase("Detect Language")){
                        textRecognized.append(element.getText()+" ");
                        if (!uniqueRecognizedLanguage.contains(languages.get(0).getLanguageCode())) {
                            uniqueRecognizedLanguage.add(languages.get(0).getLanguageCode());
                        }
                    }

                }

            }
        }
*/
      //  srcText.setText(textRecognized.toString());
       // textDetect.add(textRecognized.toString());
       // getLanguageCode(textRecognized.toString());


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

        int inputLang=allLanguagesAbbrevations.indexOf(inputLanguage.getText().toString());
        int inputLanguageCode = FirebaseTranslateLanguage.languageForLanguageCode(allLanguagesCode.get(inputLang));


        int targetL=allLanguagesAbbrevations.indexOf(outputLanguage.getText().toString());
        int outputLanguageCode = FirebaseTranslateLanguage.languageForLanguageCode(allLanguagesCode.get(targetL));


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
                                                        translatedTextView.setText(translatedText);
                                                        textTranslation.add(translatedText);



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
