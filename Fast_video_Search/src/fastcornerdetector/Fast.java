/*Copyright (c) 2006, 2008 Edward Rosten, translated to Java by Torben Schinke 2009
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:


 *Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

 *Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

 *Neither the name of the University of Cambridge nor the names of
its contributors may be used to endorse or promote products derived
from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/
package fastcornerdetector;

import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the translation of the fast c file, you can find the source at
 * http://mi.eng.cam.ac.uk/~er258/work/fast.html
 * @author tschinke
 */
public class Fast {

    /**
     *
     * @param im image intensity pixels all in a row
     * @param xsize width of image
     * @param ysize height of image
     * @param stride size of aligned image row
     * @param b windowssize in which a corner will be detected
     * @param ret_num_corners array's first element containing amount of detected corners
     * @return an array containing XY coordinate-objects
     */
    public static List<XY> fast9_detect_nonmax2(int[] im, int xsize, int ysize, int stride, int threshold_detection, int threshold_scoring, int[] ret_num_corners) {
        XY[] corners;
        int[] num_corners = new int[1];
        int[] scores;
        List<XY> nonmax;

        corners = Fast_9.fast9_detect(im, xsize, ysize, stride, threshold_detection, num_corners);
        scores = Fast_9.fast9_score(im, stride, corners, num_corners[0], threshold_detection);
        nonmax = nonmax_suppression(corners, scores, num_corners[0], ret_num_corners,threshold_scoring);

        corners = null;
        scores = null;


        return nonmax;
    }


    public static void init_XY_array(XY[] a)
    {
        for (int i=0;i<a.length;i++)
            a[i]=new XY();
    }

    public static List<XY> nonmax_suppression(XY[] corners, final int[] scores, int num_corners, int[] ret_num_nonmax,int threshold_score_suppression) {
        int num_nonmax = 0;
        int last_row;
        int[] row_start;
        int i, j;
        //XY[] ret_nonmax;
        List<XY> ret_nonmax=new LinkedList<XY>();
        final int sz = (int) num_corners;

        /*Point above points (roughly) to the pixel above the one of interest, if there
        is a feature there.*/
        int point_above = 0;
        int point_below = 0;


        if (num_corners < 1) {
            ret_num_nonmax[0] = 0;
            //return new XY[0];
            return new LinkedList<XY>();
        }

        //ret_nonmax = new XY[num_corners];
        //init_XY_array(ret_nonmax);

        /* Find where each row begins
        (the corners are output in raster scan order). A beginning of -1 signifies
        that there are no corners on that row. */
        last_row = corners[num_corners - 1].y;
        row_start = new int[last_row + 1];

        for (i = 0; i < last_row + 1; i++) {
            row_start[i] = -1;
        }

        {
            int prev_row = -1;
            for (i = 0; i < num_corners; i++) {
                if (corners[i].y != prev_row) {
                    row_start[corners[i].y] = i;
                    prev_row = corners[i].y;
                }
            }
        }



        for (i = 0; i < sz; i++) {
            int score = scores[i];

            //System.out.println(score);
            if (score<threshold_score_suppression)
                continue;

            XY pos = corners[i];

            /*Check left */
            if (i > 0) {
                if (corners[i - 1].x == pos.x - 1 && corners[i - 1].y == pos.y && compare(scores[i - 1], score)) {
                    continue;
                }
            }

            /*Check right*/
            if (i < (sz - 1)) {
                if (corners[i + 1].x == pos.x + 1 && corners[i + 1].y == pos.y && compare(scores[i + 1], score)) {
                    continue;
                }
            }

            /*Check above (if there is a valid row above)*/
            if (pos.y != 0 && row_start[pos.y - 1] != -1) {
                /*Make sure that current point_above is one
                row above.*/
                if (corners[point_above].y < pos.y - 1) {
                    point_above = row_start[pos.y - 1];
                }

                /*Make point_above point to the first of the pixels above the current point,
                if it exists.*/
                for (; corners[point_above].y < pos.y && corners[point_above].x < pos.x - 1; point_above++) {
                }


                for (j = point_above; corners[j].y < pos.y && corners[j].x <= pos.x + 1; j++) {
                    int x = corners[j].x;
                    if ((x == pos.x - 1 || x == pos.x || x == pos.x + 1) && compare(scores[j], score))
                    {
                        //break cont; //isn't this the same like continue?
                        continue;
                    }
                }

            }

            /*Check below (if there is anything below)*/
            if (pos.y != last_row && row_start[pos.y + 1] != -1 && point_below < sz) /*Nothing below*/ {
                if (corners[point_below].y < pos.y + 1) {
                    point_below = row_start[pos.y + 1];
                }

                /* Make point below point to one of the pixels belowthe current point, if it
                exists.*/
                for (; point_below < sz && corners[point_below].y == pos.y + 1 && corners[point_below].x < pos.x - 1; point_below++) {
                }

                for (j = point_below; j < sz && corners[j].y == pos.y + 1 && corners[j].x <= pos.x + 1; j++) {
                    int x = corners[j].x;
                    if ((x == pos.x - 1 || x == pos.x || x == pos.x + 1) && compare(scores[j], score))
                    {
                        //break cont; //isn't this the same like continue?
                        continue;
                    }
                }
            }

            
            
            //ret_nonmax[num_nonmax++] = corners[i];
            ret_nonmax.add(corners[i]);
            cont:
            ;
        }

        row_start = null;
        ret_num_nonmax[0] = num_nonmax;
        return ret_nonmax;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean compare(int x, int y) {
        return x >= y;
    }


    public static List<Point> convert(Collection<XY> ps)
    {
        List<Point> res=new LinkedList<Point>();
        for (XY p:ps)
            res.add(new Point(p.x,p.y));
        return res;
    }
}
