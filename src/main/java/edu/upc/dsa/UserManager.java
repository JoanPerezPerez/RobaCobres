package edu.upc.dsa;

import edu.upc.dsa.exceptions.UserNotFoundException;
import edu.upc.dsa.models.User;

import java.util.List;

public interface UserManager {

    public User addUser(String id, String user, String password);
    public User addUser(String user, String password);
    public User addUser(User u);
    public User getUser(String id);
    public User getUser2(String id) throws UserNotFoundException;
    public User getUserFromUsername(String _username);

    public List<User> findAll();
    public void deleteUser(String id);
    public User updateUser(User u);

    public void clear();
    public int size();
}
