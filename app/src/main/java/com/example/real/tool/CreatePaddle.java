package com.example.real.tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class CreatePaddle {

    public CreatePaddle() {
    }
    public Bitmap createPaddle(Bitmap Background, Bitmap Center, Bitmap Handle, int Paddle_Size_x){

        //Base Options
        float display_ratio = 2; // X:Y ratio
        Bitmap Paddle = Bitmap.createBitmap(Paddle_Size_x,
                (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Rect Rect_Paddle = new Rect(0,0,Paddle_Size_x, (int) (Paddle_Size_x*display_ratio));
        final int color = 0xff424242;
        int Sweep_Angle = 320;
        int Background_Rad = Paddle_Size_x/2;
        int Handle_Length = (int) (Paddle_Size_x*0.5);
        Canvas Canvas_Paddle = new Canvas(Paddle);
        Canvas_Paddle.save();
        Canvas_Paddle.rotate(0,Background_Rad,Background_Rad);



        //  Case - Background


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Background = Bitmap.createBitmap(Paddle_Size_x,
                (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Background = new Canvas(Paper_Background);
        Paint Paint_Background = new Paint();
        Path Path_Background = new Path();
        Paint Paint_Background_Stroke = new Paint();
        Path Path_Background_Stroke = new Path();

        //      Setting options
        Paint_Background.setAntiAlias(true);
        Paint_Background.setFilterBitmap(true);
        Paint_Background.setDither(true);
        Canvas_Background.drawARGB(0,0,0,0);
        Paint_Background_Stroke.setAntiAlias(true);
        Paint_Background_Stroke.setColor(0xffCD7F32);
        Paint_Background_Stroke.setStyle(Paint.Style.STROKE);
        Paint_Background_Stroke.setStrokeWidth(5);

        //      Draw Path
        RectF RectF_Background_circle = new RectF(0,0,Paddle_Size_x,Paddle_Size_x);
        Path_Background.addArc(RectF_Background_circle,270-Sweep_Angle/2,Sweep_Angle);

        float Handle_Width = (float) ((float) Paddle_Size_x*Math.sin( Math.toRadians(180-Sweep_Angle/2)));
        float x1 = (float) (Background_Rad+Background_Rad*Math.cos( Math.toRadians(Sweep_Angle/2-90) ));
        float y1 = (float) (Background_Rad+Background_Rad*Math.sin( Math.toRadians(Sweep_Angle/2-90) ));
        int Handle_Width_Int = (int) Handle_Width;
        Path_Background.moveTo(x1,y1);
        Path_Background.lineTo(x1,y1+Handle_Length);
        Path_Background.lineTo(x1-Handle_Width,y1+Handle_Length);
        Path_Background.lineTo(x1-Handle_Width,y1);

        RectF RectF_Background_bottom_circle = new RectF(
                x1-Handle_Width,y1+Handle_Length-Handle_Width/2,x1,y1+Handle_Length+Handle_Width/2);
        Path_Background.arcTo(RectF_Background_bottom_circle,0,180,true);

        //      Draw Stroke_Path
        Path_Background_Stroke.addArc(RectF_Background_circle,270-Sweep_Angle/2,Sweep_Angle);
        Path_Background_Stroke.moveTo(x1,y1);
        Path_Background_Stroke.lineTo(x1,y1+Handle_Length);
        Path_Background_Stroke.moveTo(x1-Handle_Width,y1+Handle_Length);
        Path_Background_Stroke.lineTo(x1-Handle_Width,y1);
        Path_Background_Stroke.arcTo(RectF_Background_bottom_circle,0,180,true);

        //      Draw Paint to Paper
        Canvas_Background.drawPath(Path_Background,Paint_Background);
        Paint_Background.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Canvas_Background.drawBitmap(Background, null, Rect_Paddle,Paint_Background);

        //      Paper_Background is Done So u can apply it by
        Paint_Background.reset();
        Canvas_Paddle.drawBitmap(Paper_Background,Rect_Paddle,Rect_Paddle,Paint_Background);
        Canvas_Paddle.drawPath(Path_Background_Stroke,Paint_Background_Stroke);



        //  Case - Center


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Center = Bitmap.createBitmap(Paddle_Size_x,
                (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Center = new Canvas(Paper_Center);
        Paint Paint_Center = new Paint();
        Path Path_Center = new Path();
        Paint Paint_Center_Stroke = new Paint();
        Path Path_Center_Stroke = new Path();

        //      Setting options
        int Center_rad = (int) ((int) Paddle_Size_x*0.4); // Center radius
        Paint_Center.setAntiAlias(true);
        Paint_Center.setFilterBitmap(true);
        Paint_Center.setDither(true);
        Canvas_Center.drawARGB(0,0,0,0);
        Paint_Center_Stroke.setAntiAlias(true);
        Paint_Center_Stroke.setColor(0xffCD7F32);
        Paint_Center_Stroke.setStyle(Paint.Style.STROKE);
        Paint_Center_Stroke.setStrokeWidth(5);

        //      Draw Path
        RectF Rectf_Center = new RectF(Background_Rad-Center_rad,Background_Rad-Center_rad,
                Background_Rad+Center_rad,Background_Rad+Center_rad);
        Rect Rect_Center = new Rect(Background_Rad-Center_rad,Background_Rad-Center_rad,
                Background_Rad+Center_rad,Background_Rad+Center_rad);
        Path_Center.addArc(Rectf_Center,0,360);

        //      Draw Stroke_Path
        Path_Center_Stroke.addArc(Rectf_Center,0,360);

        //      Draw Paint to Paper
        Canvas_Center.drawPath(Path_Center,Paint_Center);
        Paint_Center.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Canvas_Center.drawBitmap(Center, null, Rect_Center,Paint_Center);

        //      Paper_Center is Done So u can apply it by
        Paint_Center.reset();
        Canvas_Paddle.drawBitmap(Paper_Center,Rect_Center,Rect_Center,Paint_Center);
        Canvas_Paddle.drawPath(Path_Center_Stroke,Paint_Center_Stroke);



        //  Case - Handle


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Handle = Bitmap.createBitmap(Paddle_Size_x,
                (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Handle = new Canvas(Paper_Handle);
        Paint Paint_Handle = new Paint();
        Path Path_Handle = new Path();
        Paint Paint_Handle_Stroke = new Paint();
        Path Path_Handle_Stroke = new Path();

        //      Setting options
        int margin = 10; // margin between background
        Paint_Handle.setAntiAlias(true);
        Paint_Handle.setFilterBitmap(true);
        Paint_Handle.setDither(true);
        Canvas_Handle.drawARGB(0,0,0,0);
        Paint_Handle_Stroke.setAntiAlias(true);
        Paint_Handle_Stroke.setColor(0xffCD7F32);
        Paint_Handle_Stroke.setStyle(Paint.Style.STROKE);
        Paint_Handle_Stroke.setStrokeWidth(5);

        //      Draw Path
        Path_Handle.moveTo(x1-margin,y1+margin);
        Path_Handle.lineTo(x1-margin,y1+Handle_Length-margin);
        Path_Handle.lineTo(x1-Handle_Width+margin,y1+Handle_Length-margin);
        Path_Handle.lineTo(x1-Handle_Width+margin,y1+margin);

        int middlex = (int) (x1-Handle_Width/2);
        int middley = (int) (y1+Handle_Length-margin);
        int half = (int) Handle_Width/2-margin;
        RectF Rectf_Handle_bottom = new RectF(middlex-half,middley-half,middlex+half,middley+half);
        Path_Handle.arcTo(Rectf_Handle_bottom,0,180,true);

        Rect Rect_Handle = new Rect((int) (x1-Handle_Width+margin),(int) (y1+margin),(int) (x1-margin), (int) (y1+Handle_Length-margin+Handle_Width/2));

        //      Draw Stroke_Path
        Path_Handle_Stroke.moveTo(x1-margin,y1+margin);
        Path_Handle_Stroke.lineTo(x1-margin,y1+Handle_Length-margin);
        Path_Handle_Stroke.moveTo(x1-Handle_Width+margin,y1+Handle_Length-margin);
        Path_Handle_Stroke.lineTo(x1-Handle_Width+margin,y1+margin);
        Path_Handle_Stroke.lineTo(x1-margin,y1+margin);
        Path_Handle_Stroke.arcTo(Rectf_Handle_bottom,0,180,true);
        //      Draw Paint to Paper
        Canvas_Handle.drawPath(Path_Handle, Paint_Handle);
        Paint_Handle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Canvas_Handle.drawBitmap(Handle,null,Rect_Handle,Paint_Handle);
        //      Paper_Handle is Done So u can apply it by
        Paint_Handle.reset();
        Canvas_Paddle.drawBitmap(Paper_Handle,Rect_Handle,Rect_Handle,Paint_Handle);
        Canvas_Paddle.drawPath(Path_Handle_Stroke,Paint_Handle_Stroke);


        Canvas_Paddle.restore();
        return Paddle;
    }
}
