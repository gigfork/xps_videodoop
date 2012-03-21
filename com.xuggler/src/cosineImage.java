import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import java.awt.image.PixelGrabber;

public class cosineImage {
	
	static int w,h;
	public static void main(String[] args) throws IOException
	{
		
		BufferedImage image1 = ImageIO.read(new File("/home/rohit/Pictures/5.png"));
		BufferedImage image2 = ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		BufferedImage image3 = ImageIO.read(new File("/home/rohit/Pictures/1.png"));
		w=h=128;
		BufferedImage img1= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img1.getGraphics();
        g.drawImage(image1, 0, 0,w,h, null);
        g.dispose();
        
		BufferedImage img2= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        g = img2.getGraphics();
        g.drawImage(image2, 0, 0,w,h, null);
        g.dispose();
        
        BufferedImage img3= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        g = img3.getGraphics();
        g.drawImage(image3, 0, 0,w,h, null);
        g.dispose();
		
		int[] pix1,pix2,pix3 ;
		int[][] pixels = new int[h][w];
		int[] dummy=null;
		
		Raster data;
		data = img1.getData();		
		pix1 = data.getPixels(0, 0, w,h, dummy);
		int k=0;
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				pixels[i][j] = (char)pix1[k++];	
		pix1=DCT(pixels);
		
		data = img2.getData();	
		pix2= data.getPixels(0, 0, w,h, dummy);
		k=0;
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				pixels[i][j] = (char)pix2[k++];	
		pix2=DCT(pixels);
		
		data = img3.getData();	
		pix3= data.getPixels(0, 0, w,h, dummy);
		k=0;
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				pixels[i][j] = (char)pix3[k++];	
		pix3=DCT(pixels);
		
		for(int i=0;i<h*w;i++)
		{
        		//System.out.print(pix1[i]+" ");
		}
		//System.out.print("\n");
		for(int i=0;i<h*w;i++)
		{
        		//System.out.print(pix2[i]+" ");
		}
	//	System.out.print(eucld_distance(pix1,pix2)+"");

		
	}
	
	static double eucld_distance(int[] mat1,int[] mat2)
	{
		int sum=0;
		for(int i=0;i<mat1.length;i++)
			sum+=(mat1[i]-mat2[i])*(mat1[i]-mat2[i]);
		return Math.sqrt(sum);
	}
	
	
	static int[] DCT(int[][] matrix) throws IOException
	{

		int[][] temp = new int[h][w];
		
		int m, n, x, y, u, v, N=32;
		double  in[][], dct[][], sum, au, av;
		double  n1=Math.sqrt(1.0/N), n2=Math.sqrt(2.0/N);
		in = new double[h][w];

		// For each NxN block[m,n]
	    for ( m=0; m<h; m+=N) 
	    {
	      for ( n=0; n<w; n+=N) 
	      {
	    	// For each pixel[u,v] in block[m,n]
	        for (u=m; u<m+N; u++) 
	        {
	          au = (u==m)? n1: n2;
	          for (v=n; v<n+N; v++) 
	          {
	            av = (v==n)? n1: n2;
	            // Sum up all pixels in the block
	            for (x=m, sum=0; x<m+N; x++) {
	              for (y=n; y<n+N; y++) {
	                in[x][y] = matrix[x][y] - 128.0;  // Subtract by 128
	                sum += in[x][y] * Math.cos((2*(x-m)+1)*(u-m)*Math.PI/(2*N)) *
	                                  Math.cos((2*(y-n)+1)*(v-n)*Math.PI/(2*N));
	              }
	            }
		    temp[u][v] = (int)Math.round(au * av * sum);

	          } // for v
	        } // for u

	      }  // for n
	    }  // for m
		
		
		int[] tempar = new int[w*h];
		int k=0;
		for(int i=0;i<h;i++)
		{
			for(int j=0;j<w;j++)
        		tempar[k++]=temp[i][j];			
		}
		
		FileWriter f1 = new FileWriter("/home/rohit/Videos/pack/cont1.txt"); 
		
		/*for(int i=0;i<h;i++)
		{
			for(int j=0;j<w;j++)
        		f1.append(temp[i][j]+" ");
			f1.append("\n");
		}
		*/
		Arrays.sort(tempar);
		int count=0;
		for(int i=w*h-1;i>w*h-50;i--)
		{
				System.out.print(tempar[i]+" ");		
				f1.append(tempar[i]+" ");
		}
		f1.append("\n");
		f1.close();
				System.out.println("--------------------------");
		return tempar;
	}
	
	
	
	
	
	

}
