import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class twoPart {

	int w,h;
	float[] elements = { -1.0f, -1.0f, -1.0f,
            			 -1.0f, 8.f, -1.0f,
                         -1.0f, -1.0f, -1.0f};
	
	void edge(BufferedImage image1,BufferedImage image2) throws IOException
	{
		w=h=128;
		Kernel kernel = new Kernel(3, 3, elements);
		BufferedImage image12 = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage image22 = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		//cop.filter(image1, image12);
		//cop.filter(image2, image22);
		image12=image1;
		image22=image2;
		int[] pix1,pix2,dummy=null;
		
		Raster data;
		data = image12.getData();		
		pix1 = data.getPixels(0, 0, w,h, dummy);
		data = image22.getData();
		pix2 = data.getPixels(0, 0, w,h, dummy);	
		
		pix1 = DCT(convertToMatrix(pix1));
		pix2 = DCT(convertToMatrix(pix2));
		
		WritableRaster rster = image12.getRaster();						
		rster.setPixels(0, 0, w, h, pix1);
		image12.copyData(rster);
	
		rster = image22.getRaster();
		rster.setPixels(0, 0, w, h, pix2);
		image22.copyData(rster);
		
		BufferedImage[][] ImageMatrixHor = new BufferedImage[2][2];
		BufferedImage[][] ImageMatrixVer = new BufferedImage[2][2];
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<2;j++)
			{
				ImageMatrixHor[i][j] = new BufferedImage(w, h/2, BufferedImage.TYPE_BYTE_GRAY);
				ImageMatrixVer[i][j] = new BufferedImage(w/2, h, BufferedImage.TYPE_BYTE_GRAY);
			}
		}
		
		ImageMatrixHor[0][0] = image12.getSubimage(0, 0, w, h/2);
		ImageMatrixHor[0][1] = image12.getSubimage(0, h/2, w, h/2);
		ImageMatrixHor[1][0] = image22.getSubimage(0, 0, w, h/2);
		ImageMatrixHor[1][1] = image22.getSubimage(0, h/2, w, h/2);
		
		ImageMatrixVer[0][0] = image12.getSubimage(0, 0, w/2, h);
		ImageMatrixVer[0][1] = image12.getSubimage(w/2, 0, w/2, h);
		ImageMatrixVer[1][0] = image22.getSubimage(0, 0, w/2, h);
		ImageMatrixVer[1][1] = image22.getSubimage(w/2, 0, w/2, h);

		
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<2;j++)
			{
				ImageIO.write(ImageMatrixHor[i][j], "png", new File("/home/rohit/Pictures/test1/Hori_"+i+""+j+".png"));
				ImageIO.write(ImageMatrixVer[i][j], "png", new File("/home/rohit/Pictures/test1/Veri_"+i+""+j+".png"));
			}
			
		}		
		
		// image1-left image2-left	
		System.out.println("\n\nED Img1-top and Img2-top is " + eucld_distance(vectorize(ImageMatrixHor[0][0]), vectorize(ImageMatrixHor[1][0])));

		//image1-left image2-right	
		System.out.println("ED Img1-top and Img2-bottom is " + eucld_distance(vectorize(ImageMatrixHor[0][0]), vectorize(ImageMatrixHor[1][1])));
		
		//image1-right image2-left	
		System.out.println("ED Img1-bottom and Img2-top is " + eucld_distance(vectorize(ImageMatrixHor[0][1]), vectorize(ImageMatrixHor[1][0])));
		
		//image1-right image2-right
	
		System.out.println("ED Img1-bottom and Img2-bottom is " + eucld_distance(vectorize(ImageMatrixHor[0][1]), vectorize(ImageMatrixHor[1][1])));
		
		
		// image1-left image2-left	
		System.out.println("\n\nED Img1-left and Img2-left is " + eucld_distance(vectorize(ImageMatrixVer[0][0]), vectorize(ImageMatrixVer[1][0])));

		//image1-left image2-right	
		System.out.println("ED Img1-left and Img2-right is " + eucld_distance(vectorize(ImageMatrixVer[0][0]), vectorize(ImageMatrixVer[1][1])));
		
		//image1-right image2-left	
		System.out.println("ED Img1-right and Img2-left is " + eucld_distance(vectorize(ImageMatrixVer[0][1]), vectorize(ImageMatrixVer[1][0])));
		
		//image1-right image2-right
	
		System.out.println("ED Img1-right and Img2-right is " + eucld_distance(vectorize(ImageMatrixVer[0][1]), vectorize(ImageMatrixVer[1][1])));
	}
	
	
	
	double IMED(int[] mat1,int[] mat2)
	{
		double sum=0;
		for(int i=0;i<mat1.length;i++)
			for(int j=0;j<mat2.length;j++)
			{
				sum+=gFunction(i,j)*(mat1[i]-mat2[i])*(mat1[j]-mat2[j]);
			}
		
		return sum;
	}
	
	double gFunction(int i,int j)
	{
		return ((1/(2*Math.PI))*(Math.exp(-(Math.pow(i/128-j/128,2)+Math.pow(i%128-j%128,2))/2)));
	}
	
	
	
	
	double eucld_distance(int[] mat1,int[] mat2)
	{
		int sum=0;
		for(int i=0;i<mat1.length;i++)
			sum+=(mat1[i]-mat2[i])*(mat1[i]-mat2[i]);
		return Math.sqrt(sum);
	}

	int[] vectorize(BufferedImage img1)
	{
		int [] pix1,dummy=null;
		Raster data;
		data = img1.getData();		
		pix1 = data.getPixels(0, 0, w/2,h/2, dummy);
		return pix1;
	}
	
	int[][] convertToMatrix(int[] pix)
	{
		int k=0;
		int[][] matrix = new int[h][w];
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
			{
				matrix[i][j] = pix[k++];
				
			}
				return matrix;
	}
	
	
	int[] DCT(int[][] matrix) throws IOException
	{
		int[][] temp = new int[h][w];
		
		int m, n, x, y, u, v, N=8;
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
		
		return tempar;
	}	

	
	
	
	public static void main(String[] args) throws IOException {
		
		BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/image1.jpg"));
		BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/image3.jpg"));
		
		//BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		//BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/5.png"));
		
		//BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		//BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/1.png"));
		
		int w,h;
		w=h=128;
		
		BufferedImage image1 = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image1.getGraphics();
        g.drawImage(img1, 0, 0,w,h, null);
        g.dispose();
        
		BufferedImage image2 = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
        g = image2.getGraphics();
        g.drawImage(img2, 0, 0,w,h, null);
        g.dispose();
       
        twoPart dct = new twoPart();
        dct.edge(image1, image2);

	}

}
