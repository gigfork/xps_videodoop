/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastcornerdetector;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author tschinke
 */
public class FastCornerDetector {

   
    private int threshold_detection=70; //windowsize
    private int threshold_scoring=0;



    public List<XY> detectFASTCornerFeatures(Image img,int w,int h,int n)
    {

    	
        int[] data=LowLevelConversion.convert2grey_fast((BufferedImage)img);
        int width=img.getWidth(null);
        int height=img.getHeight(null);

        int[] num_corners=new int[1];
        long start=System.nanoTime();


        List<XY> corners=Fast.fast9_detect_nonmax2(data, width, height, width, threshold_detection,threshold_scoring, num_corners);
       long durance=(System.nanoTime()-start)/1000000;
       System.out.println(n+"--- detect-durance "+durance+"  "+corners.size());
        return corners;
    }
}
