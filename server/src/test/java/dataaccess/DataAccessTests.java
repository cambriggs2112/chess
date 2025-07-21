package dataaccess;

import org.junit.jupiter.api.*;

public class DataAccessTests {
    @Test
    public void sandbox() {
        try {
            SQLAuthDAO test = new SQLAuthDAO();
            System.out.println("Created database");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        Assertions.assertTrue(true);
    }
}
