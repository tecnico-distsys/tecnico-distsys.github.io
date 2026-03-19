# Criptografia

## Objectivos

- Utilizar os mecanismos criptográficos da plataforma Java
- Concretizar um mecanismo de segurança para um gRPC
- Utilizar os mecanismos criptográficos da Java Cryptography Extension numa troca de mensagens sobre gRPC

***

## Segurança e criptografia em Java

A plataforma Java disponibiliza um conjunto abrangente de classes para usar mecanismos criptográficos e canais seguros, permitindo o desenvolvimento de aplicações fiáveis em ambientes locais e distribuídos. Atualmente as funcionalidades fundamentais encontram-se no JDK, dentro da Java Cryptography Architecture.

- **Java Cryptography Architecture (JCA)** – fornece os mecanismos criptográficos base, incluindo cifras simétricas e assimétricas, geração e gestão de chaves, funções de resumo (hash) e assinaturas digitais. Pode aceder a um [pequeno exemplo](https://github.com/tecnico-distsys/example_crypto) da implementação destes mecanismos em Java.

## Exercício

Neste exercício vamos acrescentar segurança a uma aplicação distribuída que usa gRPC. Nomeadamente, vamos garantir integridade da comunicação entre servidor e cliente.


### Fornecedor gRPC / Supplier

O ponto de partida para o exercício é um serviço fornecedor de produtos para venda. O cliente contacta o servidor, chamando a operação remota ```listProducts```, e o servidor responde com uma lista de produtos. Pode aceder à implementação base [aqui](https://github.com/tecnico-distsys/exercise_supplier-grpc).
1. Faça Clone or Download do ponto de partida no repositório cliente-servidor que usa gRPC GitHub.
2. Comecemos pela pasta ```contract```, onde vamos executar o comando ```mvn install```.
3. De seguida, vá à pasta ```server``` e execute o comando ```mvn compile exec:java -Ddebug```.
4. Por fim, na pasta ```client``` execute o comando ```mvn compile exec:java -Ddebug```.
5. Verifique que o cliente recebe a lista de produtos do servidor.

### Criação e distribuição de chaves

Num mundo real, a lista devolvida pelo servidor ao cliente pode ser intercetada e modificada por um atacante. É necessário acrescentar uma assinatura para proteger a resposta do servidor. Vamos fazer uma assinatura digital. O servidor utilizará a sua chave privada para assinar e o cliente verificará a assinatura com a chave pública do servidor. 

Vamos começar por criar um par de chaves RSA, que é um algoritmo de criptografia assimétrica amplamente utilizado para assinaturas digitais. Para tal vamos usar o *OpenSSL*, uma ferramenta de linha de comando que suporta uma vasta gama de operações criptográficas. Siga os passos 1,2,3 e 4 para criar as chaves. Caso tenha algum problema na criação, nós oferecemos um par de [chaves de exemplo](https://github.com/tecnico-distsys/tecnico-distsys.github.io/blob/crypto-lab-updated/src/resources/keys.zip) e pode usá-las a partir do passo 5.

1. Verifique que tem o *OpenSSL* instalado no seu sistema:
```bash
openssl version
```
2. Idealmente, deverá ter uma versão recente do *OpenSSL*.
```bash
OpenSSL 3.0.2 15 Mar 2022 (Library: OpenSSL 3.0.2 15 Mar 2022)
```
3. Caso não o tenha, instale-o:
   3.1. No *Ubuntu*, use o seguinte comando:
   ```bash
   sudo apt update
   sudo apt install openssl
   ```
   3.2. No *Windows*, pode descarregar o *OpenSSL* a partir do site oficial: https://www.openssl.org/source/.
   3.3 No macOS, pode usar o *Homebrew* para instalar o *OpenSSL*:
   ```bash
   brew install openssl
   ```
4. Para gerar um par de chaves RSA, use os seguintes comando no terminal:
```bash
openssl genrsa -out priv.key 2048 # gera a chave privada
openssl rsa -in priv.key -pubout -out pub.key # gera a chave pública a partir da chave privada
```
Esta seria uma maneira correta de criar um par de chaves genéricas. No entanto, como vamos usar Java neste exercício, vamos criar as chaves da seguinte maneira para serem mais fácil de importar mais logo:
```bash
openssl genrsa -out private.pem 2048 # Gerar a chave privada RSA
openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -out private.der -nocrypt # Converter a privada para PKCS#8 DER (O formato que o Java lê nativamente)
openssl rsa -in private.pem -pubout -outform DER -out public.der # Gerar a chave pública em formato X.509 DER
```
5. Copie a chave privada (ficheiro ```private.der```) para o servidor (pasta ```server/src/main/resources```. Se a pasta ```resources``` não existir, ela deve ser criada no diretório ```main```).
Em sistemas reais, a chave pública é tipicamente distribuída através de uma infraestrutura de chave pública (PKI), num certificado digital de chave pública emitido por uma autoridade de certificação (CA). No entanto, por simplificação, neste exercício vamos entregar a chave pública manualmente ao cliente, copiando-a para a pasta do mesmo.
6. Copie a chave pública (ficheiro ```public.der```) para o cliente (pasta ```client/src/main/resources```. Se a pasta ```resources``` não existir, ela deve ser criada no diretório ```main```).
Deste modo criamos e distribuímos um par de chaves público-privadas usando o terminal. No entanto, também teria sido possível fazer isto com o Java, mediante a classe java.security.KeyPairGenerator.

### Acrescentar assinatura à definição da operação


Vamos agora acrescentar uma assinatura à definição da mensagem de resposta da operação `listProducts`..
1. Aceda à definição *Protobuf* no ```contract```.
2. Acrescente a definição de uma nova estrutura de dados (`message`) para a assinatura, composta por identificador do assinante e o valor a calcular.
```protobuf
...
message Signature {
	string signerIdentifier = 1;
	bytes signatureValue = 2;
}
...
```
3. Acrescente a `message` com a assinatura digital à mensagem da resposta.
```protobuf
...
message SignedResponse {
  	ProductsResponse response = 1;
  	Signature signature = 2;
}
...
```
4. Modifique o tipo do resultado da operação RPC:
```protobuf
...
rpc listProducts(ProductsRequest) returns (SignedResponse);
...
```
5. Re-execute nas pasta ```contract``` o comando ```mvn install``` para atualizar o código Java gerado.
6. Efetue as alterações necessárias no código do servidor para refletir a modificação:
```java
...
import pt.tecnico.supplier.grpc.SignedResponse;
import pt.tecnico.supplier.grpc.Signature;
...
@Override
public void listProducts(ProductsRequest request, StreamObserver<SignedResponse> responseObserver) { 
		// atualize o resto da função
...
```
7. Atualize também a chamada do lado do cliente:
```java
...
import pt.tecnico.supplier.grpc.SignedResponse;
import pt.tecnico.supplier.grpc.Signature;
...
SignedResponse response = stub.listProducts(request);
...
```
### Assinar a resposta a enviar

A partir de agora, as mensagens enviadas pelo servidor serão assinadas.
1. Para tal, o servidor vai precisar da sua chave privada, que se encontra nos resources.
```java
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
...
	private byte[] readResource(String path) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Ficheiro não encontrado: " + path);
            }
            return is.readAllBytes();
        }
    }
	public static PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        byte[] keyBytes = readResource(resourcePath);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
```
Desta maneira podemos obter a nossa chave privada com este método que criamos: `loadPrivateKey`. Podemos obtê-la no momento da criação do serviço, por exemplo, chamando o método no contrutor:
```java
	public SupplierServiceImpl() {
		debug("Loading demo data...");
		supplier.demoData();
		try {
            this.privateKey = loadPrivateKey("private.der");
            System.out.println("Chave privada do servidor carregada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar a chave privada: " + e.getMessage());
            e.printStackTrace();
        }
	}
```
2. Para assinar vamos usar a classe java.security.Signature. Primeiro queremos obter uma instância do algoritmo que quisermos usar:
```java
		java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
```
3. Esta instância pode desempenhar tanto a função de assinar como de verificar a assinatura. Como estamos no servidor, queremos que assine:
```java
		sig.initSign(this.privateKey);
```
4. Carregamos todos os dados que quisermos assinar (pode ser chamado várias vezes), e assinamos, obtendo assim os bytes da assinatura.
```java
		sig.update(response.toByteArray());
		byte[] signatureBytes = sig.sign();
```
5. Por último, incluímos a assinatura na mensagem de resposta, se bem que tipicamente o mais correto seria enviar esta assinatura como metadado.
```java
import com.google.protobuf.ByteString;
...
		Signature signature = Signature.newBuilder()
			.setSignerIdentifier(supplier.getId())
			.setSignatureValue(ByteString.copyFrom(signatureBytes))
			.build();
		SignedResponse signedResponse = SignedResponse.newBuilder()
			.setResponse(response)
			.setSignature(signature)
			.build();
		responseObserver.onNext(signedResponse);
```
NOTA: Muitas destas operações lançam exceções, que devem ser tratadas.

### Verificar a assinatura da resposta recebida

O cliente quer certificar-se que as mensagens recebidas foram realmente enviadas pelo servidor, pelo que iremos verificar que a assinatura das mensagens é válida.
1. Para tal, iremos precisar da chave pública do servidor:
```java
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
...
	private static PublicKey loadPublicKey(String resourcePath) throws Exception {
        byte[] keyBytes = readResource(resourcePath);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
	private static byte[] readResource(String path) throws Exception {
        try (InputStream is = SupplierClient.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Ficheiro não encontrado nos resources: " + path);
            }
            return is.readAllBytes();
        }
    }


```
Igual que no servidor, estes são dois métodos auxiliares para importar a chave. Agora podemos obtê-la no inicio do main(), por exemplo:
```java
		PublicKey publicKey = null;
        try {
            publicKey = loadPublicKey("public.der");
            System.out.println("Chave pública do servidor carregada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar a chave pública: " + e.getMessage());
        }
```
2. Vamos inicializar a assinatura com a chave pública, para poder verificar assinaturas:
```java
		java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
```
3. Para verificar, carregamos os dados que foram assinados e depois verificamos se a assinatura recebida coincide:
```java
		SignedResponse signedResponse = stub.listProducts(request);
		ProductsResponse response = signedResponse.getResponse();
		Signature receivedSignature = signedResponse.getSignature();

		sig.update(response.toByteArray());
		boolean isValid = sig.verify(receivedSignature.getSignatureValue().toByteArray());
```

### Verificar eficácia da assinatura

Vamos modificar o conteúdo da mensagem de resposta depois de assinada, para confirmar que o cliente é capaz de detetar a alteração.
1. No servidor, após a realização da assinatura, modifique um dos campos de um dos produtos. Os objetos construídos para os pedidos e respostas são imutáveis, ou seja, não podem ser mudados depois de construídos. Para criar um objeto modificado a partir de um objeto existente pode-se usar o método ```toBuilder()```, semelhante ao seguinte:
```java
...
			response = response.toBuilder().setSupplierIdentifier("intruder").build();
...
```
2. Para testar, execute no **server** o comando ```mvn compile exec:java -Ddebug```.
3. De seguida, execute também no **client** o comando ```mvn compile exec:java -Ddebug```.

### ...e se alguem quiser ler as nossas mensagens?

Assinar a mensagem proporciona três propriedades: autenticidade, integridade e não repudiação. No entanto, se a mensagem for intercetada, os atacantes podem ler os seus conteúdos. Vamos alterar o nosso programa para que as mensagens sejam confidenciais. Para tal, vamos encriptar a mensagem.

1. Os dados encriptados serão um conjunto de bytes, por isso vamos atualizar o protocolo:
```protobuf
message EncryptedResponse {
	bytes encriptedPayload = 1;
	Signature signature = 2;
}
...
  rpc listProducts(ProductsRequest) returns (EncryptedResponse);
```
2. A criptografia assimétrica é muito pesada e por isso não é adecuada para encriptar grandes pacotes de dados. Nestes casos, usamos chaves simétricas. Com este comando poderá criar uma chave simétrica AES-128 de 16 bytes:
```bash
openssl rand -out secret.key 16
```
Copie esta chave para os resources do servidor e para os do cliente também. Estamos a fazer esta distribuição manual das chaves simétricas para simplificar o exercício, mas normalmente usam-se algoritmos como Diffie-Helman ou PGP para acordar a chave a usar.
3. Atualizar o servidor para encriptar a mensagem.
Primeiro vamos importar a chave usando os métodos que criamos anteriormente:
```java
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
...
private SecretKeySpec aesKey;
...
byte[] aesKeyBytes = readResource("secret.key");
aesKey = new SecretKeySpec(aesKeyBytes, "AES");
```
Agora vamos encriptar os dados:
```java
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] encryptedPayload = cipher.doFinal(response.toByteArray());
```
Resta enviar a EncryptedResponse correspondente. Não se esqueça de atualizar a função `listProducts`, uma vez que atualizamos o proto!
4. Agora o cliente terá de receber a mensagem e desencriptá-la.
Devemos importar a chave simétrica que partilhamos com o servidor.
```java
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
...
byte[] aesKeyBytes = readResource("secret.key");
SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
```
E desencriptar:
```java
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		byte[] plainTextBytes = cipher.doFinal(cipherTextBytes);
		ProductsResponse payload = ProductsResponse.parseFrom(plainTextBytes);	
```
Deste modo, os dados estão a transitar encriptados, e mesmo que sejam intercetados não poderão ser lidos sem a chave simétrica!

## Aproveite o que construiu para aplicar no seu projeto

Neste exercício explorou os mecanismos base de criptografia em Java — leitura de chaves, cálculo de resumos e assinaturas digitais — num contexto simples com gRPC. Estes mesmos conceitos devem agora ser aplicados no seu projeto de SD: os clientes passam a assinar transações, os nós validam essas assinaturas antes de aceitar pedidos, e os blocos produzidos pelo sequenciador são assinados e verificados antes de serem aplicados à blockchain.
