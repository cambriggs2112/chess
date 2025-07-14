package dataaccess;

import java.util.ArrayList;

public class UserDAO {
    private ArrayList<UserData> userDatabase;

    public UserDAO() {
        this.userDatabase = new ArrayList<UserData>();
    }
}
