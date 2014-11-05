package cn.answer;

import cn.model.Client;

import java.util.List;

/**
 * List of clients
 */
public class ClientListAnswer extends Answer{

  private List<Client> clients;

  public ClientListAnswer() {
    setType(CLIENTS_LIST);
    setMessage("List of clients");
  }

  public List<Client> getClients() {
    return clients;
  }

  public void setClients(List<Client> clients) {
    this.clients = clients;
  }

  @Override
  public String toString(){
    StringBuilder data = new StringBuilder();
    for (Client client : clients){
      data.append("<id>");
      data.append(client.getId());
      data.append("</id>");
      data.append("\n");
    }

    return "<ans>\n" +
            "    <type>"+ getType() +"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "    <commandId>"+ getCommandId()+"</commandId>\n" +
            "    <timeOfReceiptServer>"+getTimeOfReceiptServer()+"</timeOfReceiptServer>\n" +
            "    <timeOfExecSql>"+getTimeOfExecSql()+"</timeOfExecSql>\n" +
            "    <timeOfExecuteServer>"+getTimeOfExecuteServer()+"</timeOfExecuteServer>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "    <dateAnswer>"+getDateAnswer()+"</dateAnswer>\n" +
            "    <commandSetId>"+getCommandSetId()+"</commandSetId>\n" +
            "    <ids>\n" +
            "" + data.toString() + "\n" +
            "    </ids>\n" +
            "</ans>";
  }

}
