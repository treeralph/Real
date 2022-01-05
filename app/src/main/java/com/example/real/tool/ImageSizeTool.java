package com.example.real.tool;

import android.graphics.Bitmap;

public class ImageSizeTool {

    private int ThresholdPixel;

    public ImageSizeTool(int thresholdPixel) {
        ThresholdPixel = thresholdPixel;
    }

    public int getRatio(Bitmap bitmap){

        int Ratio = 1;

        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        float minimum;
        if(bitmapHeight > bitmapWidth){ minimum = bitmapWidth; }
        else{ minimum = bitmapHeight; }


        if(minimum > ThresholdPixel) {
            while (minimum > ThresholdPixel) {
                minimum = minimum / 2;
                Ratio = Ratio * 2;
            }
            return Ratio/2;
        } else {
            return Ratio;
        }
    }
}
