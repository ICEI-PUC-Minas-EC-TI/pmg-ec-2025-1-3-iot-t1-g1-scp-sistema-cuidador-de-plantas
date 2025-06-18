package scp.backend.database;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MysqlConnectionFactory {
    private static final String url = "jdbc:mariadb://localhost/scp";
    private static final String username = "root";
    private static String password = "root";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if(connection != null) {
            return connection;
        }

        connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    public static void createDatabase() throws SQLException, IOException {
        Connection conn = getConnection();
        Scanner scanner = new Scanner(Paths.get("database.sql"));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
            sb.append("\n");
        }
        scanner.close();

        String sql = sb.toString();

        String[] commands = sql.split(";");

        for (String command : commands) {
            if(command.trim().isEmpty())
                continue;

            Statement stmt = conn.createStatement();
            stmt.execute(command);
            stmt.close();
        }
    }

    public static void setArgsDbPassword(String[] args) {
        if(args.length > 0) {
            password = args[0];
        }
    }
}
