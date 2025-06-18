package scp.backend.database.daos;

import scp.backend.database.MysqlConnectionFactory;
import scp.backend.database.models.SoilHumidity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SoilHumidityDAO {
    public static void insertHumidity(SoilHumidity humidity) throws SQLException, ClassNotFoundException {
        final String sql = "insert into soil_humidity_sensor(raw, percentage) values (?, ?)";

        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, humidity.getRaw());
        stmt.setDouble(2, humidity.getPercentage());

        stmt.executeUpdate();
        stmt.close();
    }

    public static List<SoilHumidity> getAllSoilHumidity() throws SQLException {
        final String sql = "select * from soil_humidity_sensor order by created_at desc limit 50000";
        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        List<SoilHumidity> soilHumidityList = new ArrayList<>();
        while(rs.next()) {
            int id = rs.getInt("id");
            double raw = rs.getDouble("raw");
            double percentage = rs.getDouble("percentage");
            Calendar createdAt = Calendar.getInstance();
            Timestamp ts = rs.getTimestamp("created_at");
            createdAt.setTime(ts);

            SoilHumidity soilHumidity = new SoilHumidity(id, raw, percentage, createdAt);
            soilHumidityList.add(soilHumidity);
        }

        return soilHumidityList;
    }
}
