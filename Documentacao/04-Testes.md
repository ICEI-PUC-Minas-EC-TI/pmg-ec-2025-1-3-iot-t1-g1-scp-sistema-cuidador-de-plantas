# Testes do Projeto

Os testes do projeto foram realizados em etapas para garantir o correto funcionamento de cada componente e da integração geral entre hardware e aplicativo.

Inicialmente, foram testados separadamente os sensores (umidade do solo, temperatura, luminosidade) para verificar se as leituras estavam precisas e estáveis. As medições foram comparadas com instrumentos manuais simples para validação dos valores.

Em seguida, os atuadores — a bomba d’água e o LED de iluminação — foram testados individualmente para garantir que respondiam corretamente aos comandos do ESP32.

Posteriormente, foram realizados testes da comunicação via protocolo MQTT entre o aplicativo em Java e o ESP32. Verificou-se que os dados eram atualizados em tempo real no aplicativo e que os comandos enviados pelo usuário eram recebidos e executados pelo hardware.

Durante os testes integrados, a ativação automática da irrigação e da iluminação com base nos limites definidos pelo usuário foi validada com sucesso, demonstrando a eficácia do sistema em manter as condições ideais da mini estufa.

Limitações identificadas:

O sistema utiliza apenas um LED para iluminação, o que pode não ser suficiente para estufas maiores ou para plantas que demandam luz específica.

A bomba d’água é simples e adequada para volumes pequenos, não sendo indicada para cultivos de maior escala.

O projeto depende de conexão estável com o broker MQTT público, o que pode gerar atrasos ou falhas temporárias na comunicação.

A interface do aplicativo é básica, podendo ser aprimorada para oferecer melhor usabilidade e funcionalidades extras.

Apesar dessas limitações, o projeto cumpriu os objetivos propostos, demonstrando um sistema funcional de automação simples para mini estufas.
