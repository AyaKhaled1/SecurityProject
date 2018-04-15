import java.util.ArrayList;


public class Transaction {

	private int id;
	private String data;
	private int srcID;
	private ArrayList<Integer> receiversIDs = new ArrayList<Integer>();
	
	public ArrayList<Integer> getReceiversIDs() {
		return receiversIDs;
	}

	public int getSrcID() {
		return srcID;
	}

	public void setSrcID(int srcID) {
		this.srcID = srcID;
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
