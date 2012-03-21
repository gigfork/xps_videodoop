package job;

import java.io.IOException;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class generateMap extends MapReduceBase 
implements Mapper<LongWritable, Text, Text, Text> {
	
	public static class Node {	
		int depth;
		String box;
		String parent;
		Node(int d,String b,String p)
		{
			depth = d;
			box = b;
			parent = p;
		}
	}	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		StringTokenizer token = new StringTokenizer(value.toString(),"	");
		String box = token.nextToken();
		String type = token.nextToken();
		String Prnt = token.nextToken();
		
		int count=0;
		Stack<Node> s = new Stack<Node>();		
		s.push(new Node (0,box,Prnt));
		
		
		Node top = s.pop();		
		while(true)
		{
			if(top.depth<=10)
			{
				generate gn = new generate();
				int l = (int)Math.sqrt(top.box.length());
				int ind = top.box.indexOf("x");
				String tmp;
				
				if((tmp=gn.Down(top.box, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));
				if((tmp=gn.Up(top.box, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));			
				if((tmp=gn.Left(top.box, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));			
				if((tmp=gn.Right(top.box, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));
				output.collect(new Text(top.box),new Text("P"+'\t'+top.parent));
			}
			else
				output.collect(new Text(top.box),new Text("C"+'\t'+top.parent));			
			if(!s.empty())top = s.pop();
			else break;
		}		
	}

}
