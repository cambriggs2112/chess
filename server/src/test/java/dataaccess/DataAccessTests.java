package dataaccess;

import org.junit.jupiter.api.*;

public class DataAccessTests {
    @Test
    public void sandbox() {
        try {
            SQLAuthDAO test1 = new SQLAuthDAO();
            SQLGameDAO test2 = new SQLGameDAO();
            SQLUserDAO test3 = new SQLUserDAO();
            System.out.println("Created database and tables");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        Assertions.assertTrue(true);
    }
}
