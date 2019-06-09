package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import common.LogUtils;

class UDPServer {
  
  // Tamanho do buffer
  private static final int DATA_SIZE = 1024;
  
  // Porta do servidor
  private static final int SERVER_PORT = 9876;
  
  public static void main(String args[]) throws Exception {
    
    while (true) {
      DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
      
      byte[] receiveData = new byte[DATA_SIZE];
      byte[] sendData = new byte[DATA_SIZE];
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      
      System.out.println("Servidor aguardando...");
      serverSocket.receive(receivePacket);
      LogUtils.logReceivedDatagramPacketInfo(receivePacket);
      
      // Forma resposta do servidor
      String response = String.format("O servidor recebeu: " + new String(receivePacket.getData()));
      
      sendData = response.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(),
          receivePacket.getPort());
      
      LogUtils.logSentDatagramPacketInfo(sendPacket);
      System.out.println();
      
      // Envia resposta para cliente
      serverSocket.send(sendPacket);
      serverSocket.close();
    }
  }
}