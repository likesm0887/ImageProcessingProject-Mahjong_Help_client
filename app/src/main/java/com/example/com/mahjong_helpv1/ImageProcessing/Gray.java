package com.example.com.mahjong_helpv1.ImageProcessing;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_16SC3;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;

public class Gray {



    public static Bitmap getGrayImage(Bitmap bmp) {
        Mat image = new Mat(bmp.getWidth(), bmp.getHeight(), CV_16SC3);
        Utils.bitmapToMat(bmp, image);
        Mat gray =new Mat(bmp.getWidth(), bmp.getHeight(), CV_16SC3);
        cvtColor(image, gray, COLOR_RGB2GRAY);
        Mat bin = new Mat();
        //threshold(gray, bin, 120, 255, 100);
        Utils.matToBitmap(image, bmp);
        return bmp;
    }


}
