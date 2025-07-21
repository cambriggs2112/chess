package dataaccess;

import org.junit.jupiter.api.*;
import model.*;

public class DataAccessTests {
    @Test
    public void sandbox() {
        try {
            SQLAuthDAO test = new SQLAuthDAO();
            AuthData testAuth = new AuthData("testToken", "testName");
            System.out.println("Created auth data");
            test.clear();
            System.out.println("Cleared auth data");
            test.createAuth(testAuth);
            System.out.println("Added auth data");
            test.deleteAuth("otherTestToken");
            System.out.println("Deleted auth data");
            test.clear();
            System.out.println("Cleared auth data");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        Assertions.assertTrue(true);
    }
}
