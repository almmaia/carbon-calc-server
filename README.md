# AL Carbon Calculator

## Visão Geral

Backend em Java, Spring Boot e MongoDB desenvolvido para o desafio técnico de calculadora de carbono. A aplicação expõe uma API para iniciar um cálculo, registrar informações complementares e obter a pegada de carbono final. O projeto também inclui endpoint de saúde e documentação Swagger.

## Endpoints

### `POST /open/start-calc`

Recebe `name`, `email`, `phoneNumber` e `uf`, valida os campos obrigatórios, cria um novo cálculo e devolve o identificador para as próximas etapas.

### `PUT /open/info`

Recebe o `id` do cálculo e os dados de consumo de energia, transporte, resíduos sólidos e `recyclePercentage`. Se o endpoint for chamado novamente para o mesmo `id`, os dados anteriores são substituídos.

### `GET /open/result/{id}`

Busca o cálculo salvo, carrega os fatores do banco e devolve a emissão de energia, a emissão de transporte, a emissão de resíduos e o total final.

## Regras de Negócio

A emissão de energia é calculada por `energyConsumption * emissionFactor`. A emissão de transporte corresponde à soma de `monthlyDistance * factor` para cada item informado. Já os resíduos utilizam `solidWasteTotal` ponderado entre os fatores reciclável e não reciclável, conforme o valor de `recyclePercentage`.

## Execução Local

O banco de dados é inicializado com:

```bash
docker compose up -d
```

Os fatores são carregados pelo script `init-mongo.js`. Se for necessário recriar o estado inicial, execute:

```bash
docker compose down -v
docker compose up -d
```

A aplicação roda em `http://localhost:8085`, e a documentação Swagger fica disponível em `http://localhost:8085/swagger-ui.html`.

Para executar localmente, utilize Java 17, inicie o Mongo com Docker Compose e depois execute a aplicação com:

```bash
.\gradlew.bat bootRun
```

Os testes podem ser executados com:

```bash
.\gradlew.bat test
```

## Observações

Este repositório foi organizado para entregar a solução final do desafio, preservando o contrato da API e mantendo os nomes dos DTOs definidos pelo projeto original.
