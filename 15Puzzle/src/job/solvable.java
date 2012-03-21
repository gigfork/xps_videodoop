package job;
import java.util.Arrays;


public class solvable 
{
	public boolean solv(String s)
	{
		generate gn = new generate(s);
		String[] box = gn.convertToArray(s);
		boolean[] chck = new boolean[box.length-1];
		int num,inv=0;
		
		for(String s1:box)
		{
			if(!s1.equals("x"))
			{
				num = Integer.parseInt(s1);
				chck[num-1]=true;
				while(num--!=1)
					if(!chck[num-1])inv++;
			}
		}
		
		int l = (int)Math.sqrt(box.length);
		int row = gn.find(box,"x")/l;
		if((l%2==1 && inv%2==0) || (l%2==0 && (l-1-row)%2==1 && inv%2==1) || (l%2==0 && (l-1-row)%2==0 && inv%2==0))
			return true;
		return false;		
	}
}