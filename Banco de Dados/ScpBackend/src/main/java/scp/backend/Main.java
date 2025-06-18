package scp.backend;

import com.google.gson.Gson;
import scp.backend.database.MysqlConnectionFactory;
import scp.backend.database.daos.HumidityDAO;
import scp.backend.database.daos.LightDAO;
import scp.backend.database.daos.SoilHumidityDAO;
import scp.backend.database.daos.TemperatureDAO;
import scp.backend.database.models.Humidity;
import scp.backend.database.models.Light;
import scp.backend.database.models.SoilHumidity;
import scp.backend.database.models.Temperature;
import scp.backend.dtos.ChartElementDto;
import scp.backend.dtos.Chartable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException, IOException {
        MysqlConnectionFactory.setArgsDbPassword(args);
        MysqlConnectionFactory.createDatabase();

        MqttAdapter mqttAdapter = new MqttAdapter();
        mqttAdapter.connectMqtt();
        mqttAdapter.receiveMessages("mini_estufa/temperatura", Main::handleTemperature);
        mqttAdapter.receiveMessages("mini_estufa/umidade_ar", Main::handleHumidity);
        mqttAdapter.receiveMessages("mini_estufa/umidade_solo", Main::handleSoilMoisture);
        mqttAdapter.receiveMessages("mini_estufa/luminosidade", Main::handleLight);
        mqttAdapter.receiveMessages("mini_estufa/comandos", command -> {
            try {
                handleCommands(command, mqttAdapter);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.currentThread().join();
    }


    private static void handleTemperature(String message) {
        Temperature temperature = new Temperature(message);
        try {
            TemperatureDAO.insertTemperature(temperature);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erro ao inserir temperatura no banco de dados " + e.getMessage());
        }
    }

    private static void handleHumidity(String message) {
        Humidity humidity = new Humidity(message);
        try {
            HumidityDAO.insertHumidity(humidity);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erro ao inserir humidade no banco de dados " + e.getMessage());
        }
    }

    private static void handleSoilMoisture(String message) {
        SoilHumidity soilHumidity = new SoilHumidity(message);
        try {
            SoilHumidityDAO.insertHumidity(soilHumidity);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erro ao inserir humidade do solo no banco de dados " + e.getMessage());
        }
    }

    private static void handleLight(String message) {
        Light light = new Light(message);
        try {
            LightDAO.insertLight(light);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erro ao inserir a luminosidade no banco de dados " + e.getMessage());
        }
    }


    private static void handleCommands(String command, MqttAdapter adapter) throws SQLException, ClassNotFoundException {
        if(!command.contains("|")) {
            return;
        }

        String[] commandSplit = command.split("\\|");
        if(!commandSplit[0].equals("report")) {
            System.out.println("Comando inválido: " + command);
        }

        String entity = commandSplit[1];
        List<ChartElementDto> elements;
        switch (entity) {
            case "Umidade da Terra": {
                elements = SoilHumidityDAO.getAllSoilHumidity().stream().map(Chartable::toChartElementDto).toList();
                break;
            }
            case "Umidade da Estufa": {
                elements = HumidityDAO.getAllHumidity().stream().map(Chartable::toChartElementDto).toList();
                break;
            }
            case "Luminosidade": {
                elements = LightDAO.getAllLight().stream().map(Chartable::toChartElementDto).toList();
                break;
            }
            case "Temperatura": {
                elements = TemperatureDAO.getAllTemp().stream().map(Chartable::toChartElementDto).toList();
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + entity);
            }
        }

        adapter.sendMessage("mini_estufa/resultado", new Gson().toJson(elements));
    }
}