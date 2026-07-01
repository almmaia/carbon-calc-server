# AL Carbon Calculator

## Visao Geral

Backend em Java, Spring Boot e MongoDB desenvolvido para o desafio tecnico de calculadora de carbono. O sistema expoe uma API para iniciar um calculo, registrar informacoes complementares e obter a pegada de carbono final. A aplicacao tambem disponibiliza um endpoint de saude e a documentacao Swagger.

## Endpoints

`POST /open/start-calc` recebe `name`, `email`, `phoneNumber` e `uf`, valida os campos obrigatorios, cria um novo calculo e devolve o identificador para as proximas etapas.

`PUT /open/info` recebe o `id` do calculo e os dados de consumo de energia, transporte, residuos solidos e `recyclePercentage`. Se esse endpoint for chamado novamente para o mesmo `id`, os dados anteriores sao substituidos.

`GET /open/result/{id}` busca o calculo salvo, carrega os fatores do banco e devolve a emissao de energia, a emissao de transporte, a emissao de residuos e o total final.

## Regras de Negocio

Energia e calculada por `energyConsumption * emissionFactor`. Transporte e a soma de `monthlyDistance * factor` para cada item informado. Residuos utilizam `solidWasteTotal` ponderado entre os fatores reciclavel e nao reciclavel conforme o valor de `recyclePercentage`.

## Execucao

O banco de dados e inicializado com `docker compose up -d`, e os fatores sao carregados pelo script `init-mongo.js`. Se for necessario recriar o estado inicial, basta executar `docker compose down -v` e subir novamente.

A aplicacao roda em `http://localhost:8085`, e a documentacao Swagger fica em `http://localhost:8085/swagger-ui.html`.

Para executar localmente, utilize Java 17, inicie o Mongo com Docker Compose e depois execute a aplicacao com `.\gradlew.bat bootRun`. Os testes podem ser executados com `.\gradlew.bat test`.

## Observações

Este repositorio foi organizado para entregar a solucao final do desafio, preservando o contrato da API e mantendo os nomes dos DTOs definidos pelo projeto original.
