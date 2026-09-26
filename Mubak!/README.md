# Mubak

O Mubak e um e-commerce/catalogo desenvolvido em Spring Boot para o projeto final da turma de Desenvolvimento de Sistemas. A aplicacao usa Java no back-end, Thymeleaf no front-end, PostgreSQL/Supabase para persistencia e Three.js para a vitrine 3D da pagina inicial.

## Como ler o projeto

A requisicao normalmente percorre este caminho:

1. O navegador abre uma URL e envia um formulario.
2. Um controller recebe a requisicao e escolhe a tela ou chama um service.
3. O service aplica as regras do negocio.
4. O repository conversa com o banco por meio do Spring Data JPA.
5. O controller devolve um template Thymeleaf ou uma resposta JSON.

As pastas principais sao:

- [src/main/java/com/Senai/Mubak/Application.java](src/main/java/com/Senai/Mubak/Application.java): inicia o Spring Boot.
- `controller`: recebe URLs e liga as telas as regras do sistema.
- `service`: concentra validacoes, autenticacao, imagens e produtos.
- `repository`: cria o acesso as tabelas sem SQL repetitivo.
- `model`: representa entidades do banco e dados recebidos dos formularios.
- `config`: protege rotas e publica os arquivos enviados.
- `src/main/resources/templates`: telas HTML renderizadas pelo Thymeleaf.
- `src/main/resources/static`: CSS, JavaScript e imagens publicas.

## Mapa por tela

### Pagina inicial

- Back-end: [HomeController.java](src/main/java/com/Senai/Mubak/controller/HomeController.java) mapeia `GET /` para a home.
- Front-end: [home.html](src/main/resources/templates/home.html) organiza navegacao, hero, secoes e chamadas para login.
- Estilo: [home.css](src/main/resources/static/css/home.css) cuida do layout exclusivo da home; [produtos.css](src/main/resources/static/css/produtos.css) fornece estilos compartilhados.
- Interacao: [marketplace-3d.js](src/main/resources/static/js/marketplace-3d.js) cria a vitrine Three.js; [three.min.js](src/main/resources/static/js/three.min.js) e a biblioteca externa usada por ela.
- Menu: [navigation.js](src/main/resources/static/js/navigation.js) abre o menu em telas pequenas.

### Login

- Front-end: [login.html](src/main/resources/templates/auth/login.html) envia `identificador` e `senha` por `POST /login`.
- Controller: [AuthController.java](src/main/java/com/Senai/Mubak/controller/AuthController.java) mostra a tela, chama o login e cria a sessao.
- Regra: [UsuarioService.java](src/main/java/com/Senai/Mubak/service/UsuarioService.java) procura por e-mail, CPF/CNPJ ou pelo identificador `root`, gera o hash da senha e compara com o hash salvo.
- Hash: [SenhaService.java](src/main/java/com/Senai/Mubak/service/SenhaService.java) usa SHA-256 e devolve o resultado em hexadecimal.
- Sessao: depois do sucesso, `AuthController` salva `usuarioId` e `perfil` na `HttpSession` e redireciona para `/produtos`.
- Protecao: [AuthenticationInterceptor.java](src/main/java/com/Senai/Mubak/config/AuthenticationInterceptor.java) manda usuarios sem sessao para `/login` e bloqueia `/admin` para quem nao tem perfil `ROOT`.

Fluxo intuitivo do login:

`login.html` -> `POST /login` -> `AuthController` -> `UsuarioService.login` -> `UsuarioRepository` -> comparacao do hash -> sessao -> `/produtos`.

Quando a credencial falha, o controller redireciona para `/login?erro=Login+invalido`; o Thymeleaf exibe a mensagem somente se `erro` existir no Model.

### Cadastro de cliente

- Tela: [cadastro.html](src/main/resources/templates/auth/cadastro.html).
- DTO: [UsuarioForm.java](src/main/java/com/Senai/Mubak/model/UsuarioForm.java) recebe os campos e as confirmacoes de e-mail/senha.
- Controller: `AuthController` valida o formulario e encaminha para o service.
- Regra e persistencia: `UsuarioService.cadastrar` valida documento, confirmacoes, normaliza valores, gera hash e salva um usuario `CLIENTE`.
- Validacao de documento: [DocumentoValidator.java](src/main/java/com/Senai/Mubak/service/DocumentoValidator.java) remove a mascara e verifica 11 ou 14 digitos.
- Confirmacao: [sucesso.html](src/main/resources/templates/auth/sucesso.html) informa que o cadastro foi concluido e aponta para o login.

