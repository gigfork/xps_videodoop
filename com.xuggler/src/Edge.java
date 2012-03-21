/*
 * @(#)Edge.java	1.2 99/07/26
 * 
 * Copyright 1997, 1998, 1999 Sun Microsystems, Inc. All Rights
 * Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free,
 * license to use, modify and redistribute this software in source and
 * binary code form, provided that i) this copyright notice and license
 * appear on all copies of the software; and ii) Licensee does not utilize
 * the software in a manner which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE SOFTWARE
 * OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND  
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF 
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line
 * control of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and warrants
 * that it will not use or redistribute the Software for such purposes. 
 */

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;


public class Edge extends Applet {

    private BufferedImage bi;
    float[] elements = { -1.0f, -1.0f, -1.0f,
                         -1.0f, 8.f, -1.0f,
                         -1.0f, -1.0f, -1.0f};

    public Edge() {
            setBackground(Color.white);
            Image img = getToolkit().getImage("/home/rohit/Pictures/1.png");
            try {
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(img, 0);
                tracker.waitForID(0);
            } catch (Exception e) {}

            int iw = img.getWidth(this);
            int ih = img.getHeight(this);
            bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
            Graphics2D big = bi.createGraphics();
            big.drawImage(img,0,0,this);

    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        int w = getSize().width;
        int h = getSize().height;
        int bw = bi.getWidth(this);
        int bh = bi.getHeight(this);

	AffineTransform at = new AffineTransform();
	at.scale(w/2.0/bw, h/1.0/bh);

	BufferedImageOp biop = null;


	BufferedImage bimg = new BufferedImage(bw,bh,BufferedImage.TYPE_INT_RGB);

        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	cop.filter(bi,bimg);
	biop = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

	g2.drawImage(bi, biop, 0, 0);
      	g2.drawImage(bimg, biop, w/2+3,0); 

	}

    public static void main(String s[]) {
	WindowListener l = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	};
	Frame f = new Frame("Edge");
	f.addWindowListener(l);
	f.add("Center", new Edge());
	f.pack();
	f.setSize(new Dimension(600, 300));
	f.show();
    }
}
