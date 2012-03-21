package job;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;



public class vrp extends Configured implements Tool{
	
	public static boolean  last=false;
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new vrp(), args);
		System.exit(exitCode);
	}
	
	
	@SuppressWarnings("deprecation")
	private JobConf getJobConf(int ch) throws URISyntaxException {
	    JobConf conf = new JobConf(getConf(), getClass());
	    
	    if(ch==0)
	    {
	    	conf.setInputFormat(TextInputFormat.class);
			conf.setMapperClass(fitnessMap_phase1.class);
			conf.setReducerClass(selectionReduce.class);
			conf.setMapOutputKeyClass(IntWritable.class);
			conf.setMapOutputValueClass(Text.class);
			conf.setOutputKeyClass(IntWritable.class);
			conf.setOutputValueClass(Text.class);
			conf.setOutputFormat(TextOutputFormat.class);
			conf.set("mapred.textoutputformat.separator", ">");
	    }
	    else if(ch<100 && !last)
	    {
	    	System.err.println(ch+"   "+last);
	    	conf.set("key.value.separator.in.input.line", ">");
	    	conf.setInputFormat(KeyValueTextInputFormat.class);
			conf.setMapperClass(fitnessMap2.class);
			conf.setReducerClass(selectionReduce.class);
			conf.setMapOutputKeyClass(IntWritable.class);
			conf.setMapOutputValueClass(Text.class);
			conf.setOutputKeyClass(IntWritable.class);
			conf.setOutputValueClass(Text.class);
			conf.setOutputFormat(TextOutputFormat.class);
			conf.set("mapred.textoutputformat.separator", ">");
	    }
	    else
	    {
	    	System.err.println("-----------"+ch+"   "+last);
	    	conf.set("key.value.separator.in.input.line", ">");
	    	conf.setInputFormat(KeyValueTextInputFormat.class);
			conf.setMapperClass(fitnessMap3.class);
			conf.setMapOutputKeyClass(IntWritable.class);
			conf.setMapOutputValueClass(Text.class);
			conf.setOutputKeyClass(IntWritable.class);
			conf.setOutputValueClass(Text.class);
			conf.setOutputFormat(TextOutputFormat.class);
	    }
	    return conf;
	 
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] arg0) throws Exception {
		
		
		JobConf conf;
		FileSystem fs;		

		int ch=0,count=0,fit;
		long last_fitness =0;		
		
		while(true)
		{
			System.err.println("Iteration -- "+ch);
			System.err.println("max -- "+last_fitness);
			
			conf=getJobConf(ch);
			fs = FileSystem.get(conf);
			if(ch==0)
			{
				fs.delete(new Path("/user/user/user/rohit/input/vrp_output"));
				fs.delete(new Path("/user/user/user/rohit/input/islands/"));
				
				FileInputFormat.addInputPath(conf, new Path("/user/user/user/rohit/input/vrp_input/pop.txt"));
				FileOutputFormat.setOutputPath(conf, new Path("/user/user/user/rohit/input/vrp_output/"+ch));
			}
			else 
			{
				if(last!=true){
				fs.delete(new Path("/user/user/user/rohit/input/islands/"));}
				FileInputFormat.addInputPath(conf, new Path("/user/user/user/rohit/input/vrp_output/"+(ch-1)));
				FileOutputFormat.setOutputPath(conf, new Path("/user/user/user/rohit/input/vrp_output/"+ch));
			}
			if(ch>1);
				fs.delete(new Path("/user/user/user/rohit/input/vrp_output/"+(ch-3)));
		
		
			URI archiveUri = new URI("/user/user/user/rohit/input/vrp_input/customerFile.map.tar.gz"); 
			//DistributedCache.addCacheArchive(archiveUri, conf); 
			
			archiveUri = new URI("/user/user/user/rohit/input/vrp_input/type.map.tar.gz"); 
			//DistributedCache.addCacheArchive(archiveUri, conf);
			
			archiveUri = new URI("/user/user/user/rohit/input/vrp_input/speed.map.tar.gz"); 
		    //DistributedCache.addCacheArchive(archiveUri, conf);
			
		
			RunningJob job; 
			job = JobClient.runJob(conf);
			job.waitForCompletion();
			
			ch++;
			fit = readMax(fs, conf.getNumReduceTasks());
			System.err.println("fit ---- "+fit);
			if(fit==last_fitness)
				count++;
			else
			{
				count=0;
				if(fit>last_fitness)
					last_fitness=fit;
			}
			if(last)break;
			
			if(count==10 || ch>=200)
			{ 
				if(count==10) System.err.println("Terminated Due to convergence <count>");
				last=true;		//count = how many times same fitness came	
			}
		}
	
		return 0;
	}
	
	int readMax(FileSystem fs,int numReducers) throws IOException
	{
		int max = 0,i=-1,tmp;
		FSDataInputStream in = null;
	    System.err.println(numReducers);
		while(++i<numReducers)
		{
			try{
		    in = fs.open(new Path("/user/user/user/rohit/input/islands/"+i));
		    tmp = in.readInt();
		   // System.err.println("***"+tmp);
		    if(tmp>max)
		    	max = tmp;
		    in.close();
			}
			catch(Exception e)
			{if(in!=null)in.close();}
		}
		return max;
	}

}
