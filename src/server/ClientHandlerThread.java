package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import common.EnumReturn;
import common.LogUtils;
import common.StructuredMessage;

public class ClientHandlerThread extends Thread {
  
  private static final int DATA_SIZE = 1024;
  
  private int clientPort;
  
  private InetAddress clientIP;
  
  private ArrayList<StructuredMessage> messages = new ArrayList<StructuredMessage>();
  
  private DatagramSocket serverSocket;
  
  private boolean shouldRun;
  
  public ClientHandlerThread(DatagramSocket serverSocket, int port, InetAddress address) {
    super();
    this.serverSocket = serverSocket;
    this.clientPort = port;
    this.clientIP = address;
  }
  
  public void run() {
    
    while (getShouldRun()) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
      }
    }
    
  }
  
  private boolean getShouldRun() {
    return shouldRun;
  }
  
  public int getClientPort() {
    return clientPort;
  }
  
  public void setClientPort(int clientPort) {
    this.clientPort = clientPort;
  }
  
  public InetAddress getClientIP() {
    return clientIP;
  }
  
  public void setClientIP(InetAddress clientIP) {
    this.clientIP = clientIP;
  }
  
  public ArrayList<StructuredMessage> getMessages() {
    return messages;
  }
  
  public EnumReturn addMessage(StructuredMessage message) {
    if (!getMessages().contains(message)) {
      getMessages().add(message);
      return EnumReturn.OK;
    }
    return EnumReturn.REPEATED_PACKET;
  }
  
  public void processMessagePacket(DatagramPacket receivePacket) {
    System.out.println(getClientHandlerStringIdentification() + "/message: " + (new String(receivePacket.getData())));
    
    byte[] sendData = new byte[DATA_SIZE];
    
    // Pega byte array da mensagem que deverá ser enviada
    sendData = new StructuredMessage(0, EnumReturn.OK.toString(), 1).getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getClientIP(), getClientPort());
    LogUtils.logSentDatagramPacketInfo(sendPacket);
    try {
      getServerSocket().send(sendPacket);
    } catch (IOException e) {
      System.out.println("Erro ao enviar reposta para cliente, " + e);
    }
    
  }
  
  private String getClientHandlerStringIdentification() {
    return "Handler of " + getClientIP() + ":" + getClientPort();
  }
  
  public DatagramSocket getServerSocket() {
    return serverSocket;
  }
  
  public void setServerSocket(DatagramSocket serverSocket) {
    this.serverSocket = serverSocket;
  }
}
