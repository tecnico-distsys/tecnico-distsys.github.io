# 5.  gRPC: multi-linguagem


## Objetivos desta aula:

- Desenvolvimento de aplicações distribuídas com gRPC com múltiplas linguagens de programação.
- Em particular, desenvolvimento de um servidor de nomes que permite publicar e pesquisar serviços gRPC no servidor de nomes

## Materiais de apoio à aula

- Tutorial de introdução ao [gRPC em Python](https://grpc.io/docs/languages/python/basics/)

## Pré-requisitos

- Python 3.5+
- Packages: grpcio, grpcio-tools e venv

## Instalação das packages

- Windows:
    - Correr o seguinte comando para criar um ambiente virtual:

        `python -m venv .venv`
    
    - Correr o comando para ativar o ambiente virtual:

        `.venv\Scripts\activate`
    
    - Correr o comando para instalar a package `grpcio`:

        `python -m pip install grpcio`

    - Correr o comando para instalar a package `grpcio-tools`:
    
        `python -m pip install grpcio-tools`
    
    - Correr o comando para desativar o ambiente virtual:

        `deactivate`

- Linux:
    - Correr o seguinte comando para criar um ambiente virtual:

        `python -m venv .venv`

    - Correr o comando para ativar o ambiente virtual:

        `source .venv/bin/activate`

    - Correr o comando para instalar a package `grpcio`:

        `python -m pip install grpcio`

    - Correr o comando para instalar a package `grpcio-tools`:

        `python -m pip install grpcio-tools`

    - Correr o comando para desativar o ambiente virtual:

        `deactivate`

***

## Java vs Python gRPC

- Começe por fazer Clone ou Download do código fonte do [exemplo de gRPC multi-linguagem](https://github.com/tecnico-distsys/example_grpc_multilanguage).

- Crie um ambiente virtual na diretoria base seguindo as instruções dadas acima.

- Na diretoria `contract`, compile e execute os seguintes comandos:
    ```bash
    mvn install
    mvn exec:exec
    ```

    - Assegure-se que, na sua máquina, o interpretador Python é lançado pelo comando que está indicado na tag `executable` no POM. Se não for, corrija o valor nessa tag e corra o último comando de novo.

- Analise a diretoria `generated-sources/protobuf` e o código gerado nas diretorias `java` e `python`.

- Teste o servidor, executando na diretoria `java_server` o comando `mvn compile exec:java`.

- Teste o cliente, executando na diretoria `python_client` o comando python `client.py`.

- Analise as diferenças e as semelhanças entre os dois clientes java (na pasta `java_client`) e python (na pasta `python_client`):

    - Criação de stubs:

        - Java:
            ```java
            final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub = HelloWorldServiceGrpc.newBlockingStub(channel);
            ```

        - Python:
            ```python
            with grpc.insecure_channel('localhost:8080') as channel:
                stub = pb2_grpc.HelloWorldServiceStub(channel)
            ```

    - Chamadas aos procedimentos remotos:

        - Java:
            ```java
            HelloWorld.HelloRequest request = HelloWorld.HelloRequest.newBuilder().setName("friend").build();
            HelloWorld.HelloResponse response = stub.greeting(request);
            ```

        - Python:
            ```python
            response = stub.greeting(pb2.HelloRequest(name='friend'))
            ```

- Termine agora o servidor java e teste o servidor python na pasta `python_server` correndo o comando `python HelloServer.py 8080`. Corra ambos os clientes java e python.

- Analise as diferencças e as semelhanças entre os dois servidores java (na pasta `java_server`) e python (na pasta `python_server`):

    - Adição do serviço ao servidor:
        - Java:
            ```java
            Server server = ServerBuilder.forPort(port).addService(impl).build();;
            ```
        - Python:
            ```python
            pb2_grpc.add_HelloWorldServiceServicer_to_server(HelloWorldServiceImpl(), server)
            ```

    - Acesso aos campos dos pedidos:

        - Java:
            ```java
            List hobbies = request.getHobbiesList();
            ```

        - Python:
            ```python
            hobbies = request.hobbies
            ```

### Sobre a compilação do proto para Python

O comando descrito abaixo gera 2 ficheiros .py na indicada: o `_pb2.py` e o `_pb2_grpc.py` com classes que representam os tipos de dados das mensagens e com classes de suporte ao servidor e ao cliente do RPC. Nos exemplos deste guião a compilação é automatizada com o Exec Maven Plugin.

```bash
python -m grpc_tools.protoc -I<pasta-para-o-contrato> --python_out=<diretoria-output> --grpc_python_out=<diretoria-output> <protos-para-compilar>
```

## Exercício

Aplique o que aprendeu acima para resolver o requisito multi-linguagem do projeto.