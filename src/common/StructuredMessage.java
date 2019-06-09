package common;

public class StructuredMessage {
  
  private static final String STRING_SEPARATOR = ":";
  
  private String data;
  
  private int sequenceNumber;
  
  private int numberOfPackets;
  
  public StructuredMessage() {
    
  }
  
  public StructuredMessage(int sequenceNumber, String data, int numberOfPackets) {
    this.sequenceNumber = sequenceNumber;
    this.data = data;
    this.numberOfPackets = numberOfPackets;
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
    return String.format("%d" + STRING_SEPARATOR + "%s" + STRING_SEPARATOR + "%d" + STRING_SEPARATOR, //
        getSequenceNumber(), getData(), getNumberOfPackets());
  }
  
  public byte[] getBytes() {
    return toString().getBytes();
  }
  
  public static StructuredMessage getStructuredMessage(byte[] byteArray) {
    String messageString = new String(byteArray);
//    System.out.println(messageString);
    
    String[] stringArray = messageString.split(STRING_SEPARATOR);
    
//    System.out.println("sequenceNumber: " + stringArray[0]);
//    System.out.println("data: " + stringArray[1]);
//    System.out.println("numberOfPackets: " + stringArray[2]);
    
    return new StructuredMessage(Integer.parseInt(stringArray[0]), stringArray[1], Integer.parseInt(stringArray[2]));
  }
  
  public int getNumberOfPackets() {
    return numberOfPackets;
  }
  
  public void setNumberOfPackets(int numberOfPackets) {
    this.numberOfPackets = numberOfPackets;
  }
}
