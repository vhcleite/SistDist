package common;

import java.net.DatagramPacket;

public class LogUtils {
  
  public static void logSentDatagramPacketInfo(String id, DatagramPacket datagramPackage) {
    logDatagramPacketInfo(id + "enviada ", datagramPackage);
  }
  
  public static void logReceivedDatagramPacketInfo(String id, DatagramPacket datagramPackage) {
    logDatagramPacketInfo(id + "recebida ", datagramPackage);
  }
  
  private static void logDatagramPacketInfo(String origin, DatagramPacket datagramPackage) {
    String message = new String(datagramPackage.getData());
    
    System.out.println(String.format("%s [IP: %s] [PORTA: %d], mensagem: %s", //
        origin, datagramPackage.getAddress(), datagramPackage.getPort(), message));
  }
}
