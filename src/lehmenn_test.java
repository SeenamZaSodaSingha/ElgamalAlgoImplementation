// Java code for Lehmann's Primality Test
// importing "random" for random operations
import java.math.BigInteger;
import java.util.Random;

class lehmenn_test
{
	private FastExponentiation fastExpo;
	private BigInteger _zero, _one, _two;
	lehmenn_test(){
		fastExpo = new FastExponentiation();
		_zero = BigInteger.valueOf(0);
		_one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
	}
	// function to check Lehmann's test
	private int lehmann(BigInteger n, int t)
	{
		// create instance of Random class
		Random rand = new Random();
		// generating a random base less than n
		BigInteger minLimit = _two;
		BigInteger maxLimit = n.subtract(minLimit);
		int len = n.bitLength();
		BigInteger a =  new BigInteger(len, rand);
		if (a.compareTo(minLimit) < 0)
			a = a.add(minLimit);
		if (a.compareTo(maxLimit) >= 0)
			a = a.mod(maxLimit).add(minLimit);
		//System.out.println("a = "+a);
		// calculating exponent
		BigInteger e = n.subtract(_one).divide(_two);
		// iterate to check for different base values
		// for given number of tries 't'
		while(t > 0)
		{
			// calculating final value using formula
			// BigInteger result = a.modPow(e, n);
			BigInteger result = fastExpo.fastExponentiation(a, e, n);
			//System.out.println("round i = "+(++i)+" result = "+result);
			// if not equal, try for different base
			if( result.mod(n).equals( _one ) || 
				result.mod(n).equals( n.subtract( _one ) ) )
			{
				a =  new BigInteger(len, rand);
				if (a.compareTo(minLimit) < 0)
					a = a.add(minLimit);
				if (a.compareTo(maxLimit) >= 0)
					a = a.mod(maxLimit).add(minLimit);
				//System.out.println("a : "+a);
				t -= 1;
			}
			// else return negative
			else
				return -1;
				
		}
		// return positive after attempting
		return 1;
	}
	// Driver code
	public boolean testPrime (BigInteger n, int t)
	{
		// if n is 2, it is prime
		if(n.equals( _two )){
			//System.out.println(" 2 is Prime");
			return true;
		}
		// if even, it is composite
		if( n.mod( _two ).equals( _zero ) ){
			//System.out.println(n + " is Composite");
			return false;
		}
		// if odd, check
		else
		{
			long flag = lehmann(n, t);
			if(flag == 1){
				//System.out.println(n + " may be Prime.");
				return true;
			}
			else{
				//System.out.println(n + " is Composite.");
				return false;
			}
		}
	}
}
// This code is contributed by AnkitRai01