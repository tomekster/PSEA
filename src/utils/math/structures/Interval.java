package utils.math.structures;

public class Interval implements Comparable<Interval> {
	private double beg, end;
	private double[] l1, l2;
	private int CV;
	
	public Interval(double b, double e, int CV, double[] l1, double[] l2){
		this.beg = b;
		this.end = e;
		this.CV = CV;
		this.l1 = l1;
		this.l2 = l2;
	}
	
	public Pair<Interval, Interval> halfSplit(){
		double m = (this.beg + this.end)/2;
		double M[] = new double[l1.length];
		for(int i = 0; i<l1.length; i++){
			M[i] = (l1[i] + l2[i])/2;
		}
		
		Interval a = new Interval(beg, m, CV, l1, M);
		Interval b = new Interval(m, end, CV, M, l2);
		
		return new Pair<Interval, Interval>(a,b);
	}
	
	@Override
	public int compareTo(Interval i) {
		return Integer.compare(CV, i.CV);
	}
	public double getBeg() {
		return beg;
	}
	public double getEnd() {
		return end;
	}
	public double[] getL1() {
		return l1.clone();
	}
	public double[] getL2() {
		return l2.clone();
	}
	public int getCV() {
		return CV;
	}
}
