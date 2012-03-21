package job;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


@SuppressWarnings("deprecation")
public class topWordsMap extends MapReduceBase 
			implements Mapper<LongWritable, Text, Text, myCustomWritable>{

	private JobConf conf;
	private String words[] = {"a","the","of","an","in","on","and","i","to"};
	
	public void Configure(JobConf conf)
	{
		this.conf = conf;
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, myCustomWritable> output, Reporter reporter)
			throws IOException {
		
		
		FileSplit fileSplit = (FileSplit)reporter.getInputSplit();
	    String filename = fileSplit.getPath().toString();		
		
		String line = value.toString().toLowerCase();
		StringTokenizer token = new StringTokenizer(line);
		String s;
		boolean flag=false;
		myCustomWritable tmp = new myCustomWritable();
		
		while(token.hasMoreTokens())
		{
			s = token.nextToken().toString();
			if(s!=null && !s.matches(".*[-+()^$@*_~`=%<>;!.\"\\/].*"))
			{
				for(int i=0;i<words.length;i++)
					if(s.compareToIgnoreCase(words[i])==0)
					{
						flag = true;
						break;
					}
				if(!flag)
				{
					tmp.set(new Text(s),new IntWritable(1));
					output.collect(new Text(filename),tmp);			
				}
			}
		}
		
	}

}
