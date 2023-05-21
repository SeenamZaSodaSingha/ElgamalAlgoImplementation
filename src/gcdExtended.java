// Java program to demonstrate working of extended
// Euclidean Algorithm
import java.math.BigInteger;

class gcdExtended {
	private BigInteger _zero, _one;

	gcdExtended() {
		_zero = BigInteger.valueOf(0);
		_one = BigInteger.valueOf(1);
	}

	public boolean testGCD(BigInteger a, BigInteger b) {
		BigInteger x = _zero, y = _one, lastx = _one, lasty = _zero, temp;
		while (!b.equals(_zero)) {
			BigInteger q = a.divide(b);
			BigInteger r = a.mod(b);

			a = b;
			b = r;

			temp = x;
			x = lastx.subtract(q.multiply(x));
			lastx = temp;

			temp = y;
			y = lasty.subtract(q.multiply(y));
			lasty = temp;
		}
		// System.out.println("GCD " + a + " and its Roots x : " + lastx + " y :" + lasty);
		return a.equals(BigInteger.valueOf(1));
	}
}
// Code Contributed by Aryan itsmearyan <(0-o)>
