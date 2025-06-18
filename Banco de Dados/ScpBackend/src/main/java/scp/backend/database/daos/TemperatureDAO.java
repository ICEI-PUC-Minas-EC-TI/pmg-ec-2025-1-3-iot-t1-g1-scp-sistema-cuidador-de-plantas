package scp.backend.database.daos;

import scp.backend.database.MysqlConnectionFactory;
import scp.backend.database.models.Light;
import scp.backend.database.models.Temperature;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TemperatureDAO {
    public static void insertTemperature(Temperature temperature) throws SQLException, ClassNotFoundException {
        final String sql = "insert into temperature_sensor(temperature) values (?)";

        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, temperature.getTemperature());

        stmt.executeUpdate();
        stmt.close();
    }

    public static List<Temperature> getAllTemp() throws SQLException {
        final String sql = "select * from temperature_sensor order by created_at desc limit 50000";
        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        List<Temperature> list = new ArrayList<>();
        while(rs.next()) {
            int id = rs.getInt("id");
            double temp = rs.getDouble("temperature");
            Calendar createdAt = Calendar.getInstance();
            Timestamp ts = rs.getTimestamp("created_at");
            createdAt.setTime(ts);

            Temperature model = new Temperature(id, temp, createdAt);
            list.add(model);
        }

        return list;
    }
}
