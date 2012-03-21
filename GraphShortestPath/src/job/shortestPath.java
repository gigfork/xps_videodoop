package job;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


@SuppressWarnings("deprecation")
public class shortestPath extends Configured implements Tool {

	public static enum myCounters {
		Total,
		Black;
	}
	
	@Override
	public int run(String[] args) throws Exception {
		
		boolean cont=true;
		int ct=0;
		JobConf conf = null;
		FileSystem fs;
		
		try{
			while(cont)
			{
				conf = new JobConf(getConf(),getClass());
				if(conf==null)
					return -1;
				
				if(ct==0)
					FileInputFormat.addInputPath(conf, new Path("/user/rohit/data_graphShortestPath/input"));
				else
					FileInputFormat.addInputPath(conf, new Path("/user/rohit/data_graphShortestPath/output/o"+ct));
				
				if(ct>1)
				{
					fs = FileSystem.get(conf);
					fs.delete(new Path("/user/rohit/data_graphShortestPath/output/o"+(ct-1)));
				}
				
				FileOutputFormat.setOutputPath(conf, new Path("/user/rohit/data_graphShortestPath/output/o"+(ct+1)));
				
				conf.setInputFormat(TextInputFormat.class);
				conf.setMapperClass(shortestPathMap.class);
				conf.setMapOutputKeyClass(IntWritable.class);
				conf.setMapOutputValueClass(Text.class);
				conf.setReducerClass(shortestPathReduce.class);
				conf.setOutputKeyClass(IntWritable.class);
				conf.setOutputValueClass(Text.class);
				conf.setOutputFormat(TextOutputFormat.class);
				
				RunningJob job; 
				job = JobClient.runJob(conf);
					
				job.waitForCompletion();
				
				Counters counters = job.getCounters();
				Counter black = counters.findCounter(myCounters.Black);
				Counter total = counters.findCounter(myCounters.Total);
				
				System.err.println("Counters B="+black.getValue()+" T="+total.getValue());
				if(total.getValue() == black.getValue())
				{
					cont = false;
					if(ct>1){
						fs = FileSystem.get(conf);
						fs.delete(new Path("/user/rohit/data_graphShortestPath/output/o"+(ct)));
					}
				}
				ct++;
			}		
		}
		catch(Exception e)
		{
			fs = FileSystem.get(conf);
			fs.delete(new Path("/user/rohit/data_graphShortestPath/output"));
		}
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new shortestPath(), args);
		System.exit(exitCode);
	}

	

}
