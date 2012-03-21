import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;


public class cAvg {
	
	int w=129;
	int h=129;
	int count = 0;
	int no=12;
	
	public class frames{	
		int frameno;
		int[] pixels;
		int avg;
		
		frames()
		{
			pixels=new int[h*w];
			avg=-1;
		}
	}
	
	void getFrames(String filename) throws IOException
	{
		
		FileWriter f1 = new FileWriter("/home/rohit/Documents/avgData.txt");
		
		
		IContainer container = IContainer.make();
		container.open(filename,IContainer.Type.READ,null);
		
		IStreamCoder videoCoder = null;
		
		int numstreams = container.getNumStreams();
		
		int videostreamid=-1;
		
		for(int i=0;i<numstreams;i++)
		{
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
			{
				videoCoder = coder;
				videostreamid = i;
				break;
			}			
		}
		
		if(videostreamid==-1)throw new RuntimeException("No video stream");
		if(videoCoder.open()<0)throw new RuntimeException("Could not open video coder");
		
		IVideoResampler resampler = null;
		
		if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
		{
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if(resampler==null) throw new RuntimeException("Resampler error");
		}
		
		IPacket packet = IPacket.make();
		int[] newPixel=null,dummy=null;
		int num=0;
		
		
		frames[] fms=new frames[no+1];
		for(int i=0;i<no+1;i++)
			fms[i] = new frames();			
				
		while(container.readNextPacket(packet) >=0)
		{
			if(packet.getStreamIndex() == videostreamid)
			{
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),videoCoder.getWidth(),videoCoder.getHeight());
				int offset = 0;
				while(offset<packet.getSize())
				{
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, 0);
					if (bytesDecoded < 0)
			            throw new RuntimeException("got error decoding video");
					offset+=bytesDecoded;
					
					if(picture.isComplete() & picture.isKeyFrame())
					{
						IVideoPicture newPic = picture;
				        if (resampler != null)
				        {
				           newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(),picture.getHeight());
				              if (resampler.resample(newPic, picture) < 0)
				                throw new RuntimeException("could not resample video");
				        }
				        
				        if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
				              throw new RuntimeException("could not decode video as BGR 24 bit data");
				     
				        num++;
				        
				        BufferedImage jImage = new BufferedImage(videoCoder.getWidth(),videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR); 
				        IConverter converter = ConverterFactory.createConverter(jImage,IPixelFormat.Type.BGR24);
				        jImage = converter.toImage(newPic);
				        
				        BufferedImage image = new BufferedImage(129,129,BufferedImage.TYPE_3BYTE_BGR);				       
				        Graphics2D g = image.createGraphics();
				        g.drawImage(jImage, 0, 0,w,h, null);
				        g.dispose();
				        
				        Raster data = image.getData();		
						newPixel = data.getPixels(0, 0, w,h,dummy);
						
						
						//ImageIO.write(image, "png", new File("/home/rohit/Documents/frames/"+num+".png"));
						calcAvg(newPixel,fms,num,f1);
				        
					}
				}

			}			
		}
		f1.flush();
		f1.close();
	}	
	
	void calcAvg(int[] newPixel,frames[] fms,int num,FileWriter f1) throws IOException
	{
		//System.out.println(newPixel.length+"  "+fms[0].pixels.length);
		
		int avg=0;
		if(count<=no)
		{			
			fms[count].frameno = num;
			for(int i=0;i<fms[0].pixels.length;i++)
				fms[count].pixels[i] = newPixel[i];
			count++;
		}
		if(count == 13)
		{
			for(int i=0;i<no+1;i++)
				if(i!=no/2)
					avg+=fms[i].pixels[8320];
			
			avg=avg/no;
			fms[no/2].avg=avg;
			f1.append(fms[no/2].frameno+"  " + fms[no/2].avg+"  "+fms[no/2].pixels[8320]+"  "+(fms[no/2].pixels[8320]-fms[no/2].avg)+'\n');
			
			for(int i=0;i<no;i++)
			{
				fms[i].avg = fms[i+1].avg;
				fms[i].frameno = fms[i+1].frameno;
				for(int j=0;j<fms[i].pixels.length;j++)
					fms[i].pixels[j] = fms[i+1].pixels[j];				
			}
			count--;
		}
	}
	
	public static void main(String[] args) throws Exception {	
		
		String filename = new String("/home/rohit/Videos/packs/12.flv");
		
		cAvg avg = new cAvg();
		avg.getFrames(filename);		
	}

}
