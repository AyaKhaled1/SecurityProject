import java.util.ArrayList;

public class Network {

	private ArrayList<Node> networkNodes;
	private Block startBlock;
	public static int idCount = 1;
	public static Network network;
	
	public Network() throws Exception{
		
		networkNodes = new ArrayList<Node>();
		startBlock = new Block("2349237082");
		generateNodes();
	}
	
	public static Network getInstance() throws Exception{
		
		if(Network.network == null){
			Network.network = new Network();
			return Network.network;
		}
		return Network.network;
	}
	
	public void generateNodes() throws Exception{
		
		for(int i = 0; i < 20; i++){
			
			ArrayList<Integer> peers = new ArrayList<Integer>();
			for(int j = 1; j < 6; j++){
				peers.add((i+j)%20);
			}
			Node n = new Node(i, peers, startBlock);
			networkNodes.add(n);
		}
	}
	
	public void sendTransactions() throws Exception{
		
		int randomNumberOfNodes = (int)(Math.random()*(networkNodes.size()))+1;
		ArrayList<Integer> randomNodes = new ArrayList<Integer>();
		
		for(int i = 0; i < randomNumberOfNodes; i++){
			
			int randomNode = (int)(Math.random()*networkNodes.size());
			while(randomNodes.contains(randomNode))
				randomNode = (int)(Math.random()*networkNodes.size());
			
			Node n = networkNodes.get(randomNode);
			System.out.println("From Node: " + n.getId());
			Transaction message = n.generateTransaction();
			message.setSrcID(randomNode);
			sendTransaction(n, message);
		}
	}
	
	public void sendTransaction(Node n, Transaction message) throws Exception{
		
		int randomNpeers= (int)(Math.random()*n.getPeers().size())+1;
		ArrayList<Integer> randomIndices = new ArrayList<Integer>();
		
	    for(int i = 0; i < randomNpeers; i++){
	    	
	    	int peerNumber = n.getPeers().get((int)(Math.random()*n.getPeers().size()));
	    	while(randomIndices.contains(peerNumber)){
	    		peerNumber = n.getPeers().get((int)(Math.random()*n.getPeers().size()));
	    	}
	    	
	    	randomIndices.add(peerNumber);
	    	
	    	if(!message.getReceiversIDs().contains(peerNumber) && message.getSrcID() != peerNumber){
	    		message.getReceiversIDs().add(peerNumber);
		    	networkNodes.get(peerNumber).receiveTransaction(message);
		    	sendTransaction(networkNodes.get(peerNumber), message);
	    	}	
	    }   	    
	}
	
	public void announceBlock(Node n, Block block){
		
		int randomNpeers= (int)(Math.random()*n.getPeers().size())+1;
		ArrayList<Integer> randomIndices = new ArrayList<Integer>();
		
		for(int i = 0; i < randomNpeers; i++){
			
			int peerNumber = n.getPeers().get((int)(Math.random()*n.getPeers().size()));
	    	while(randomIndices.contains(peerNumber)){
	    		peerNumber = n.getPeers().get((int)(Math.random()*n.getPeers().size()));
	    	}
	    	
	    	randomIndices.add(peerNumber);
	    	
	    	if(!block.getReceiversIDs().contains(peerNumber) && block.getSrcID() != peerNumber){
	    		block.getReceiversIDs().add(peerNumber);
	    		Boolean verified = networkNodes.get(peerNumber).receiveBlock(block);
	    		if(verified)
	    			announceBlock(networkNodes.get(peerNumber), block);
	    	}
		}
	}
	
	public static void main(String []args) throws Exception{
		
		Network n = Network.getInstance();
		n.sendTransactions();
		
		for(int i = 0; i < n.networkNodes.size();i++)
			System.out.println("Node " + i + " :" + n.networkNodes.get(i).getTransactions());
	}
}
