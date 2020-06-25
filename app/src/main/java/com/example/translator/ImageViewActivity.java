package com.example.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;
import com.example.translator.Helper.BitmapHelper;
import com.example.translator.Helper.LanguageRemindHelper;
import com.example.translator.camera.CameraSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

     CropImageView  imageView;
    Button check,close;
    Bitmap bitmap;
    android.app.AlertDialog waitingDialog;
    boolean testifier=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        waitingDialog=new SpotsDialog.Builder()
                .setCancelable(true)
                .setMessage("Please Wait")
                .setContext(this)
                .build();


        waitingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
               // getApplicationContext().startActivity(new Intent(getApplicationContext(),ImageViewActivity.class));
               // finish();
                testifier=false;
            }
        });


        check=(Button)findViewById(R.id.checkbtn);
        close=(Button) findViewById(R.id.closebtn);

        imageView=findViewById(R.id.ImageView);
        bitmap= BitmapHelper.getInstance().getBitmap();
         imageView.setImageBitmap(bitmap);

        imageView.setOnCropWindowChangedListener(new CropImageView.OnSetCropWindowChangeListener() {
            @Override
            public void onCropWindowChanged() {
               // Toast.makeText(getApplicationContext(),"Getting SOme thing OnsetCropwindow",Toast.LENGTH_SHORT).show();
                Bitmap cropped = imageView.getCroppedImage();
                BitmapHelper.getInstance().setBitmap(cropped);
                 bitmap=cropped;

            }
        });

    }




    public void OnClickMethod(View view){
        if (view==check){
            testifier=true;
            waitingDialog.show();
            recognizeText(this.bitmap);
        }
        if (view==close){
            //Toast.makeText(this,"Close Btn CLick",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),TextAugmenter.class);
            this.startActivity(intent);

        }
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
                         //Toast.makeText(getApplicationContext(), "ON Success Found", Toast.LENGTH_LONG).show();

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

        //Toast.makeText(this, "Proces Text Found", Toast.LENGTH_LONG).show();

        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

            for (FirebaseVisionText.Line line : block.getLines()) {

                List<RecognizedLanguage> languagesReco=line.getRecognizedLanguages();


                    //Toast.makeText(getApplicationContext(),"Language Recognized specific: "+languagesReco.get(0).getLanguageCode(),Toast.LENGTH_SHORT).show();


                for (FirebaseVisionText.Element element: line.getElements()){
                    languages = line.getRecognizedLanguages();
  /*
                    if (!uniqueRecognizedLanguage.contains(languages.get(0).getLanguageCode())) {
                        uniqueRecognizedLanguage.add(languages.get(0).getLanguageCode());
                    }
                    textRecognized.append(element.getText()+" ");

*/

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



                    waitingDialog.dismiss();

            }
        }




        if (testifier) {

             //  Toast.makeText(getApplicationContext(),"SentArray: "+uniqueRecognizedLanguage ,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), TextExtracted.class);

            intent.putExtra("Text", textRecognized.toString());
            Bundle bundle = new Bundle();
            bundle.putSerializable("languages", uniqueRecognizedLanguage);
            //intent.putStringArrayListExtra("languages", uniqueRecognizedLanguage);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }


}
