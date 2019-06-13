package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import common.EnumReturn;
import common.LogUtils;
import common.StructuredMessage;
import common.StructuredMessagesComparator;

public class ClientHandlerThread extends Thread {
  
  private static final int DATA_SIZE = 1024;
  
  private static final long TIMEOUT = 5 * (long) Math.pow(10, 9); // nanoseconds
  
  private int clientPort;
  private InetAddress clientIP;
  private DatagramSocket serverSocket;
  private boolean shouldRun;
  private ArrayList<StructuredMessage> messages = new ArrayList<StructuredMessage>();
  private Queue<DatagramPacket> notProcessedMessages = new LinkedList<DatagramPacket>();
  
  public ClientHandlerThread(DatagramSocket serverSocket, int port, InetAddress address) {
    super();
    this.serverSocket = serverSocket;
    this.clientPort = port;
    this.clientIP = address;
  }
  
  public void run() {
    System.out.println(getClientHandlerId() + " inicializado");
    setShouldRun(true);
    
    long startTime = System.nanoTime();
    
    while (getShouldRun()) {
      try {
        
        if (getNotProcessedMessages().peek() != null) {
          System.out.println(
              getClientHandlerId() + "processando mensagem em fila com tamanho " + getNotProcessedMessages().size());
          processMessagePacket(getNotProcessedMessages().remove());
        }
        
        // timeout para receber todas as mensagens
        if ((System.nanoTime() - startTime) >= TIMEOUT) {
          // Verifica se mensagem está completa
          if (hasReceivedAllPackets()) {
            logSuccessMessage();
            getMessages().clear();
            setShouldRun(false);
          } else {
            // Caso mensagens não tenham chego, pedir para cliente reenviar
            returnLostMessageSignalToClient();
            startTime = System.nanoTime();
          }
        }
        
        Thread.sleep(50);
        
      } catch (InterruptedException e) {
      }
    }
    
    System.out.println("Terminando " + getClientHandlerId());
    
  }
  
  private void returnLostMessageSignalToClient() {
    byte[] sendData = new byte[DATA_SIZE];
    sendData = new StructuredMessage(0, EnumReturn.MISSING_PACKET.toString(), 1).getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getClientIP(), getClientPort());
    LogUtils.logSentDatagramPacketInfo(getClientHandlerId(), sendPacket);
    
    try {
      getServerSocket().send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  private void logSuccessMessage() {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!MENSAGENS RECEBIDAS!!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println(getMessages());
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  }
  
  private boolean hasReceivedAllPackets() {
    return getMessages().size() == getMessages().get(0).getNumberOfPackets();
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
    System.out.printf(getClientHandlerId() + "menssagem [%s] adicionada no buffer\r\n", message);
    System.out.println(getMessages());
  }
  
  public void processMessagePacket(DatagramPacket receivePacket) {
    
    EnumReturn ret = EnumReturn.OK;
    
    System.out.println("---" + getClientHandlerId());
    LogUtils.logReceivedDatagramPacketInfo(getClientHandlerId(), receivePacket);
    
    StructuredMessage message = StructuredMessage.getStructuredMessage(receivePacket.getData());
    
    // verifico se lista de mensagens já não contém a mensagem
    if (!bufferContainsMessage(message)) {
      // adiciono na lista caso não contenha
      addMessageInBuffer(message);
      System.out.println(getClientHandlerId() + " Numero de itens no buffer: " + getMessages().size());
    } else {
      // Retorno mensagem duplicada caso contenha
      ret = EnumReturn.REPEATED_PACKET;
    }
    
    // Prepara resposta
    System.out.println(getClientHandlerId() + "enviando resposta para cliente");
    byte[] sendData = new byte[DATA_SIZE];
    sendData = new StructuredMessage(0, ret.toString(), 1).getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getClientIP(), getClientPort());
    LogUtils.logSentDatagramPacketInfo(getClientHandlerId(), sendPacket);
    
    // Envia resposta
    try {
      getServerSocket().send(sendPacket);
    } catch (IOException e) {
      System.out.println(getClientHandlerId() + "Erro ao enviar reposta para cliente, " + e);
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
  
  private String getClientHandlerId() {
    return "Handler of " + getClientIP() + ":" + getClientPort() + " - ";
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
  
  public void passMessagePacketToClientHandler(DatagramPacket receivePacket) {
    getNotProcessedMessages().add(receivePacket);
  }
  
  public Queue<DatagramPacket> getNotProcessedMessages() {
    return notProcessedMessages;
  }
  
  public void setNotProcessedMessages(Queue<DatagramPacket> notProcessedMessages) {
    this.notProcessedMessages = notProcessedMessages;
  }
}
