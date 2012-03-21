package job;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class keyPair implements WritableComparable<keyPair> {

	private IntWritable num;
	private IntWritable heuristic;
	
	
	public IntWritable getNum()
	{
		return num;
	}
	
	public IntWritable getHeuristic()
	{
		return heuristic;
	}
	
	public keyPair()
	{
		this.num = new IntWritable();
		this.heuristic = new IntWritable();
	}
	
	public keyPair(int num1,int heur)
	{
		this.num = new IntWritable(num1);
		this.heuristic = new IntWritable(heur);

	}	
	
	@Override
	public void readFields(DataInput in) throws IOException {
		num.readFields(in);
		heuristic.readFields(in);		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		num.write(out);
		heuristic.write(out);	
	}


	@Override
	public int compareTo(keyPair key) {		
		int cmp =  num.compareTo(key.getNum());
		if(cmp!=0)
			return cmp;
		return heuristic.compareTo(key.getHeuristic());
	}

}
