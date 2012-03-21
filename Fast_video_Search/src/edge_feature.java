import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.qos.logback.classic.filter.ThresholdFilter;

import filter.GaussianFilter;

public class edge_feature {

	float[] elements = { -1.0f, -1.0f, -1.0f,
			-1.0f, 8.0f, -1.0f,
			-1.0f, -1.0f, -1.0f };
	
	float[] elements1 = { -1.0f, -1.0f, -1.0f,
			-1.0f, 10.0f, -1.0f,
			-1.0f, -1.0f, -1.0f };
	
	int totLineP;
	double threshold;
	
	public int[] get_edge_feature(BufferedImage image1,int w,int h,int n) throws IOException
	{
		threshold = 0.15;
		BufferedImage image= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
	    Graphics g = image.getGraphics();
	    g.drawImage(image1, 0, 0,w,h, null);
	    g.dispose();		
	    
		GaussianFilter filter = new GaussianFilter();
	    filter.setRadius(4);
	    filter.filter(image, image);
	    	    
		BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);		
		
		Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        cop.filter(image,img);
        image = img;
        
        img= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        
        kernel = new Kernel(3, 3, elements1);
        cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        cop.filter(image,img);
        image = img;       
        
        int[] pixel,dummy=null;
		WritableRaster rster = image.getRaster();		
		pixel = rster.getPixels(0, 0, w,h,dummy);
        
        totLineP = binarize_image(pixel);        

        rster.setPixels(0, 0, w, h, pixel);
        image.copyData(rster);
        
        ImageIO.write(image, "png", new File("/home/rohit/Documents/FYP/color_packs/Bframes/"+n+".png"));

        
        return generate_edge_histrogram(rster,w,h);   
        
	}
	
	public int binarize_image(int[] pixel)
	{
		int count =0;
	    for (int i=0; i<pixel.length; i++) 
	    {
	        if (pixel[i] < 128)
	          pixel[i] = 0;
	        else
	        {
	          pixel[i] = 255;
	          count++;
	        }	    
	    }
	    return count;
	}

	int[] generate_edge_histrogram(WritableRaster rster,int w, int h) throws IOException
	{
		int[] hist = new int[256];
		int[] csi = new int[8];
		int[] dummy = null;
		for (int i=0; i<h; i++)
			for (int j=0; j<w; j++) 
			{
				if(rster.getPixel(i,j,dummy)[0]!=0)
				{				
					make_csi_zero(csi);
					count_pixs(hist,rster,csi,i,j,w,h);
				}				
			}		
		return hist;
	}
	
	void count_pixs(int[] hist,WritableRaster rster,int[] csi,int i,int j,int w,int h)
	{
		int j2,max,i2,max1;
		int  val = 0,val1=0,val2=0,val3=0;
		int[] dummy = null;
		
		for (int i1=0; i1<h; i1++ )
			for (int j1=0; j1<w; j1++) 
			{
				if(rster.getPixel(i1,j1,dummy)[0]!=0)
				{					
					if(j1>=j && i1<=i)
					{
						j2 = j1-j;
						max = i+j; 
						if(j2+i1>=max)csi[0]++;
						else csi[1]++;
					}
					else if(j1<j && i1<i)
					{
						if(j1>=i1)
							csi[2]++;
						else csi[3]++;					
					}
					else if(j1<=j && i1>=i)
					{
						i2 = i1-i;
						max = i+j;
						if(i2+j1<=max)
							csi[4]++;
						else
							csi[5]++;						
					}
					else
					{
						i2=i1-i;
						j2=j1-j;
						if(j2>=i2)
							csi[6]++;
						else csi[7]++;					
					}
					
				}
			}		
		
		for (int k=0; k<8; k++)
		{			
			if(totLineP!=1)
				csi[k] = (float)csi[k]/(totLineP-1) > threshold ? 1:0;
			else
				csi[k] = 0;			

			if(csi[k]!=0)
			{
				val |= 1<<k;
				if(k!=7)
					val1 |= 1<<(k+1);
				else
					val1 |= 1<<0;
			}
		}	
		
		hist[val]++;	
	}
	
	void make_csi_zero(int[] csi)
	{
		for (int i=0; i<8; i++)
			csi[i] = 0;
	}
	
}
	


