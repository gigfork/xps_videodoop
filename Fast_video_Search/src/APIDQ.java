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

	int w=128;
	int h=128;
    
    float[] elements = { -1.0f, -1.0f, -1.0f,
            			 -1.0f, 8.f, -1.0f,
                         -1.0f, -1.0f, -1.0f };
	
	public void calc_APIDQ(BufferedImage org_image,int[] range,String frameno,FileWriter vector) throws IOException
	{        
		BufferedImage image = org_image;
        BufferedImage img= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(image, 0, 0,w,h, null);
        g.dispose();
        
      //ImageIO.write(img, "png", new File("/home/rohit/Documents/FYP/color_packs/Bframes/"+n+".png"));

        GaussianFilter filter = new GaussianFilter();
        filter.setRadius(4);
        filter.filter(img, img);
        

        /*
		BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);;
		Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        cop.filter(img,bi);
        img = bi;
        */
        
        //ImageIO.write(img, "png", new File("/home/rohit/Documents/FYP/color_packs/frames/"+n+".png"));
        
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
				else if(dy==0) theta = 0;
				else if(dy>0) theta = 90;
				else theta = -90;
				t=(float)theta;
				if(t.intValue()<-180/8)
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t+360;
				}
				else
				{
					mat2[k][0] = rd.intValue();
					mat2[k++][1] = t.intValue();
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
				if(t.intValue()<-180/8)
				{
					mat2[k][0] = rd;
					mat2[k++][1] = t+360;
				}
				else
				{
					mat2[k][0] = rd.intValue();
					mat2[k++][1] = t.intValue();
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
		int[][]	count = new int[8][8];
		double val1,val2;
		int m,n;
		
		for(int i=0;i<mat.length;i++)
		{
			val1 = mat[i][0];
			val2 = mat[i][1];
			vector.append(val1+""+',');

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
		vector.append('\n');
		System.out.println(mat.length+" -- "+count[0][0]);
		int k=0;
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				range[k++] = count[i][j];		
	}
	
	public void APIDQ_start(int[] Qrange1,int[] Qrange2, int[] Qrange_cl,File infile,String videoname,String frameno,FileWriter output,FileWriter vector) throws IOException 
	{
		int[] range1 = new int[64];
		//System.out.println(infile);
		BufferedImage image = ImageIO.read(infile);
		calc_APIDQ(image,range1,frameno,vector);
		
		
		int[] range2 = new int[64];
		//calc_APIDQ2(image,range2,frameno);

		/*
		for(int i=0;i<range2.length;i++)
		{
			vector.append(range2[i]+""+',');
		}
		vector.append('\n');
		*/
		colorHistogram ch = new colorHistogram();	
		
		//edge_feature edf = new edge_feature();		
		//edf.get_edge_feature(image2, w, h,n);
		//int[] hist = edf.get_edge_feature(image2, w, h, n);	
		//for (int i=0; i<hist.length; i++ )
		//	f1.append(hist[i]+""+',');
		//f1.append('\n');
		
		int rTheta1 = calcSimilarity(Qrange1,range1);
		int rTheta2 = 0;//calcSimilarity(Qrange2,range2);

		int cHist = (int)eucld_distance(Qrange_cl,ch.calcHistogram(image, w, h, frameno));
		//System.out.print(videoname+" -- "+frameno+" --->"+rTheta1+" , " +rTheta2+"  ,  "+ cHist +"\n");
		if((rTheta1>220 && cHist<2000)||(rTheta1>225 && rTheta2>225 && cHist<4000))
			output.append(videoname+","+frameno+","+rTheta1+","+rTheta2+","+ cHist +"\n");
		
			
			//f1.append(" , "+(int)eucld_distance(ch.calcHistogram(image1, w, h,-1), ch.calcHistogram(image2, w, h,n))+"\n");
		//f1.append(" , "+calcSimilarity(ch.calcHistogram(image1, w, h,-1), ch.calcHistogram(image2, w, h,n))+"\n");

		//	System.out.println(" , "+(int)calcSimilarity(edf.get_edge_feature(image1, w, h, n),edf.get_edge_feature(image2, w, h, n)));
	}

	public int calcSimilarity(int[] range1,int[] range2)
	{
		int sum=0,sum1=0;
		for(int i=0;i<range1.length;i++)
		{
			sum+=(range1[i]>range2[i]?range2[i]:range1[i]);	
		
		}
		return sum/range1.length;
	}
}
