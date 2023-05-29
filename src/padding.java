public class padding {
    private String paddingsStatus;

    padding(){
        paddingsStatus = "0";
    }

    public String paddingMsg(String msg, int blockSize){
        String pad = "";
        //for increse block size to same as receiver block size
        blockSize++;
        int lenRemining = (blockSize - msg.length()) % 10;
        pad = ""+lenRemining;
        System.out.println("padding num : "+pad);
        if (msg.length() < blockSize) {
            padCheck();
        }
        while (msg.length() < blockSize) {
            msg = msg + pad;
        }
        return msg;
    }

    public String unpaddingMsg(String msg, int blockSize){
        int lastNum = Integer.valueOf( msg.substring( (blockSize-1) ) );
        int count = 1;
        //counting same num as last num
        for (int i = blockSize-2; i > 0; i--) {
            if ( Integer.valueOf(msg.substring(i, i+1)) == lastNum ) {
                count++;
            }
            else{
                break;
            }
        }
        if (lastNum==(count % 10)) {
            msg = msg.substring( 0, blockSize - count);
        }
        else if(count > 1 && count > lastNum) {
            if (count % 10 > lastNum) {
                //like pad 3 3byte but read last number same as 3 is 4 byte
                //(count % 10) - lastNum should be real plaintext not padding
                //(4%10)-1 = first 1 number should be real plaintext
                //another 3 should be padding
                count -= ( (count % 10) - lastNum );
                msg = msg.substring( 0, blockSize - count);
            }
            else if(count % 10 < lastNum){
                //like pad 9 9byte but read last number same as 9 is 12 byte
                //(count%10) + (10-lastnum) should be real plaintext not padding
                //(12%10)+(10-9) = first 3 number should be real plaintext
                //another 9 should be padding
                System.out.println("before count : "+count);
                System.out.println((count % 10) + (10 - lastNum));
                count  -= ( (count % 10) + (10 - lastNum) );
                System.out.println("after count : "+count);
                System.out.println("lastNum : "+lastNum);
                msg = msg.substring( 0, blockSize - count);
            }
        }
        return msg;
    }

    public String zeroPadding(String msg, int blockSize){
        blockSize--;
        while (msg.length() < blockSize) {
            msg = "0"+msg;
        }
        return msg;
    }

    public String isPad(){
        System.out.println("sign bit : "+paddingsStatus);
        return paddingsStatus;
    }

    public void resetStatus(){
        paddingsStatus = "0";
    }

    private void padCheck(){
        paddingsStatus = "1";
    }
}
