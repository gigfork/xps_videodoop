/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fastcornerdetector;

/**
 *
 * @author tschinke
 */
public class XY {

    int x,y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public XY() {
        x=-1;
        y=-1;
    }

    public String toString()
    {
        return "("+x+"|"+y+")";
    }
}
