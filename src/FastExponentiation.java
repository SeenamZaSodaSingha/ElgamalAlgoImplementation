import java.math.BigInteger;

public class FastExponentiation {
    private BigInteger _zero, _one, _two;

    FastExponentiation(){
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
    }

    public BigInteger fastExponentiation(BigInteger base, BigInteger exponent, BigInteger modulo) {
        if (exponent.compareTo( _zero ) < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }

        BigInteger result = _one;
        while (exponent.compareTo( _zero ) > 0) {
            if (exponent.mod( _two ).equals( _one )) {
                //result *= base;
                result = result.multiply(base).mod(modulo);
            }
            //base *= base;
            base = base.multiply(base).mod(modulo);
            //exponent /= 2;
            exponent = exponent.divide( _two );
        }
        return result;
    }
}