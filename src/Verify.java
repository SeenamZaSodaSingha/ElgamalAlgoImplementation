import java.math.BigInteger;

public class Verify {
    public BigInteger p, alpha, beta, m, r, s;

    Verify(BigInteger a, BigInteger b, BigInteger c, BigInteger d, BigInteger e, BigInteger f) {
        p = a;
        alpha = b;
        beta = c;
        m = d;
        r = e;
        s = f;
    }

    BigInteger v1(BigInteger b, BigInteger c, BigInteger d, BigInteger e) {
        BigInteger result1 = new FastExponentiation().fastExponentiation(b, c, p);
        BigInteger result2 = new FastExponentiation().fastExponentiation(d, e, p);
        return result1.multiply(result2).mod(p);
    }

    BigInteger v2(BigInteger b, BigInteger c) {
        return new FastExponentiation().fastExponentiation(b, c, p);
    }

    void verified() {
        if (v1(beta, r, r, s).equals(v2(alpha, m))) {
            System.out.println("Signature verified using ElGamal.");
            System.out.println("The value of v1 mod p: " + v1(beta, r, r, s));
            System.out.println("The value of v2 mod p: " + v2(alpha, m));
        } else {
            System.out.println("Signature mismatch");
            System.out.println("The value of v1 mod p: " + v1(beta, r, r, s));
            System.out.println("The value of v2 mod p: " + v2(alpha, m));
        }
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