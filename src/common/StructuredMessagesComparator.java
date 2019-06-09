package common;

import java.util.Comparator;

public class StructuredMessagesComparator implements Comparator<StructuredMessage> {
  
  @Override
  public int compare(StructuredMessage arg0, StructuredMessage arg1) {
    if (arg0.getSequenceNumber() > arg1.getSequenceNumber()) {
      return 1;
    } else if (arg0.getSequenceNumber() == arg1.getSequenceNumber()) {
      return 0;
    } else {
      return -1;
    }
  }
  
}
