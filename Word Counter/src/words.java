import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class words extends Configured implements Tool {	
	
	@SuppressWarnings("deprecation")
	static class wordMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
	{

		Text word = new Text();
		private JobConf conf;
		
		@Override
		public void configure(JobConf conf) 
		{
			this.conf = conf;
		}

		@Override
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			
			String line = value.toString().toLowerCase();
			StringTokenizer token = new StringTokenizer(line);
			String s;
			
			while(token.hasMoreTokens())
			{		
				s = token.nextToken();
				if(!s.matches(".*[-+()^$!].*"))
				{
					word.set(s);
					output.collect(word, new Text(conf.get("map.input.file")));
				}
			}						
		}		
	}
	
	@SuppressWarnings("deprecation")
	public static class reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>
	{

		@Override
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			
			String names = "",val;
			int sum = 0;
			while(values.hasNext())
			{
				sum += 1;
				val = values.next().toString();				
				if(!names.matches(val))
						names = names+ ","+val;
			}
			names = names+","+sum;
			output.collect(key,new Text(names));			
		}
		
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		
		JobConf conf = new JobConf(getConf(),getClass());
		if(conf==null)
		{
			return -1;
		}
		FileInputFormat.addInputPath(conf, new Path("/user/rohit/data/input/film"));
		FileOutputFormat.setOutputPath(conf, new Path("/user/rohit/data/output/film"));
		
		conf.setInputFormat(MyCombineFileInputFormat.class);
		conf.setMapperClass(wordMapper.class);
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Text.class);
		conf.setReducerClass(reduce.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		JobClient.runJob(conf);
		return 0;
	}

	
	public static void main(String[] args) throws Exception {
		
		int exitCode = ToolRunner.run(new words(), args);
		System.exit(exitCode);
	}

}
