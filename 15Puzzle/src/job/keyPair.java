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
	private IntWritable MD_heuristic;
	private IntWritable Max_Swap_heuristic;
	private IntWritable row_col_heuristic;
	
	
	public IntWritable getNum()
	{
		return num;
	}
	
	public IntWritable getMDHeuristic()
	{
		return MD_heuristic;
	}
	
	public IntWritable getMaxHeuristic()
	{
		return Max_Swap_heuristic;
	}
	
	public IntWritable getRowColHeuristic()
	{
		return row_col_heuristic;
	}
	
	public keyPair()
	{
		this.num = new IntWritable();
		this.MD_heuristic = new IntWritable();
		this.Max_Swap_heuristic = new IntWritable();
		this.row_col_heuristic = new IntWritable();
	}
	
	public keyPair(int num1,int heur1,int heur2,int heur3)
	{
		this.num = new IntWritable(num1);
		this.MD_heuristic = new IntWritable(heur1);
		this.Max_Swap_heuristic = new IntWritable(heur3);
		this.row_col_heuristic = new IntWritable(heur3);
	}	
	
	@Override
	public void readFields(DataInput in) throws IOException {
		num.readFields(in);
		MD_heuristic.readFields(in);
		Max_Swap_heuristic.readFields(in);
		row_col_heuristic.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		num.write(out);
		MD_heuristic.write(out);
		Max_Swap_heuristic.write(out);
		row_col_heuristic.write(out);
	}
	
	@Override
	public int compareTo(keyPair key) {		
		int cmp =  num.compareTo(key.getNum());
		if(cmp!=0)
			return cmp;
		cmp = MD_heuristic.compareTo(key.getMDHeuristic());
		if(cmp!=0)
			return cmp;
		cmp = Max_Swap_heuristic.compareTo(key.getMaxHeuristic());
		if(cmp!=0)
			return cmp;
		
		return row_col_heuristic.compareTo(key.getRowColHeuristic());		
	}
}
