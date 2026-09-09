package source.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Database() {}

    final static private String DATABASE_DIRECTORY = "jdbc:sqlite:database/main.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_DIRECTORY);
    }
}