### Catalogo de produtos

- Tela: [lista.html](src/main/resources/templates/produtos/lista.html) mostra cards, busca, estoque, edicao e exclusao.
- Controller: [ProdutoController.java](src/main/java/com/Senai/Mubak/controller/ProdutoController.java) atende `GET /produtos`, detalhes, novo, editar, salvar, atualizar e excluir.
- Regra: [ProdutoService.java](src/main/java/com/Senai/Mubak/service/ProdutoService.java) valida nome, descricao, preco, estoque, imagem, categoria e vendedor.
- Persistencia: [ProdutoRepository.java](src/main/java/com/Senai/Mubak/repository/ProdutoRepository.java) lista, busca, salva e remove produtos.
- Estilo: [produtos.css](src/main/resources/static/css/produtos.css) define cards, busca, alertas e responsividade.

### Cadastro e edicao de produto

- Tela: [form.html](src/main/resources/templates/produtos/form.html) e um formulario compartilhado por novo e editar.
- Imagem: [ImagemService.java](src/main/java/com/Senai/Mubak/service/ImagemService.java) aceita JPG, PNG, WEBP e GIF, cria um nome UUID e salva em `uploads/`.
- Arquivos publicados: [WebConfig.java](src/main/java/com/Senai/Mubak/config/WebConfig.java) transforma `/uploads/**` em recursos acessiveis pelo navegador.
- Detalhes: [detalhes.html](src/main/resources/templates/produtos/detalhes.html) apresenta um produto sem permitir edicao.

### Administracao de usuarios

- Controller: [AdminUsuarioController.java](src/main/java/com/Senai/Mubak/controller/AdminUsuarioController.java) lista usuarios e cria usuarios administrativos.
- Telas: [usuarios.html](src/main/resources/templates/admin/usuarios.html) e [usuario-form.html](src/main/resources/templates/admin/usuario-form.html).
- Regra de acesso: o interceptor exige sessao e perfil `ROOT` antes de permitir `/admin`.
- Entidade e repository: [Usuario.java](src/main/java/com/Senai/Mubak/model/Usuario.java) e [UsuarioRepository.java](src/main/java/com/Senai/Mubak/repository/UsuarioRepository.java).

### API de produtos

- [ProdutoRestController.java](src/main/java/com/Senai/Mubak/controller/ProdutoRestController.java) oferece JSON em `/api/produtos` para listar, buscar, cadastrar, atualizar e excluir.
- Ele reutiliza [ProdutoService.java](src/main/java/com/Senai/Mubak/service/ProdutoService.java), portanto aplica as mesmas regras do fluxo HTML.

## Banco e configuracao

- [application.properties](src/main/resources/application.properties): nome da aplicacao, conexao PostgreSQL, Hibernate, Thymeleaf e separador do SQL. As credenciais existentes foram mantidas como estavam.
- [schema.sql](src/main/resources/schema.sql): cria a tabela `usuarios` e o trigger que normaliza e-mail/documento e atualiza `atualizado_em`.
- [SUPABASE.md](SUPABASE.md): orienta a configuracao por variaveis de ambiente. Nao coloque senhas em arquivos versionados.
- [pom.xml](pom.xml): define Java 17, Spring Boot, JPA, Thymeleaf, validacao, web, PostgreSQL e dependencias de teste.

## Testes

- [ProdutoServiceTests.java](src/test/java/com/Senai/Mubak/service/ProdutoServiceTests.java) verifica salvar, validar preco, atualizar e excluir produtos usando Mockito.
- Para executar:

```powershell
.\mvnw.cmd test
```

## O que observar na apresentacao

- `Controller` e a porta de entrada, nao o lugar das regras complexas.
- `Service` decide se os dados sao validos e chama o repository.
- `Repository` representa as operacoes do banco.
- `Model` representa dados persistidos ou recebidos de formularios.
- Thymeleaf usa expressao `th:*` para inserir dados do Model no HTML.
- A sessao nao guarda a senha: guarda somente o ID e o perfil do usuario autenticado.
- A home e publica; catalogo, venda e administracao passam pelo interceptor.
