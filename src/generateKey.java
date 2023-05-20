import java.math.BigInteger;
import java.util.Random;

public class generateKey {
    private lehmenn_test lehm;
    gcdExtended gcdE;
    private BigInteger _zero, _one, _two;
    generateKey(){
        lehm = new lehmenn_test();
        gcdE = new gcdExtended();
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
    }
    public BigInteger random_P(int n){
        int roundTest = 100;
        Random rand = new Random();
        //byte[] arr = new byte[n];
        int bitLenght = (n+1) * 4;
        BigInteger p = _zero;
        boolean loopI = true, loopJ = true;
        for (int i = 0; i < 100 && loopI; i++) {
            for (int j = 0; j < 100 && loopJ; j++) {
                // rand.nextBytes(arr);
                // System.out.println(arr);
                // p = new BigInteger(1, arr);
                // System.out.println(p);
                do {
                    p = new BigInteger(bitLenght, rand);
                } while (p.toString().length() != (n+1));
                //System.out.println(p);
                
                loopJ = !lehm.testPrime(p, roundTest);
            }
            loopJ = true;

            //check safe prime
            if(!lehm.testPrime(p.subtract( _one ).divide( _two ), roundTest))
            {
                //System.out.println(p+" is not safe prime");
                p = p.multiply( _two ).add( _one );
                loopI = !lehm.testPrime(p, roundTest);
                //System.out.println("is new p is prime? : "+(!loopI));
            }
        }
        return p;
    }

    public BigInteger random_G(BigInteger p){
        // g is root of p
        Random rand = new Random();
        // g = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger g =  new BigInteger(len, rand);
        if (g.compareTo(minLimit) < 0)
            g = g.add(minLimit);
        if (g.compareTo(maxLimit) >= 0)
            g = g.mod(maxLimit).add(minLimit);
        //System.out.println("g = "+g);

        // g^(p-1)/2 % p must != 1
        // if g^(p-1)/2 % p = 1 then g = p-g 
        if( g.modPow(p.subtract( _one ).divide( _two ), p).equals( _one ) )
            g = p.subtract(g);

        return g;
    }

    public BigInteger random_U(BigInteger p){
        Random rand = new Random();
        // u = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger u =  new BigInteger(len, rand);
        if (u.compareTo(minLimit) < 0)
            u = u.add(minLimit);
        if (u.compareTo(maxLimit) >= 0)
            u = u.mod(maxLimit).add(minLimit);
        //System.out.println("u = "+u);

        return u;
    }

    public BigInteger random_K(BigInteger p){
        Random rand = new Random();
        boolean loopI = true;
        // k = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger k = _zero;

        for (int i = 0; i < 100 && loopI; i++) {
            k =  new BigInteger(len, rand);
            if (k.compareTo(minLimit) < 0)
                k = k.add(minLimit);
            if (k.compareTo(maxLimit) >= 0)
                k = k.mod(maxLimit).add(minLimit);
            //System.out.println("k = "+k);
            
            loopI = !gcdE.testGCD(p.subtract( _one ), k);
        }

        return k;
    }
}
