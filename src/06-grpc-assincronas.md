# Invocações assíncronas em gRPC

## Objetivos desta aula:

- Fazer chamadas remotas assíncronas usando *stubs* não bloqueantes em gRPC

***

## Operações assíncronas

Até agora vimos exemplos de chamadas remotas síncronas, usando um *stub* bloqueante: o cliente faz um pedido ao servidor e fica bloqueado à espera da resposta. Caso o cliente não pretenda ficar bloqueado à espera da resposta do servidor, é possível fazê-lo através de uma **chamada assíncrona**. Neste caso o cliente faz o pedido, continua a executar, e vai receber a resposta mais tarde.

A forma de fazer chamadas assíncronas é através de um *stub* diferente, não-bloqueante, no cliente. Não é preciso alterar o servidor.

### Chamada remota assíncrona em *stub* não bloqueante

Para fazer este tipo de chamada, é passado, como argumento na chamada do cliente, um objeto de *callback* do tipo `StreamObserver`. Quando chega uma resposta, um método do objeto de *callback* (que foi passado ao *stub* quando a operação remota foi invocada) será executado.

O excerto seguinte ilustra a criação e invocação de um *stub* não bloqueante

```java
final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

// Criamos um stub não bloqueante
HelloWorldServiceGrpc.HelloWorldServiceStub stub = HelloWorldServiceGrpc.newStub(channel);

[...]

// Fazemos a chamada (assíncrona), passando um objeto de callback (HelloObserver implementa StreamObserver)
stub.greeting(request, new HelloObserver<HelloWorld.HelloResponse>());

//O programa continua a sua execução, mesmo antes de chegar alguma resposta ao pedido acima!

[...]
```

O objeto de tipo `StreamObserver` precisa implementar três métodos: `onNext` (executado quando chega uma resposta normal), `onError` (resposta de erro) e, finalmente, `onCompleted` (fim).


```java
public class HelloObserver<R> implements StreamObserver<R> {
    @Override
    public void onNext(R r) {
        //Aqui deve estar o código a executar no caso de resposta normal
    }

    @Override
    public void onError(Throwable throwable) {
        //Aqui deve estar o código a executar no caso de resposta de erro
    }

    @Override
    public void onCompleted() {
        //Aqui deve estar o código a executar no caso do final das respostas
    }
}
```

## Exercício

- Analise o código do cliente neste [exemplo do uso de gRPC com chamadas assíncronas](https://github.com/tecnico-distsys/example_grpc/tree/async).
- Experimente correr este projeto e confira o que acontece.
- Compare esse cliente com o cliente do [exemplo base, que fazia chamadas síncronas através de *stub* bloqueante](https://github.com/tecnico-distsys/example_grpc/compare/main...async).

### Vamos tornar este projeto mais interessante

- Lance 2 processos servidores, um no porto 8080 e outro no porto 8081. Para o segundo, pode fazer `mvn exec:java -Dexec.args="8081"` (e assim substitui o argumento que está definido no `pom.xml`).
- Estenda o cliente para passar a ter dois *stubs* não bloqueantes, cada um ligado a um dos dois servidores.
- Agora que tem 2 *stubs*, chame o método remoto `greeting` a ambos. Ao primeiro passe "Alice", ao segundo passe "Bob".
- Experimente executar este novo cliente e confirme que ambas as respostas são impressas assincronamente.
- No servidor, na classe `HelloWorldServiceImpl.java`, acrescente um atraso pseudo-aleatório no método greeting antes deste retornar a resposta. Sugestão: use `Thread.sleep()`, passando um número pseudo-aleatório entre 0 e 5000 (milisegundos).
- Experimente de novo lançar os dois servidores e, finalmente, o cliente. Agora deverá observar as respostas a serem impressas em ordens que podem variar de cada vez que executa o cliente.

### Ainda mais interessante...

- Agora queremos que o cliente, depois de enviar os pedidos a ambos os servidores, se bloqueie até ambas as respostas chegarem. Só nesse momento, é que ele deve imprimir ambas as respostas.
- Isto é possível fazer com *stub* bloqueante? Veja a resposta no fundo desta página [1]. De qualquer forma, cumprir este objetivo mantendo a solução que compôs até aqui, com *stubs* não bloqueantes.
- **Primeiro objetivo**: acumular as respostas num objeto comum, referenciado pelos objetos de *callback*.
    - Crie uma classe que é capaz de guardar as múltiplas respostas. Pode, por exemplo, chamá-la `ResponseCollector`.
    - Crie uma instância de `ResponseCollector` e faça com que ambos os objetos de *callback* passem a manter uma referência a ela (por exemplo, passada pelo método construtor de `HelloObserver`).
    - Altere o método `onNext` de `HelloObserver` para, em vez de imprimir a resposta, a adicionar ao objeto `ResponseCollector`.
- **Segundo objetivo**: bloquear o cliente até todas as respostas terem sido adicionadas ao objeto `ResponseCollector`.
    - Acrescente um método `waitUntilAllReceived` na classe `ResponseCollector`.
    - Este método deve bloquear até que o número de respostas acumuladas nesse objeto seja 2. Relembre as [primitivas para programação concorrente em Java](./02-java-avancado.md) e implemente a lógica bloqueante pretendida.
    - No método `Main` do cliente, após as duas chamadas assíncronas, chame o método `waitUntilAllReceived` e, quando este retornar, imprima as respostas guardadas no objeto `ResponseCollector`.
    - Experimente e confirme que a sua solução cumpre o pretendido!

### E ainda mais uma variante.

- Adapte a solução que compôs, mas agora para que o cliente só se bloqueie até chegar a primeira resposta (que chegará do servidor que responder mais cedo).

## Já resolveram?

Podem conferir a [nossa proposta de resolução](https://github.com/tecnico-distsys/example_grpc/tree/async-solution).

## Aproveite o que construiu para aplicar no seu projeto

Uma vez bem percebidos os mecanismos de chamadas assíncronas e de sincronização, pode começar a desenhar os próximos passos do projeto.

No seu projeto também tem operações que implicam enviar um pedido a múltiplos servidores e a esperar ou pelas respostas de todos, ou simplesmente pela resposta mais rápida. Quais operações correspondem a cada caso?

Se já concluiu o exercício acima, pense como pode incorporar a mesma estratégia no seu projeto.

***

[1] Sim, é possível se criarmos múltiplas threads, sendo que cada uma invoca um *stub* bloqueante. Mesmo que essas threads fiquem bloqueadas até receberem a sua resposta, a thread principal fica livre para continuar a sua execução. No entanto, esta via é tipicamente mais difícil de programar.