/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tracking;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import java.awt.*;
import java.util.*;
import java.util.Random;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
/**
 *
 * @author JPMM
 */
public class Tracking {
    
public static IplImage img,out,imgTem,imgDiff;
public static void main(String[] args) {
      CanvasFrame canvas = new CanvasFrame("VideoCanvas");    
      canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);   
      FrameGrabber grabber = new OpenCVFrameGrabber("C:\\Users\\Rogelio\\Documents\\NetBeansProjects\\TestOpencv2\\Final.avi");  
      int i = 0;
       ArrayList<Rect> array = new ArrayList<Rect>();
      try {      
        grabber.start();    
        
        while (true) {   
        img = grabber.grab();
        img=FiltreGray(img);
       
        out=img;
        if(i==0){
            imgDiff= cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);
            imgTem= cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);
            imgDiff=img.clone();
        }
        if(i==1){
          cvAbsDiff(img,imgTem,imgDiff);
          cvAdaptiveThreshold(imgDiff, imgDiff,255, CV_ADAPTIVE_THRESH_MEAN_C,CV_THRESH_BINARY_INV,9,5);
        }
        i=1;  
       IplConvKernel mat=cvCreateStructuringElementEx(4, 4, 1, 1, CV_SHAPE_CROSS, null);  
       IplConvKernel mat2=cvCreateStructuringElementEx(2, 2, 0, 0, CV_SHAPE_CROSS, null);  
       cvErode(imgDiff, imgDiff,mat2, 1);
       cvDilate(imgDiff, imgDiff, mat, 9);
        imgDiff=detection_contours(imgDiff, out);
        canvas.setCanvasSize(imgDiff.width(),imgDiff.height()); 
       
       if (imgDiff != null) {       
           canvas.showImage(imgDiff);
        }
       imgTem=img;
       }
      }
     catch (Exception e) {      
     }
    }

    public static IplImage FiltreGray(IplImage img){
     IplImage grayImg=cvCreateImage(cvGetSize(img), 8, 1);
     cvCvtColor(img, grayImg, CV_RGB2GRAY);
     cvSmooth(grayImg, grayImg, CV_GAUSSIAN, 9);
     return grayImg;
    } 
    
     public static IplImage detection_contours(IplImage outmat,IplImage outmat2) {
        int cnts=0; 
        IplImage temv= outmat.clone();
        CvSeq cvSeq=new CvSeq();
        CvMemStorage memory=CvMemStorage.create();
         cnts=cvFindContours(temv, memory, cvSeq,Loader.sizeof(CvContour.class)
                 , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        if(cnts>0){
        for (int i = 0; i < cnts; i++) {
            CvScalar color =  CV_RGB( 155, 155,155 );
            CvRect r = new CvRect(cvGetSeqElem(cvSeq, i));
            if(r.y()>10){
            cvRectangle (outmat2,cvPoint(r.x(), r.y()),
	    	 	     cvPoint( r.x()+40,  40+r.y()),
	    		     CvScalar.RED,1,CV_AA,0);
            }
        }
        }
        return outmat2;
    }
    
  }
    
    