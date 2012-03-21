package job;

import java.io.IOException;
import java.util.Stack;
import java.util.StringTokenizer;

import job.Puzzle.myCounters;

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
		String[] array;
		Text text = new Text();
		Text text1 = new Text();
		Node top = s.pop();		
		
		
		solvable sol = new solvable();
		if(!sol.solv(box))
		{
			reporter.incrCounter(myCounters.Finished, 1);
			System.exit(0);
		}
		
		
		while(true)
		{
			if(top.depth<=10)
			{
				generate gn = new generate(box);
				array = gn.convertToArray(top.box);
				int l = (int)Math.sqrt(array.length);
				int ind = gn.find(array,"x");
				
				String tmp;
				
				if((tmp=gn.Down(array, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));
				if((tmp=gn.Up(array, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));			
				if((tmp=gn.Left(array, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));			
				if((tmp=gn.Right(array, l, ind))!=null)
					s.push(new Node(top.depth+1,tmp,top.box));
				text.set(top.box);
				text1.set("P"+'\t'+top.parent+'\t'+gn.MDistPair(array)+'\t'+gn.Max_swap(array)+'\t'+gn.out_rc(array)+'\t'+top.depth);
				output.collect(text,text1);
			}
			else
			{
				text.set(top.box);
				text1.set("C"+'\t'+top.parent);
				output.collect(text,text1);	
			}
						
			if(!s.empty())top = s.pop();
			else break;
		}		
	}

}
