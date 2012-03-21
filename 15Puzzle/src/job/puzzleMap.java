package job;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

@SuppressWarnings("deprecation")
public class puzzleMap extends MapReduceBase 
implements Mapper<LongWritable, Text, Text, Text> {

	private JobConf conf;
	
	@Override
	public void configure(JobConf conf)
	{
		this.conf = conf;
		//System.exit(0);
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		StringTokenizer token = new StringTokenizer(value.toString(),"	");
		String box = token.nextToken();
		String type = token.nextToken();
		String Prnt = token.nextToken();
		String other = null;
		
		if(!type.equals("C"))
			other = token.nextToken()+'\t'+token.nextToken()+'\t'+token.nextToken()+'\t'+token.nextToken();
		
		generate gn = new generate(box);
		String[] array = gn.convertToArray(box);
		int ind = gn.find(array,"x");
		int l = (int)Math.sqrt(array.length);
		
		if(type.compareTo("C")==0)
		{			
			String tmp;
			Text text = new Text();
			Text text1 = new Text();
			
			if((tmp=gn.Down(array, l, ind))!=null)
			{
				text.set(tmp);
				text1.set("C	"+box+'\t'+gn.MDistPair(gn.convertToArray(tmp))+'\t'+gn.Max_swap(gn.convertToArray(tmp))+'\t'+gn.out_rc(gn.convertToArray(tmp)));
				output.collect(text,text1);
				
			}
			if((tmp=gn.Up(array, l, ind))!=null)
			{
				text.set(tmp);
				text1.set("C	"+box+'\t'+gn.MDistPair(gn.convertToArray(tmp))+'\t'+gn.Max_swap(gn.convertToArray(tmp))+'\t'+gn.out_rc(gn.convertToArray(tmp)));
				output.collect(text,text1);
			}			
			if((tmp=gn.Left(array, l, ind))!=null)
			{
				text.set(tmp);
				text1.set("C	"+box+'\t'+gn.MDistPair(gn.convertToArray(tmp))+'\t'+gn.Max_swap(gn.convertToArray(tmp))+'\t'+gn.out_rc(gn.convertToArray(tmp)));
				output.collect(text,text1);
			}
			if((tmp=gn.Right(array, l, ind))!=null)
			{
				text.set(tmp);
				text1.set("C	"+box+'\t'+gn.MDistPair(gn.convertToArray(tmp))+'\t'+gn.Max_swap(gn.convertToArray(tmp))+'\t'+gn.out_rc(gn.convertToArray(tmp)));
				output.collect(text,text1);
			}
			output.collect(new Text(box),new Text("P	"+Prnt+'\t'+gn.MDistPair(array)+'\t'+gn.Max_swap(array)+'\t'+gn.out_rc(array)+'\t'+gn.getJobId(conf.get("mapred.task.id"))));		

		}
		else		
			output.collect(new Text(box),new Text("P	"+Prnt+'\t'+other));		
	}
}
