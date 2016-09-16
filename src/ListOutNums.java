
public class ListOutNums {
	static int numOfnums = 1;
	static int list_size = 200;
	
	public static int nextNum(int currentNum) {
		int place = 1;
		int result = 0;
		result = currentNum + 1;
		
		while (result / (int)Math.pow(10, place - 1) != 0) {
			int digit = findDigit(result, place);
			if (digit == numOfnums + 1) {
				result = (int)(result - (digit * Math.pow(10, place - 1)) 
						+ Math.pow(10, place));
			}
			place++;
		}
		return result;
	}
	
	public static int findDigit(int num, int digit) {
		return ((int)(num % Math.pow(10, digit)) / (int)Math.pow(10, digit - 1));
	}

	public static void main(String[] args) {
		int testnum = 0;
		for (int k1 = 0; k1 < list_size; k1++) {
			testnum = nextNum(testnum);
			System.out.println(testnum);
		}

	}

}
