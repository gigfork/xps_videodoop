import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;


public class colorHistogram {
	
	
	public int[] calcHistogram(BufferedImage image1,int w,int h,String frameno) throws IOException
	{
		BufferedImage image = image1;
		BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_INT_BGR);
        Graphics g1 = img.getGraphics();
        g1.drawImage(image, 0, 0,w,h, null);
        g1.dispose();
        
        image = img;
		
		//FileWriter f1 = new FileWriter("/home/rohit/Documents/FYP/color_packs/hist.csv");
		int[] result = new int[64];
		int i,j,k,r,g,b;
		
		int[] pixelArray,dummy=null;
		Raster data = image.getData();		
		pixelArray = data.getPixels(0, 0, w,h,dummy);
		
		int[][][] mat = new int[4][4][4];
		
		for(int l=0;l<image.getHeight();l++)
		{
			for(int m=0;m<image.getHeight();m++)
			{
				r = image.getRGB(l,m) & 0xff;
				g = (image.getRGB(l,m)>>8) & 0xff;
				b = (image.getRGB(l,m)>>16) & 0xff;	
				
				/*
				if((r+g+b)!=0)
				image.setRGB(l,m,image.getRGB(l,m)/(r+g+b));				
				else 
				image.setRGB(l,m,0);

				r = image.getRGB(l,m) & 0xff;
				g = (image.getRGB(l,m)>>8) & 0xff;
				b = (image.getRGB(l,m)>>16) & 0xff;	
				*/
				//f1.append(r+","+g+","+b);
				//f1.append('\n');
				mat[r/64][g/64][b/64]++;	
			}
			
		}						
		//ImageIO.write(image, "png", new File("/home/rohit/Documents/FYP/color_packs/Bframes/"+n+".png"));

		int l=0;
		for(int p=0;p<4;p++)
			for(int q=0;q<4;q++)
				for(int s=0;s<4;s++)
					result[l++] = mat[p][q][s];
	//	f1.flush();
		//f1.close();
		return result;
	}
	

}
