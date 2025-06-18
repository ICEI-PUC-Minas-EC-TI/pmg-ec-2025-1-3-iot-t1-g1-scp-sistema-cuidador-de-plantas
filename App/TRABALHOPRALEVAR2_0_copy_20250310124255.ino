#include <Wire.h>
#include <BH1750.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <WiFi.h>
#include <PubSubClient.h>

#define SENSOR_PIN 34           // Pino do sensor de umidade do solo
#define BOMBINHA_PIN 15
#define VENTOINHA_PIN 4

const int freq = 5000;
const int resolution = 8;
int sla = 0;
int inputValue = 0;

float umidade_value = 0;  // Umidade desejada
float temp_value = 0;     // Temperatura desejada
float sensibilidade = 0;  // Sensibilidade do LED

#define DHTPIN 23
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
BH1750 lightMeter;  // Sensor de luminosidade

// Credenciais Wi-Fi
const char* ssid = "Leonardo_2.4GHZ";
const char* password = "levyleo240995";

// Broker MQTT
const char* mqtt_server = "broker.emqx.io";

// broker.emqx.io
WiFiClient espClient;
PubSubClient client(espClient);

// Função para reconectar ao MQTT
void reconnect() {
  while (!client.connected()) {
    Serial.print("Conectando ao MQTT...");
    if (client.connect("ESP32_MiniEstufa")) {
      Serial.println("Conectado!");
      client.subscribe("mini_estufa/temp_value");
      client.subscribe("mini_estufa/umidade_value");
      client.subscribe("mini_estufa/sensibilidade");
    } else {
      Serial.print("Falha, rc=");
      Serial.print(client.state());
      delay(5000);
    }
  }
}

// Callback para receber mensagens MQTT
void callback(char* topic, byte* message, unsigned int length) {
  String msg = "";
  for (int i = 0; i < length; i++) {
    msg += (char)message[i];
  }
  Serial.print("fijdsgjnsg");
  Serial.print(msg);
  if (String(topic) == "mini_estufa/temp_value") {
    temp_value = msg.toFloat();
    Serial.println("Temperatura atualizada: " + String(temp_value));
  } else if (String(topic) == "mini_estufa/umidade_value") {
    umidade_value = msg.toFloat();
    Serial.println("Umidade atualizada: " + String(umidade_value));
  } else if (String(topic) == "mini_estufa/sensibilidade") {
    sensibilidade = msg.toFloat();
    Serial.println("Sensibilidade atualizada: " + String(sensibilidade));
  }
}

void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi conectado");

  pinMode(SENSOR_PIN, INPUT);

  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  Wire.begin(21, 19);
  lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE);
  dht.begin();

  pinMode(BOMBINHA_PIN, OUTPUT);
  pinMode(VENTOINHA_PIN, OUTPUT);
  pinMode(13, OUTPUT);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // ===========================
  // Leitura do sensor de umidade YL-69
  // ===========================
  float umidade = analogRead(SENSOR_PIN);  // Leitura do pino analógico do sensor de umidade do solo
  Serial.print("Umidade do solo: ");
  Serial.println(umidade);

  

  // Converter umidade para String
String umidadeStr = String(umidade);
const char* umidadeChar = umidadeStr.c_str();  // Convertendo para const char*

// Publicar o valor de umidade no MQTT
if (client.publish("mini_estufa/umidade_solo", umidadeChar)) {
  Serial.println("Umidade do solo publicada no MQTT com sucesso!");
} else {
  Serial.println("Falha ao publicar a umidade do solo.");
}

  // Controle da bombinha de água (liga quando a umidade é baixa)
  if (umidade < umidade_value) {  // Ajuste o valor conforme necessário
    digitalWrite(BOMBINHA_PIN, HIGH);  // Liga a bombinha
    Serial.println("Bomba ativada: solo seco");
  } else {
    digitalWrite(BOMBINHA_PIN, LOW);   // Desliga a bombinha
    Serial.println("Bomba desativada: solo com umidade suficiente");
  }

  // Leitura do sensor de luminosidade (BH1750)
  float lux = lightMeter.readLightLevel();












