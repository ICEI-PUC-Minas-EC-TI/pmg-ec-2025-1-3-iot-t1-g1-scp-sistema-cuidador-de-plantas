# Introdução

O avanço da tecnologia tem possibilitado a automação de processos simples do dia a dia, como o cuidado com plantas em ambientes residenciais, comerciais e escolares. Muitas vezes, por falta de tempo ou conhecimento, as pessoas deixam de cuidar adequadamente de suas plantas, o que pode levar à sua deterioração.

Pensando nisso, o projeto Sistema Cuidador de Plantas (SCP) foi desenvolvido com o objetivo de facilitar o monitoramento e a irrigação automatizada de plantas por meio de sensores, atuadores e um aplicativo de controle remoto.

A proposta visa proporcionar uma solução prática, acessível e eficiente, utilizando microcontroladores como o ESP32, sensores ambientais e um aplicativo Android que permita ao usuário acompanhar, em tempo real, as condições da planta e interagir com o sistema — configurando limites para a ativação da irrigação, da iluminação e da ventilação conforme a necessidade.

## Problema

Muitas pessoas não conseguem manter uma rotina adequada de cuidados com plantas devido à falta de tempo, conhecimento técnico ou mesmo por esquecerem de regá-las. Esse problema se intensifica em locais onde há várias plantas, como escritórios, escolas, jardins comunitários ou residências com hortas.

Pensando nisso, este projeto propõe uma estufa pequena e automatizada, capaz de monitorar as condições do ambiente e cuidar das plantas mesmo sem a presença constante de uma pessoa. No contexto tecnológico atual, esse tipo de problema pode ser resolvido com o uso de microcontroladores, sensores de umidade, temperatura e luminosidade, além de atuadores como bombas d’água, ventiladores e LEDs.

Além disso, o sistema permite que o usuário defina os limites para ativação automática desses recursos, de acordo com as necessidades específicas das plantas ou do ambiente, trazendo maior controle e praticidade ao cuidado.



## Objetivos

Desenvolver uma mini estufa automatizada capaz de monitorar variáveis ambientais (temperatura, umidade do ar, umidade do solo e luminosidade) e acionar automaticamente sistemas de irrigação, ventilação e iluminação com base em limites configurados pelo próprio usuário, oferecendo praticidade e autonomia no cuidado com plantas.

Objetivos Específicos:
Integrar sensores ambientais ao microcontrolador ESP32 para realizar a leitura das condições internas da estufa.

Desenvolver um aplicativo Android simples, que permita ao usuário acompanhar os dados em tempo real e definir os limites para a ativação dos atuadores.

Implementar a comunicação entre aplicativo e hardware utilizando o protocolo MQTT, possibilitando controle remoto eficiente.

Automatizar a ativação da irrigação, ventilação e iluminação com base nos valores definidos pelo usuário, garantindo um cuidado inteligente e personalizado com as plantas.


 
## Público-Alvo

O projeto é voltado principalmente para estudantes, professores, hobbystas e entusiastas de tecnologia e jardinagem, que desejam aprender ou aplicar conhecimentos em automação, Internet das Coisas (IoT) e cuidados com plantas.

Os usuários deste sistema podem ter conhecimentos básicos ou intermediários em tecnologia, mas interesse em soluções práticas e acessíveis para automatizar pequenas estufas, hortas domésticas ou projetos escolares. Além disso, o sistema também pode ser útil para quem tem pouco tempo disponível, mas deseja manter um ambiente verde bem cuidado.
