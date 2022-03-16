package com.example.real.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.real.R;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomView extends View {

    private final double properRatio = 0.08;

    private Paint paintR;
    private Paint paintW;
    private int mX;
    private int mY;

    private int vWidth;
    private int vHeight;

    private int baseLength;

    private Context context;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getPaddingLeft() + getPaddingRight();
        int height = getPaddingTop() + getPaddingBottom();

        Log.w("CUSTOMVIEW1",
                String.valueOf(width) + " / " + String.valueOf(height) + " / " +
                        String.valueOf(widthMeasureSpec) + " / " + String.valueOf(heightMeasureSpec));

        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);

        vWidth = (int)(width * properRatio);
        vHeight = (int)(height * properRatio);

        if(width >= height){
            baseLength = height;
        }else{
            baseLength = width;
        }


        Log.w("CUSTOMVIEW2", String.valueOf(width) + " / " + String.valueOf(height));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mX = (right - left) / 2;
        mY = (bottom - top) / 2;

        CircleImageView circleImageView = new CircleImageView(context);
        circleImageView.setX(mX);
        circleImageView.setY(mY);
        circleImageView.setImageResource(R.drawable.push_img);
        Log.w("CUSTOMVIEW3", String.valueOf(mX) + " / " + String.valueOf(mY));
    }

    private void init(Context context){
        paintR = new Paint();
        paintR.setColor(Color.RED);

        paintW = new Paint();
        paintW.setColor(Color.WHITE);
        /*
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        paintW.setXfermode(new PorterDuffXfermode(mode));

         */
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double pickle = 0.05;
        canvas.drawCircle(mX, mY - (int)(baseLength/4 - 2*pickle*baseLength), (int)(baseLength/4 + pickle*baseLength), paintR);
        canvas.drawRoundRect(mX - (int)(baseLength * properRatio), mY, mX + (int)(baseLength * properRatio), mY + (int)(baseLength/2), 100, 100, paintR);
        canvas.drawCircle(mX, mY - (int)(baseLength/4 - 2*pickle*baseLength), (int)(baseLength/4 + pickle*baseLength*0.5), paintW);
        canvas.drawRoundRect(mX - (int)(baseLength * properRatio * 0.7), mY + (int)(baseLength * 0.15), mX + (int)(baseLength * properRatio * 0.7), mY + (int)((baseLength/2) * 0.95), 100, 100, paintW);
    }
}
