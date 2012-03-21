package job;

import java.util.ArrayList;

public class generate {

	String Up (String s, int l, int ind)
	{		
		if(ind+1 > l)
		{
			StringBuffer s1 = new StringBuffer(s);
			s1.setCharAt(ind, s1.charAt(ind-l));
			s1.setCharAt(ind-l,'x');
			return s1.toString();
		}	
		return null;
	}
	
	String Down (String s, int l, int ind)
	{		
		if(ind+1 <= l*(l-1))
		{
			StringBuffer s1 = new StringBuffer(s);
			s1.setCharAt(ind, s1.charAt(ind+l));
			s1.setCharAt(ind+l, 'x');
			return s1.toString();
		}	
		return null;
	}
	
	String Right (String s, int l, int ind)
	{		
		if((ind+1)%l!=0)
		{
			StringBuffer s1 = new StringBuffer(s);
			s1.setCharAt(ind,s1.charAt(ind+1));
			s1.setCharAt(ind+1,'x');
			return s1.toString();
		}	
		return null;
	}
	
	String Left (String s, int l, int ind)
	{		
		if((ind+1)%l!=1)
		{
			StringBuffer s1 = new StringBuffer(s);
			s1.setCharAt(ind,s1.charAt(ind-1));
			s1.setCharAt(ind-1,'x');
			return s1.toString();
		}	
		return null;
	}
	
	boolean check(String s)
	{
		if(s.equals("12345678x"))
			return true;
		return false;
	}
	
	
	
	int MDistPair(String s)
	{
		int l = (int)Math.sqrt(s.length());
		return (ManhattanHeuristics(s, l)+distancePair(s, l));
	}	
	
	
	int ManhattanHeuristics(String s, int l)
	{
		int mdist=0;
		String s1 =new String("123456789abcdefx");
		
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)!='x')
			{
				mdist += Math.abs(i/l - (s1.indexOf(s.charAt(i))/l)); // row difference
				mdist += Math.abs((i%l)- (s1.indexOf(s.charAt(i))%l)); // col difference
				//System.err.print(Math.abs((i%l)- (s1.indexOf(s.charAt(i))%l)) + "   ");
			}
		}
		//System.err.println(s+" mdist= "+mdist);
		return mdist;
	}
	
	int distancePair(String s, int l)
	{
		ArrayList<Integer> rowlist = new ArrayList<Integer>();
		int pdist=0;
		for(int i=0;i<s.length()/l;i++)
		{
			for(int j=i*l;j<(i+1)*l;j++)
				for(int k=j+1;k<(i+1)*l;k++)
				{
					if(s.charAt(j)!='x' && s.charAt(k)!='x' && rowlist.indexOf(j)==-1 && rowlist.indexOf(k)==-1 && rPair(s,i,j,k,l))
					{
						rowlist.add(j);
						rowlist.add(k);
						pdist+=2;
					}						
				}
		}
		//System.err.println(s+" pdist= "+pdist);
		ArrayList<Integer> collist = new ArrayList<Integer>();
		
		
		for(int i=0;i<s.length()/l;i++)
		{
			for(int j=i;j<i+1+l*(l-1);j=j+l)
				for(int k=j+l;k<i+1+l*(l-1);k=k+l)
				{
					if(s.charAt(j)!='x' && s.charAt(k)!='x' && collist.indexOf(j)==-1 && collist.indexOf(k)==-1 && cPair(s,i,j,k,l))
					{
						collist.add(j);
						collist.add(k);
						pdist+=2;
					}						
				}
		}
		//System.err.println(s+" pdist= "+pdist);
		return pdist;		
	}
	
	boolean rPair(String s,int i,int j,int k,int l)
	{
		String s1 =new String("123456789abcdef");
		
		int x,y;
		if( (x=s1.indexOf(s.charAt(j))/l) == s1.indexOf(s.charAt(k))/l && x==i)
		{
			x = s1.indexOf(s.charAt(j));
			y = s1.indexOf(s.charAt(k));
			
			if((j>k && x<y)||(j<k && x>y))
				return true;
		}
		return false;
	}
	
	boolean cPair(String s,int i,int j,int k,int l)
	{
		String s1 =new String("123456789abcdef");
		
		int x,y;
		if( (x=s1.indexOf(s.charAt(j))%l) == s1.indexOf(s.charAt(k))%l && x==i)
		{
			x = s1.indexOf(s.charAt(j));
			y = s1.indexOf(s.charAt(k));
			
			if((j>k && x<y)||(j<k && x>y))
				return true;
		}
		return false;
	}
	
}
