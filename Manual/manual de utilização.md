
## Instruções de utilização

Para operar a mini estufa automatizada, siga os passos abaixo para configurar o hardware e o software:

Configuração do Hardware
Montagem dos componentes:

Certifique-se que todos os sensores (YL-69, DHT11, BH1750) estejam conectados corretamente aos pinos indicados do ESP32.

Verifique a ligação dos atuadores (bomba d’água, ventoinha, LED) aos relés e seus respectivos pinos digitais.

Conecte a fonte de 5V para alimentar o circuito.

Posicione o copo como reservatório e a planta no potinho dentro da estufa.

Alimentação:

Ligue a fonte 5V ao circuito.

Conecte o ESP32 via USB ou outra fonte adequada para alimentação e comunicação.

Configuração do Software
Conexão Wi-Fi:

O ESP32 deve estar configurado para conectar à rede Wi-Fi local, cujas credenciais (SSID e senha) estão definidas no código-fonte.

Broker MQTT:

O sistema utiliza o broker público HiveMQ para comunicação entre o ESP32 e o aplicativo.

Verifique se a conexão com o broker está ativa.

Aplicativo Android:

Instale o aplicativo desenvolvido no dispositivo Android.

Abra o aplicativo e conecte-o à mesma rede Wi-Fi que o ESP32.

Configure os limites de umidade, temperatura e luminosidade na tela de controle para ativação automática dos atuadores.

Funcionamento:

Após a configuração, o sistema realizará leituras periódicas dos sensores e acionará automaticamente a bomba, ventoinha e LED conforme os limites definidos.

O usuário pode acompanhar os dados em tempo real pelo aplicativo.

Observações importantes
Mantenha a fonte de alimentação estável para evitar falhas no sistema.

Em caso de desconexão do Wi-Fi ou broker MQTT, reinicie o ESP32 e o aplicativo para restabelecer a comunicação.

