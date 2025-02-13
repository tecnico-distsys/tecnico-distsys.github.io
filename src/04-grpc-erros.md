# 4.  gRPC: tratamento de erros e métodos remotos bloqueantes


## Objetivos desta aula:

- Aprender a enviar e receber erros com gRPC
- Aprender a implementar métodos remotos bloqueantes

## Materiais de apoio à aula

- Tratamento de erros com gRPC Concorrência e Sincronização em Java

***


## Exercício

O ponto de partida será a solução construída pelo seu grupo na [aula anterior para o Jogo do Galo em gRPC](./03-grpc.md).

O objetivo deste novo exercício é estender essa solução de modo a ser devolvido um erro caso um pedido de jogada leve argumentos inválidos, assim como adicionar-lhe alguns testes unitários.

Vamos então começar!

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