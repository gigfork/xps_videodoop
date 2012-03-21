package job;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;


public class myCustomWritable implements Writable{

	private Text word;
	private IntWritable count;
	
	public myCustomWritable()
	{
		set(new Text(),new IntWritable());
	}
	
	public myCustomWritable(String word,int count)
	{
		set(new Text(word),new IntWritable(count));
	}
	
	public myCustomWritable(Text word, IntWritable count)
	{
		set(word,count);
	}
	
	public void set(Text word, IntWritable count)
	{
		this.word = word;
		this.count = count;
	}
	
	public Text getFilename()
	{
		return word;
	}
	
	public IntWritable getCount()
	{
		return count;
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		word.readFields(in);
		count.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		word.write(out);
		count.write(out);		
	}

}
