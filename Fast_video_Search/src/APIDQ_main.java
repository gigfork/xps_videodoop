import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;


public class APIDQ_main {

	int w=128;
	int h=128;

	public void calc_APIDQ(BufferedImage image,int[] range) throws IOException
	{
		BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_USHORT_555_RGB);
        Graphics g = img.getGraphics();
        g.drawImage(image, 0, 0,w,h, null);
        g.dispose();
        
		FileWriter f1 = new FileWriter("/home/rohit/Documents/APIDQ_data2.csv");
		FileWriter f2 = new FileWriter("/home/rohit/Documents/APIDQ_pixels2.csv");
		FileWriter f3 = new FileWriter("/home/rohit/Documents/APIDQ_matrix2.csv");
		//this.w = w;
		//this.h = h;
		int[] pixelArray,dummy=null;
		Raster data = img.getData();		
		pixelArray = data.getPixels(0, 0, w,h,dummy);
		
		int[][] mat = convertToMatrix(pixelArray);
		double[][] mat2 = new double[(w-1)*(h-1)][2];
		
		int k=0;
		double dx,dy,r;
		double theta;
		Float t;
		Double rd;
		for(int i=0;i<h-1;i++)
		{
			for(int j=0;j<w-1;j++)
			{
				dx = mat[i][j+1]-mat[i][j];
				dy = mat[i+1][j]-mat[i][j];
				rd = r = Math.sqrt(dx*dx + dy*dy);
				if(dx!=0)theta = Math.atan(dy/dx)*180/Math.PI;
				else if(dy>=0) theta = 90;
				else theta = -90;
				t=(float)theta;
				if(t.intValue()<-180/8)
				{
					f1.append(rd.intValue()+"//"+(t.intValue()+360)+',');
					mat2[k][0] = rd;
					mat2[k++][1] = t+360;
				}
				else
				{
					f1.append(rd.intValue()+"//"+(t.intValue())+',');
					mat2[k][0] = rd.intValue();
					mat2[k++][1] = t.intValue();
				}
				f2.append(mat[i][j]+""+',');
			}
			f1.append('\n');
			f2.append('\n');
		}
		f1.flush();
		f1.close();
		f2.flush();
		f2.close();
		
		generateHist(mat2,range);
		
	}
	
	int[][] convertToMatrix(int[] pix)
	{
		int k=0;
		int[][] matrix = new int[h][w];
		for(int i=0;i<h;i++)
			for(int j=0;j<w;j++)
				matrix[i][j] = pix[k++];
		return matrix;
	}
	
	void generateHist(double[][] mat,int[] range) throws IOException
	{
		int[][]	count = new int[8][8];
		double val1,val2;
		int m,n;
		FileWriter f3 = new FileWriter("/home/rohit/Documents/APIDQ_matrix2.csv");

		for(int i=0;i<(h-1)*(w-1);i++)
		{
			val1 = mat[i][0];
			val2 = mat[i][1];
			if(val1==0)m=0;
			else if(val1<=2)m=1;
			else if(val1<=4)m=2;
			else if(val1<=7)m=3;
			else if(val1<=12)m=4;
			else if(val1<=20)m=5;
			else if(val1<=30)m=6;
			else m=7;
			
			if(val2<=180/8)n=0;
			else if(val2<=3*180/8)n=1;
			else if(val2<=5*180/8)n=2;
			else if(val2<=7*180/8)n=3;
			else if(val2<=9*180/8)n=4;
			else if(val2<=11*180/8)n=5;
			else if(val2<=13*180/8)n=6;
			else n=7;
			count[m][n]++;
		}
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				f3.append(count[i][j]+""+',');
			}
			f3.append('\n');
		}
		f3.flush();
		f3.close();
		
		int k=0;
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				range[k++] = count[i][j];
		
	}
	public static void main(String[] args) throws IOException 
	{
		int[] range1 = new int[64];
		int[] range2 = new int[64];
		BufferedImage image = ImageIO.read(new File("/home/rohit/Documents/FYP/color_packs/17/52.png"));		
		APIDQ_main ap = new APIDQ_main();
		ap.calc_APIDQ(image,range1);
		
		image = ImageIO.read(new File("/home/rohit/Documents/FYP/color_packs/17/80.png"));		
		ap = new APIDQ_main();
		ap.calc_APIDQ(image,range2);
		for(int i=0;i<range1.length;i++)
			System.out.print(range1[i]+"  ");
		System.out.print(" \n ");
		for(int i=0;i<range1.length;i++)
			System.out.print(range2[i]+"  ");
		System.out.print("\n"+ap.calcSimilarity(range1,range2));
	}

	public int calcSimilarity(int[] range1,int[] range2)
	{
		int sum=0;
		for(int i=0;i<range1.length;i++)
			sum+=(range1[i]>range2[i]?range2[i]:range1[i]);	
		return sum/range1.length;
	}
}
