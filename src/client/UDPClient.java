package client;

class UDPClient {
  
  public static void main(String args[]) {
    
    ClientThread client1 = new ClientThread(TestCaseEnum.ORDELY_MESSAGES);
    ClientThread client2 = new ClientThread(TestCaseEnum.LOST_MESSAGES);
    
    System.out.println("Start do cliente1");
    client1.start();
    System.out.println("Start do cliente2");
    client2.start();
    System.out.println("Clientes inicializados");
    
    while (client1.isAlive() || client2.isAlive()) {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Finalizando programa");
  }
  
}