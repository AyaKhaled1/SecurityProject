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
	private ArrayList<Block> blockChain;
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Block BC_lastBlock;
    private HashMap<String, Block> all_blocks;
	
	public Node(int id, ArrayList<Integer> peers, Block startBlock) throws NoSuchAlgorithmException {
	
		this.id = id;
		this.peers = peers;
		transactions = new ArrayList<Transaction>();
		keyGen = KeyPairGenerator.getInstance("DSA");
		keyPair = keyGen.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		BC_lastBlock = startBlock;
		all_blocks = new HashMap<String, Block>();
		all_blocks.put(startBlock.getId(), startBlock);
		blockChain = new ArrayList<Block>();
		blockChain.add(startBlock);
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
        signature.update(Block.toString(b.getTransactions()).getBytes());
        
        byte [] digitalSignature = signature.sign();
        String res = Base64.getEncoder().encodeToString(digitalSignature);
        b.setSignature(res);
	}
	
	public void receiveTransaction(Transaction message) throws Exception{
		
		transactions.add(message);
		Network.writer.println("Transaction Received By Node: " + id);
		
		if(transactions.size() == 5){
			
			Block b = new Block(this.BC_lastBlock.getId(), this.publicKey);
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
	
	public String printBlockChain(Block b){

		String startHash = "2349237082";
		String previousHash = b.getPrevHash();
		
		if(previousHash.equals(startHash) || !all_blocks.containsKey(previousHash)){
			return b.toString();
		}
		else{
			return printBlockChain(all_blocks.get(previousHash)) + "\n" + b.toString();
		}
	}
	
	public void updateBlockChain(Block b){

		String startHash = "2349237082";
		String previousHash = b.getPrevHash();
		
		if(previousHash.equals(startHash) || !all_blocks.containsKey(previousHash)){
			blockChain.add(b);
		}
		else{
			updateBlockChain(all_blocks.get(previousHash));
			blockChain.add(b);
		}
	}

	public boolean verifySignature(Block block) throws Exception{
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
		signature.initVerify(block.getSrcPK());
		signature.update(Block.toString(block.getTransactions()).getBytes());
		byte [] sig =  Base64.getDecoder().decode(block.getSignature());
		
		boolean verified = signature.verify(sig);
		return verified;
	}

	public void receiveBlock(Block block) throws Exception{
		
		all_blocks.put(block.getId(), block);

		if(block.validate() && verifySignature(block)){
			
			Network.writer.println("Block Received and Verified by Node: " + id);
			
			if(findLengthOfChain(BC_lastBlock) < findLengthOfChain(block)){
				BC_lastBlock = block;
				Network.writer.println("Block Chain Updated");
				blockChain = new ArrayList<Block>();
				updateBlockChain(BC_lastBlock);
			}
		}
	}
	
	public String getOrphanedBlocks(){
		
		ArrayList<Block> blocks = new ArrayList<Block>(all_blocks.values());
		String s = "";
		
		for(int i = 0; i < blocks.size(); i++){
			if(!blockChain.contains(blocks.get(i))){
				s+= blocks.get(i).toString() + "\n";
			}
		}
		return s;
	}

	public String toString() {
		return  "Node [id=" + id + ", peers=" + peers 
					+ "\ntransactions not in blocks: \n" + transactions 
					+ "\nBlock Chain\n" + printBlockChain(BC_lastBlock)
					+ "\nOrphaned Blocks: \n" + getOrphanedBlocks() + "]";
	}

}
