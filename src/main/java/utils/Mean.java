package utils;

public class Mean {
	double sum;
	int n;
	
	public Mean() {
		sum = 0.0;
		n = 0;
	}
	
	public void addValue(Double value) {
		if (value == null)
			return;
		sum += value;
		n++;
	}
	
	public double getMean() {
		return sum /n;
	}
	
	public boolean isEmpty() {
		return n == 0;
	}
}
