import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


public class DCT_video {
	
    int w ,h;
    List<int[]> list = new ArrayList<int[]>();
    
    void getPicture(String infile) throws Exception
    {
    	FileWriter f1 = new FileWriter("/home/rohit/Videos/pack/ecd.txt");

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
				videoCoder = coder;
				videostreamid = i;
				break;
			}			
		}
		videoCoder.setHeight(128);videoCoder.setWidth(128);
		System.out.println("height ="+videoCoder.getHeight() + " width = "+videoCoder.getWidth());
		if(videostreamid==-1)throw new RuntimeException("No video stream");		
		if(videoCoder.open()<0)throw new RuntimeException("could not open video coder");
		
		IVideoResampler resampler = null;
		
		
		if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
		{
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if(resampler==null) throw new RuntimeException("Resampler error");
		}
		
		int count=0;
		IPacket packet = IPacket.make();
		int[] newPixel=null,dummy=null;
		
		while(container.readNextPacket(packet) >=0)
		{
			if(packet.getStreamIndex() == videostreamid)
			{
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),videoCoder.getWidth(),videoCoder.getHeight());
				int offset = 0;
				long lastPTS=0;
				while(offset<packet.getSize() & packet.isKeyPacket())
				{
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, 0);
					if (bytesDecoded < 0)
			            throw new RuntimeException("got error decoding video");
					offset+=bytesDecoded;
					
					if(picture.isComplete() && picture.isKeyFrame())
					{						
						System.out.println(picture.getPts());
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
				        
				        h=w=128;		        
				       			        
				        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
				        Graphics g = image.getGraphics();
				        g.drawImage(javaImage, 0, 0,w,h, null);
				        g.dispose();
				        
				      //  ImageIO.write(image, "png", new File("/home/rohit/Videos/pack/"+picture.getPts()+".png"));
				        
						Raster data = image.getData();		
						newPixel = data.getPixels(0, 0, w,h,dummy);
						
						int[][] newPixs = new int[h][w];
						int[][] oldPixs = new int[h][w];
						int k=0;
						for(int i=0;i<h;i++)
							for(int j=0;j<w;j++)
								newPixs[i][j] = newPixel[k++];
						
						newPixel = DCT(newPixs);
						
						
						
						
						if(list.size()==0)
						{
							System.out.println("-"+count);
							ImageIO.write(image, "png", new File("/home/rohit/Videos/pack2/"+picture.getPts()+".png"));
							list.add(newPixel);
						}
						else
						{
							
						  boolean flag=true;
						  System.out.println("------------------------" +list.size());
						  for(int z=0;z<list.size();z++)
                          {
                        	  double distance = eucld_distance(list.get(z), newPixel);
                        	  System.out.println("%"+distance);
                        	  if(distance<8000)
                        	  {
                        		  flag=false;
                        		  break;
                        	  }
                          }
						  System.out.println("------------------------");
                    	  if(flag)
                    	  {
                    		  System.out.println("*****");
                    		  ImageIO.write(image, "png", new File("/home/rohit/Videos/pack2/"+picture.getPts()+".png"));  
                    		  list.add(newPixel);
      							WritableRaster rster = image.getRaster();						

      							rster.setPixels(0, 0, w, h, newPixel);
      							image.copyData(rster);
      							ImageIO.write(image, "png", new File("/home/rohit/Videos/pack2/pack12/"+picture.getPts()+".png"));
                    	  }
                        }			      
					}
				}			
			}
		}		
		System.out.println(count);
    }
    
	public static void main(String[] args) throws Exception {	
		
		String infile = new String("/home/rohit/Videos/12.flv");
		
		DCT_video dct = new DCT_video();
		dct.getPicture(infile);		
	}
	
	
	double eucld_distance(int[] mat1,int[] mat2)
	{
		int sum=0;
		for(int i=0;i<mat1.length;i++)
			sum+=(mat1[i]-mat2[i])*(mat1[i]-mat2[i]);
		return Math.sqrt(sum);
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

}
