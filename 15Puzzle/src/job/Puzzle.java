package job;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


@SuppressWarnings("deprecation")
public class Puzzle extends Configured implements Tool {	
	
	public static enum myCounters {
		Finished,
		NoCs,
		NoCs1,
	}
	
	
	private JobConf getJobConf(int ch) throws URISyntaxException {
	    JobConf conf = new JobConf(getConf(), Puzzle.class);

	   if(ch==0)
	   {
		   conf.setInputFormat(TextInputFormat.class);
		   conf.setMapperClass(generateMap.class);
		   conf.setMapOutputKeyClass(Text.class);
		   conf.setMapOutputValueClass(Text.class);
		  // conf.setReducerClass(puzzleReduce.class);
		   conf.setOutputKeyClass(Text.class);
		   conf.setOutputValueClass(Text.class);
		   conf.setOutputFormat(TextOutputFormat.class);
	   }
	   else if(ch==1)
	   {
		   conf.setInputFormat(MyCombineFileInputFormat.class);
		   conf.setMapperClass(puzzleMap.class);
		   conf.setMapOutputKeyClass(Text.class);
		   conf.setMapOutputValueClass(Text.class);
		   conf.setReducerClass(puzzleReduce.class);
		   conf.setOutputKeyClass(Text.class);
		   conf.setOutputValueClass(Text.class);
		   conf.setOutputFormat(TextOutputFormat.class);
	   }
	   else
	   {
			conf.setInputFormat(MyCombineFileInputFormat.class);
		    conf.setMapperClass(puzzleMap_second.class);
			conf.setReducerClass(puzzleReduce_second.class);
			conf.setPartitionerClass(randomPartitioner.class);
			conf.setOutputValueGroupingComparator(GroupComparator.class);
			conf.setMapOutputKeyClass(keyPair.class);
			conf.setMapOutputValueClass(Text.class);
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(Text.class);
			conf.setOutputFormat(TextOutputFormat.class);
	   }
	   return conf;
	}	
	
 public int run(String[] arg0) throws Exception {		
	
	
	JobConf conf1 = null;
	JobConf conf2 = null;
	
	FileSystem fs;
	int ct=1;
	boolean flag=false;	
	
	try{
		while(true)
		{	
			if(ct==1)
				conf1 = getJobConf(0);
			else
				conf1 = getJobConf(1);
			
			fs = FileSystem.get(conf1);
			
			System.err.println("---"+ct);
			if(ct==1)
				FileInputFormat.addInputPath(conf1, new Path("/home/abc/"));
			else
				if(flag)
					FileInputFormat.addInputPath(conf1, new Path("/user/user/user/rohit/output/outputPuzzle_tmp/"+(ct-1)));
				else
					FileInputFormat.addInputPath(conf1, new Path("/user/user/user/rohit/output/outputPuzzle/"+(ct-1)));
			
			flag = false;
			
			if(ct>3)
			{
				fs.delete(new Path("/user/user/user/rohit/output/outputPuzzle/"+(ct-2)));
				fs.delete(new Path("/user/user/user/rohit/output/outputPuzzle_tmp/"+(ct-2)));
			}
			
			FileOutputFormat.setOutputPath(conf1, new Path("/user/user/user/rohit/output/outputPuzzle/"+ct));
			
			RunningJob job; 
			job = JobClient.runJob(conf1);
			job.waitForCompletion();
			
			Counters counters = job.getCounters();
			Counter finished = counters.findCounter(myCounters.Finished);
			Counter Cs = counters.findCounter(myCounters.NoCs);

			if(finished.getValue()==1)
				break;
			if(Cs.getValue()==0)
				break;
			
			
			//-----------------------------------------------------------------------------------
			
			if(Cs.getValue()>=400000)
			{
				conf2 = getJobConf(2);
				flag=true;			
				FileInputFormat.addInputPath(conf2, new Path("/user/user/user/rohit/output/outputPuzzle/"+ct));
				FileOutputFormat.setOutputPath(conf2, new Path("/user/user/user/rohit/output/outputPuzzle_tmp/"+ct));
				RunningJob job1; 
				job1 = JobClient.runJob(conf2);
				job1.waitForCompletion();
				counters = job1.getCounters();
			}
			System.err.println("Counter CS= "+Cs.getValue());
			Cs = counters.findCounter(myCounters.NoCs1);
			System.err.println("Counter CS= "+Cs.getValue());
			ct++;
		}
			
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.err.println();
		fs = FileSystem.get(conf1);
		fs.delete(new Path("/user/user/user/rohit/output/"));
	}			
	return 0;		
}
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new Puzzle(), args);
		System.exit(exitCode);
	}

	public static class randomPartitioner implements Partitioner<keyPair, Text>
	{
		@Override
		public void configure(JobConf arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public int getPartition(keyPair key, Text arg1, int numPartition) {
			return key.getNum().get();
		}	
	}
	
	public static class GroupComparator extends WritableComparator {
		
		protected GroupComparator()
		{
			super(keyPair.class,true);
		}
		public int compare(WritableComparable w1, WritableComparable w2)
		{
			keyPair k1 = (keyPair)w1;
			keyPair k2 = (keyPair)w2;			
			return(k1.getNum().compareTo(k2.getNum()));			
		}
	}	
	
}
