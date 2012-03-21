package job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import job.shortestPath.myCounters;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class shortestPathMap extends MapReduceBase 
		implements Mapper<LongWritable, Text, IntWritable, Text>{

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<IntWritable, Text> output, Reporter reporter)
			throws IOException {
		
		Node node = new Node(value.toString());
		
		reporter.incrCounter(myCounters.Total,1);
		
		if(node.getColor() == Node.Color.BLACK)
			reporter.incrCounter(myCounters.Black,1);
		
		
		if(node.getColor() == Node.Color.GRAY)
		{
			for(int v:node.getAdj())
			{
				Node n = new Node(v);
				n.setColor(Node.Color.GRAY);
				n.setDistance(node.getDistance()+1);
				output.collect(new IntWritable(v), n.getLine());
			}
			
			node.setColor(Node.Color.BLACK);
			reporter.incrCounter(myCounters.Black,1);
		}		
		output.collect(new IntWritable(node.getId()), node.getLine());		
	}
	
	

}
