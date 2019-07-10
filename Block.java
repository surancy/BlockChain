import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;

/**
 *
 * @author rainy Last Modified: 03/15/2019 This is the Block class that will be
 * used by the BlockChain class to build a block.
 */
final class Block {

    int index; //position within the chain
    Timestamp timeStamp;
    long time; //as number of milliseconds since 1/1/1970.
    String data; //transaction
    String previousHash = ""; //the SHA256 hash of a block's parent. This is also called a hash pointer. default is an empty string
    BigInteger nonce;  // a BigInteger value determined by a proof of work routine
    int difficulty; //the number of leftmost nibbles that need to be 0
    String hash; //in SHA256 format

    /**
     * Constructor index - This is the position within the chain. Genesis is at
     * 0. timestamp - This is the time this block was added. data - This is the
     * transaction to be included on the blockchain. difficulty - This is the
     * number of leftmost nibbles that need to be 0.
     *
     * @param index
     * @param timestamp
     * @param data
     * @param difficulty
     */
    Block(int index, Timestamp timestamp, String Tx, int difficulty) {
        setIndex(index);
        setTimestamp(timestamp);
        setData(Tx);
        setDifficulty(difficulty);
        nonce = getNonce();
        previousHash = getPreviousHash();
        //calculate hash after all variables are set
        this.hash = calculateHash();

    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp,
     * data, previousHash, nonce, and difficulty. Returns: a String holding
     * Hexadecimal characters
     *
     * @return
     */
    public String calculateHash() {
        //data conversion
        String ind = String.valueOf(index);
        //hash
        String calculatedhash = applySha256(ind + timeStamp + data + previousHash + nonce + difficulty);
        return calculatedhash;
    }

    //helper class to apply Sha256 to a string and returns the result. 
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to input
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            // This will contain hash as hexidecimal
            StringBuilder hexString = new StringBuilder();
            //hashing
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            //handle exceptions
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method returns the nonce for this block. The nonce is a number that
     * has been found to cause the hash of this block to have the correct number
     * of leading hexadecimal zeroes. Returns: a BigInteger representing the
     * nonce for this block.
     *
     * @return
     */
    public BigInteger getNonce() {
        return this.nonce;
    }

    //setter
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    /**
     * The proof of work methods finds a good hash. It increments the nonce
     * until it produces a good hash.
     *
     * This method calls calculateHash() to compute a hash of the concatenation
     * of the index, timestamp, data, previousHash, nonce, and difficulty. If
     * the hash has the appropriate number of leading hex zeroes, it is done and
     * returns that proper hash. If the hash does not have the appropriate
     * number of leading hex zeroes, it increments the nonce by 1 and tries
     * again. It continues this process, burning electricity and CPU cycles,
     * until it gets lucky and finds a good hash.
     *
     * Returns: a String with a hash that has the appropriate number of leading
     * hex zeroes. The difficulty value is already in the block. This is the
     * number of hex 0's a proper hash must have.
     *
     * @return
     */
    public String proofOfWork(int difficulty) {
        //initiate the nonce
        nonce = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        //Create a string with difficulty * "0" 
        String target = new String(new char[difficulty]).replace('\0', '0');
        //increments the nonce until we have a good hash
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce = getNonce().add(one);
            //compute hash
            hash = calculateHash();
        }

        return hash;
    }

    /**
     * return difficulty
     *
     * @return
     */
    public int getDifficulty() {
        return this.difficulty;

    }

    /**
     * difficulty - determines how much work is required to produce a proper
     * hash
     *
     * @param difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Override Java's toString method
     *
     * Overrides: toString in class java.lang.Object Returns: A JSON
     * representation of all of this block's data is returned.
     *
     * @return
     */
    @Override
    public java.lang.String toString() {
        String jsonFormat = String.format("{\"index\" : %d,\"time stamp \" : \"%s\",\"Tx \":\"%s\",\"PrevHash\" : \"%s\",\"nonce\" : %d,\"difficulty\": %d}%n",
                index, timeStamp, data, previousHash, nonce, difficulty);
        return jsonFormat;

    }

    /**
     * previousHash - a hashpointer to this block's parent
     *
     * @param previousHash
     */
    public void setPreviousHash(java.lang.String previousHash) {
        this.previousHash = previousHash;
    }

    //getter
    public String getPreviousHash() {
        return this.previousHash;
    }

    /**
     * index of block
     *
     * @return
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * index - the index of this block in the chain
     *
     * @param index
     */
    public final void setIndex(int index) {
        this.index = index;
    }

    /**
     * timestamp - of when this block was created
     *
     * @param timestamp
     */
    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timeStamp = timestamp;
    }

    /**
     * timestamp of this block
     *
     * @return
     */
    public Timestamp getTimestamp() {
        return this.timeStamp;
    }

    /**
     * this block's transaction
     *
     * @return
     */
    public String getData() {
        return this.data;

    }

    /**
     * data - represents the transaction held by this block
     *
     * @param data
     */
    public void setData(java.lang.String data) {
        this.data = data;
    }

}//end class
