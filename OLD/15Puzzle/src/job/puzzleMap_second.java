package job;

import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

@SuppressWarnings("deprecation")
public class puzzleMap_second extends MapReduceBase 
implements Mapper<LongWritable, Text, keyPair, Text> {
	
	private JobConf conf;
	
	public void configure(JobConf conf)
	{
		this.conf = conf;
	}
	
	public void map(LongWritable key, Text value,
			OutputCollector<keyPair, Text> output, Reporter reporter)
			throws IOException {
		

		int num = conf.getNumReduceTasks();	    
		
		StringTokenizer token = new StringTokenizer(value.toString(),"	");
		String box = token.nextToken();
		String type = token.nextToken();
		String parent = token.nextToken();
		int heuristics=0;
		if(type.equals("C"))
			heuristics = Integer.parseInt(token.nextToken());
		Random rnd = new Random();
		int t = rnd.nextInt(num);
		output.collect(new keyPair(t,heuristics),new Text(box +'\t'+type+'\t'+parent));
	}

}
