package job;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;

public class Node {
	
	public static enum Color {WHITE,GRAY,BLACK};
	
	private int nodeid;
	private int distance;
	private List<Integer> adj = new ArrayList<Integer>();
	private Color color = Color.WHITE;
	
	public Node(String value)
	{
		String[] map = value.split("\t");
		nodeid = Integer.parseInt(map[0]);
		String[] contnt = map[1].split("\\|");
		
		for (String s : contnt[0].split(",")) {
		      if (s.length() > 0) {
		        adj.add(Integer.parseInt(s));
		      }
		}
		
		if(contnt[1].equals("Integer.MAX_VALUE"))
			this.distance = Integer.MAX_VALUE;
		else
			this.distance = Integer.parseInt(contnt[1]);
		
		color = Color.valueOf(contnt[2]);		
	}
	
	public Node(int id)
	{
		nodeid = id;
	}
	
	public int getId() {
	    return nodeid;
	  }

	  public int getDistance() {
	    return distance;
	  }

	  public void setDistance(int distance) {
	    this.distance = distance;
	  }

	  public Color getColor() {
	    return this.color;
	  }

	  public void setColor(Color color) {
	    this.color = color;
	  }

	  public List<Integer> getAdj() {
	    return this.adj;
	  }

	  public void setAdj(List<Integer> adj) {
	    this.adj = adj;
	  }
	  
	  public Text getLine() {	    
		 
		  	StringBuffer s = new StringBuffer();
	    
		    for (int v : adj) {
		      s.append(v).append(",");
		    }
		    s.append("|");
	
		    if (this.distance < Integer.MAX_VALUE) {
		      s.append(this.distance).append("|");
		    } else {
		      s.append("Integer.MAX_VALUE").append("|");
		    }
	
		    s.append(color.toString());
	
		    return new Text(s.toString());
	  }

}
