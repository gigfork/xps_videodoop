import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_contrib.*;




public class Smoother {
    public static void smooth(String filename) { 
    	CvMat scr1;
    	Cvr
        IplImage image = cvLoadImage(filename);
        if (image != null) {
        	System.out.print("hey");
            cvSmooth(image, image, CV_GAUSSIAN, 3);
            cvSaveImage("/home/rohit/Pictures/12345.jpg", image);
            cvReleaseImage(image);
        }System.out.print("hey");
    }
    
    public static void main(String[] args)
    {
    	smooth("/home/rohit/Pictures/1.jpg");
    }
}