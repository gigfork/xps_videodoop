import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.color.ColorSpace;
import javax.imageio.ImageIO;

import com.xuggle.xuggler.demos.VideoImage.ImageComponent;
import filter.*;

public class APIDQ{
    
	int w,h;
    float[] elements = { -1.0f, -1.0f, -1.0f,
            			 -1.0f, 8.f, -1.0f,
                         -1.0f, -1.0f, -1.0f };
	
    public APIDQ(int w,int h) {
    	this.w = w;
    	this.h = h;
	}
    
	public void calc_APIDQ(BufferedImage org_image,int[] range,String frameno,FileWriter vector) throws IOException
	{        
		BufferedImage image = org_image;
        BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(image, 0, 0,w,h, null);
        g.dispose();

        GaussianFilter filter = new GaussianFilter();
        filter.setRadius(4);
        filter.filter(img, img);
   
		int[] pixelArray,dummy=null;
		Raster data = img.getData();		
		pixelArray = data.getPixels(0, 0, w,h,dummy);
        
		int[][] mat = convertToMatrix(pixelArray);
		double[][] mat2 = new double[(w-1)*(h-1)][2];
		
		int k=0;
		double dx,dy,theta;
		double t;
		Double rd;
		for(int i=0;i<h-1;i++)
		{
			for(int j=0;j<w-1;j++)
			{
				dx = mat[i][j+1]-mat[i][j];
				dy = mat[i+1][j]-mat[i][j];
				rd = Math.sqrt(dx*dx + dy*dy);
				if(dx!=0)theta = Math.atan(dy/dx)*180/Math.PI;
				else if(rd==0) theta = 0;
				else if(dy>0) theta = 90;
				else theta = -90;
				t = theta;
				if(t < -180/16)
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t+360;
				}
				else
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t;
				}
			}
		}		
		generateHist(mat2,range,vector);
		
	}
	
	
	public void calc_APIDQ2(BufferedImage org_image,int[] range,String frameno,FileWriter vector) throws IOException
	{        
		BufferedImage image = org_image;
        BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(image, 0, 0,w,h, null);
        g.dispose();
        
        GaussianFilter filter = new GaussianFilter();
        filter.setRadius(4);
        filter.filter(img, img);
     
		int[] pixelArray,dummy=null;
		Raster data = img.getData();		
		pixelArray = data.getPixels(0, 0, w,h,dummy);
        
		int[][] mat = convertToMatrix(pixelArray);
		double[][] mat2 = new double[(w-4)*(h-8)][2];
		
		int k=0;
		double dx,dy,r;
		double theta;
		Float t;
		Double rd;
		for(int i=4;i<h-4;i++)
		{
			for(int j=1;j<w-4;j++)
			{
				dx = mat[i+4][j+4]-mat[i][j];
				dy = mat[i-4][j+4]-mat[i][j];
				rd = r = Math.sqrt(dx*dx + dy*dy);
				if(dx!=0)theta = Math.atan(dy/dx)*180/Math.PI;
				else if(dy==0) theta = 0;
				else if(dy>0) theta = 90;
				else theta = -90;
				t=(float)theta;
				if(t<-180/16)
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t+360;
				}
				else
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t;
				}
			}
		}		
		generateHist(mat2,range,vector);
		
	}	
	
	double eucld_distance(int[] mat1,int[] mat2)
	{
		int sum=0;
		for(int i=0;i<mat1.length;i++)
			sum+=(mat1[i]-mat2[i])*(mat1[i]-mat2[i]);
		return Math.sqrt(sum);
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
	
	void generateHist(double[][] mat,int[] range,FileWriter vector) throws IOException
	{
		int[][]	count = new int[32][32];
		double val1,val2;
		int m,n;
		
		for(int i=0;i<mat.length;i++)
		{
			val1 = mat[i][0];
			val2 = mat[i][1];
			vector.append(val1+""+',');

			if(val1==0)m=0;
			else if(val1<=0.25)m=1;
			else if(val1<=0.5)m=2;
			else if(val1<=0.75)m=3;
			else if(val1<=1)m=4;
			else if(val1<=1.25)m=5;
			else if(val1<=1.5)m=6;
			else if(val1<=1.75)m=7;
			else if(val1<=2)m=8;
			else if(val1<=2.5)m=9;
			else if(val1<=3)m=10;
			else if(val1<=4)m=11;
			else if(val1<=6)m=12;
			else if(val1<=7)m=13;
			else if(val1<=8)m=14;
			else if(val1<=9)m=15;
			else if(val1<=10)m=16;
			else if(val1<=12)m=17;
			else if(val1<=14)m=18;
			else if(val1<=16)m=19;
			else if(val1<=18)m=20;
			else if(val1<=20)m=21;
			else if(val1<=23)m=22;
			else if(val1<=26)m=23;
			else if(val1<=29)m=24;
			else if(val1<=33)m=25;
			else if(val1<=37)m=26;
			else if(val1<=40)m=27;
			else if(val1<=50)m=28;
			else if(val1<=60)m=29;
			else if(val1<=70)m=30;
			else m=31;
			
			n=(int)((val2/(180/16)+1)/2 );
			count[m][n]++;
		}
		vector.append('\n');
		System.out.println(mat.length+" -- "+count[0][0]);
		int k=0;
		for(int i=0;i<32;i++)
			for(int j=0;j<32;j++)
				range[k++] = count[i][j];		
	}
	
	public void APIDQ_start(int[] Qrange1,int[] Qrange2, int[] Qrange_cl,File infile,String videoname,String frameno,FileWriter output,FileWriter vector) throws IOException 
	{
		int[] range1 = new int[1024];
		BufferedImage image = ImageIO.read(infile);
		calc_APIDQ(image,range1,frameno,vector);	
		
		int[] range2 = new int[64];
		calc_APIDQ2(image,range2,frameno,vector);

		colorHistogram ch = new colorHistogram();	

		
		int rTheta1 = calcSimilarity(Qrange1,range1);
		int rTheta2 = calcSimilarity(Qrange2,range2);

		int cHist = (int)eucld_distance(Qrange_cl,ch.calcHistogram(image, w, h));

		System.out.print(videoname+" -- "+frameno+" --->"+rTheta1+" , " +rTheta2+"  ,  "+ cHist +"\n");
		if((rTheta1>220 && cHist<2000)||(rTheta1>225 && rTheta2>225 && cHist<4000))
			output.append(videoname+","+frameno+","+rTheta1+","+rTheta2+","+ cHist +"\n");
	}

	public int calcSimilarity(int[] range1,int[] range2)
	{
		int sum=0;
		for(int i=0;i<range1.length;i++)
			sum+=(range1[i]>range2[i]?range2[i]:range1[i]);	

		return sum/range1.length;
	}
}
