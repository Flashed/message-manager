package sr.dao;

import sr.model.Client;

import java.util.List;

/**
 * Data access object for Client
 */
public interface ClientDao {

  /**
   * Get client by id
   *
   * @param id the id
   * @return client
   */
  Client get(int id);

  /**
   * Get all clients
   *
   * @return list of clients
   */
  List<Client> getAll();

  /**
   * Save the client
   * @param client client
   */
  void save(Client client);


}
