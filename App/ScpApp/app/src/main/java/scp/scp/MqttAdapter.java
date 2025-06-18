package scp.scp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.datatypes.MqttUtf8String;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MqttAdapter {
    private static final String LogTag = "MQTT";
    private static Mqtt5Client client = null;
    private static Mqtt5AsyncClient asyncClient = null;

    public void initConnection() {
        if(client != null || asyncClient != null) {
            return;
        }

        MqttClientBuilder clientBuilder = MqttClient.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.emqx.io");

        client = clientBuilder.useMqttVersion5().build();
        asyncClient = client.toAsync();

        CompletableFuture<Mqtt5ConnAck> connectFuture = asyncClient.connect();
        connectFuture.thenAccept(this::whenMqttConnect);
    }

    private void whenMqttConnect(@NonNull Mqtt5ConnAck ack) {
        Optional<MqttUtf8String> result = ack.getReasonString();
        result.ifPresent(mqttUtf8String -> Log.i(LogTag, "Algo ocorreu no mqtt: " + mqttUtf8String));
    }

    public void receiveMessages(@NonNull String topic, @NonNull Consumer<String> callback) {
        asyncClient.subscribeWith()
                .addSubscription()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .applySubscription()
                .callback(mqtt5Publish -> {
                    String msg = new String(mqtt5Publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    callback.accept(msg);
                })
                .send();
    }


    public void sendMessage(@NonNull String topic, @NonNull String message) {
        asyncClient.publishWith()
                .topic(topic)
                .payload(message.getBytes())
                .send();
    }

    public void waitSendMessage(@NonNull String topic, @NonNull String message) throws ExecutionException, InterruptedException {
        asyncClient.publishWith()
                .topic(topic)
                .payload(message.getBytes())
                .send()
                .get();
    }

    public Optional<String> receiveUnique(@NonNull String topic) {
        try {
            CompletableFuture<String> cf = new CompletableFuture<>();
            receiveMessages(topic, cf::complete);
            String message = cf.get();
            return Optional.of(message);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(LogTag, "Erro na future", e);
        }

        return Optional.empty();
    }

    public Optional<String> sendCommand(@NonNull String topic, @NonNull String cmd, @NonNull String resultTopic)
            throws ExecutionException, InterruptedException {
        waitSendMessage(topic, cmd);
        return receiveUnique(resultTopic);
    }
}
