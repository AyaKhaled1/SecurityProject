import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction implements Serializable{

	private int id;
	private String data;
	private PublicKey srcPK;
	private ArrayList<Integer> receiversIDs = new ArrayList<Integer>();
	
	public ArrayList<Integer> getReceiversIDs() {
		return receiversIDs;
	}

	public PublicKey getSrcPK() {
		return srcPK;
	}

	public void setSrcPK(PublicKey srcPK) {
		this.srcPK = srcPK;
	}

	public Transaction(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	public String toString() {
		return "Transaction [id=" + id + ", data=" + data + "]";
	}

}
