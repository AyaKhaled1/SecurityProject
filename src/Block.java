import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Block implements Serializable{
	
	private String id;
	private String prevHash;
	private ArrayList<Transaction> transactions;
	private String signature;
	private ArrayList<Integer> receiversIDs = new ArrayList<Integer>();
	private int srcID;
	
	public Block(String prevHash) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		transactions = new ArrayList<Transaction>();
		this.prevHash = prevHash;
		encode();
	}
	
	public String getPrevHash() {
		return prevHash;
	}

	public void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getSrcID() {
		return srcID;
	}

	public void setSrcID(int srcID) {
		this.srcID = srcID;
	}

	public String getId() {
		return id;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public ArrayList<Integer> getReceiversIDs() {
		return receiversIDs;
	}

	public String encodeTransactions(ArrayList<Transaction> transactions) {
		
        try {
            return toString(transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hash(String values) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	
    	byte[] bytesOfMessage = values.getBytes("UTF-8");
    	MessageDigest md = MessageDigest.getInstance("MD5");
    	byte [] thedigest = md.digest(bytesOfMessage);
    	String res = Arrays.toString(thedigest).replace(", ", "").replace("[", "").replace("]", "");
    	
		return res;
    }

    public void encode() throws UnsupportedEncodingException, NoSuchAlgorithmException{
    	
    	String currentHash = "";
        String encodedTransactions = encodeTransactions(this.transactions);
        String result = "";
        result += prevHash;
        result += encodedTransactions;
        int nonce = 0;
        result += nonce;
        currentHash = hash(result);
        
        while(!currentHash.startsWith("00")){
        	nonce++;
        	result = "";
            result += prevHash;
            result += encodedTransactions;
            result += nonce;
            currentHash = hash(result);
        }
        
        this.id = currentHash;
    }
	
	/** Read the object from Base64 string. */
    public static Object fromString(String s) throws IOException, ClassNotFoundException {
    	
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString(Serializable o) throws IOException {
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

	public String toString() {
		return "Block [id=" + id + ", prevHash=" + prevHash + ", transactions="
				+ transactions + ", signature=" + signature + ", receiversIDs="
				+ receiversIDs + ", srcID=" + srcID + "]";
	}

}
