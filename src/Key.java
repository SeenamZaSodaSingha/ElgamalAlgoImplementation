import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.json.JSONObject;

public class Key {
    private JSONObject jsonObject = new JSONObject();
    private FileOpr fileOpr = new FileOpr();
    private lehmenn_test lehm;
    private gcdExtended gcdE;
    private FastExponentiation fastExpo;
    private BigInteger _zero, _one, _two;
    private BigInteger p = new BigInteger("10"), g, u, y, k, a;
    private int blocksize;

    public Key() {
        lehm = new lehmenn_test();
        gcdE = new gcdExtended();
        fastExpo = new FastExponentiation();
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
    }

    public Key(String path) throws IOException {
        // will fix to read accept key from file
        lehm = new lehmenn_test();
        gcdE = new gcdExtended();
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
        readKeyFromFile(path);
    }

    public void setA(BigInteger a) {
        this.a = a;
        jsonObject.put("a", a);
    }

    public void setP(BigInteger p) {
        this.p = p;
        jsonObject.put("p", p);
    }

    public void setG(BigInteger g) {
        this.g = g;
        jsonObject.put("g", g);
    }

    public void setU(BigInteger u) {
        this.u = u;
        jsonObject.put("u", u);
    }

    public void setY(BigInteger y) {
        this.y = y;
        jsonObject.put("y", y);
    }

    public void setK(BigInteger k) {
        this.k = k;
        jsonObject.put("k", k);

    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getU() {
        return u;
    }

    public BigInteger getY() {
        return y;
    }

    public BigInteger getK() {
        return k;
    }

    public BigInteger getA() {
        return a;
    }

    public int getBlockSize() {
        return blocksize;
    }

    public void writeKeytoFile(String filePath) {
        // System.out.println("FROM WRITE KEY TO FILE");
        // System.out.println("p: " + jsonObject.getBigInteger("p"));
        // System.out.println("g: " + jsonObject.getBigInteger("g"));
        // System.out.println("a: " + jsonObject.getBigInteger("a"));
        // System.out.println("u: " + jsonObject.getBigInteger("u"));
        fileOpr.writeJsonToFile(jsonObject, filePath);
    }

    public void readKeyFromFile(String filePath) throws IOException {
        // read in json form
        jsonObject = fileOpr.readJsonFromFile(filePath);
        this.p = jsonObject.getBigInteger("p");
        this.g = jsonObject.getBigInteger("g");
        this.a = jsonObject.getBigInteger("a");
        this.u = jsonObject.getBigInteger("u");
        this.blocksize = jsonObject.getInt("block_size");
    }

    // Beat's code
    // Copy the whole method
    public void random_P(int n) {
        int roundTest = 100;
        Random rand = new Random();
        boolean loopI = true;
        n /= 4;

        int block_size = n-1;
        this.blocksize = block_size;
        System.out.println("block size from randomP: "+block_size);
        jsonObject.put("block_size", block_size);

        //setting for prime*2+1 = safe prime and still in bound length
        String upperboundString = "4";
        String lowerboundString = "5";
        for (int i = 2; i < n; i++) {
            upperboundString = upperboundString + "9";
            lowerboundString = lowerboundString + "0";
        }
        upperboundString = upperboundString + "9";
        BigInteger minLimit = new BigInteger(lowerboundString);
        BigInteger maxLimit = new BigInteger(upperboundString);
        maxLimit = maxLimit.subtract(minLimit);
        int len = maxLimit.bitLength();
        BigInteger p =  _zero;

        // System.out.println("i = "+i);
        p = new BigInteger(len, rand);
        if (p.compareTo(minLimit) < 0)
            p = p.add(minLimit);
        if (p.compareTo(maxLimit) >= 0)
            p = p.mod(maxLimit).add(minLimit);
        // System.out.println("p = "+p);

        while(p.compareTo(maxLimit) < 0) {
            // System.out.println(p);

            //for cut down even number
            if (p.mod(_two).equals(_zero))
                p = p.add(_one);

            //check prime
            if(lehm.testPrime(p, roundTest)){
                //prime
                //change prime to safe prime even if is safe prime already
                //lowerbound and upperbound is set for *2+1 don't mind to increase p
                p = p.multiply(_two).add(_one);
                if (lehm.testPrime(p, roundTest)) {
                    System.out.println(p);
                    System.out.println("safe prime gen p size : "+p.toString().length());
                    break;
                }
                else {
                    //not prime
                    p = p.subtract(_one).divide(_two);
                    p = p.add(_two);
                }
            }
            else{
                //not prime
                p = p.add(_two);
                continue;
            }

            // check safe prime
            // if (!lehm.testPrime(p.subtract(_one).divide(_two), roundTest)) {
            //     // System.out.println(p+" is not safe prime");
            //     //p = p.multiply(_two).add(_one);

            //     //for break loopI
            //     //loopI = !lehm.testPrime(p, roundTest);
            //     // System.out.println("is new p is prime? : "+(!loopI));
            // } else{
            //     //break in safe prime case
            //     loopI = false;
            //     break;
            // }
        }
        if (loopI) {
            System.out.println("Loop I is true");
        }
        this.p = p;
        jsonObject.put("p", p);
    }

    public void random_G() {
        // g is root of p
        Random rand = new Random();
        // g = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger g = new BigInteger(len, rand);
        if (g.compareTo(minLimit) < 0)
            g = g.add(minLimit);
        if (g.compareTo(maxLimit) >= 0)
            g = g.mod(maxLimit).add(minLimit);
        // System.out.println("g = "+g);

        // g^(p-1)/2 % p must != 1
        // if g^(p-1)/2 % p = 1 then g = p-g
        // if( g.modPow(p.subtract( _one ).divide( _two ), p).equals( _one ) )
        g = fastExpo.fastExponentiation(g, p.subtract(_one).divide(_two), p);
        if (g.equals(_one))
            g = p.subtract(g);
        this.g = g;
        jsonObject.put("g", g);
    }

    public void random_U() {
        Random rand = new Random();
        // u = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger u = new BigInteger(len, rand);
        if (u.compareTo(minLimit) < 0)
            u = u.add(minLimit);
        if (u.compareTo(maxLimit) >= 0)
            u = u.mod(maxLimit).add(minLimit);
        // System.out.println("u = "+u);

        this.u = u;
        jsonObject.put("u", u);
    }

    public void random_K() {
        Random rand = new Random();
        boolean loopI = true;
        // k = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger k = _zero;

        for (int i = 0; i < 100 && loopI; i++) {
            k = new BigInteger(len, rand);
            if (k.compareTo(minLimit) < 0)
                k = k.add(minLimit);
            if (k.compareTo(maxLimit) >= 0)
                k = k.mod(maxLimit).add(minLimit);
            // System.out.println("k = "+k);

            loopI = !gcdE.testGCD(p.subtract(_one), k);
        }
        this.k = k;
        jsonObject.put("k", k);
    }

    public void generateY() {
        // this.y = g.modPow(u, p);
        this.y = fastExpo.fastExponentiation(g, u, p);
    }
}