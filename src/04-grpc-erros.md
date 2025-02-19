# 4.  gRPC: tratamento de erros e métodos remotos bloqueantes


## Objetivos desta aula:

- Aprender a enviar e receber erros com gRPC
- Aprender a implementar métodos remotos bloqueantes

***


## Tratamento de erros com gRPC

Uma chamada remota de procedimento (RPC) pode falhar devido a problemas na rede, no servidor ou no próprio cliente.
Mesmo quando a comunicação é possível entre cliente e servidor, este último pode detetar um problema nos argumentos recebidos ou ter um problema interno que precisa de reportar ao cliente.

O gRPC reporta todos os problemas através de um código de estado de erro (*status code*) que é devolvido em situações de erro. Este mecanismo é básico e menos sofiscado, por exemplo, do que as exceções do Java. A opção de desenho deve-se ao facto do gRPC ser uma biblioteca agnóstica relativamente à linguagem de programação que se utiliza, pelo que o recurso a um conjunto limitado de códigos de erro é a forma base de tratar situações em que a invocação remota falha.

De acordo com a [documentação oficial](https://grpc.io/docs/guides/error/), existem três categorias de códigos de erro suportadas por todas as bibliotecas cliente/servidor gRPC e independentes do formato de dados:

- Erros gerais, como o cancelamento do pedido por parte do cliente ou a invocação de um método que não está implementado no servidor;
- Falhas na rede, como a situação em que apenas parte dos dados é transmitida antes de a ligação terminar;
- Erros de protocolo, como acesso não autenticado ou violação do protocolo de fluxo de controlo.


 Os código de base são suficientes em muitas situações, mas não permitem comunicar informações mais detalhadas acerca do erro em causa. Por este motivo, o gRPC tem também um conjunto mais alargado de códigos de erro, que já são definidos em *protocol buffers*.

A utilização de *protocol buffers* [permite incluir detalhes](https://github.com/googleapis/googleapis/blob/master/google/rpc/error_details.proto) que podem ser relevantes para o cliente conseguir recuperar do erro, como uma descrição textual do erro e/ou metadados. Nem todas as implementações de gRPC suportam este modelo, mas o Java, que usaremos, suporta. Felizmente, a união dos conjuntos de códigos de erro é apresentada ao programador Java através de uma classe apenas: [`io.grpc.Status`](https://github.com/grpc/grpc-java/blob/master/api/src/main/java/io/grpc/Status.java). 


## Como retornar um erro?

Após a invocação de um procedimento remoto com gRPC, a chamada pode ter sucesso ou falhar, sendo enviado para o cliente um código de erro (ou *error status code*) neste último caso.

Tome como exemplo a implementação do método `currentBoard` do [laboratório anterior](03-grpc.md):

```java
public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
    String board = ttt.currentBoard();
    CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(board).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
}
```

Em caso de erro, é importante notificar o cliente de que a execução remota falhou, para que consiga recuperar (por exemplo, repetindo a invocação ou reportando o erro ao utilizador). Esse estado de erro pode ser induzido, por exemplo, por uma falha na conexão entre cliente e servidor (como descrito anteriormente) ou introduzido pelo próprio programador, para acautelar violações do domínio da aplicação. A validação de argumentos é um exemplo clássico.

No caso de Java, é possível explicitar que a chamada remota falhou invocando `responseObserver.onError(...)` quando necessário. Este método recebe um `Throwable`, sendo qualquer exceção em Java uma subclasse desta. Esta invocação altera o fluxo de execução do programa. Quer isto dizer que, num determinado fluxo, `onCompleted` e `onError` só podem ser invocadas uma vez e, se forem, devem ser as últimas (não podendo, por isso, ser executadas em conjunto). A título de exemplo, e considerando a guarda `errorCondition`, deve ter-se algo como:

```java
    ...

    if (errorCondition) {
        ...
        responseObserver.onError(...);

    } else {
        ...
        responseObserver.onNext(...);
        responseObserver.onCompleted();
    }

    ...
````

No entanto, há que ter em conta que as exceções passadas como argumento a `onError` são automaticamente encapsuladas dentro de `StatusRuntimeException` ou `StatusException`, perdendo informação relevante sobre a sua origem/causa (uma vez que esta informação pertence exclusivamente ao domínio do servidor e não deve ser enviada ao cliente). Assim sendo, as únicas exceções que o cliente poderá receber do seu lado são do tipo `StatusRuntimeException` (que herda de `RuntimeException`) ou `StatusException` (que herda de `Exception`).

O gRPC oferece uma estrutura que permite representar o estado de erro devolvido por uma invocação remota, Status. Em Java, a classe com o mesmo nome define um código e uma descrição textual do estado de erro. Há vários códigos previstos, 
que [podem ser consultados aqui](https://github.com/grpc/grpc-java/blob/master/api/src/main/java/io/grpc/Status.java). 

Um exemplo é o código `INVALID_ARGUMENT`, para representar situações em que o cliente especificou um argumento inválido. Os códigos `NOT_FOUND`, `ALREADY_EXISTS` e `FAILED_PRECONDITION` cobrem outras situações em que a invocação remota não pode ser executada por alguma condição da lógica do domínio não estar satisfeita. A grande vantagem de usar a classe `Status` para retornar estados de erro em gRPC é que esta possui um método que encapsula automaticamente o código pretendido numa exceção, pronta a ser passada ao método onError.

Por exemplo, para que o cliente receba uma `StatusRuntimeException` (análogo para `StatusException`) ao especificar um argumento inválido, o servidor pode invocar:

```java
        ...

        responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());

        ...
```


### Como adicionar informação ao erro?

Como discutido anteriormente, é ainda possível passar mais informação ao cliente acerca do erro em causa. Por exemplo, para passar uma descrição textual do erro, o servidor pode invocar:

```java
        ...

        responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid input!").asRuntimeException());

        ...
```

### Como captar o erro no cliente?

Para captar o erro do lado do cliente, basta introduzir um bloco `try-catch` para uma exceção do tipo `StatusRuntimeException`, que oferece o método `getStatus`, que devolve a instância de `Status` previamente encapsulada. Supondo, então, que existe, do lado do cliente, um stub, imprimir o código é simples:

```java
    ...
    try {
        CurrentBoardResponse response = stub.currentBoard(request);

    } catch (StatusRuntimeException e) {
        Status status = e.getStatus();
        System.out.println(status.getDescription());
    }
    ...
```

Atente-se que a descrição que se obtém ao invocar `status.getDescription()` é igual à descrição que foi passada no servidor em `Status.INVALID_ARGUMENT.withDescription(desc).asRuntimeException()`.

 


## Exercício

O ponto de partida será a solução construída pelo seu grupo na [aula anterior para o Jogo do Galo em gRPC](./03-grpc.md).

O objetivo deste novo exercício é estender essa solução de modo a ser devolvido um erro caso um pedido de jogada leve argumentos inválidos, assim como adicionar-lhe alguns testes unitários.

Vamos então começar!

### Apanhar erros simples de comunicação

Experimente as seguintes situações.

1. Lançar o cliente sem que o servidor tenha ainda sido lançado.

2. Lançar o servidor; lançar o cliente; realizar uma jogada (com sucesso); desligar o servidor; tentar realizar nova jogada (agora sem sucesso).

Em casa cenário acima, que exceções foram apanhadas pelo cliente?

### Enviar informação de erro do servidor para o cliente

Vamos agora adicionar um retorno de erro ao servidor caso a mensagem do pedido seja com uma jogada fora do tabuleiro. Relembramos que a operação play recebe o nome do jogador, e a coluna e a linha em que o mesmo pretende fazer umas jogada.

- Comece por ler os materiais sobre o [tratamento de erros com gRPC](https://grpc.github.io/grpc/core/md_doc_statuscodes.html).

- Vamos agora estender a sua solução. No servidor, comece por importar a definição de um estado de erro para argumentos inválidos:

    ```java
    import static io.grpc.Status.INVALID_ARGUMENT;
    ...
    ```

    Verifique se a jogada está fora do tabuleiro e, em caso afirmativo, devolver o erro.

    ```java
    ...
    PlayResult result = ttt.play(row, column, player);

    if (result == PlayResult.OUT_OF_BOUNDS){
        responseObserver.onError(INVALID_ARGUMENT.withDescription("Input has to be a valid position").asRuntimeException());
    }
    else{
        // Send a single response through the stream.
        PlayResponse response = PlayResponse.newBuilder().setPlay(result).build();
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    } 
    ...
    ```

- Do lado do cliente, deve apanhar uma exceção e imprimir a mensagem de erro:

    ```java
    play_res = null;
    ...
    try{
        play_res =  stub.play(PlayRequest.newBuilder().setRow(row).setColumn(column).setPlayer(player).build()).getPlay();
        if (play_res != PlayResult.SUCCESS) {
            displayResult(play_res);
        }
    }
    catch (StatusRuntimeException e) {
        System.out.println("Caught exception with description: " + 
            e.getStatus().getDescription());
    } 
    ```

### Implementar um método bloqueante

Vamos agora adicionar uma variante bloqueante da operação checkWinner.

- No ficheiro `.proto`, acrescente uma nova operação chamada `waitForWinner`, cujas mensagens de pedido e respostas são idênticas às da operação `checkWinner`. A grande diferença é que a `waitForWinner` deve bloquear-se enquanto o jogo não tiver terminado.
- Depois de gerar os novos *stubs*, crie o método associado à operação `waitForWinner` e acrescente-o à classe do servidor.
- Relembre as [primitivas para programação concorrente em Java](./02-java-avancado.md).
- No novo método, use a primitiva `wait()` para, enquanto o jogo não tenha ainda terminado, a *thread* que executa esse método se bloquear. Lembre-se que, para chamar `wait()`, precisa estar dentro de um método (ou bloco) synchronized.
- Precisa também chamar `notifyAll()` sempre que o estado do jogo muda com uma nova jogada.
- Finalmente, estenda o cliente para também invocar esta nova operação.
- Experimente! Lance um cliente que fará as jogadas. Em paralelo, lance outro cliente que simplesmente invoca `waitForWinner`.

## Já resolveram?

Podem conferir a nossa proposta de resolução.

Nota: esta solução resolve o conjunto dos exercícios deste guião e do anterior.