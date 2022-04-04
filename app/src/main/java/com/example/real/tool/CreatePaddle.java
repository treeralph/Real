package com.example.real.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.PaddleCustomActivity;
import com.example.real.R;
import com.example.real.databasemanager.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreatePaddle {
    Context context;
    String useruid;
    Bitmap UserBackground; Bitmap UserCenter; Bitmap UserHandle;


    public CreatePaddle(){

    }

    public CreatePaddle(Context context, String useruid) {
        this.context = context;
        this.useruid = useruid;
    }
    public void Initializer(String someoneuid , Callback callback){
        // Input : Uid -> Output : Uid's 3 Paddle Bitmap
        Bitmap Test = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Test.eraseColor(0xfffaeb87);
        Bitmap InitialBG = Test;
        Bitmap InitialCenter = BitmapFactory.decodeResource(context.getResources(), R.drawable.mango_flaticon_1032525);
        Bitmap InitialHandle = BitmapFactory.decodeResource(context.getResources(), R.drawable.aucto1);
        UserBackground = InitialBG;
        UserCenter = InitialCenter;
        UserHandle = InitialHandle;
        StorageManager storageManagerForUserPaddle = new StorageManager(context,"UserPaddleImage", useruid);
        storageManagerForUserPaddle.downloadforpaddle("UserPaddleImage/" + useruid, new Callback() {
            @Override
            public void OnCallback(Object object) {
                if(object == null){
                }
                else{ Map<String, Bitmap> Map = (java.util.Map<String, Bitmap>) object;
                    for(String key : Map.keySet()){
                        switch (key){
                            case "Background" : UserBackground = Map.get("Background");break;
                            case "Center" : UserCenter = Map.get("Center");break;
                            case "Handle" : UserHandle = Map.get("Handle");break;
                            default: Toast.makeText(context, key, Toast.LENGTH_SHORT).show();break;
                        }}
                }
                List<Bitmap> bitmapList = new ArrayList<>();
                bitmapList.add(UserBackground);bitmapList.add(UserCenter);bitmapList.add(UserHandle);
                callback.OnCallback(bitmapList);
            }
        });

    }

    public Bitmap createPaddle(Bitmap Background, Bitmap Center, Bitmap Handle, int Paddle_Size_x){
        // Input : 3 Paddle Bitmap -> Output : 1 Paddlized Bitmap

        //Base Options
        int Sweep_Angle = 320;
        float BOTTom_Rad = (float) ((float) Paddle_Size_x*Math.sin( Math.toRadians(180-Sweep_Angle/2)));
        int Handle_Length = (int) (Paddle_Size_x*0.5);
        float display_ratio = 1 + (float)Handle_Length/Paddle_Size_x + (float)BOTTom_Rad/(2*Paddle_Size_x); // X:Y ratio


        int Padding = (int) (Paddle_Size_x*0.025);
        Bitmap Paddle = Bitmap.createBitmap(Paddle_Size_x+6*Padding,
                (int) (Paddle_Size_x*display_ratio+4*Padding), Bitmap.Config.ARGB_8888);
        Rect Rect_Paddle = new Rect(0,0,Paddle_Size_x+2*Padding, (int) (Paddle_Size_x*display_ratio)+2*Padding);
        final int color = 0xff424242;

        int Background_Rad = Paddle_Size_x/2;
        Canvas Canvas_Paddle = new Canvas(Paddle);
        Canvas_Paddle.save();
        Canvas_Paddle.rotate(0,Background_Rad,Background_Rad); // rotator
        Canvas_Paddle.translate((float) (Padding*2),0);


        //  Case - Background


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Background = Bitmap.createBitmap(Padding + 2*Padding + Paddle_Size_x,Padding +Padding +
                (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Background = new Canvas(Paper_Background);
        Paint Paint_Background = new Paint();
        Path Path_Background = new Path();
        Paint Paint_Background_Stroke = new Paint();
        Path Path_Background_Stroke = new Path();
        Paint Paint_Background_Shadow = new Paint();

        //      Setting options
        Paint_Background.setAntiAlias(true);
        Paint_Background.setFilterBitmap(true);
        Paint_Background.setDither(true);
        Canvas_Background.drawARGB(0,0,0,0);
        Paint_Background_Stroke.setAntiAlias(true);
        Paint_Background_Stroke.setColor(Color.BLACK);
        Paint_Background_Stroke.setStyle(Paint.Style.STROKE);
        Paint_Background_Stroke.setStrokeWidth(Padding);

        Paint_Background_Shadow.setAntiAlias(true);
        Paint_Background_Shadow.setColor(Color.BLACK);
        Paint_Background_Shadow.setStyle(Paint.Style.STROKE);
        Paint_Background_Shadow.setStrokeWidth((float) (Padding*1.5));

        //      Draw Path
        RectF RectF_Background_circle = new RectF(Padding,Padding,Padding + Paddle_Size_x,Padding + Paddle_Size_x);
        Path_Background.addArc(RectF_Background_circle,270-Sweep_Angle/2,Sweep_Angle);

        float Handle_Width = (float) ((float) Paddle_Size_x*Math.sin( Math.toRadians(180-Sweep_Angle/2)));
        float x1 = (float) (Padding + Background_Rad+Background_Rad*Math.cos( Math.toRadians(Sweep_Angle/2-90) ));
        float y1 = (float) (Padding + Background_Rad+Background_Rad*Math.sin( Math.toRadians(Sweep_Angle/2-90) ));
        int Handle_Width_Int = (int) Handle_Width;
        Path_Background.moveTo(x1,y1);
        Path_Background.lineTo(x1,y1+Handle_Length);
        Path_Background.lineTo(x1-Handle_Width,y1+Handle_Length);
        Path_Background.lineTo(x1-Handle_Width,y1);

        RectF RectF_Background_bottom_circle = new RectF(
                 x1-Handle_Width, y1+Handle_Length-Handle_Width/2, x1, y1+Handle_Length+Handle_Width/2);
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

        // Shadow test
        Canvas_Paddle.save();
        Canvas_Paddle.translate(1,1);
        Canvas_Paddle.drawPath(Path_Background_Stroke,Paint_Background_Shadow);
        if (Paddle_Size_x>=150){
            for(int i =0; i<Paddle_Size_x/150 ;i++){
                Canvas_Paddle.translate(2,2);
                Canvas_Paddle.drawPath(Path_Background_Stroke,Paint_Background_Shadow);
            }
        }
        Canvas_Paddle.translate(1,1);
        Canvas_Paddle.drawPath(Path_Background_Stroke,Paint_Background_Shadow);
        Canvas_Paddle.restore();
        Canvas_Paddle.drawPath(Path_Background_Stroke,Paint_Background_Stroke);
        Canvas_Paddle.drawBitmap(Paper_Background,Rect_Paddle,Rect_Paddle,Paint_Background);



        //  Case - Center


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Center = Bitmap.createBitmap(Padding + Padding + Paddle_Size_x,
                Padding + Padding + (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Center = new Canvas(Paper_Center);
        Paint Paint_Center = new Paint();
        Path Path_Center = new Path();
        Paint Paint_Center_Stroke = new Paint();
        Path Path_Center_Stroke = new Path();

        //      Setting options
        int Center_rad = (int) ((int) Paddle_Size_x*0.45); // Center radius
        Paint_Center.setAntiAlias(true);
        Paint_Center.setFilterBitmap(true);
        Paint_Center.setDither(true);
        Canvas_Center.drawARGB(0,0,0,0);
        Paint_Center_Stroke.setAntiAlias(true);
        Paint_Center_Stroke.setColor(0xffCD7F32);
        Paint_Center_Stroke.setStyle(Paint.Style.STROKE);
        Paint_Center_Stroke.setStrokeWidth(5);

        //      Draw Path
        RectF Rectf_Center = new RectF(Padding + Background_Rad-Center_rad,Padding + Background_Rad-Center_rad,
                Padding + Background_Rad+Center_rad,Padding + Background_Rad+Center_rad);
        Rect Rect_Center = new Rect(Padding + Background_Rad-Center_rad,Padding + Background_Rad-Center_rad,
                Padding + Background_Rad+Center_rad,Padding + Background_Rad+Center_rad);
        Path_Center.addArc(Rectf_Center,0,360);

        //      Draw Stroke_Path
        Path_Center_Stroke.addArc(Rectf_Center,0,360);

        //      Draw Paint to Paper
        Canvas_Center.drawPath(Path_Center,Paint_Center);
        Paint_Center.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        if(Center.getHeight() >= Center.getWidth()){
            Rect Rect_Center_MaxSquare = new Rect(0,(-Center.getWidth()+Center.getHeight())/2,Center.getWidth(),(Center.getWidth()+Center.getHeight())/2);
            Canvas_Center.drawBitmap(Center, Rect_Center_MaxSquare, Rect_Center,Paint_Center);
        } else{
            Rect Rect_Center_MaxSquare = new Rect((Center.getWidth()-Center.getHeight())/2,0,(Center.getWidth()+Center.getHeight())/2,Center.getHeight());
            Canvas_Center.drawBitmap(Center, Rect_Center_MaxSquare, Rect_Center,Paint_Center);
        }

        //      Paper_Center is Done So u can apply it by
        Paint_Center.reset();
        Canvas_Paddle.drawBitmap(Paper_Center,Rect_Center,Rect_Center,Paint_Center);
        //Canvas_Paddle.drawPath(Path_Center_Stroke,Paint_Center_Stroke);



        //  Case - Handle


        //      Create - Paper Canvas Paint Path StrokePaint StrokePath
        Bitmap Paper_Handle = Bitmap.createBitmap(Padding + Padding + Paddle_Size_x,
                Padding + Padding + (int) (Paddle_Size_x*display_ratio), Bitmap.Config.ARGB_8888);
        Canvas Canvas_Handle = new Canvas(Paper_Handle);
        Paint Paint_Handle = new Paint();
        Path Path_Handle = new Path();
        Paint Paint_Handle_Stroke = new Paint();
        Path Path_Handle_Stroke = new Path();

        //      Setting options
        int margin = Background_Rad-Center_rad; // margin between background
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

        float Handle_Ratio = ((y1+Handle_Length-margin+Handle_Width/2)-(y1+margin))/((x1-margin)-(x1-Handle_Width+margin));//2.36
        if(Handle.getWidth()*Handle_Ratio >= Handle.getHeight()){
            Rect Rect_Handle_MaxRectangle = new Rect((int)(Handle.getWidth()-Handle.getHeight()/Handle_Ratio)/2,
                    0,
                    (int)(Handle.getWidth()+Handle.getHeight()/Handle_Ratio)/2,
                    Handle.getHeight());
            Canvas_Handle.drawBitmap(Handle,Rect_Handle_MaxRectangle,Rect_Handle,Paint_Handle);
        }else{
            Rect Rect_Handle_MaxRectangle = new Rect(0,(int)(Handle.getHeight()-Handle.getWidth()*Handle_Ratio)/2,Handle.getWidth(),(int)(Handle.getHeight()+Handle.getWidth()*Handle_Ratio)/2);
            Canvas_Handle.drawBitmap(Handle,Rect_Handle_MaxRectangle,Rect_Handle,Paint_Handle);
        }

        //      Paper_Handle is Done So u can apply it by
        Paint_Handle.reset();
        Canvas_Paddle.drawBitmap(Paper_Handle,Rect_Handle,Rect_Handle,Paint_Handle);
        //Canvas_Paddle.drawPath(Path_Handle_Stroke,Paint_Handle_Stroke);


        Canvas_Paddle.restore();

        return Paddle;
    }
}
