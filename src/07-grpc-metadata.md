# Metadados em gRPC

## Objetivos desta aula:

- Enviar e receber metadados usando *gRPC headers*

***

## Metadados em chamada remota de procedimentos

Em chamada remota de procedimentos (RPC), a interface remota de um servidor define
os parâmetros de entrada e de saída de cada operação oferecida pelo servidor.
A implementação de cada operação do servidor (em Java, cada método) recebe 
os parâmetros de entrada e devolve os parâmetros de saída.

No caso de gRPC, esses parâmetros são definidos nas mensagens (de pedido e de resposta)
declaradas no ficheiro `.proto`.

Ora, em muitas aplicações distribuídas, é conveniente que o cliente e servidor possam 
trocar *metadados* que não fazem parte dos parâmetros definidos pela interface remota, 
*sem que tal exija alterar a interface remota nem as implementações de cada operação*.

Há diferentes situações em que a troca de metadados pode ser útil. 
Por exemplo, para autenticação, *tracing* ou *debugging*, ou balanceamento de carga. 
Para uma melhor explicação destas situações, consultar [este link](https://grpc.io/docs/guides/metadata/).

Tal como em outros RPCs, o gRPC suporta que um cliente e um servidor mantenham um *canal lateral* 
através do qual podem trocar metadados, sem que o `.proto` ou os métodos que implementam 
as operações no servidor tenham de ser modificados.

Em gRPC, cada elemento de metadados consiste num par chave-valor que é transportado como um cabeçalho (*header*) HTTP/2.
As chaves são *ASCII strings*. Os valores podem ser *ASCII strings* ou conteúdo binário.

Existe suporte para enviar e receber metadados gRPC em diferentes linguagens, incluindo Java.

Para mais detalhes, devem consultar a [documentação do gRPC](https://grpc.io/docs/guides/metadata/).


## Exercício: um *hello world* com metadados gRPC

Para ilustrar o envio e a receção de metadados em gRPC, vamos estender o exemplo 
simples de gRPC que vimos na aula de introdução ao gRPC de forma a que o cliente envie uma 
*string* adicional ao servidor. 
Esta *string* será, claro, enviada como metadados (ou seja, como um cabeçalho HTTP/2), sem precisarmos
de alterar o `.proto` ou a implementação das operações remotas.

### Antes de começar

- Comece por obter o código do [exemplo gRPC](https://github.com/tecnico-distsys/example_grpc). 

- Compile o servidor e o cliente (não se esqueça de, antes, compilar o `.proto`!) e experimente
correr o projeto para confirmar que funciona.

### Cliente a enviar metadados

- No cliente, comece por importar as *packages* `io.grpc.Metadata` e
`io.grpc.stub.MetadataUtils`, pois serão usadas nos passos seguintes.

- Defina uma chave para o cabeçalho que será enviado ao servidor. Pode escolher um nome que ache melhor para a chave, claro.

```java
static final Metadata.Key<String> MY_HEADER_KEY =
      Metadata.Key.of("my_header_key", Metadata.ASCII_STRING_MARSHALLER);
```

- Antes de usarmos o *stub*, vamos estendê-lo com um *interceptor* que tratará de, 
a cada pedido que sai, lhe adicionar o cabeçalho que desejarmos.
Para obtermos o *stub* com *interceptor, basta adicionar estas linhas imediatamente após 
termos o *stub* original criado:

```java
Metadata metadata = new Metadata();
metadata.put(MY_HEADER_KEY, "aqui substituir pela string que quiserem enviar no cabecalho!");

HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stubWithHeader = 
  stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor((metadata)));
```

- A partir deste momento, quando o cliente quiser enviar um pedido que transportará o cabeçalho, 
basta invocar o *stub* criado acima, `stubWithHeader`. 
Caso contrário, se se invocar o *stub* original (`stub`), o pedido não levará cabeçalhos adicionais.

- Experimente compilar e executar este cliente modificado. Notou alguma diferença no servidor?...


### Adicionar um *interceptor* no servidor para receber metadados

Até agora, o servidor não está ainda a tentar obter o novo cabeçalho que o cliente lhe passou a enviar. Por isso, ele está a ignorar esse novo cabeçalho. 

Vamos agora modificá-lo para que ele passe a ter também um *interceptor* que procura, 
nos pedidos que recebe, um cabeçalho com a chave pretendida. 
Como veremos já de seguida, esta parte é um pouco mais complicada.

- Para termos um *interceptor* que processa mensagens recebidas, precisamos 
definir uma classe com esse *interceptor*. 
Como ponto de partida, adicione a classe seguinte ao projeto do servidor.

```java
package pt.tecnico.grpc.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class HeaderServerInterceptor implements ServerInterceptor {

  /* TO DO: Ensure that the name of the header key to match the header key used by the sender */
  static final Metadata.Key<String> CUSTOM_HEADER_KEY =
      Metadata.Key.of("my_header_key", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      final Metadata requestHeaders,
      ServerCallHandler<ReqT, RespT> next) {
        
        String headerValue = requestHeaders.get(CUSTOM_HEADER_KEY);

        if (headerValue != null) {
          /* We found the expected header in the request message! */

          /* TO DO: do something with the header value (see some ideas in the text below). */
        }

        return next.startCall(call, requestHeaders);
  }
}
```

- Se, no código do seu cliente, alterou a chave do cabeçalho que este envia, altere-a também na classe acima.

- Para observarmos o cabeçalho a chegar ao servidor, imprima o valor do cabeçalho (`headerValue`) dentro do `if` 
no *stdout* do servidor. 
*(Na secção seguinte veremos uma alternativa mais interessante.)*

- Até agora, já tem a classe do *interceptor* mas ele ainda não é usado pelo servidor.
Para tal é preciso de instalar uma instância deste *interceptor* no serviço que é criado no `Main` do servidor.

- Para fazer isso, deve substituir a chamada ao método `addService` (no `Main` do servidor) por esta variante.

```java
.addService(ServerInterceptors.intercept(impl, new HeaderServerInterceptor())).build();
```

- Compile o servidor. Agora, experimente lançar o servidor e, depois, o cliente. 
E agora, notou alguma diferença no *stdout* do servidor? 
Se cumpriu todos os passos até este ponto, devia observar o servidor a imprimir o valor enviado pelo cliente.

## Utilizar os metadados na implementação do serviço

Já vimos como conseguimos enviar informação extra no cliente através de metadados e como podemos obter essa informação no servidor utilizando um *interceptor*.

No entanto, uma das utilidades de enviar informação extra através de metadados é conseguir utilizar essa informação na implementação do serviço do lado do servidor.

Conseguimos fazer isto adicionando esta informação através do mecanismo que o gRPC tem para representar contextos (`io.grpc.Context`).

Para tal, basta importar as seguintes *packages*:
```java
import io.grpc.Context;
import io.grpc.Contexts;
```
e estender o código da classe `HeaderServerInterceptor` com os seguintes excertos de código:

```java
public static final Context.Key<String> HEADER_VALUE_CONTEXT_KEY = Context.key("contextKey");
```

Aqui, a chave do par chave/valor a ser adicionado ao contexto será `"contextKey"`.

E as seguintes linhas à função `interceptCall`:

```java
if (headerValue != null) {
    Context context = Context.current().withValue(HEADER_VALUE_CONTEXT_KEY, headerValue);
    return Contexts.interceptCall(context, call, requestHeaders, next);
}
```

Desta forma, estamos a adicionar um novo par chave/valor ao contexto atual, onde a chave é `HEADER_VALUE_CONTEXT_KEY` e o valor associado é `headerValue`.

Depois, na implementação do método `greeting` do nosso serviço (`HelloWorldServiceImpl.java`), podemos simplesmente aceder a este valor fazendo:

```java
@Override
public void greeting(HelloWorld.HelloRequest request, StreamObserver<HelloWorld.HelloResponse> responseObserver) {

  // HelloRequest has auto-generated toString method that shows its contents
  System.out.println(request);
  String headerValue = HeaderServerInterceptor.HEADER_VALUE_CONTEXT_KEY.get();

  if (headerValue != null) {
      // do something with headerValue ...
  }
  ...
}
```

## Curiosa/o por saber mais?

O exercício acima ilustra o envio de metadados no pedido. 
No entanto, é também possível enviar metadados na resposta devolvida pelo servidor.

A documentação gRPC fornece [um outro exemplo de código](https://github.com/grpc/grpc-java/tree/master/examples/src/main/java/io/grpc/examples/header) que explora essa e outras possibilidades. 


## Passo seguinte

Aplicar esta tecnologia ao seu projeto!