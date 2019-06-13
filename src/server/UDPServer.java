package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;

import common.LogUtils;

class UDPServer {
  
  // Lista com handler dos clientes
  private static ArrayList<ClientHandlerThread> clientHandlerList = new ArrayList<ClientHandlerThread>();
  
  // Tamanho do buffer
  private static final int DATA_SIZE = 1024;
  
  // Porta do servidor
  private static final int SERVER_PORT = 9876;
  
  public static void main(String args[]) throws Exception {
    
    DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
    while (true) {
      
      byte[] receiveData = new byte[DATA_SIZE];
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      
      System.out.println("\r\nServidor aguardando...");
      serverSocket.receive(receivePacket);
      LogUtils.logReceivedDatagramPacketInfo(getDispatcherName(), receivePacket);
      
      passPacketToClientHandler(serverSocket, receivePacket);
      
    }
  }
  
  private static String getDispatcherName() {
    return "Dispatcher - ";
  }
  
  private static void passPacketToClientHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
    
    System.out.println(getDispatcherName() + "verificando se cliente esta na lista");
    ClientHandlerThread clientHandler = findClientHandler(receivePacket);
    
    if (clientHandler == null) {
      // Caso não haja um hanfler, necessário criar e adicionar na lista
      clientHandler = new ClientHandlerThread(serverSocket, receivePacket.getPort(), receivePacket.getAddress());
      getClientHandlerList().add(clientHandler);
      clientHandler.start();
    }
    
    clientHandler.passMessagePacketToClientHandler(receivePacket);
  }
  
  private static ClientHandlerThread findClientHandler(DatagramPacket receivePacket) {
    
    Iterator<ClientHandlerThread> iterator = getClientHandlerList().iterator();
    while (iterator.hasNext()) {
      ClientHandlerThread clientHandler = iterator.next();
      
      if (clientHandler.getClientIP().equals(receivePacket.getAddress())
          && clientHandler.getClientPort() == receivePacket.getPort()) {
        System.out.println("Cliente encontrado na lista de clientHandler");
        return clientHandler;
      }
    }
    System.out.println("Cliente não encontrado na lista de clientHandler");
    return null;
  }
  
  public static ArrayList<ClientHandlerThread> getClientHandlerList() {
    return clientHandlerList;
  }
}