# Criptografia

## Objectivos

- Utilizar os mecanismos criptográficos da plataforma Java
- Concretizar um mecanismo de segurança para um gRPC

***

## Segurança e criptografia em Java

A plataforma Java disponibiliza um conjunto abrangente de bibliotecas para suportar segurança, criptografia e comunicações protegidas, permitindo o desenvolvimento de aplicações fiáveis em ambientes locais e distribuídos. No centro deste ecossistema estão três componentes fundamentais:

- **Java Cryptography Extension (JCE)** – fornece os mecanismos criptográficos base, incluindo cifras simétricas e assimétricas, geração e gestão de chaves, funções de resumo (hash), assinaturas digitais e geração de números aleatórios imprevisíveis. Pode aceder a um [pequeno exemplo](https://github.com/tecnico-distsys/example_crypto) da implementação destes mecanismos em Java. A JCE funciona através de providers, bibliotecas que implementam algoritmos específicos. Esta abordagem permite escolher dinamicamente diferentes implementações, como as fornecidas pela Oracle ou por projetos externos como o Bouncy Castle.

- **Java Secure Sockets Extension (JSSE)** – abstrai o uso de criptografia nas comunicações em rede, suportando TLS (antigo SSL) e viabilizando canais seguros, como no HTTPS, garantindo confidencialidade, integridade dos dados e autenticação entre cliente e servidor.

- **Java Authentication and Authorization Service (JAAS)** – disponibiliza uma arquitetura modular para autenticação e autorização, centrada no utilizador, permitindo controlar acessos a recursos e integrar diferentes mecanismos de identidade.

Para além dos mecanismos criptográficos, é importante considerar a representação dos dados. Os algoritmos trabalham nativamente com dados binários (```byte[]```), mas frequentemente é necessário armazenar ou transmitir informação cifrada em formato texto (por exemplo, em *XML*). Nesses casos recorre-se à codificação *Base64*, que converte dados binários num conjunto reduzido de caracteres *ASCII* universais. Embora aumente o tamanho dos dados em cerca de 33%, esta técnica garante compatibilidade com sistemas baseados em texto. A plataforma Java fornece utilitários standard para codificar e descodificar Base64 de forma simples.

Em conjunto, JCE, JSSE e JAAS, aliados aos mecanismos criptográficos fundamentais e à codificação *Base64*, constituem uma infraestrutura completa que permite ao Java oferecer proteção de dados, comunicações seguras e controlo de acessos — pilares essenciais para aplicações modernas com elevados requisitos de segurança.

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

Num mundo real, a lista devolvida pelo servidor ao cliente pode ser intercetada e modificada por um atacante. É necessário acrescentar uma assinatura para proteger a resposta do servidor. Vamos fazer uma assinatura digital. O servidor utilizará a sua chave privada para assinar e o cliente verificará a assintura com a chave pública do servidor. 

Vamos começar por criar um par de chaves RSA, que é um algoritmo de criptografia assimétrica amplamente utilizado para assinaturas digitais. Para tal vamos usar o *OpenSSL*, uma ferramenta de linha de comando que suporta uma vasta gama de operações criptográficas. Siga os passos 1,2,3 e 4 para criar as chaves. Caso tenha algum problema na criação, nós oferecemos um par de [chaves de exemplo](https://github.com/tecnico-distsys/tecnico-distsys.github.io/blob/crypto-lab-updated/src/resources/keys.zip) e pode usá-las apartir do passo 5.

1. Verifique que tem o *OpenSSL* instalado no seu sistema:
```bash
openssl version
```
2. Idealmente, deverá ter uma versão do *OpenSSL* igual ou superior a 3.0.0.
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
5. Copie a chave privada (ficheiro ```priv.key```) para o servidor (pasta ```server/src/main/resources```. Se a pasta ```resources``` não existir, ela deve ser criada no diretório ```main```).
6. Copie a chave pública (ficheiro ```pub.key```) para o cliente (pasta ```client/src/main/resources```. Se a pasta ```resources``` não existir, ela deve ser criada no diretório ```main```).


### Acrescentar assinatura à definição da operação


Vamos agora acrescentar uma assinatura à definição da operação RPC.
1. Aceda à definição *Protobuf* no ```contract```.
2. Acrescente a definição de uma nova estrutura de dados para a assinatura, composta por identificador do assinante e o valor a calcular.
```protobuf
...
message Signature {
	string signerIdentifier = 1;
	string signatureValue = 2;
}
...
```
3. Acrescente a definição de uma mensagem para a resposta original com uma assinatura:
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

Uma forma simples de implementar uma assinatura digital consiste em calcular um resumo da mensagem e cifrar esse resumo com a chave privada. Vamos então calcular o resumo da resposta e depois cifrar esse resumo com a chave privada.

1. Aceda à classe de implementação do serviço no server (ficheiro ```SupplierServiceImpl.java```).
2. Crie um objecto ```MessageDigest``` com o algoritmo *SHA-256*:
```java
...
MessageDigest message_digest = MessageDigest.getInstance("SHA-256");
...
```
3. Para obter os dados a resumir, serialize o resultado com o seguinte método:
```java
...
byte[] responseBytes = response.toByteArray();
...
```
4. Para calcular o resumo, use o método ```digest``` do objecto ```MessageDigest```:
```java
...
byte[] hash = message_digest.digest(responseBytes);
...
```
5. Para cifrar o resumo, crie um objecto ```Cipher``` com o algoritmo *RSA/ECB/PKCS1Padding* e inicialize em *ENCRYPT_MODE* com a chave:
```java
...
private static byte[] readFile(String path) throws FileNotFoundException, IOException {
	FileInputStream fis = new FileInputStream(path);
	byte[] content = new byte[fis.available()];
	fis.read(content);
	fis.close();
	return content;
}
...
public static PrivateKey readPrivateKey(String privateKeyPath) throws Exception {

	System.out.println("Reading private key from file " + privateKeyPath + " ...");
	byte[] privEncoded = readFile(privateKeyPath);

	PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
	KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
	PrivateKey priv = keyFacPriv.generatePrivate(privSpec);

	return priv;
}
...
try{
	...
	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(Cipher.ENCRYPT_MODE, readPrivateKey("src/main/resources/priv.key"));
	...
}catch (Exception e) {
	e.printStackTrace();
	...
}
...
```
6. Calcule a assinatura cifrando o resumo:
```java
...
try{
	...
	byte[] signatureBytes = cipher.doFinal(hash);
	...
}catch (Exception e) {
	...
}
...
```
7. Preencha a resposta a devolver com a assinatura. A assinatura deve ser codificada em *Base64* para ser convertida num formato texto:
```java
...
String signatureString = Base64.getEncoder().encodeToString(signatureBytes);
...
```
8. Por fim, execute no **server** o comando ```mvn compile exec:java -Ddebug```.

### Verificar a assinatura da resposta recebida

Para verificar a assinatura é necessário calcular o resumo da mensagem recebida e comparar com a decifra do resumo recebido na assinatura. Para tal:
1. Aceda à classe do **client**.
3. Para recalcular o resumo, crie um objecto ```MessageDigest``` com o algoritmo *SHA-256*. Calcule o resumo a partir dos dados recebidos:
```java
...
MessageDigest message_digest = MessageDigest.getInstance("SHA-256");
byte[] responseBytes = response.getResponse().toByteArray();
byte[] hash = message_digest.digest(responseBytes);
...
```
3. Para decifrar o resumo cifrado recebido na assinatura, crie um objecto ```Cipher``` com o algoritmo *RSA/ECB/PKCS1Padding*, e inicialize em *DECRYPT_MODE* com a chave pública do **server**:
```java
...
private static byte[] readFile(String path) throws FileNotFoundException, IOException {
	FileInputStream fis = new FileInputStream(path);
	byte[] content = new byte[fis.available()];
	fis.read(content);
	fis.close();
	return content;
}
...
public static PublicKey readPublicKey(String publicKeyPath) throws Exception {

	System.out.println("Reading public key from file " + publicKeyPath + " ...");
	byte[] pubEncoded = readFile(publicKeyPath);

	X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
	KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
	PublicKey pub = keyFacPub.generatePublic(pubSpec);
	System.out.println(pub);

	return pub;
}
...
try{
	...
	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(Cipher.DECRYPT_MODE, readPublicKey("src/main/resources/pub.key"));
	byte[] signatureBytes = Base64.getDecoder().decode(signature.getSignatureValue());
	byte[] decryptedHash = cipher.doFinal(signatureBytes);
	...
}catch (Exception e) {
	...
}
...
```
4. Compare o resumo decifrado com o resumo calculado:
```java
...
if (Arrays.equals(decryptedHash, hash)) {
	System.out.println("Signature verified.");
} else {
	System.out.println("Signature verification failed.");
}
...
```
5. De seguida, execute também no **client** o comando ```mvn compile exec:java -Ddebug```.

### Verificar eficácia da assinatura

Vamos modificar o conteúdo da mensagem de resposta depois de assinada, para confirmar que o cliente é capaz de detetar a alteração.
1. No servidor, após a realização da assinatura, modifique um dos campos de um dos produtos. Os objetos construídos para os pedidos e respostas são imutáveis, ou seja, não podem ser mudados depois de construídos. Para criar um objeto modificado a partir de um objeto existente pode-se usar o método ```toBuilder()```, semelhante ao seguinte:
```java
...
ProductsResponse.Builder modifiedProducts = products.toBuilder();
modifiedProducts.setSupplierIdentifier("modifiedID");
...
```
2. Para testar, execute no **server** o comando ```mvn compile exec:java -Ddebug```.
3. De seguida, execute também no **client** o comando ```mvn compile exec:java -Ddebug```.


## Aproveite o que construiu para aplicar no seu projeto

Neste exercício explorou os mecanismos base de criptografia em Java — leitura de chaves, cálculo de resumos e assinaturas digitais — num contexto simples com gRPC. Estes mesmos conceitos devem agora ser aplicados no projeto **BlockchainIST**: os clientes passam a assinar transações, os nós validam essas assinaturas antes de aceitar pedidos, e os blocos produzidos pelo sequenciador são assinados e verificados antes de serem aplicados à blockchain.