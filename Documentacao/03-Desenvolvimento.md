
# Materiais

Os materiais utilizados no projeto foram:
ESP32 – microcontrolador principal responsável pela leitura dos sensores e controle dos atuadores.

Sensor de Umidade do Solo (YL-69) – utilizado para medir a umidade do solo.

Sensor de Temperatura e Umidade (DHT11) – para monitorar as condições do ar na estufa.

Sensor de Luminosidade (BH1750) – para medir a intensidade luminosa do ambiente.

Bomba d’água 5V – usada para realizar a irrigação automática.

Relé 5V – para controlar o acionamento da bomba e ventoinha.

Fonte 5V - para energizar a ventoinha e bomba.

LED – responsável pela iluminação artificial da planta.

Protoboard – base para montagem do circuito eletrônico.

Cabos jumpers (macho-macho e macho-fêmea) – para conexão entre os componentes.

Copo plástico de 700ml – utilizado como reservatório de água.

Potinho de planta pequeno – recipiente onde foi colocada a planta para simulação na estufa.

Cola quente – utilizada para fixação de componentes e montagem estrutural.


# Desenvolvimento

O desenvolvimento do projeto foi dividido em três grandes etapas: o desenvolvimento do hardware, o desenvolvimento do aplicativo Android em Java e a integração com banco de dados e comunicação via MQTT. O objetivo principal foi automatizar o cuidado com plantas em uma mini estufa, realizando o monitoramento ambiental e acionamento de recursos como irrigação, ventilação e iluminação de forma automática ou remota.

## Desenvolvimento do Aplicativo

### Interface

A interface do aplicativo Android foi desenvolvida no Android Studio, usando Java e o SDK do Android. As telas apresentam:

Tela principal com exibição em tempo real dos dados dos sensores: temperatura, umidade do ar, umidade do solo e luminosidade.

Tela de controle com barras deslizantes (sliders) que permitem ao usuário configurar os limites de ativação automática para cada atuador — por exemplo, o nível mínimo de umidade para ativar a bomba, o limite de luminosidade para acionar o LED, e o limite de temperatura para ligar a ventoinha.

### Código

O aplicativo foi desenvolvido em Java, utilizando o SDK do Android no Android Studio. A comunicação MQTT com o ESP32 foi implementada utilizando o broker público HiveMQ .

No código, são realizadas as seguintes operações:

Conexão ao broker HiveMQ para troca de mensagens MQTT.

Assinatura dos tópicos que transmitem os dados dos sensores (temperatura, umidade do solo, umidade do ar e luminosidade) para atualização em tempo real da interface.

Publicação dos limites configurados pelo usuário em tópicos específicos, que controlam o acionamento automático dos atuadores no hardware.

Atualização dinâmica da interface gráfica conforme as mensagens recebidas.

Armazenamento dos dados recebidos em banco de dados MySQL via JDBC para manutenção do histórico.

## Desenvolvimento do Hardware

### Montagem

A montagem do hardware foi realizada em protoboard, organizando os componentes para simular uma mini estufa automatizada. O ESP32 foi posicionado como controlador central, com conexões elétricas feitas para os sensores e atuadores:

Sensores:

Sensor de umidade do solo YL-69 conectado à entrada analógica do ESP32.

Sensor DHT11 conectado a um pino digital para leitura de temperatura e umidade do ar.

Sensor BH1750 conectado via protocolo I²C para medição de luminosidade.

Atuadores:

Bomba d’água 5V para irrigação, acionada via relé controlado por pino digital do ESP32.

Ventoinha 5V para ventilação, acionada por outro relé.

LED para iluminação artificial.

Os atuadores foram alimentados pela fonte de 5V adequada. Jumpers e cola quente foram usados para fixação dos componentes e organização do circuito. O copo de 700ml foi utilizado como reservatório de água e o potinho serviu para acomodar a planta na estufa.

### Desenvolvimento do Código

O firmware para o ESP32 foi desenvolvido na Arduino IDE, utilizando bibliotecas específicas para cada função:

WiFi.h para conexão do dispositivo à rede Wi-Fi local.

PubSubClient.h para implementação do protocolo MQTT, permitindo comunicação com o broker e troca de mensagens com o aplicativo.

DHT.h para leitura dos dados do sensor DHT11.

Wire.h e BH1750.h para comunicação I²C com o sensor de luminosidade.

O código executa a leitura periódica dos sensores, publica os dados em tópicos MQTT, e subscreve tópicos para receber comandos de controle dos atuadores. A lógica implementa o acionamento automático da bomba, ventoinha e LED conforme os limites configurados pelo usuário via aplicativo.

## Comunicação entre App e Hardware

A comunicação entre o aplicativo Android e o ESP32 foi implementada via protocolo MQTT, utilizando o broker público HiveMQ. O fluxo de dados ocorre da seguinte forma:

O ESP32 publica os dados coletados pelos sensores em tópicos dedicados (ex.: /estufa/temperatura, /estufa/umidade_solo).

O aplicativo assina esses tópicos para receber atualizações em tempo real e exibir os dados na interface.

O usuário configura os limites para acionamento automático no aplicativo, que publica essas configurações em tópicos de controle específicos.

O ESP32 subscreve os tópicos de controle e, ao receber as configurações, ajusta o acionamento dos atuadores automaticamente.

Essa arquitetura possibilita um controle remoto eficiente e em tempo real da mini estufa, utilizando comunicação leve e confiável adequada para dispositivos IoT.

