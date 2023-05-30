import java.math.BigInteger;

public class Verify {
    public BigInteger p, alpha, beta, m, r, s;

    Verify(BigInteger a, BigInteger b, BigInteger c, BigInteger d, BigInteger e, BigInteger f) {
        p = a;
        alpha = b; //g
        beta = c; //y
        m = d; // hash value from message decryption
        r = e;
        s = f;
    }

    BigInteger v1(BigInteger y, BigInteger r, BigInteger s) { //y^r * r^s mod p
		// System.out.println("-------------------");
		// System.out.println("y: " + y + "\nr: " + r + "\ns: " + s + "\np: " + p);
		// System.out.println("-------------------");
        BigInteger result1 = new FastExponentiation().fastExponentiation(y, r, p);
        BigInteger result2 = new FastExponentiation().fastExponentiation(r, s, p);
		// System.out.println("result1 v1: " + result1);
		// System.out.println("result2 v1: " + result2);
		// System.out.println("result v1: " + result1.multiply(result2).mod(p));
		// System.out.println("====================================");
        return result1.multiply(result2).mod(p);
    }

    BigInteger v2(BigInteger g, BigInteger x) { //g^x mod p
		// System.out.println("g: " + g + "\nx: " + x + "\np: " + p);
		// System.out.println("result v2: " + new FastExponentiation().fastExponentiation(g, x, p));
        return new FastExponentiation().fastExponentiation(g, x, p);
    }

    String verifySignature() {
        if (v1(beta, r, s).equals(v2(alpha, m))) {
            return "\n===============\nSignature MATCH\n===============\n";
        } else {
            return "\n=====================\nSignature Mismatch\n=====================\n";
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