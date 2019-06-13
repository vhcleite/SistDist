package client;

class UDPClient {
  
  public static void main(String args[]) {
    
    ClientThread client1 = new ClientThread(TestCaseEnum.ORDELY_MESSAGES);
    ClientThread client2 = new ClientThread(TestCaseEnum.LOST_MESSAGES);
    
    System.out.println("Inicializando cliente");
    client1.start();
    client2.start();
    System.out.println("Clientes inicializados");
    
    while (client1.isAlive() && client2.isAlive()) {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Finalizando programa");
  }
  
}