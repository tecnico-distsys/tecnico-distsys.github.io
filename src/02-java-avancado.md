# 2.  Java avançado (exceções, concorrência e troca de mensagens)

## Objetivos desta aula:

- Programação concorrente em Java
- Exceções em Java
- Troca de mensagens usando sockets em Java

***



## Programação concorrente em Java

A linguagem Java permite a programação de programas concorrentes com múltiplas *threads*.

***TODO: threads em java***


Como as *threads*  partilham objetos, que os seus dados estão coerentes.
Caso a sincronização não seja implementada, pode ocorrer interferência entre *threads*, levando a situações de incoerência nos dados partilhados.
Por outro lado, a presença de mecanismos de sincronização pode originar contenção quando duas ou mais *threads* tentam aceder ao mesmo recurso em simultâneo.

 
### Sincronização em Java

De seguida resumimos as principais primitivas de sincronização disponíveis em Java.
Para saber mais sobre este tema, [recomendamos que consulte este tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html).

Cada objeto Java tem um trinco lógico (*mutex*) implícito, que pode ser (implicitamente) adquirido através da primitiva synchronized.

[A primitiva `synchronized` pode ser aplicada a métodos da classe de um objeto.](https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html) Ao fazermos isso, qualquer chamada a um método de uma instância dessa classe é executada em exclusão mútua (em relação a outras chamadas concorrentes a outros métodos synchronized da mesma instância).
Um método sincronizado adquire o trinco implícito do objeto no início de execução e liberta-o no fim.

O seguinte exemplo mostra uma utilização da sincronização em Java:
 
 ```java
public class MySynchronizedCounter {
    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }
}
```

Os métodos `increment()`, `decrement()` e `value()` da classe `MySynchronizedCounter` estão sincronizados. Isto significa que, caso um dos três métodos seja invocado, o trinco do objeto é adquirido, e se houver outra tarefa a tentar aceder a qualquer um deles, ficará bloqueada à espera que o recurso/método seja libertado pela primeira invocação.

Também é possível usar o synchronized para adquirir o trinco apenas numa parte do código.

```java
    // acquire lock of object referenced by 'this'
    synchronized (this) {

        // access shared variables protected by lock

    }
    // release lock
```

