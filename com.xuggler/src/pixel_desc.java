import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class pixel_desc {
	
	
	
public static void main(String[] args) throws IOException {
		
		//BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/image1.jpg"));
		//BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/image3.jpg"));
		
		BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/5.png"));
		
		//BufferedImage img1 =  ImageIO.read(new File("/home/rohit/Pictures/4.png"));
		//BufferedImage img2 =  ImageIO.read(new File("/home/rohit/Pictures/1.png"));
		
		int w,h;
		w=h=128;
		
		BufferedImage image1 = new BufferedImage(w, h,BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = image1.getGraphics();
        g.drawImage(img1, 0, 0,w,h, null);
        g.dispose();
        
		BufferedImage image2 = new BufferedImage(w, h,BufferedImage.TYPE_BYTE_GRAY);
        g = image2.getGraphics();
        g.drawImage(img2, 0, 0,w,h, null);
        g.dispose();
       
        pixel_desc dct = new pixel_desc();
        dct.edge(image1, image2);

	}

	
	

}
