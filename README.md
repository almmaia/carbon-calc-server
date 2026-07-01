# AL Carbon Calculator

## Visao Geral

Backend em Java, Spring Boot e MongoDB para o desafio tecnico de calculadora de carbono. O sistema expoe uma API para iniciar um calculo, registrar informacoes complementares e obter a pegada de carbono final. A aplicacao tambem inclui um endpoint de saude e documentacao Swagger.

## Fluxo da API

O fluxo comeca em `POST /open/start-calc`. Esse endpoint recebe `name`, `email`, `phoneNumber` e `uf`, valida os campos obrigatorios, cria um novo calculo e devolve o identificador para as proximas etapas.

Em seguida, `PUT /open/info` recebe o `id` do calculo e os dados de consumo de energia, transporte, residuos solidos e `recyclePercentage`. Se esse endpoint for chamado novamente para o mesmo `id`, os dados anteriores sao substituidos.

Por fim, `GET /open/result/{id}` busca o calculo salvo, carrega os fatores do banco e devolve a emissao de energia, a emissao de transporte, a emissao de residuos e o total final.

## Regras de Negocio

As regras seguem as formulas do desafio. Energia e calculada por `energyConsumption * emissionFactor`. Transporte e a soma de `monthlyDistance * factor` para cada item informado. Residuos usam `solidWasteTotal` ponderado entre os fatores reciclavel e nao reciclavel conforme o valor de `recyclePercentage`.

## Execucao

O banco de dados e inicializado com `docker compose up -d` e os fatores sao carregados pelo script `init-mongo.js`. Se for necessario recriar o estado inicial, basta executar `docker compose down -v` e depois subir novamente.

A aplicacao roda em `http://localhost:8085` e a documentacao Swagger fica em `http://localhost:8085/swagger-ui.html`.

Para executar localmente, use Java 17, rode o Mongo com Docker Compose e depois inicie a aplicacao com `.\gradlew.bat bootRun`. Os testes podem ser executados com `.\gradlew.bat test`.

## Observacoes

Este repositorio foi organizado para entregar a solucao final do desafio, preservando o contrato da API e mantendo os nomes dos DTOs definidos pelo projeto original.
