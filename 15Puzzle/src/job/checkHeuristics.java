package job;

public class checkHeuristics {
	
	public static void main(String[] args) throws Exception {
		
		String box = "2,1,3,4,5,6,7,8,9,10,11,12,13,15,14,x";//1,2,3,4,5,6,7,8,9,10,11,12,13,15,14,x";
		
		
		generate gn = new generate(box);
		String[] array = gn.convertToArray(box);		
		
		solvable sol = new solvable();
		System.err.println(sol.solv(box));
		
		
		
		int l = (int)Math.sqrt(array.length);
		int ind = gn.find(array,"x");
		String tmp;		
		
		System.out.println("Self = "+box+"  "+gn.MDistPair(array)+"  "+gn.Max_swap(array)+"  "+gn.out_rc(array));
		System.out.println(gn.finalString);
		if((tmp=gn.Down(array, l, ind))!=null)
			System.out.println("Down = "+tmp+"  "+gn.MDistPair(gn.convertToArray(tmp))+"  "+gn.Max_swap(gn.convertToArray(tmp)));
		if((tmp=gn.Up(array, l, ind))!=null)
			System.out.println("Up = "+tmp+"  "+gn.MDistPair(gn.convertToArray(tmp))+"  "+gn.Max_swap(gn.convertToArray(tmp)));
		
		if((tmp=gn.Left(array, l, ind))!=null)
			System.out.println("Left = "+tmp+"  "+gn.MDistPair(gn.convertToArray(tmp))+"  "+gn.Max_swap(gn.convertToArray(tmp)));
		
		if((tmp=gn.Right(array, l, ind))!=null)
			System.out.println("Right = "+tmp+"  "+gn.MDistPair(gn.convertToArray(tmp))+"  "+gn.Max_swap(gn.convertToArray(tmp)));
		
	}
	
	

}
