# 1. Apresentação e ferramentas (Java, Maven, IDE)

## Objetivos desta aula:

- Apresentação da/o docente e formação de grupos de projeto
- Familiarizar-se com as ferramentas de desenvolvimento que usaremos na cadeira    


***

## Apoio ao trabalho laboratorial durante o período

O professor do laboratório vai acompanhar o vosso percurso ao longo de todo o semestre. É importante ficar a conhecê-lo desde já. :-)

O apoio a dúvidas fora das aulas é feito através do Moodle de SD, onde é possível colocar perguntas e receber respostas, quer de colegas, quer dos professores. Se não conseguir aceder ao Moodle, peça ajuda ao docente laboratorial.

 
## Inscrição de grupo

O trabalho de laboratório e no projeto é realizado em grupo, com 3 elementos. Os grupos terão de ser formados entre estudantes inscritos no mesmo turno de laboratório.

Para fazer:

- Para registar o grupo, indicar na aula, a pedido do professor, os números de aluno IST e os nomes de utilizadores GitHub de todos os membros
  - Caso ainda não tenha uma conta GitHub, deve [criar uma](https://github.com/join)
  - Atualize a sua foto na conta de perfil, para que possa ser reconhecido
  - Aproveite e atualize também a foto do Fénix

Caso ainda não tenha grupo completo:

- Compareça no seu turno de laboratório e fale com colegas na mesma situação. Esta é a forma mais eficaz de formar grupo!
- Procure colegas no fórum no Moodle, onde os docentes criarão um tópico de discussão sobre este tema. Deixe lá a indicação de que procura elemento(s) para formar grupo ou respondendo a mensagens lá publicadas por colegas. Indique sempre o(s) turno(s) que pode frequentar e onde ainda há vagas.
- Note bem: a formação do grupo é da responsabilidade dos estudantes; os professores apenas fazem o registo.


***

## Familiarização com as ferramentas de desenvolvimento

As ferramentas para o desenvolvimento do projeto são: Java (linguagem e plataforma), Maven (construção), e um  IDE (ambiente integrado de desenvolvimento) de Java.

Esta página contém textos introdutórios a cada uma das ferramentas e um exercício.

A tabela seguinte resume as utilizações mais comuns do JDK, Maven, e de um IDE (e.g., o Eclipse), 
que entenderemos melhor ao longo desta aula.

![Java tools reference card](./images/java-tools-table.png)


Atenção: antes de começar, é necessário [já ter o software instalado](./00-software.md). 

### Java

O JDK (*Java Developer Kit*) é um conjunto de ferramentas para programação na linguagem Java.
As mais importantes são o *javac* que compila os programas e o *java* que lança as aplicações.

Os *javac* e *java* são suficientes para construir pequenos programas. No entanto, para programas de maior dimensão, é muito útil ter:

- Uma ferramenta que dê suporte a todas as tarefas de forma integrada, incluíndo a gestão de dependências: Maven.
- Um ambiente de desenvolvimento (IDE) que apoie o programador em todas as tarefas (por exemplo, IntelliJ, Eclipse, VSCode, ou outro).




### Maven

A ferramenta Maven é a mais importante logo a seguir ao próprio JDK. A utilização do Maven é obrigatória em SD para permitir a construção dos projetos de forma automática na linha de comandos.

O Maven desempenha o papel muito importante de automatizar toda a construção do código e de explicitar dependências de outros programas. Todos os programas devem ter a configuração Maven no ficheiro `pom.xml` para que possam ser (re)construídos de forma repetível. Os programas devem ter também um ficheiro `README` com instruções de construção e de execução.

#### Introdução ao Maven

O Maven é uma ferramenta Java para a gestão de projetos que fornece aos programadores uma estrutura completa para suportar o ciclo de desenvolvimento de uma aplicação. Em particular, o Maven trata da compilação, distribuição, documentação, e colaboração em equipa, entre outras atividades.

A estrutura e conteúdo do projeto Maven são declaradas num ficheiro XML, chamado POM (*Project Object Model*) `pom.xml`, que é a unidade fundamental deste sistema. Cada POM descreve um módulo.

Estrutura de um ficheiro POM ([documentação](http://maven.apache.org/pom.html)):

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <!--
    Os elementos de configuração básicos permitem identificar unicamente o projeto, 
    especificar dependências e definir propriedades (variáveis). 
  -->
  <groupId>...</groupId>
  <artifactId>...</artifactId>
  <version>...</version>
  <packaging>...</packaging>

  <dependencies>...</dependencies>
  <parent>...</parent>
  <dependencyManagement>...</dependencyManagement>
  <modules>...</modules>

  <properties>...</properties>

  <!-- 
    Os elementos de configuração de construção permitem declarar 
    a estrutura de pastas do projeto e a gestão de extensões (plugins).
  -->
  <build>...</build>
  <reporting>...</reporting>

  <!-- 
    Informações adicionais do projeto 
  -->
  <name>...</name>
  <description>...</description>
  <url>...</url>
  <inceptionYear>...</inceptionYear>
  <licenses>...</licenses>
  <organization>...</organization>
  <developers>...</developers>
  <contributors>...</contributors>

  <!-- 
    Elementos de configuração do ambiente 
  -->
  <issueManagement>...</issueManagement>
  <ciManagement>...</ciManagement>
  <mailingLists>...</mailingLists>
  <scm>...</scm>
  <prerequisites>...</prerequisites>
  <repositories>...</repositories>
  <pluginRepositories>...</pluginRepositories>
  <distributionManagement>...</distributionManagement>
  <profiles>...</profiles>
</project>
```
 

Exemplo de ficheiro POM:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <!-- http://maven.apache.org/pom.html#Quick_Overview -->

  <!-- The Basics -->
  <groupId>example</groupId>
  <artifactId>java-app</artifactId>
  <packaging>jar</packaging>
  <version>1.0.0-SNAPSHOT</version>
  <name>java-app</name>
  <url>http://maven.apache.org</url>
  
  <!-- Build Settings -->
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>appassembler-maven-plugin</artifactId>
        <version>1.10</version>
        <configuration>
          <programs>
            <program>
              <mainClass>mypackage.MyClass</mainClass>
              <id>java-app</id>
            </program>
          </programs>
        </configuration>
      </plugin>
    </plugins>
  </build>

</project>
```
 
#### Estrutura típica de pastas

Assumindo que ${basedir} corresponde à localização do projeto Maven, a estrutura de pastas associada é a seguinte:

- `${basedir}/src/main/java` - código fonte
- `${basedir}/src/main/resources` - recursos
- `${basedir}/src/test` - código de teste
- `${basedir}/target` - A pasta target é temporária e serve para guardar as classes do programa compiladas (`*.class`) e outros ficheiros auxiliares - pode ser descartada a qualquer momento e não deve ser guardada em controlo de versões

 
#### Comandos mais frequentes

A lista seguinte apresenta alguns dos comandos Maven mais frequentes:

- `mvn clean` - limpa a pasta temporária
- `mvn compile` - compila o código do programa
- `mvn compile exec:java` - compila o código do programa e executa a classe definida como principal no `pom.xml`
- `mvn test` - compila o código do programa e executa os testes
- `mvn verify` - compila o código do programa e executa os testes de integração (e.g. cliente-servidor)

 
#### Conceitos fundamentais

##### Ciclos de vida de construção, fases e objetivos ([documentação](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Build_Lifecycle_Basics))

Em Maven, o processo de construção é dividido em ciclos de vida de construção, fases e objetivos. Um ciclo de vida de construção é composto por uma sequência de fases de construção e por sua vez cada fase de construção consiste numa sequência de objetivos.

Por exemplo, o ciclo default inclui as seguintes fases (lista completa de fases):

- *validate* - verifica se projecto está correcto e toda a informação está disponível
- *compile* - compila o código fonte
- *test* - testa o código fonte compilado
- *package* - pega no código compilado e empacota-o num formato que se pode partilhar/distribuir, como JAR.
- *integration-test* - processa e integra o pacote se necessário num ambiente onde testes de integração possam correr
- *verify* - corre verificações para confirmar que o pacote é válido e corresponde aos critérios de qualidade definidos
- *install* - instala o pacote no repositório local, para poder ser usado localmente como dependência noutros projectos
- *deploy* - copia pacote final para um repositório remoto

Uma execução no Maven consiste em passar um argumento ao executável `mvn`. Este argumento corresponde ao nome dum ciclo de vida de construção, fase ou objetivo.

Se um ciclo de vida solicitado é executado, todas as fases de construção deste ciclo de vida são executadas. Por conseguinte, se uma fase de construção solicitada é executada, todas as fases de construção que a antecedem na sequência pré-definida de fases de construção são também executadas.

Por exemplo, executar
`mvn install`
irá correr todas as fases anteriores -- *validate, compile, ...* -- antes de empacotar e instalar o módulo no repositório local.

##### Dependências e repositórios

Um dos primeiros objetivos executados pelo Maven é a verificação das dependências do projeto. As dependências são arquivos externos JAR (bibliotecas Java) necessárias para o projeto. Se as dependências não forem encontrados no repositório local, isto é, numa pasta no disco rígido do computador local, o Maven descarrega-as de um repositório central para o repositório local. Por omissão, o repositório local encontra-se na pasta `%USER_HOME%`. Contudo, é possível especificar um repositório local onde Maven irá guardar os artefactos.

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository>C:/RepositorioLocal</localRepository>
    ...
```

***TODO: substituir por outro plugin***

Um exemplo de uma dependência é o JUnit, que como pode ser vista abaixo.

```xml
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
  </dependencies>
</project>
```

Os três primeiros campos identificam a dependência. O parâmetro scope especifica que a dependência apenas existe para os testes. Isto significa que o Maven vai providenciar um *classpath* sem o JUnit para compilação do código principal e um *classpath* com o JUnit (na versão indicada) para compilação e execução do código de testes.

As dependências podem ser pesquisadas no motor de pesquisa amigável do repositório central Maven. Por exemplo, o JUnit 4.12 tem a seguinte página informativa: http://mvnrepository.com/artifact/junit/junit/4.12

Para o caso de projetos em desenvolvimento com inúmeros módulos (por exemplo: módulos A, B e C), com dependências entre eles, o conceito de *SNAPSHOT* é muitas vezes usado. Se um módulo A está em desenvolvimento rápido, e a criar novas versões com muita frequência, o sufixo `-SNAPSHOT` é adicionado no elemento `<version>`.

Exemplo do `pom.xml` de A:

```xml
<project>
  ...
  <groupId>exemplo</groupId>
  <artifactId>modA</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  ...
</project>
```

Assim, cada vez que o módulo A enviar uma *SNAPSHOT* do seu código actualizado para o repositório, vai substituir a versão que existia anteriormente. Por sua vez, os outros módulos, B e C, que dependem de A escolhem essa mesma versão *SNAPSHOT* como dependência.

O `pom.xml` de B e C iriam conter:

```xml
<project>
  ...
    <dependency>
        <dependency>
            <groupId>exemplo</groupId>
            <artifactId>modA</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>
  </dependencies>
</project>
```

Deste modo, sempre que os módulos B e C são construídos, o Maven automaticamente actualiza o módulo A, obtendo fazendo o JAR correspondente ao SNAPSHOT mais recente.

##### Plugins de construção ([documentação](http://maven.apache.org/guides/mini/guide-configuring-plugins.html))

Os plugins de construção são utilizados para inserir objetivos adicionais numa fase de construção, caso seja necessário executar um conjunto de ações no projeto que não estejam cobertos pelas fases e objetivos padrão do Maven. Os plugins podem ser adicionados ao ficheiro POM. Para além dos plugins padrão disponibilizados, outros podem também ser implementados em Java.

Damos como exemplo o *plugin* `AppAssembler`:

```xml
<build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>appassembler-maven-plugin</artifactId>
        <version>1.10</version>
        <configuration>
          <programs>
            <program>
              <mainClass>mypackage.MyClass</mainClass>
              <id>java-app</id>
            </program>
          </programs>
        </configuration>
      </plugin>
    </plugins>
</build>
```

Este plugin é usado para criar um script de arranque do programa, tanto para Windows (ficheiro `.bat`) como para Linux (ficheiro com o atributo executável definido). Neste caso, o programa a executar é um programa de exemplo (`java-app`) cuja classe principal se chama `mypackage.MyClass`.

##### Perfis de construção

Os perfis de construção são utilizados caso seja necessário construir um projeto de maneira diferente. Por exemplo, pode ser necessário construir num computador pessoal projetos para desenvolvimento e teste. Pode ainda ser necesssário construí-los para um ambiente de produção. Estas versões poderão ser diferentes. Para permitir diferentes versões, diferentes perfis de construção podem ser adicionados aos ficheiros POM. Durante a execução pode ser especificado que perfil de construção deve ser usado. 

***


### Java IDE

Tanto o Eclipse, o IntelliJ como o VSCode podem ser configurados em cima do JDK ou do Maven. 
Veja abaixo algumas instruções (basta ver para o seu IDE favorito).

- [Maven no IntelliJ](https://www.jetbrains.com/help/idea/maven-support.html)

- Maven no Eclipse:
  
  - Se já tiver um projeto com os ficheiros de configuração do Maven (`pom.xml`):
    - 'File', 'Import...', 'Maven'-'Existing Maven Projects
    - 'Select root directory' e 'Browse' até à pasta do projecto.
    - Confirmar que está tudo como desejado e 'Finish'.

  - Se não existirem os ficheiros de configuração do Maven:
    - Criar um 'Project', do tipo 'Maven Project'.
    - Selecionar 'Create a simple project (skip architype selection)'.
    - Remover a seleção 'Use default Workspace location' e 'Browse' até à pasta raiz do projecto (pasta mãe das 'sources').
    - Preencher os campos em 'New Maven Project'.
  
  -  Para executar ou depurar (*debug*) o projeto Maven no eclipse:
     - Carregar com o botão direito do rato sobre o projeto.
     - Seleccionar 'Run As' ou 'Debug As' e depois carregar em 'Maven build ...'.
     - Indicar os objectivos (p.ex: compile, package) do ciclo de vida de construção em 'Edit Configuration'.





- [Maven no Visual Studio Code](https://code.visualstudio.com/docs/java/java-build)

***

## Experimentar

### Exemplo de aplicação Java
        
  - Obtenha este [projeto de exemplo](https://github.com/tecnico-distsys/example_java-app), que utiliza o Maven para compilar e executar o código Java.
  - Para experimentar o código: fazer *Clone or Download* e depois seguir as instruções do `README`
  - O comando `mvn compile exec:java` compila o programa e executa a classe principal indicada no `pom.xml`
  - Experimente as funcionalidades de depuração (debug) do seu IDE favorito:
    - Criar um ponto de paragem (breakpoint) no programa e fazer debug
    - Alterar os argumentos do programa (-Dexec.args="(...)" pode ser usado para especificar os argumentos do programa quando executado através do comando mvn) e inspecionar as variáveis durante a execução
  - O exemplo tem variantes que podem ser consultadas noutros ramos (*branches*) do Git
    - `appassembler`: permite gerar scripts de lançamento da aplicação em Linux e Windows
      - [Ver código alternativo](https://github.com/tecnico-distsys/example_java-app/tree/appassembler) e comparar com [exemplo base](https://github.com/tecnico-distsys/example_java-app/compare/master...appassembler)
    - `config`: utiliza ficheiro com propriedades de configuração, algumas delas preenchidas dinamicamente pelo Maven
      - [Ver código alternativo](https://github.com/tecnico-distsys/example_java-app/tree/config) e comparar com [exemplo base](https://github.com/tecnico-distsys/example_java-app/compare/master...config)

### Exemplo de biblioteca Java GitHub
  - [Obtenha o projeto](https://github.com/tecnico-distsys/example_java-lib) 
  - Este projeto permite agrupar um conjunto de classes comuns, que podem depois ser reutilizadas por outros programas
  - Para experimentar o código: fazer *Clone or Download* e depois seguir as instruções do `README`
  - O comando `mvn install` disponibiliza o módulo no repositório local
  - O módulo instalado pode depois ser usado como dependência (*dependency*) através das coordenadas (groupId, artifactId, e version).

