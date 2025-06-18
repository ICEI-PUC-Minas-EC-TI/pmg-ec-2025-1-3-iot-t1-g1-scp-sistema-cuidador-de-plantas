package scp.backend.database.daos;

import scp.backend.database.MysqlConnectionFactory;
import scp.backend.database.models.Humidity;
import scp.backend.database.models.SoilHumidity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HumidityDAO {
    public static void insertHumidity(Humidity humidity) throws SQLException, ClassNotFoundException {
        final String sql = "insert into humidity_sensor(humidity) values (?)";

        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, humidity.getHumidity());

        stmt.executeUpdate();
        stmt.close();
    }

    public static List<Humidity> getAllHumidity() throws SQLException {
        final String sql = "select * from humidity_sensor order by created_at desc limit 50000";
        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        List<Humidity> list = new ArrayList<>();
        while(rs.next()) {
            int id = rs.getInt("id");
            double humidity = rs.getDouble("humidity");
            Calendar createdAt = Calendar.getInstance();
            Timestamp ts = rs.getTimestamp("created_at");
            createdAt.setTime(ts);

            Humidity humidityModel = new Humidity(id, humidity, createdAt);
            list.add(humidityModel);
        }

        return list;
    }
}
