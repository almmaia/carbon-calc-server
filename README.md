# AL Carbon Calculator

## Visão Geral

Backend em Java, Spring Boot e MongoDB desenvolvido para o desafio técnico de calculadora de carbono. O sistema expõe uma API para iniciar um cálculo, registrar informações complementares e obter a pegada de carbono final. A aplicação também disponibiliza um endpoint de saúde e a documentação Swagger.

## Endpoints

`POST /open/start-calc` recebe `name`, `email`, `phoneNumber` e `uf`, valida os campos obrigatórios, cria um novo cálculo e devolve o identificador para as próximas etapas.

`PUT /open/info` recebe o `id` do cálculo e os dados de consumo de energia, transporte, resíduos sólidos e `recyclePercentage`. Se esse endpoint for chamado novamente para o mesmo `id`, os dados anteriores são substituídos.

`GET /open/result/{id}` busca o cálculo salvo, carrega os fatores do banco e devolve a emissão de energia, a emissão de transporte, a emissão de resíduos e o total final.

## Regras de Negócio

Energia é calculada por `energyConsumption * emissionFactor`. Transporte é a soma de `monthlyDistance * factor` para cada item informado. Resíduos utilizam `solidWasteTotal` ponderado entre os fatores reciclável e não reciclável conforme o valor de `recyclePercentage`.

## Execução

O banco de dados é inicializado com `docker compose up -d`, e os fatores são carregados pelo script `init-mongo.js`. Se for necessário recriar o estado inicial, basta executar `docker compose down -v` e subir novamente.

A aplicação roda em `http://localhost:8085`, e a documentação Swagger fica em `http://localhost:8085/swagger-ui.html`.

Para executar localmente, utilize Java 17, inicie o Mongo com Docker Compose e depois execute a aplicação com `.\gradlew.bat bootRun`. Os testes podem ser executados com `.\gradlew.bat test`.

## Observações

Este repositório foi organizado para entregar a solução final do desafio, preservando o contrato da API e mantendo os nomes dos DTOs definidos pelo projeto original.