No contexto de uma região `synchronized`, é também possível [utilizar variáveis de condição, usando as primitivas `wait`, `notify` e `notifyAll`](https://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html).

 
### Concorrência com coleções

As coleções são objetos que armazenam vários outros objetos. Algumas das coleções mais conhecidas no Java são: `ArrayList`, `LinkedList`, `HashMap`, `HashSet`, `TreeMap`, `TreeSet`, etc.
Cada coleção implementa uma interface que dita o tipo de acesso esperado:

- `List` é uma lista (preserva a ordem e pode haver repetidos);
- `Set` é um conjunto (sem ordem nem repetidos);
- `Map` estabelece uma associação entre chave e valor;
- etc.

Por omissão, as coleções não são sincronizadas, pois assim conseguem melhor desempenho sequencial.

Para situações em que é necessário sincronizar os acessos às coleções, existem versões sincronizadas que apenas permitem um acesso de cada vez.
Para construir uma coleção sincronizada usa-se um método especial da classe `Collections`, que cria a nova coleção "embrulhando" uma coleção do mesmo tipo:

```java
    List synchronizedList = Collections.synchronizedList(regularList);
```

Posteriormente, foram acrescentadas ao Java [coleções concorrentes](https://docs.oracle.com/javase/tutorial/essential/concurrency/collections.html) (*Concurrent Collections* no pacote `java.util.concurrent`), que utilizam estruturas de dados sofisticadas, desenhadas de raiz para garantir a consistência da coleção mesmo quando esta é acedida concorrentemente por muitas tarefas. Exemplos destas coleções concorrentes são: `ConcurrentHashMap`, `CopyOnWriteArrayList`, e `CopyOnWriteHashSet`.
No exemplo seguinte constroi-se um mapa optimizado para acessos concorrentes:

```java
    Map<String,Object> map = new ConcurrentHashMap<>();
```

***

## Exceções

O tratamento de exceções é também um aspeto muito importante, e que irá ser especialmente importante para lidar com problemas de comunicação.

As exceções são usadas na linguagem Java para assinalar que algo não correu como esperado.
São classes que herdam de `java.lang.Exception` e cujos objetos podem ser atirados (*throw*) e apanhados (*caught*).

As exceções que herdam de `java.lang.RuntimeException` (RTE) são chamadas exceções não verificadas (*unchecked exceptions*). Neste caso, o compilador não obriga o programador a declarar se apanha ou se atira. Por este motivo, qualquer linha de código pode atirar uma exceção destas. A mais conhecida é a `NullPointerException` (NPE).

As exceções que herdam de `java.lang.Exception` são chamadas exceções verificadas (*checked exceptions*), no sentido, em que a sua utilização é verificada pelo compilador. Nestes casos é preciso explicitar se se apanha a exceção (*catch*) ou se se lança (*throws*). Normalmente, se não se vai tentar recuperar a exceção, pode simplesmente dizer que se atira. É preferível atirar do que fazer um falso tratamento de exceção.

 
### Abordagens ao tratamento de exceções

Podemos ter as seguintes abordagens em relação às exceções:

- Deixar-passar (pass-through)
- Apanhar-e-tratar (catch-and-handle)
  - Apanhar-e-ignorar! (swallow!)
  - Apanhar-registar-e-atirar (logging)
  - Apanhar-embrulhar-e-atirar (wrapping)
  - Apanhar-e-recuperar (recovery)

A abordagem 'apanhar-e-ignorar' é claramente errada porque perde informação e torna muito mais difícil diagnosticar e resolver problemas.

 
### Exceções impressas na consola

Os outputs seguintes foram produzidos por uma aplicação que usa sockets para comunicação, e ilustram diversas situações.

Na situação abaixo, o servidor tenta criar o socket com um porto fora do intervalo [0;65535].

```java
java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.codehaus.mojo.exec.ExecJavaMojo$1.run(ExecJavaMojo.java:293)
	at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.IllegalArgumentException: Port value out of range: 65536
	at java.net.ServerSocket.<init>(ServerSocket.java:232)
	at java.net.ServerSocket.<init>(ServerSocket.java:128)
	at example.SocketServer.main(SocketServer.java:24)
    ... 6 more
```

Na situação abaixo, o cliente tenta ligar ao servidor por meio dum porto incorreto e não consegue.

```java
java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.codehaus.mojo.exec.ExecJavaMojo$1.run(ExecJavaMojo.java:293)
	at java.lang.Thread.run(Thread.java:745)
Caused by: java.net.ConnectException: Connection refused: connect
	at java.net.DualStackPlainSocketImpl.connect0(Native Method)
	at java.net.DualStackPlainSocketImpl.socketConnect(DualStackPlainSocketImpl.java:79)
	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:345)
	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
	at java.net.PlainSocketImpl.connect(PlainSocketImpl.java:172)
	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
	at java.net.Socket.connect(Socket.java:589)
	at java.net.Socket.connect(Socket.java:538)
	at java.net.Socket.<init>(Socket.java:434)
	at java.net.Socket.<init>(Socket.java:211)
	at example.SocketClient.main(SocketClient.java:32)
    ... 6 more
```

As linhas mais importantes dos outputs são as começadas por *Caused by*. É aí que se podem encontrar as mensagens das exceções. Dado que uma exceção pode ter outra exceção aninhada (*cause*) pode ser necessário consultar várias linhas para perceber o que causou a exceção de topo.

As linhas começadas por at indicam o contexto de execução. Observando estas linhas é possível ver o conteúdo da pilha de execução do programa, que diz que parte do código estava a chamar que outra parte.

 
### Código de tratamento de exceções

O seguinte código mostra **maus exemplos** de tratamento de exceções.

```java
    // ANTI-padrão 'apanhar-e-ignorar!'
    // Ignorar a exceção sem dizer nada a ninguém.
    // Evitar! 
    try {
        doSomething();
        
    } catch(Exception e) {
    }
    
    ...
    
    // ANTI-padrão 'apanhar-imprimir-e-ignorar!' 
    // Imprimir o stack trace, não resolve nada.
    // O programa vai "rebentar" mais à frente, onde será mais difícil perceber porquê.
    // Evitar também! 
    try {
        doSomething();
        
    } catch(Exception e) {
        e.printStackTrace();
    }
```

Ignorar a exceção torna muito mais difícil detetar e corrigir erros no código!

 

Vamos então ilustrar alguns **bons exemplos**:

```java
    // padrão 'deixar-passar'
    // Se a exceção não vai ser tratada, mais vale lançá-la (throws)
    public static void main(String[] args) throws Exception {
        doSomething();
    }

    ...
    
    // padrão 'apanhar-imprimir-e-atirar'
    // Registar onde foi apanhada a exceção, mas voltar a atirá-la para que seja tratada depois
    try {
        doSomething();
    
    } catch(MyException e) {
        System.err.println("Caught exception when doing something: " + e);
        System.err.println("Rethrowing"):
        throw e;
    }
    
    
    // padrão 'apanhar-embrulhar-e-atirar'
    // Apanhar exceção da camada inferior
    // Envolver com mais contexto (novo tipo, mensagem de erro melhor)
    // Atirar
    try {
        doSomething();
    
    } catch(MyLowerLevelException e) {
        System.err.println("Caught exception when doing something: " + e);
        System.err.println("Wrapping and throwing, adding meaningful message")
        throw new MyHigherLevelException("Failed to do something.", e);
    }
```
 
*** 

## Comunicação usando *sockets* em Java

O Java disponibiliza uma [biblioteca de sockets](https://docs.oracle.com/javase/tutorial/networking/sockets/index.html) que está disponível no pacote `java.net`.

Os sockets definem uma interface de programação, mas não definem o conteúdo e significado das mensagens que vão ser trocadas. Para isso é necessário um protocolo de comunicação. Um protocolo é um sistema de regras que define uma convenção para permitir que diferentes entidades troquem informação de forma não ambígua. Assim tem que ser na comunicação em sockets. É preciso "dizer" como é enviado um pedido, quando termina o pedido, quando chega a resposta, quando já foi recebida, e assim por diante.

Um exemplo de protocolo é o HTTP (*HyperText Transfer Protocol*), que está na base da comunicação na WWW (*World Wide Web*).
Pode consultar também a Secção 1.6 da bibliografia principal cadeira (Coulouris et al.) sobre a World Wide Web e Sockets. 

***


## Exercício a resolver até ao fim da aula

O ponto de partida para o exercício ilustra a comunicação entre dois programas Java usando a biblioteca de sockets:
[Java Sockets](https://github.com/tecnico-distsys/exercise_java-sockets)

1. Obter o código: fazer *Clone or Download*. Temos dois programas que colaboram entre si: servidor e cliente.
    - Estudar o código fonte e os ficheiros pom.xml do servidor e do cliente
    - Configurar os dois projetos no Java IDE
    - Compilar e executar primeiro o servidor e depois o cliente, seguindo as instruções no ficheiro `README`

**Problemas**? Observar atentamente as exceções produzidas.

2. Analisar o output do Maven, em especial as linhas começadas por [WARNING]:
    - Qual foi a causa da exceção?
    - Que exceção foi lançada?
    - Em que linha do código do cliente é que foi lançada a exceção?
        - Será um problema na configuração dos argumentos?

3. Compilar e executar o servidor até funcionar sem erros.
    - Em casos mais complicados, pode usar-se o depurador (debugger).
    - Problema resolvido? [Sim](http://www.phdcomics.com/comics/archive.php?comicid=180) ou [Não](https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Keep_Calm_and_Carry_On_Poster.svg/683px-Keep_Calm_and_Carry_On_Poster.svg.png) :)

4. Modificar os programas para que o servidor responda ao cliente com uma mensagem de confirmação.




