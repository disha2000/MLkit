package com.example.hp.mlkit.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextGraphic extends GraphicOverlay.Graphic {
    public static final  int text_color= Color.BLUE;
    public static  final  float text_size=54.8f;
    public  static  final float stroke_width=8.0f;
    public  final Paint rectpaint,textpaint;
    public  final FirebaseVisionText.Element Text;
    public TextGraphic(GraphicOverlay overlay,FirebaseVisionText.Element text) {
        super(overlay);
        this.Text=text;
        rectpaint=new Paint();
        rectpaint.setColor(text_color);
        rectpaint.setStyle(Paint.Style.STROKE);
        rectpaint.setStrokeWidth(stroke_width);

        textpaint=new Paint();
        rectpaint.setColor(text_color);
        rectpaint.setTextSize(text_size);
        postInvalidate();


    }

    @Override
    public void draw(Canvas canvas) {
        if(Text==null)
        {
            throw new IllegalStateException("Attempting to draw a null text");

        }
        RectF rect=new RectF(Text.getBoundingBox());
        rect.left=translateX(rect.left);
        rect.top=translateY(rect.top);
        rect.bottom=translateY(rect.bottom);
        rect.right=translateX(rect.right);
        canvas.drawRect(rect,rectpaint);
        canvas.drawText(Text.getText(),rect.left,rect.bottom,textpaint);



    }
}
