package com.example.andrejavbelj.driveit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class CameraSmile extends AppCompatActivity {

    CameraView camera;
    ImageView slikajBtn;

    android.app.AlertDialog dialog;


    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_smile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nasmej se");

        camera = (CameraView) findViewById(R.id.IDcameraView);
        slikajBtn = (ImageView) findViewById(R.id.IDslikaBtn);


        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please wait")
                .setCancelable(false)
                .build();

        slikajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.start();
                camera.captureImage();

            }
        });



        camera.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                dialog.show();

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, camera.getWidth(), camera.getHeight(), false);
                camera.stop();

                detectSmile(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void detectSmile(Bitmap b){
        FirebaseVisionImage img = FirebaseVisionImage.fromBitmap(b);

        FirebaseVisionFaceDetectorOptions opt = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS).build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(opt);

        detector.detectInImage(img).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                procesirajObraz(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CameraSmile.this, "NE zanzvam", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesirajObraz(List<FirebaseVisionFace> firebaseVisionFaces){
        for(FirebaseVisionFace obraz: firebaseVisionFaces){
            if (obraz.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                float nasmeh = obraz.getSmilingProbability();

                dialog.dismiss();

                Toast.makeText(CameraSmile.this, String.valueOf(nasmeh), Toast.LENGTH_SHORT).show();

            }
        }
    }


}
