package scp.backend.database.daos;

import scp.backend.database.MysqlConnectionFactory;
import scp.backend.database.models.Humidity;
import scp.backend.database.models.Light;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LightDAO {
    public static void insertLight(Light humidity) throws SQLException, ClassNotFoundException {
        final String sql = "insert into light_sensor(lux, percentage) values (?, ?)";

        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, humidity.getLux());
        stmt.setDouble(2, humidity.getPercentage());

        stmt.executeUpdate();
        stmt.close();
    }

    public static List<Light> getAllLight() throws SQLException {
        final String sql = "select * from light_sensor order by created_at desc limit 50000";
        Connection connection = MysqlConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        List<Light> list = new ArrayList<>();
        while(rs.next()) {
            int id = rs.getInt("id");
            double lux = rs.getDouble("lux");
            double percentage = rs.getDouble("percentage");
            Calendar createdAt = Calendar.getInstance();
            Timestamp ts = rs.getTimestamp("created_at");
            createdAt.setTime(ts);

            Light model = new Light(id, lux, percentage, createdAt);
            list.add(model);
        }

        return list;
    }
}
