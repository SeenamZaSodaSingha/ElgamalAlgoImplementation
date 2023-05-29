import java.math.BigInteger;
import java.util.Random;

public class SignAlgorithm {
    public BigInteger p, alpha, beta, m, r, s, k;
	
	/*
	 * p -> use the same with message
	 * k -> same with message
	 * alpha -> g
	 * m -> message
	 */
    private BigInteger z = BigInteger.valueOf(16); //between 1 -p-1 after get z and p-1 are coprime
	

    SignAlgorithm(BigInteger a, BigInteger b, BigInteger c, BigInteger d) { // p, g, message, k
        p = a;
        alpha = b;
        beta = new FastExponentiation().fastExponentiation(alpha, z, p);
        m = c;
        k = d;
        r = createR(alpha, k); // concat w/message
        s = createS(); //concat w/message
		
    }

    BigInteger createR(BigInteger b, BigInteger c) {
        return new FastExponentiation().fastExponentiation(b, c, p);
    }

	// does this need to fix?
	boolean gcd(BigInteger a, BigInteger b) {
		BigInteger x = BigInteger.ZERO, y = BigInteger.ONE, lastx = BigInteger.ONE, lasty = BigInteger.ZERO, temp;
		while (!b.equals(BigInteger.ZERO)) {
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

    BigInteger createK() {
        BigInteger two = BigInteger.valueOf(2);
        BigInteger a = two.multiply(p.subtract(BigInteger.ONE));
        while (!gcd(a, p.subtract(BigInteger.ONE))) { //gcd equal to one
            a = new BigInteger(p.bitLength(), new Random());
        }
        return a;
    }

	BigInteger invK() {
        BigInteger result = BigInteger.ZERO;
        for (BigInteger x = BigInteger.ONE; x.compareTo(p.subtract(BigInteger.ONE)) < 0; x = x.add(BigInteger.ONE)) {
            if (k.multiply(x).mod(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE)) {
                result = x;
                break;
            }
        }
        return result;
    }

    BigInteger createS() {
        BigInteger invK = k.modInverse(p.subtract(BigInteger.ONE));
        BigInteger temp = m.subtract(z.multiply(r));
        BigInteger a = invK.multiply(temp).mod(p.subtract(BigInteger.ONE));
        if (a.compareTo(BigInteger.ZERO) >= 0)
            return a;
        else
            return a.add(p.subtract(BigInteger.ONE));
    }
	

	class FastExponentiation {
		private BigInteger _zero, _one, _two;
	
		FastExponentiation() {
			_zero = BigInteger.ZERO;
			_one = BigInteger.ONE;
			_two = BigInteger.valueOf(2);
		}
	
		public BigInteger fastExponentiation(BigInteger base, BigInteger exponent, BigInteger modulo) {
			if (exponent.compareTo(_zero) < 0) {
				throw new IllegalArgumentException("Exponent must be non-negative.");
			}
	
			BigInteger result = _one;
			while (exponent.compareTo(_zero) > 0) {
				if (exponent.mod(_two).equals(_one)) {
					result = result.multiply(base).mod(modulo);
				}
				base = base.multiply(base).mod(modulo);
				exponent = exponent.divide(_two);
			}
			return result;
		}
	}

}