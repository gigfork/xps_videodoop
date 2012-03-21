package job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class shortestPathReduce extends MapReduceBase 
		implements Reducer<IntWritable, Text, IntWritable, Text> {

	@Override
	public void reduce(IntWritable key, Iterator<Text> values,
			OutputCollector<IntWritable, Text> output, Reporter reporter)
			throws IOException {
	
		List<Integer> adj = new ArrayList<Integer>();
		int distance = Integer.MAX_VALUE;
		Node.Color color = Node.Color.WHITE;
		
		while(values.hasNext())
		{
			Text val = values.next();
			
			Node n = new Node(key.get()+"\t"+val.toString());
			
			if(n.getAdj().size()>0)
				adj = n.getAdj();
			if(n.getDistance()<distance)
				distance = n.getDistance();
			if(n.getColor().ordinal()>color.ordinal())
				color = n.getColor();
		}
		
		Node n = new Node(key.get());
		n.setAdj(adj);
		n.setColor(color);
		n.setDistance(distance);
		output.collect(new IntWritable(n.getId()),n.getLine());		
	}

}
