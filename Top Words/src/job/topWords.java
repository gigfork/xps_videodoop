package job;
import java.net.URI;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;



public class topWords extends Configured implements Tool{

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new topWords(), args);
		System.exit(exitCode);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int run(String[] arg0) throws Exception {
		
		JobConf conf = new JobConf(getConf(),getClass());
		if(conf==null)
			return -1;
		
		FileInputFormat.addInputPath(conf, new Path("/user/rohit/data/input/film"));
		FileOutputFormat.setOutputPath(conf, new Path("/user/rohit/data/output/film"));
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setMapperClass(topWordsMap.class);
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(myCustomWritable.class);
		conf.setReducerClass(topWordsReduce.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		try{
		JobClient.runJob(conf);
		}
		catch(Exception e)
		{
			FileSystem fs = FileSystem.get(conf);
			fs.delete(new Path("/user/rohit/data/output/film"));
		}
		
		return 0;
	}

}
