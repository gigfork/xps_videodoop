package job;
import java.io.IOException;
import java.util.StringTokenizer;
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
		
	public void configure(JobConf conf)
	{
		this.conf = conf;
	}
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		StringTokenizer token = new StringTokenizer(value.toString(),"	");
		String box = token.nextToken();
		String type = token.nextToken();
		String Prnt = token.nextToken();
		
		if(type.compareTo("C")==0)
		{
			generate gn = new generate();
			int l = (int)Math.sqrt(box.length());
			int ind = box.indexOf("x");
			String tmp;
			
			if((tmp=gn.Down(box, l, ind))!=null)
			{
				output.collect(new Text(tmp),new Text("C	"+box+'\t'+gn.MDistPair(tmp)));
			}
			if((tmp=gn.Up(box, l, ind))!=null)
				output.collect(new Text(tmp),new Text("C	"+box+'\t'+gn.MDistPair(tmp)));
			
			if((tmp=gn.Left(box, l, ind))!=null)
				output.collect(new Text(tmp),new Text("C	"+box+'\t'+gn.MDistPair(tmp)));
			
			if((tmp=gn.Right(box, l, ind))!=null)
			{
				output.collect(new Text(tmp),new Text("C	"+box+'\t'+gn.MDistPair(tmp)));
			}
		}
		output.collect(new Text(box),new Text("P	"+Prnt));		
	}
}
