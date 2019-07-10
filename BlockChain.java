import java.util.ArrayList;
//an alternative way to print the json
//import com.google.gson.GsonBuilder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 *
 * @author rainy Last Modified: 03/15/2019
 *
 * This is the BlockChain class that has the main functions to simulate a block
 * chain.
 *
 */
public class BlockChain extends java.lang.Object {

    static ArrayList<Block> blockchain = new ArrayList<Block>();//ArrayList to store the block
    static String chainHash; //hash of the most recent added block
    static int index = 0;//increments everytime a block is added

    /**
     * This BlockChain has exactly two instance members - an ArrayList to hold
     * Blocks and a chain hash to hold a SHA256 hash of the most recently added
     * Block. This constructor creates an empty ArrayList for Block storage.
     * This constructor sets the chain hash to the empty string.
     */
    public BlockChain() {
        BlockChain.blockchain = new ArrayList<>();
        BlockChain.chainHash = "";
    }

    /**
     * @return the current system time
     */
    public Timestamp getTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp;
    }

    /**
     * a reference to the most recently added Block.
     *
     * @return
     */
    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    /**
     * the size of the chain in blocks.
     *
     * @return
     */
    public int getChainSize() {
        return blockchain.size();

    }

    /**
     * hashes per second of the computer holding this chain. It uses a simple
     * string - "00000000" to hash.
     *
     * @return
     */
    public long hashesPerSecond() {
        StringBuilder sb = new StringBuilder();
        long start = System.currentTimeMillis();
        long end;
        String hex;
        int seconds;
        do {
            sb.append(0);
            hex = Block.applySha256(sb.toString());

            end = System.currentTimeMillis();
            //convert Mills to seconds
            seconds = (int) (((end - start) / 1000) % 60);
        } while (seconds < 2); //measures the performance in one second

        long hashesPerSecond = hex.length();
        return hashesPerSecond;

    }

    /**
     * A new Block is being added to the BlockChain. This new block's previous
     * hash must hold the hash of the most recently added block. After this call
     * on addBlock, the new block becomes the most recently added block on the
     * BlockChain. The SHA256 hash of every block must exhibit proof of work,
     * block is hashed in the Block class with previous hash
     *
     * @param newBlock
     */
    public void addBlock(Block newBlock) {

        blockchain.add(newBlock);
    }

    /**
     * a String representation of the entire chain is returned.
     *
     * @return
     */
    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        //append title
        sb.append("{ds_chain\" : [");
        for (Block b : blockchain) {
            //append each block
            sb.append(b.toString());
        }
        //append chainhash
        sb.append(String.format("],%n\"chainHash\":\"%s\"}", blockchain.get(blockchain.size() - 1).hash));
        return sb.toString();

    }

    //helper class to apply Sha256 to a string and returns the result. 
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to the input
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            // This will contain hash as hexidecimal
            StringBuilder hexString = new StringBuilder();
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
     * If the chain only contains one block, the genesis block at position 0,
     * this routine checks that the hash has the requisite number of leftmost
     * 0's (proof of work) as specified in the difficulty field. It also checks
     * that the chain hash is equal to this computed hash. If either check
     * fails, return false. Otherwise, return true. If they match and if the
     * proof of work is correct, go and visit the next block in the chain. At
     * the end, check that the chain hash is also correct.
     *
     * @return true if and only if the chain is valid
     */
    public boolean isChainValid() {
        Block currentBlock;
        //checks that the hash has the requisite number of leftmost 0's (proof of work) as specified in the difficulty field.
        //also checks the chain hash
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            char[] charArrHash = currentBlock.hash.toCharArray();
            StringBuilder sb = new StringBuilder();
            //get the leftmost zeros
            for (int j = 0; j < charArrHash.length; j++) {
                if (charArrHash[j] == 48) {//ASCII zero is 48
                    sb.append(0);
                } else if (charArrHash[j] != 48) {
                    break;
                }
            }
            //compare registered hash and calculated hash:
            //currentBlock.hash comes from Block's proof of work
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {

                System.out.println("...Improper hash on node " + currentBlock.index + " Does not begin with " + sb.toString());

                //checks the chainhash equals to the computed hash
                //chainhash = most recent block's hash
                if (!chainHash.equals(blockchain.get(blockchain.size() - 1).calculateHash())) {
                    System.out.println("...Improper chain hash");
                }
                return false;
            }//end if

        }

        return true;
    }

    /**
     * This routine repairs the chain. It checks the hashes of each block and
     * ensures that any illegal hashes are recomputed. After this routine is
     * run, the chain will be valid. The routine does not modify any difficulty
     * values. It computes new proof of work based on the difficulty specified
     * in the Block.
     */
    public void repairChain() {
        Block currentBlock;

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            //compare registered hash and calculated hash
            //if they don't match, repair the chain to hide corruption of the chain
            //also check the chainhash, if it does not match the most recent block's hash, recompute new proof of work
            if (!currentBlock.hash.equals(currentBlock.calculateHash()) || !chainHash.equals(blockchain.get(blockchain.size() - 1).calculateHash())) {
                currentBlock.hash = currentBlock.calculateHash();
            }
        }
    }

    //blockchain menu
    public static void printMenu() {
        System.out.println("Block Chain Menu\n"
                + "0. View basic blockchain status.\n"
                + "1. Add a transaction to the blockchain.\n"
                + "2. Verify the blockchain.\n"
                + "3. View the blockchain.\n"
                + "4. Corrupt the chain.\n"
                + "5. Hide the corruption by repairing the chain.\n"
                + "6. Exit.");
    }

    //for menu input out of range exception
    static class InvalidDataRangeException extends Exception {

        public InvalidDataRangeException() {
            System.out.println("**Error: entry discarded. Invalid data range! Please enter an option from 0 to 6.");
        }
    }

    public static void main(java.lang.String[] args) {
        BlockChain bc = new BlockChain();
        //take menu input
        Scanner input = new Scanner(System.in);
        //flag that is used in the do while loop to keep prompting main menu until the user stops the loop by entering "6", the exit option
        boolean stop = false;
        //initiate the first block - Genesis: index 0, diff = 2
        blockchain.add(new Block(0, bc.getTime(), "Genesis", 2));
        //mine the default block
        blockchain.get(0).proofOfWork(2);
        blockchain.get(0).getNonce();

        //menu
        do {
            //set the chainhash
            chainHash = bc.getLatestBlock().hash;
            printMenu();//keep prompting main menu
            String nextLine = input.next();

            try {
                int option = Integer.valueOf(nextLine);//if option is not an integer, will be catched as a numberformat exception
                if (option < 0 || option > 6) {//will be catched by the exception that handles the menu input other than 0-6
                    throw new InvalidDataRangeException();
                }

                //menu interaction
                switch (option) {
                    //view basic status
                    case 0:
                        System.out.println("Current size of the chain: " + blockchain.size());
                        System.out.println("Current hashes per second by this machine: " + bc.hashesPerSecond());
                        System.out.format("Difficulty of most recent block: %d%n", bc.getLatestBlock().difficulty);
                        System.out.println("Nonce for most recent block: " + bc.getLatestBlock().nonce);
                        System.out.println("Chain hash: " + chainHash);
                        break;
                    //add transaction to block chain
                    case 1:
                        System.out.println("Enter difficulty > 0");
                        //check if the input is null
                        if (input.nextLine() != null || !input.nextLine().equals("")) {
                            //check if difficulty input is an integer
                            String diffStr = input.nextLine();
                            int difficulty;
                            try {
                                difficulty = Integer.valueOf(diffStr);
                                //everytime a block is added, index increments by one
                                index++;
                                System.out.println("Enter transaction");
                                String data = input.nextLine();
                                //a new block added with the increased index, current timestamp, data entered, and difficulty entered
                                bc.addBlock(new Block(index, bc.getTime(), data, difficulty));
                                //set the previous hash
                                bc.getLatestBlock().setPreviousHash(blockchain.get(blockchain.size() - 2).hash);
                                //compute time consumed to add the block (total time to mine the block)
                                long start = System.currentTimeMillis();
                                bc.getLatestBlock().proofOfWork(difficulty);
                                long end = System.currentTimeMillis();
                                System.out.format("Total execution time to add this block was %d milliseconds %n", (end - start));

                                //difficulty input exception
                            } catch (NumberFormatException e) {
                                System.out.println("**Error: entry discarded. Invalid input. Please enter an integer difficulty > 0!**");
                            }

                        }
                        break;
                    //verify the blockchain
                    case 2:

                        System.out.println("Verifying entire chain");
                        //calculate the time consumed to verify the blockchain
                        long start = System.currentTimeMillis();
                        System.out.println("Chain verification: " + bc.isChainValid());
                        long end = System.currentTimeMillis();
                        System.out.format("Total execution time required to verify the chain was %d milliseconds %n", (end - start));
                        break;
                    //view the blockchain (in json format)
                    case 3:
                        //another way to print json formatted string
//                        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//                        System.out.println(blockchainJson);
                        System.out.println("Viewing the blockchain");
                        //return the json format of the blockchain
                        System.out.println(bc.toString());
                        break;
                    //corrupt the blockchain by modifying a block's transaction data
                    case 4:
                        int blockID;
                        System.out.println("Corrupt the Blockchain");
                        System.out.println("Enter block ID of block to Corrupt");
                        //check if the input is null
                        if (input.nextLine() != null || !input.nextLine().equals("")) {
                            //catch the exception if the block id input is not an integer
                            String idStr = input.nextLine();
                            try {
                                blockID = Integer.valueOf(idStr);
                                System.out.println("Enter new data for block " + blockID);
                                String data = input.nextLine();
                                //modify the selected block's transaction data
                                blockchain.get(blockID).setData(data);
                                System.out.println("Block " + blockID + " now holds " + data);
                                //block id input exception
                            } catch (NumberFormatException e) {
                                System.out.println("**Error: entry discarded. Invalid input. Please enter an integer Block id!**");
                            }

                        }

                        break;
                    //repair the transaction
                    case 5:
                        //calculate time consumed to repair the chain
                        long starttime = System.currentTimeMillis();
                        bc.repairChain();
                        long endtime = System.currentTimeMillis();
                        System.out.format("Repairing the entire chain %n "
                                + "Total execution time required to repair the chain was %d milliseconds %n", (endtime - starttime));

                        break;
                    //exits the program
                    case 6:
                        //exit message when user chooses "6" on the main menu and exits the program
                        System.out.println("***Execution halts. Exits the program***");
                        //set stop flag to true so that the program stops and no longer prompts menu
                        stop = true;
                        break;
                }// end menu function switch
                //exception handling for menu option - string input
            } catch (NumberFormatException e) {
                System.out.println("**Error: entry discarded. Invalid input. Please enter an integer to select menu options!**");
                //exception handling for menu option - correct integer format but out of range. 
            } catch (InvalidDataRangeException e) {

            }

        } while (stop == false);//end blockchain menu interaction, end the program

    }//end main funtion

}//end class
