package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import common.LogUtils;

class UDPClient {
  private static final int SERVER_PORT = 9876;
  private static final String SERVER_IP = "127.0.0.1";
  
  public static void main(String args[]) throws Exception {
    
    // Instancia variáveis de socket
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName(SERVER_IP);
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    
    String sentence = "Oi, meu nome é Goku";
    sendData = sentence.getBytes();
    
    // Envia mensgaem para Servidor
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
    LogUtils.logSentDatagramPacketInfo(sendPacket);
    clientSocket.send(sendPacket);
    
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    LogUtils.logReceivedDatagramPacketInfo(receivePacket);
    clientSocket.close();
  }
}