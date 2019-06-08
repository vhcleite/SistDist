package common;

import java.net.DatagramPacket;

public class LogUtils {
  
  public static void logSentDatagramPacketInfo(DatagramPacket datagramPackage) {
    logDatagramPacketInfo("Enviada para", datagramPackage);
  }
  
  public static void logReceivedDatagramPacketInfo(DatagramPacket datagramPackage) {
    logDatagramPacketInfo("Recebida de ", datagramPackage);
  }
  
  private static void logDatagramPacketInfo(String origin, DatagramPacket datagramPackage) {
    String message = new String(datagramPackage.getData());
    
    System.out.println(String.format("%s [IP: %s] [PORTA: %d], mensagem: %s", //
        origin, datagramPackage.getAddress(), datagramPackage.getPort(), message));
  }
}
