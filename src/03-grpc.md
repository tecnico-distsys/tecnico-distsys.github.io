# 3.  gRPC: conceitos base


## Objetivos desta aula:

- Distribuir uma aplicação originalmente centralizada usando o gRPC
- Descrever, em detalhe, os componentes do sistema gRPC

## Materiais de apoio à aula

- [Introdução ao gRPC](./resources/grpc-slides-labs.pdf)
- [Tutorial de gRPC para Java](https://grpc.io/docs/tutorials/basic/java.html)
- [Documentação de Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview)
- [API de gRPC para Java](https://grpc.io/grpc-java/javadoc/index.html)

***


## Antes de começar a programar com gRPC

Comece por folhear os [slides de introdução ao gRPC](./resources/grpc-slides-labs.pdf). Como são sucintos, é natural que suscitem algumas dúvidas. O exercício seguinte ajudará a esclarecê-las, assim como os materiais de apoio listados acima. E, claro, pode sempre esclarecer qualquer questão contactando os docentes (em aula, horário de dúvidas, ou moodle).

## Exercício a resolver até ao fim da aula

Neste exercício iremos transformar uma implementação do Jogo do Galo (Tic Tac Toe) numa aplicação distribuída utilizando o gRPC.

![Tic Tac Toe](./images/ttt.png)

### I. Começar por uma implementação local do Jogo do Galo/Tic Tac Toe.

- Faça Clone ou Download do [código fonte do jogo](https://github.com/tecnico-distsys/example_ttt)
- Analise o código do jogo de forma a compreender a implementação.
- Compile e execute o código com o comando: `mvn compile exec:java`

### II. Estudar a tecnologia gRPC.

Pretende-se que a nova versão da aplicação seja dividida em dois processos: servidor e cliente, através do gRPC. Para tal, vamos começar por estudar a tecnologia gRPC.

- Faça Clone ou Download do código fonte do [exemplo gRPC](https://github.com/tecnico-distsys/example_grpc)
- Veja como a aplicação está estruturada em três módulos: `contract`, `server` e `client`.
- Cada módulo tem um POM próprio.

Nos passos seguintes, vamos compilar e executar o exemplo seguindo as instruções `README.md` de cada módulo.

- Comece pelo módulo contract, executando o comando: `mvn install`. Este comando vai passar pela etapa *generate-sources*, que vai invocar o *protoc*, o compilador de *protocol buffers* que vai gerar código Java para lidar com os tipos de dados descritos no ficheiro .proto. 
- Familiarize-se com o código e responda às seguintes questões:
    - Onde estão definidas as mensagens trocadas entre o cliente e o servidor?
    - Onde estão definidos os procedimentos remotos no servidor?
    - Onde estão os ficheiros gerados pelo compilador de *Protocol Buffers*?
    - Onde são feitas as invocações remotas no cliente?
    - As invocações remotas são síncronas (bloqueantes) ou assíncronas?
- Abra uma consola, entre na diretoria do módulo `server` e corra o servidor: `mvn compile exec:java`
- Abra uma outra consola, entre na diretoria do módulo `client` e execute o cliente: `mvn compile exec:java`
- Depois de ver o `Hello World` a funcionar corretamente no seu computador, avance para o passo seguinte.

### III. Transformar o Jogo do Galo numa aplicação cliente-servidor com gRPC

A aplicação distribuída será organizada em três módulos. À semelhança do exemplo, o contrato irá definir a interface remota, com detalhes sobre as mensagens a trocar. O servidor irá manter o estado do jogo (tabuleiro). O cliente irá ter a interface utilizador na consola.

- Faça Clone ou Download do [código inicial do exercício](https://github.com/tecnico-distsys/exercise_ttt-grpc)

- Baseando-se no módulo contract da aplicação de exemplo, modifique o ficheiro .proto com as definições necessárias para as chamadas remotas de procedimentos `currentBoard`, `play` e `checkWinner`.
    - Sugestão: consulte a documentação dos [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview).
    - Declare todas as mensagens de pedido e resposta para cada procedimento do jogo. Note que algumas mensagens podem ser vazias, mas devem ser declaradas na mesma.
    - Cada campo deve ter uma etiqueta numérica única.
    - Complete o serviço TTT com as definições dos procedimentos que definiu (assim como as mensagens que definiu).
        - Instale o módulo com o comando `mvn install`.
        - Analise o código Java gerado na pasta `target/generated-sources/`.
            - Onde estão definidas as mensagens?
            - E os procedimentos?

- Baseando-se no módulo server da aplicação de exemplo, modifique o código inicial do módulo `server`.
    - Confirme que o módulo contract é uma dependência do projeto.
    - Modifique a classe `TTTServiceImpl` de forma a implementar os procedimentos remotos declarados no contrato, utilizando a classe `TTTGame` (que implementa a lógica do jogo) definida no código base. A classe de implementação do serviço estende a classe do serviço definido no contrato e faz *override* dos procedimentos declarados no contrato.

    Exemplo de um método:

    ```java
    public class TTTServiceImpl extends TTTGrpc.TTTImplBase {
        private TTTGame ttt = new TTTGame();

        @Override
        public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
            CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    ```

    Relembre a mensagem definida no contrato:

    ```java
    message CurrentBoardRequest {
        // No arguments for this request.
    }
    message CurrentBoardResponse {
        string board = 1;
    }		
    ```

- Confirme que a classe `TTTServer` inicia um servidor numa porta que recebe como argumento, instanciando a classe de implementação do serviço. Tenha em conta que o acesso a variáveis partilhadas tem de ser sincronizado.

    - Porque é que esta sincronização é necessária?
    - Onde é que há possibilidade de concorrência?

- Lance o servidor: `mvn compile exec:java`

- Por fim, complete o código do módulo `client`.

    - Confirme que o módulo `contract` é uma dependência do projeto. Confirme que a classe `TTTClient` instancia um stub do serviço `TTT` (através de um endereço e porta recebidos como argumentos).
    - Adicione as chamadas remotas aos procedimentos `play` e `checkWinner` que estão em falta.

    Exemplo de chamada local:

    ```java
    winner = ttt.checkWinner();
    ```

    Exemplo de chamada remota correspondente:

    ```java
    winner = stub.checkWinner(CheckWinnerRequest.getDefaultInstance()).getResult();
    ```

- Experimente jogar remotamente através do cliente construído: `mvn compile exec:java`

## Já resolveram?

Podem conferir a [nossa proposta de resolução](https://github.com/tecnico-distsys/exercise_ttt-grpc_solution).

Nota: esta solução resolve também o exercício do [próximo guião](./04-grpc-erros.md).