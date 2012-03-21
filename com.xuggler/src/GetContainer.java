import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import sun.misc.*;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;


public class GetContainer {


	public static void main(String[] args) throws Exception {		
		
		int count=0;
		
		String infile = new String("/home/rohit/Videos/mov1.wmv");
		FileWriter f1 = new FileWriter("/home/rohit/Videos/file2.bin"); 
				
		IContainer container = IContainer.make();		
		container.open(infile,IContainer.Type.READ,null);
		
		IStreamCoder videoCoder = null;
		
		int numstreams = container.getNumStreams();
		int videostreamid=-1;
		for(int i=0;i<numstreams;i++)
		{
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
			{
				//System.out.print(coder.getFrameRate());
				videoCoder = coder;
				videostreamid = i;
				break;
			}
			
		}
		
		if(videostreamid==-1)throw new RuntimeException("No video stream");
		
		if(videoCoder.open()<0)throw new RuntimeException("could not open video coder");
		

		
		IVideoResampler resampler = null;
		
		if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
		{
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if(resampler==null) throw new RuntimeException("Resampler error");
		}
		
		IPacket packet = IPacket.make();
		
		long lastPTS=0;
		int[] lastPixel = null;
		
		
		
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
					
					if(picture.isComplete())
					{
						
						//System.out.println(picture.getPts());
						if(picture.getPts()-lastPTS >500000)
							lastPTS = picture.getPts();
						else continue;
						count++;
						
						IVideoPicture newPic = picture;
				        if (resampler != null)
				        {
				           newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(),picture.getHeight());
				              if (resampler.resample(newPic, picture) < 0)
				                throw new RuntimeException("could not resample video");
				        }
				        
				        if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
				              throw new RuntimeException("could not decode video as BGR 24 bit data");
				        
				        
				        BufferedImage javaImage = new BufferedImage(videoCoder.getWidth(),videoCoder.getHeight(), BufferedImage.TYPE_3BYTE_BGR); 
				        IConverter converter = ConverterFactory.createConverter(javaImage,IPixelFormat.Type.BGR24);
				        javaImage = converter.toImage(newPic);
				        int w = javaImage.getWidth();
				        int h = javaImage.getHeight();		        
				        
				        
				        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
				        Graphics g = image.getGraphics();
				        g.drawImage(javaImage, 0, 0, null);
				        g.dispose();
				        
				        ImageIO.write(image, "png", new File("/home/rohit/Videos/pack/"+picture.getPts()+".png"));
				        int[] pixels = new int[javaImage.getHeight()*javaImage.getWidth()];
				        image.getRGB(0,0,w,h,pixels,0,w);
				        
				        int k=0;
				        int[][] matrix = new int[h][w];
				        for(int i=0;i<h;i++)
				        	for(int j=0;j<w;j++)
				        		matrix[i][j] = pixels[k++];
				        DCT(matrix);
					}
				}			
			}
		}		
		
		f1.close();
		System.out.println(count);
		if (videoCoder != null)
	    {
	      videoCoder.close();
	      videoCoder = null;
	    }
	    if (container !=null)
	    {
	      container.close();
	      container = null;
	    }
		
	}

	static double[][] DCT(int[][] matrix)
	{
		int h = matrix.length;
		int w = matrix[0].length;
		double[][] temp = new double[h][w];
		
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
		    temp[u][v] = au * av * sum;

	          } // for v
	        } // for u

	      }  // for n
	    }  // for m
		
		
		
		for(int i=0;i<h;i++)
		{
			for(int j=0;j<w;j++)
        		System.out.print(temp[i][j]+" ");
			System.out.println();
		}
		return temp;
	}
	
}

