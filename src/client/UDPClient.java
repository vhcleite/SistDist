package client;

import java.io.IOException;
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
  
  private static UDPTestCase testCase;
  private static int indexToRepeatOrLost;
  private static int numberOfTimesToSendRepeatedMessage = 1;
  
  // Vetor com mesagens que serão enviadas
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
    
    setTestCase(UDPTestCase.REPEATED_MESSAGE);
    setIndexToRepeatOrLost(5);
    
    // Gera mensagens de acordo com necessidade do caso de teste
    StructuredMessage[] structuredMessages = generateTestCaseStructuredMessages();
    
    for (int index = 0; index < structuredMessages.length; index++) {
      
      // Mantém, repete ou pula index de acordo com o caso de teste
      index = setTestCasesParameters(index);
      
      // Envia a mesma mensagem para o servidor de acordo com caso de teste
      for (int repeatSend = 0; repeatSend < getNumberOfTimesToSendRepeatedMessage(); repeatSend++) {
        // Envia mensagem para Servidor e espera por uma reposta
        transferDataWithServer(structuredMessages[index]);
      }
    }
  }
  
  private static StructuredMessage[] generateTestCaseStructuredMessages() {
    StructuredMessage[] structuredMessages;
    // Cria mensagens codificadas de acordo com o caso de teste
    if (getTestCase() == UDPTestCase.ORDELY_MESSAGES || getTestCase() == UDPTestCase.REPEATED_MESSAGE
        || getTestCase() == UDPTestCase.LOST_MESSAGES) {
      structuredMessages = generateOrdelySequence(getMessages());
    } else {
      structuredMessages = generateUnordelySequence(getMessages());
    }
    return structuredMessages;
  }
  
  private static int setTestCasesParameters(int index) {
    // Verifica se envio de mensagem deverá ser pulado devido ao caso de teste
    if (getTestCase() == UDPTestCase.LOST_MESSAGES && index == getIndexToRepeatOrLost()) {
      index++;
    }
    
    // Verifica se envio de mensagem deverá se repetir devido ao caso de teste
    if (getTestCase() == UDPTestCase.REPEATED_MESSAGE && index == getIndexToRepeatOrLost()) {
      setNumberOfTimesToSendRepeatedMessage(2);
    } else {
      setNumberOfTimesToSendRepeatedMessage(1);
    }
    return index;
  }
  
  private static void transferDataWithServer(StructuredMessage structuredMessage) throws IOException {
    
    // Instancia variáveis de socket
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName(SERVER_IP);
    byte[] sendData = new byte[DATA_SIZE];
    byte[] receiveData = new byte[DATA_SIZE];
    
    // Pega byte array da mensagem que deverá ser enviada
    sendData = structuredMessage.getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
    LogUtils.logSentDatagramPacketInfo(sendPacket);
    clientSocket.send(sendPacket);
    
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    LogUtils.logReceivedDatagramPacketInfo(receivePacket);
    System.out.println();
    
    clientSocket.close();
  }
  
  private static StructuredMessage[] generateSequenceWithDuplicatedMessages(String[] messages, int indexToRepeat) {
    
    System.out.println("messages.length: " + messages.length);
    int messagesLength = messages.length + 1;
    StructuredMessage[] structuredMessages = new StructuredMessage[messagesLength];
    
    for (int index = 0; index < messages.length; index++) {
      // FIXME
      if (index <= indexToRepeat) {
        structuredMessages[index] = new StructuredMessage(index, messages[index]);
        System.out.printf("StructuredMessage de Index %d codificada com %s\n", (index), messages[index]);
        if (index == indexToRepeat) {
          structuredMessages[index + 1] = new StructuredMessage(index, messages[index]);
          System.out.printf("StructuredMessage de Index %d codificada com %s\n", (index + 1), messages[index]);
        }
      } else {
        structuredMessages[index + 1] = new StructuredMessage(index + 1, messages[index]);
        System.out.printf("StructuredMessage de Index %d codificada com %s\n", (index + 1), messages[index]);
      }
      System.out.println("mensagem de index codificada: " + index);
    }
    return structuredMessages;
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
  
  private static StructuredMessage[] generateUnordelySequence(String[] messages) {
    
    System.out.println("messages.length: " + messages.length);
    StructuredMessage[] structuredMessages = new StructuredMessage[messages.length];
    
    for (int index = 0; index < messages.length; index++) {
      System.out.println("index: " + index);
      structuredMessages[messages.length - index - 1] = new StructuredMessage(index, messages[index]);
    }
    return structuredMessages;
  }
  
  public static String[] getMessages() {
    return messages;
  }
  
  public static UDPTestCase getTestCase() {
    return testCase;
  }
  
  public static void setTestCase(UDPTestCase testCase) {
    UDPClient.testCase = testCase;
  }
  
  public static int getIndexToRepeatOrLost() {
    return indexToRepeatOrLost;
  }
  
  public static void setIndexToRepeatOrLost(int indexToRepeatOrLost) {
    UDPClient.indexToRepeatOrLost = indexToRepeatOrLost;
  }
  
  public static int getNumberOfTimesToSendRepeatedMessage() {
    return numberOfTimesToSendRepeatedMessage;
  }
  
  public static void setNumberOfTimesToSendRepeatedMessage(int numberOfTimesToSendRepeatedMessage) {
    UDPClient.numberOfTimesToSendRepeatedMessage = numberOfTimesToSendRepeatedMessage;
  }
}