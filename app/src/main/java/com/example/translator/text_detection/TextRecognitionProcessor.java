
package com.example.translator.text_detection;


import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.example.translator.Helper.LanguageRemindHelper;
import com.example.translator.LiveTextTranslation;
import com.example.translator.TextAugmenter;
import com.example.translator.others.FrameMetadata;
import com.example.translator.others.GraphicOverlay;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;


/**
 * Processor for the text recognition demo.
 */
public class TextRecognitionProcessor {

	private static final String TAG = "TextRecProc";

	private final FirebaseVisionTextRecognizer detector;

	// Whether we should ignore process(). This is usually caused by feeding input data faster than
	// the model can handle.
	private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);

	public TextRecognitionProcessor() {
		String lang=LanguageRemindHelper.getInstance().getInputLanguage();
		if (lang.equalsIgnoreCase("en")){
			detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

		}else{
		detector = FirebaseVision.getInstance().getCloudTextRecognizer();
	}
	}



	//region ----- Exposed Methods -----


	public void stop() {
		try {
			detector.close();
		} catch (IOException e) {
			Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
		}
	}


	public void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) throws FirebaseMLException {

		if (shouldThrottle.get()) {
			return;
		}
		FirebaseVisionImageMetadata metadata =
				new FirebaseVisionImageMetadata.Builder()
						.setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
						.setWidth(frameMetadata.getWidth())
						.setHeight(frameMetadata.getHeight())
						.setRotation(frameMetadata.getRotation())
						.build();


		detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata, graphicOverlay);
	}

	//endregion

	//region ----- Helper Methods -----

	protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
		return detector.processImage(image);
	}


	protected void onSuccess(@NonNull FirebaseVisionText results, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay,FirebaseVisionImage image) {

		graphicOverlay.clear();

		List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();

		String a=results.getText();
		for (int i = 0; i < blocks.size(); i++) {
			List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
			for (int j = 0; j < lines.size(); j++) {
				List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
				for (int k = 0; k < elements.size(); k++) {
 					//GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
					//graphicOverlay.add(textGraphic);

				}
			}
		}
		Bitmap bitmap=image.getBitmap();

		LiveTextTranslation.getInstance().myMethod(a,bitmap);

	}

	protected void onFailure(@NonNull Exception e) {

		Log.w(TAG, "Text detection failed." + e);
	}

	private void detectInVisionImage(final FirebaseVisionImage image, final FrameMetadata metadata, final GraphicOverlay graphicOverlay) {



		 Bitmap bitmap=image.getBitmap();
		final Bitmap croppedBmp = Bitmap.createBitmap(
				bitmap, 0, 250,
				bitmap.getWidth(),
				760 );
		//LiveTextTranslation.getInstance().myMethod("Not Detected Yet",croppedBmp);


		FirebaseVisionImage image2=FirebaseVisionImage.fromBitmap(croppedBmp);

		detectInImage(image2)
				.addOnSuccessListener(
						new OnSuccessListener<FirebaseVisionText>() {
							@Override
							public void onSuccess(FirebaseVisionText results) {
								shouldThrottle.set(false);
								TextRecognitionProcessor.this.onSuccess(results, metadata, graphicOverlay,image);
							}
						})
				.addOnFailureListener(
						new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								shouldThrottle.set(false);
								TextRecognitionProcessor.this.onFailure(e);
								LiveTextTranslation.getInstance().myMethod("Cloud COuld not Run: "+e.getMessage(),croppedBmp);
							}
						});
 		// Begin throttling until this frame of input has been processed, either in onSuccess or
		// onFailure.
		shouldThrottle.set(true);
	}

	//endregion


}
