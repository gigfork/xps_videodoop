import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;


public class APIDQ_video {	

	static	int w = 128,h= 128;
	
	public static void main(String[] args) throws Exception 
	{	
		File Directory = new File("/home/rohit/Documents/FYP/APIDQ/Video_frames/");
		String[] subdirs = Directory.list();
		File query = new File("/home/rohit/Documents/FYP/APIDQ/query.png");
		
		FileWriter output = new FileWriter("/home/rohit/Documents/FYP/APIDQ/output.csv");
		FileWriter vector = new FileWriter("/home/rohit/Documents/FYP/APIDQ/vector.csv");

		APIDQ ap = new APIDQ(w,h);
		colorHistogram ch = new colorHistogram();
		
		String[] frames;
		File infile,frame;
		
		BufferedImage Qimage = ImageIO.read(query);
		int[] Qrange1 = new int[w*h];
		ap.calc_APIDQ(Qimage, Qrange1, "-1",vector);
		int[] Qrange2 = new int[w*h];
		ap.calc_APIDQ2(Qimage, Qrange2, "-1",vector);

		int[] Qrange_cl = ch.calcHistogram(Qimage, w, h);
		
		for(int i=0;i<subdirs.length;i++)
		{
			infile = new File(Directory.getAbsoluteFile()+"/"+subdirs[i]);
			frames = infile.list();
			for(int j=0;j<frames.length;j++)
			{
				frame = new File(infile.getAbsoluteFile()+"/"+frames[j]);
				ap.APIDQ_start(Qrange1, Qrange2, Qrange_cl,frame, subdirs[i], frames[j], output, vector);
			}
		}
		
		output.flush();
		output.close();
		vector.flush();
		vector.close();
		
	//	Process p = Runtime.getRuntime().exec("sort -k2 -t, /home/rohit/Documents/FYP/APIDQ/output.csv > /home/rohit/Documents/FYP/APIDQ/output_sort.csv", null);
		//p.waitFor();
	}

}
