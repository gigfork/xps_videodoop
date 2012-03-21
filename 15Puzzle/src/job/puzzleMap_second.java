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
		int heur1=0,heur2=0,heur3=0;

		heur1 = Integer.parseInt(token.nextToken());
		heur2 = Integer.parseInt(token.nextToken());
		heur3 = Integer.parseInt(token.nextToken());

		String other=null;
		if(type.equals("P"))
			other = token.nextToken();
		
		Random rnd = new Random();
		int t = rnd.nextInt(num);
		
		if(type.equals("P"))
			output.collect(new keyPair(t,heur1,heur2,heur3),new Text(box +'\t'+type+'\t'+parent+'\t'+heur1+'\t'+heur2+'\t'+heur3+'\t'+other));
		else
			output.collect(new keyPair(t,heur1,heur2,heur3),new Text(box +'\t'+type+'\t'+parent+'\t'+heur1+'\t'+heur2+'\t'+heur3));
	}

}
