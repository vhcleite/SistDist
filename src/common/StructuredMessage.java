package common;

public class StructuredMessage {
  
  private static final String STRING_SEPARATOR = ":";
  
  private String data;
  
  private int sequenceNumber;
  
  public StructuredMessage() {
    
  }
  
  public StructuredMessage(int sequenceNumber, String data) {
    this.sequenceNumber = sequenceNumber;
    this.data = data;
  }
  
  public int getSequenceNumber() {
    return sequenceNumber;
  }
  
  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
  
  public String getData() {
    return data;
  }
  
  public void setData(String data) {
    this.data = data;
  }
  
  public String toString() {
    return String.format("%d" + STRING_SEPARATOR + "%s", getSequenceNumber(), getData());
  }
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public static StructuredMessage getStructuredMessage(byte[] byteArray) {
    String messageString = new String(byteArray);
    
    // Mensagem está no formato sequenceNumber:data
    String[] stringArray = messageString.split(STRING_SEPARATOR);
    return new StructuredMessage(Integer.parseInt(stringArray[0]), stringArray[1]);
  }
  
}
