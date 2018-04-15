import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;

public class Node {
	
	private int id;
	private ArrayList<Integer> peers;
	private ArrayList<Transaction> transactions;
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private ArrayList<Block> blockChain;
	
	public Node(int id, ArrayList<Integer> peers, Block startBlock) throws NoSuchAlgorithmException {
	
		this.id = id;
		this.peers = peers;
		transactions = new ArrayList<Transaction>();
		keyGen = KeyPairGenerator.getInstance("DSA");
		keyPair = keyGen.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
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
       
        String concatenatedSignature="";
        for(int i=0;i<digitalSignature.length;i++){
        	concatenatedSignature+=digitalSignature[i];
        }

        transaction.setData(concatenatedSignature+transaction.getData());
        
        return transaction;
	}
	
	public void signBlock(Block b) throws Exception{
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initSign(privateKey);
        signature.update(Block.toString(b).getBytes());
        byte [] digitalSignature = signature.sign();
        String res = Arrays.toString(digitalSignature).replace(", ", "").replace("[", "").replace("]", "");
        b.setSignature(res);
        
	}
	
	public void receiveTransaction(Transaction message) throws Exception{
		
		transactions.add(message);
		if(transactions.size() == 5){
			
			Block b = new Block(blockChain.get(blockChain.size()-1).getId());
			b.getTransactions().addAll(transactions);
			transactions.clear();
			Network.getInstance().announceBlock(this, b);
		}
	}
	
	public Boolean receiveBlock(Block block){
		
		Boolean verified = verifyBlock(block);
		if(verified){
			blockChain.add(block);
			System.out.println(block);
		}
		return verified;
	}
	
	public Boolean verifyBlock(Block block) {
		// TODO Auto-generated method stub
		return true;
	}

	public String toString() {
		return "Node [id=" + id + ", peers=" + peers + ", transactions="
				+ transactions + ", publicKey=" + publicKey + "]";
	}
	
}
