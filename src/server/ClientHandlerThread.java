package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import common.EnumReturn;
import common.LogUtils;
import common.StructuredMessage;
import common.StructuredMessagesComparator;

public class ClientHandlerThread extends Thread {
  
  private static final int DATA_SIZE = 1024;
  
  private int clientPort;
  private InetAddress clientIP;
  private DatagramSocket serverSocket;
  private boolean shouldRun;
  private ArrayList<StructuredMessage> messages = new ArrayList<StructuredMessage>();
  
  public ClientHandlerThread(DatagramSocket serverSocket, int port, InetAddress address) {
    super();
    this.serverSocket = serverSocket;
    this.clientPort = port;
    this.clientIP = address;
  }
  
  public void run() {
    System.out.println("Cliente " + getClientHandlerStringIdentification() + " inicializado");
    setShouldRun(true);
    while (getShouldRun()) {
      try {
        Thread.sleep(1000);
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
  
  public void addMessageInBuffer(StructuredMessage message) {
    getMessages().add(message);
    Collections.sort(getMessages(), new StructuredMessagesComparator());
    System.out.printf(getClientHandlerStringIdentification() + "menssagem [%s] adicionada no buffer\r\n", message);
    System.out.println(getMessages());
  }
  
  public void processMessagePacket(DatagramPacket receivePacket) {
    
    EnumReturn ret = EnumReturn.OK;
    
    System.out.println("---" + getClientHandlerStringIdentification());
    LogUtils.logReceivedDatagramPacketInfo(receivePacket);
    
    StructuredMessage message = StructuredMessage.getStructuredMessage(receivePacket.getData());
    
    // verifico se lista de mensagens já não contém a mensagem
    if (!bufferContainsMessage(message)) {
      // adiciono na lista caso não contenha
      addMessageInBuffer(message);
      System.out
          .println(getClientHandlerStringIdentification() + " Numero de intens no buffer: " + getMessages().size());
    } else {
      // Retorno mensagem duplicada caso contenha
      ret = EnumReturn.REPEATED_PACKET;
    }
    
    // Prepara resposta
    byte[] sendData = new byte[DATA_SIZE];
    sendData = new StructuredMessage(0, ret.toString(), 1).getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getClientIP(), getClientPort());
    LogUtils.logSentDatagramPacketInfo(sendPacket);
    
    // Envia resposta
    try {
      getServerSocket().send(sendPacket);
    } catch (IOException e) {
      System.out.println("Erro ao enviar reposta para cliente, " + e);
    }
    
  }
  
  private boolean bufferContainsMessage(StructuredMessage message) {
    Iterator<StructuredMessage> iterator = getMessages().iterator();
    
    while (iterator.hasNext()) {
      StructuredMessage bufferedMessage = iterator.next();
      if (bufferedMessage.getSequenceNumber() == message.getSequenceNumber()
          && bufferedMessage.getData().equals(message.getData())) {
        return true;
      }
    }
    return false;
  }
  
  private String getClientHandlerStringIdentification() {
    return "Handler of " + getClientIP() + ":" + getClientPort() + ":";
  }
  
  public DatagramSocket getServerSocket() {
    return serverSocket;
  }
  
  public void setServerSocket(DatagramSocket serverSocket) {
    this.serverSocket = serverSocket;
  }
  
  public void setShouldRun(boolean shouldRun) {
    this.shouldRun = shouldRun;
  }
}