if (lux < 0) {
  Serial.println("Erro ao ler luminosidade!");
} else {
  Serial.print("Luminosidade: ");
  Serial.print(lux);
  Serial.println(" lx");

  // Converter luminosidade para String
  String luxStr = String(lux);
  const char* luxChar = luxStr.c_str();  // Convertendo para const char*

  // Publicar o valor de luminosidade no MQTT
  if (client.publish("mini_estufa/luminosidade", luxChar)) {
    Serial.println("Luminosidade publicada no MQTT com sucesso!");
  } else {
    Serial.println("Falha ao publicar a luminosidade.");
  }
}




















  Serial.print("Luminosidade: ");
  Serial.print(lux);
  Serial.println(" lx");

  // Leitura do sensor de temperatura (DHT11)
  float temperature = dht.readTemperature();
  if (isnan(temperature)) {
    Serial.println("Erro ao ler temperatura!");
  } else {
    Serial.print("Temperatura: ");
    Serial.print(temperature);
    Serial.println(" C");
   
    String tempStr = String(temperature);  // Converter temperatura para String
    const char* tempChar = tempStr.c_str(); // Convertendo para const char*

    if (client.publish("mini_estufa/temperatura", tempChar)) {
    Serial.println("Temperatura publicada no MQTT com sucesso!");
  } else {
    Serial.println("Falha ao publicar a temperatura.");
  }
  }








// Leitura do sensor de umidade do ar (DHT11)
float humidity = dht.readHumidity();
if (isnan(humidity)) {
  Serial.println("Erro ao ler umidade do ar!");
} else {
  Serial.print("Umidade do ar: ");
  Serial.print(humidity);
  Serial.println(" %");

  String humidityStr = String(humidity);  // Converter umidade para String
  const char* humidityChar = humidityStr.c_str(); // Convertendo para const char*

  if (client.publish("mini_estufa/umidade_ar", humidityChar)) {
    Serial.println("Umidade do ar publicada no MQTT com sucesso!");
  } else {
    Serial.println("Falha ao publicar a umidade do ar.");
  }
}

















  // Exibição das variáveis controladas via MQTT
  Serial.print("Umidade desejada: ");
  Serial.println(umidade_value);
  Serial.print("Temperatura desejada: ");
  Serial.println(temp_value);
  Serial.print("Sensibilidade: ");
  Serial.println(sensibilidade);

  // Controle da ventoinha com base na temperatura
  if (temperature < temp_value) {
    digitalWrite(VENTOINHA_PIN, HIGH); // Ativa a ventoinha se a temperatura estiver abaixo do valor desejado
  } else {
    digitalWrite(VENTOINHA_PIN, LOW);  // Desativa a ventoinha se a temperatura estiver boa
  }

  // Controle do LED (pino 13) com base na luminosidade e sensibilidade
  analogWrite(13, 255 - lux * sensibilidade);

  // Publicar a sensibilidade atual no MQTT
String sensibilidadeStr = String(sensibilidade);
const char* sensibilidadeChar = sensibilidadeStr.c_str();
if (client.publish("mini_estufa/barra_sensi", sensibilidadeChar)) {
  Serial.println("Sensibilidade atual publicada no MQTT com sucesso!");
} else {
  Serial.println("Falha ao publicar a sensibilidade atual.");
}



 // Publicar o valor atual de temperatura desejada no MQTT
  String tempValueStr = String(temp_value);
  const char* tempValueChar = tempValueStr.c_str();
  client.publish("mini_estufa/barra_temp", tempValueChar);
  Serial.println("Temperatura desejada publicada no MQTT!");

// Dentro do loop, logo após a leitura dos sensores:
String umidadeValueStr = String(umidade_value);  // Converter para String
const char* umidadeValueChar = umidadeValueStr.c_str(); // Convertendo para const char*

// Publicar o valor de umidade_value no MQTT
if (client.publish("mini_estufa/umidade_value_atual", umidadeValueChar)) {
  Serial.println("Umidade desejada atualizada e publicada no MQTT com sucesso!");
} else {
  Serial.println("Falha ao publicar a umidade desejada.");
}

  delay(600);  // Aguarda 1 segundo antes de nova leitura
}
