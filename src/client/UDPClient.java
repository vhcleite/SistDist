package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import common.LogUtils;
import common.StructuredMessage;

class UDPClient {
  // Porta do servidor
  private static final int SERVER_PORT = 9876;
  
  // IP do servidor
  private static final String SERVER_IP = "127.0.0.1";
  
  // Tamanho do buffer
  private static final int DATA_SIZE = 1024;
  
  private static String[] messages = { //
      "M0", //
      "M1", //
      "M2", //
      "M3", //
      "M4", //
      "M5", //
      "M6", //
      "M7", //
      "M8", //
      "M9", //
  };
  
  public static void main(String args[]) throws Exception {
    
    // Instancia variáveis de socket
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName(SERVER_IP);
    byte[] sendData = new byte[DATA_SIZE];
    byte[] receiveData = new byte[DATA_SIZE];
    
    // Método usado aqui vai definir caso de teste
    StructuredMessage[] structuredMessages = generateOrdelySequence(messages);
    
    for (StructuredMessage structuredMessage : structuredMessages) {
      
      sendData = structuredMessage.getBytes();
      
      // Envia mensagem para Servidor
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
      LogUtils.logSentDatagramPacketInfo(sendPacket);
      clientSocket.send(sendPacket);
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      LogUtils.logReceivedDatagramPacketInfo(receivePacket);
    }
    clientSocket.close();
  }
  
  private static StructuredMessage[] generateOrdelySequence(String[] messages) {
    
    System.out.println("messages.length: " + messages.length);
    StructuredMessage[] structuredMessages = new StructuredMessage[messages.length];
    for (int index = 0; index < messages.length; index++) {
      System.out.println("index: " + index);
      structuredMessages[index] = new StructuredMessage(index, messages[index]);
    }
    return structuredMessages;
  }
  
  public String[] getMessages() {
    return messages;
  }
}