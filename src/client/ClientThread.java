package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import common.EnumReturn;
import common.LogUtils;
import common.StructuredMessage;

public class ClientThread extends Thread {
  
  // Porta do servidor
  private static final int SERVER_PORT = 9876;
  
  // IP do servidor
  private static final String SERVER_IP = "127.0.0.1";
  
  // Tamanho do buffer
  private static final int DATA_SIZE = 1024;
  
  private TestCaseEnum testCase;
  private int indexToRepeatOrLost;
  private int numberOfTimesToSendRepeatedMessage = 1;
  
  // Vetor com mesagens que serão enviadas
  private String[] messages = { //
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
  
  public ClientThread(TestCaseEnum testCase) {
    setTestCase(testCase);
  }
  
  public void run() {
    
    setIndexToRepeatOrLost(5);
    
    try {
      // Instancia variáveis de socket
      DatagramSocket clientSocket;
      clientSocket = new DatagramSocket();
      
      // Gera mensagens de acordo com necessidade do caso de teste
      StructuredMessage[] structuredMessages = generateTestCaseStructuredMessages();
      
      for (int index = 0; index < structuredMessages.length; index++) {
        
        // Mantém, repete ou pula index de acordo com o caso de teste
        index = setTestCasesParameters(index);
        
        // Envia a mesma mensagem para o servidor de acordo com caso de teste
        for (int repeatSend = 0; repeatSend < getNumberOfTimesToSendRepeatedMessage(); repeatSend++) {
          // Envia mensagem para Servidor e espera por uma reposta
          transferDataWithServer(clientSocket, structuredMessages[index], index);
          Thread.sleep(100);
        }
      }
      
      clientSocket.close();
    } catch (Exception e) {
      System.out.println("Erro na thread do cliente");
      e.printStackTrace();
    }
  }
  
  private StructuredMessage[] generateTestCaseStructuredMessages() {
    StructuredMessage[] structuredMessages;
    // Cria mensagens codificadas de acordo com o caso de teste
    if (getTestCase() == TestCaseEnum.ORDELY_MESSAGES || getTestCase() == TestCaseEnum.REPEATED_MESSAGE
        || getTestCase() == TestCaseEnum.LOST_MESSAGES) {
      structuredMessages = generateOrdelySequence(getMessages());
    } else {
      structuredMessages = generateUnordelySequence(getMessages());
    }
    return structuredMessages;
  }
  
  private int setTestCasesParameters(int index) {
    // Verifica se envio de mensagem deverá se repetir devido ao caso de teste
    if (getTestCase() == TestCaseEnum.REPEATED_MESSAGE && index == getIndexToRepeatOrLost()) {
      setNumberOfTimesToSendRepeatedMessage(2);
    } else {
      setNumberOfTimesToSendRepeatedMessage(1);
    }
    return index;
  }
  
  private void transferDataWithServer(DatagramSocket clientSocket, StructuredMessage structuredMessage,
      int packageNumber) throws IOException {
    
    InetAddress IPAddress = InetAddress.getByName(SERVER_IP);
    
    boolean shouldResend = false;
    
    do {
      byte[] sendData = new byte[DATA_SIZE];
      byte[] receiveData = new byte[DATA_SIZE];
      
      // Pega byte array da mensagem que deverá ser enviada
      sendData = structuredMessage.getBytes();
      
      if (getTestCase() != TestCaseEnum.LOST_MESSAGES || //
          (getTestCase() == TestCaseEnum.LOST_MESSAGES
              && (shouldResend == true && getIndexToRepeatOrLost() == packageNumber)
              || getIndexToRepeatOrLost() != packageNumber)) {
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
        LogUtils.logSentDatagramPacketInfo(sendPacket);
        clientSocket.send(sendPacket);
      }
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      System.out.println("Esperando Reposta do Servidor de recebimento de pacote [" + packageNumber + "]");
      clientSocket.receive(receivePacket);
      LogUtils.logReceivedDatagramPacketInfo(receivePacket);
      System.out.println();
      
      // Verifica se pacote se perdeu
      if (EnumReturn(StructuredMessage.getStructuredMessage(receiveData).getData()) == EnumReturn.MISSING_PACKET) {
        shouldResend = true;
      } else {
        shouldResend = false;
      }
      
    } while (shouldResend);
  }
  
  private EnumReturn EnumReturn(String data) {
    switch (data) {
      case "OK":
        return EnumReturn.OK;
      case "REPEATED_PACKET":
        return EnumReturn.REPEATED_PACKET;
      default:
        return EnumReturn.MISSING_PACKET;
    }
  }
  
  private StructuredMessage[] generateOrdelySequence(String[] messages) {
    
    System.out.println("messages.length: " + messages.length);
    StructuredMessage[] structuredMessages = new StructuredMessage[messages.length];
    
    for (int index = 0; index < messages.length; index++) {
      System.out.println("index: " + index);
      structuredMessages[index] = new StructuredMessage(index, messages[index], messages.length);
    }
    return structuredMessages;
  }
  
  private StructuredMessage[] generateUnordelySequence(String[] messages) {
    
    System.out.println("messages.length: " + messages.length);
    StructuredMessage[] structuredMessages = new StructuredMessage[messages.length];
    
    for (int index = 0; index < messages.length; index++) {
      System.out.println("index: " + index);
      structuredMessages[messages.length - index - 1] = new StructuredMessage(index, messages[index], messages.length);
    }
    return structuredMessages;
  }
  
  public String[] getMessages() {
    return messages;
  }
  
  public TestCaseEnum getTestCase() {
    return testCase;
  }
  
  public void setTestCase(TestCaseEnum testCase) {
    this.testCase = testCase;
  }
  
  public int getIndexToRepeatOrLost() {
    return indexToRepeatOrLost;
  }
  
  public void setIndexToRepeatOrLost(int indexToRepeatOrLost) {
    this.indexToRepeatOrLost = indexToRepeatOrLost;
  }
  
  public int getNumberOfTimesToSendRepeatedMessage() {
    return numberOfTimesToSendRepeatedMessage;
  }
  
  public void setNumberOfTimesToSendRepeatedMessage(int numberOfTimesToSendRepeatedMessage) {
    this.numberOfTimesToSendRepeatedMessage = numberOfTimesToSendRepeatedMessage;
  }
}
