package com.example.hp.mlkit;

import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.mlkit.helper.GraphicOverlay;
import com.example.hp.mlkit.helper.TextGraphic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    CameraView cameraView;
    android.app.AlertDialog waiting_dialog;
    GraphicOverlay graphicOverlay;
    Button btn;
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waiting_dialog=new SpotsDialog.Builder()
                .setCancelable(false)
                .setMessage("Please wait")
                .setContext(this)
                .build();


        cameraView=(CameraView) findViewById(R.id.camera_view);

        graphicOverlay=(GraphicOverlay)findViewById(R.id.graphic_overlay);
        btn=(Button) findViewById(R.id.btn_capture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                //show diaglog
                waiting_dialog.show();
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
                recongnizeText(bitmap);
            }

            private void recongnizeText(Bitmap bitmap) {
                final FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionTextRecognizer textRecognizer= FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {


                        drawTextResult(firebaseVisionText);
                    }


                });

            }

            private void drawTextResult(FirebaseVisionText image) {
                List<FirebaseVisionText.TextBlock>blocks=image.getTextBlocks();
                if(blocks.size()==0)
                {
                    Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_LONG).show();
                    return;

                }
                graphicOverlay.clear();

                for(int i=0;i<blocks.size();i++)
                {
                    List<FirebaseVisionText.Line> lines=blocks.get(i).getLines();
                    for(int j=0;j<lines.size();j++)
                    {
                        List<FirebaseVisionText.Element> elements=lines.get(j).getElements();
                        for(int k=0;k<elements.size();k++)
                        {
                            Toast.makeText(getApplicationContext(),blocks.size()+" "+lines.size()+" "+elements.size(),Toast.LENGTH_LONG).show();
                            TextGraphic textGraphic=new TextGraphic(graphicOverlay,elements.get(k));
                            graphicOverlay.add(textGraphic);

                        }

                    }


                }
                waiting_dialog.dismiss();
            }


            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }
}
