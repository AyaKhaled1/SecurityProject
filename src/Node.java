import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Node {
	
	private int id;
	private ArrayList<Integer> peers;
	private ArrayList<Transaction> transactions;
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Block blockChain;
    private HashMap<String, Block> all_blocks;
	
	public Node(int id, ArrayList<Integer> peers, Block startBlock) throws NoSuchAlgorithmException {
	
		this.id = id;
		this.peers = peers;
		transactions = new ArrayList<Transaction>();
		keyGen = KeyPairGenerator.getInstance("DSA");
		keyPair = keyGen.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		blockChain = startBlock;
		all_blocks = new HashMap<String, Block>();
		all_blocks.put(startBlock.getId(), startBlock);
	}
   
	public PublicKey getPublicKey() {
		return publicKey;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<Integer> getPeers() {
		return peers;
	}
	
	public void setPeers(ArrayList<Integer> peers) {
		this.peers = peers;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
    
	public Transaction generateTransaction() throws Exception{
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initSign(privateKey);
        Transaction transaction = new Transaction(Network.idCount);
        transaction.setData(Network.idCount + "");
        Network.idCount++;
        signature.update(transaction.getData().getBytes());
        byte [] digitalSignature = signature.sign();
       
        String encodedSignature= Base64.getEncoder().encodeToString(digitalSignature);
        transaction.setData(encodedSignature+transaction.getData());
        
        return transaction;
	}
	
	public void signBlock(Block b) throws Exception{
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initSign(privateKey);
        signature.update(Block.toString(b).getBytes());
        byte [] digitalSignature = signature.sign();
        String res = Base64.getEncoder().encodeToString(digitalSignature);
        b.setSignature(res);
        
	}
	
	public void receiveTransaction(Transaction message) throws Exception{
		
		transactions.add(message);
		if(transactions.size() == 5){
			
			Block b = new Block(this.blockChain.getId(), this.publicKey);
			b.getTransactions().addAll(transactions);
			b.encode();
			signBlock(b);
			receiveBlock(b);
			transactions.clear();
			Network.getInstance().announceBlock(this, b);
		}
	}

	
	public int findLengthOfChain(Block b){

		String startHash = "2349237082";
		String previousHash = b.getPrevHash();
		
		if(previousHash.equals(startHash) || !all_blocks.containsKey(previousHash)){
			return 0;
		}
		
		return 1 + findLengthOfChain(all_blocks.get(previousHash));
	}
	
	public void printBlockChain(Block b){

		String startHash = "2349237082";
		String previousHash = b.getPrevHash();
		
		if(previousHash.equals(startHash) || !all_blocks.containsKey(previousHash)){
			System.out.println(b);
		}
		else{
			printBlockChain(all_blocks.get(previousHash));
			System.out.println(b.toString());
		}
	}
	
	public void receiveBlock(Block block) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		
		all_blocks.put(block.getId(), block);

		if(block.validate()){
			if(findLengthOfChain(blockChain) < findLengthOfChain(block)){
				blockChain = block;
			}

		}
	}

	public String toString() {
		return "Node [id=" + id + ", peers=" + peers + ", transactions="
				+ transactions + ", blockChain=" + blockChain + ", all_blocks="
				+ all_blocks + "]";
	}

}
