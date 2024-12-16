# Instalação das ferramentas de desenvolvimento

As ferramentas de desenvolvimento permitem realizar os exercícios das aulas e depois o projeto.
Podem ser usadas nos computadores dos laboratórios, mas é também aconselhável instalar no seu computador pessoal.

Todo o software listado abaixo está disponível em sistemas Windows, Linux e Mac.

### Antes de começar

Nos caminhos de ficheiros (*paths*) em Windows usa-se a barra para trás \ (*backslash*) como separador; nos caminhos Linux e Mac usa-se a barra para a frente / (*slash*).

As instalações seguintes devem ser feitas numa pasta que **não** tenha espaços nem caracteres acentuados no nome, para evitar bugs existentes e ainda não resolvidos, sobretudo nas ferramentas Java em Windows.

Nome de pasta raiz recomendada: `C:\Java`

Nomes de pastas a evitar: `C:\Program Files`, `C:\Users\João`

Uma alternativa à instalação manual é a utilização de gestores de pacotes (*package managers*) para a instalação e actualização automática de dependências, da maioria das ferramentas necessárias no vosso sistema operativo:

- Em sistemas Windows o *package manager* sugerido é o Chocolatey.
- Em sistemas MacOS o *package manager* sugerido é o Homebrew.
- Em sistemas Linux derivados do Debian, como o Ubuntu, o *package manager* é o APT (*Advanced Packaging Tool*).

A maior parte das ferramentas necessita de configurar **variáveis de ambiente**.
O procedimento para definir variáveis de ambiente depende do sistema operativo: [Windows](http://superuser.com/questions/25037/change-environment-variables-as-standard-user-windows-7), [Linux](http://www.cyberciti.biz/faq/set-environment-variable-linux/) e [Mac](http://www.mkyong.com/mac/how-to-set-environment-variables-on-mac-os-x/).


### Software a instalar


#### 1.  Java Developer Kit (JDK)

Este é o ambiente para programação na linguagem Java. Inclui o Java Runtime Environment (JRE).    
        
  - Obter [JDK 17 LTS](https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot)
  - Instalar
  - Configurar:
    - Definir variável de ambiente `JAVA_HOME` com o caminho para a pasta de instalação do JDK
    - Acrescentar `JAVA_HOME/bin` à variável de ambiente `PATH
    - Executar comando `javac -version` para confirmar

####  2. Apache Maven (MVN) 

Esta é a ferramenta de linha de comando para a gestão do ciclo de vida de uma aplicação, incluindo a gestão de dependências de bibliotecas.
  - [Obter a versão estável mais recente do Apache Maven](http://maven.apache.org/download.cgi)
    - Instalar
    - Configurar:
      - Definir variável de ambiente M2_HOME com o caminho para a pasta de instalação
      - Acrescentar `M2_HOME/bin` à `PATH`
      - Executar `comando mvn --version` para confirmar

#### 3. Git

Ferramenta de linha de comando para fazer controlo de versões.
  - [Obter a versão estável mais recente do Git](http://git-scm.com/download/)
  - [Instalar](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
  - Configurar:
    - Executar comando `git --version` para confirmar

#### 4. Plataforma de desenvolvimento Java

Existem várias plataformas de desenvolvimento Java à escolha, das quais só precisam de escolher uma delas. Podem até escolher uma que não esteja nesta lista!

- IntelliJ IDEA
  - Obter a [Toolbox](https://www.jetbrains.com/toolbox-app/) da JetBrains para gerir todas as ferramentas JetBrains facilmente.
  - Para instalar o Toolbox no Linux, sigam os [passos](https://thirddriver.medium.com/jetbrains-toolbox-the-best-way-to-install-intellij-idea-on-linux-53c1070cd03b). Para instalá-lo no Windows e MacOS, basta executar o executável obtido.
  - Na Toolbox instalem o IntelliJ Ultimate, para isso deve obter uma licença de estudante, a qual pode ser obtida preenchendo o [formulário](https://www.jetbrains.com/shop/eform/students).
  - Após instalarem o IntelliJ, recomendamos instalar o plugin [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) para maior legibilidade do código que produzem.

- Eclipse IDE for Java Developers
  - [Obter a versão estável mais recente do Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/packages/)
  - Pode obter o installer e depois especificar que pretende a opção acima.
    Atenção: 32 vs 64 bit - a versão do Eclipse deve concordar com o JDK instalado, ou seja, para o JDK 32 bit, usar o Eclipse 32 bits, para o JDK 64 bit, usar o Eclipse 64 bit.
  - Instalar.
    - Se precisar, consulte as [instruções](https://www.eclipse.org/downloads/packages/installer).
    - Configurar:
      - Especificar o JDK como Standard VM (em vez do JRE)
        Nota: só deverá ser necessário este passo em Windows.
        - Window -> Preferences -> Java -> Installed JREs -> Add...
        - Indicar o caminho até ao diretório do JDK: ex. `C:\Java\jdk-17.0.2`
        - Confirmar que as "Installed JREs" apenas fazem referência ao JDK instalado nas opções ativadas

- Visual Studio Code
  - [Obter a versão mais recente](https://code.visualstudio.com/download)
  - Instalar
  - Instalar [extensões para programação em Java](https://code.visualstudio.com/docs/languages/java)


## Dúvidas ou erros?

Se tiver dificuldades, pode pedir ajuda no Moodle mas, antes, consulte a [lista de respostas a perguntas frequentes](./faq.md).

Se detetar algum erro nas instruções acima, avise-nos!

