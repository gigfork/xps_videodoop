import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.PixelGrabber;


public class simplePixelComparison {
	
	static int w,h;
	public static void main(String[] args) throws IOException
	{
		BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/5.png"));
		BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		
		w=h=128;

		BufferedImage image1 = new BufferedImage(w, h,BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = image1.getGraphics();
        g.drawImage(img1, 0, 0,w,h, null);
        g.dispose();
        
		BufferedImage image2 = new BufferedImage(w, h,BufferedImage.TYPE_3BYTE_BGR);
        g = image2.getGraphics();
        g.drawImage(img2, 0, 0,w,h, null);
        g.dispose();
        
        
		
		int[] pixel1 = new int[h*w];	
		int[] pixel2 = new int[h*w];
		image1.getRGB(0, 0, w, h, pixel1, 0, w);
		image2.getRGB(0, 0, w, h, pixel2, 0, w);
		
		int sum1=0,sum2=0,sum3=0;
		

		
		System.out.println(eucld_distance(pixel1,pixel2)+"");		

	}
}

double eucld_distance(int[] mat1,int[] mat2)
{
	int sum=0;
	for(int i=0;i<mat1.length;i++)
		sum+=(mat1[i]-mat2[i])*(mat1[i]-mat2[i]);
	return Math.sqrt(sum);
}
	
	