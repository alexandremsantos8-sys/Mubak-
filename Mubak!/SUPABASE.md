<!-- # Configuração do Supabase

O projeto usa PostgreSQL do Supabase. Para trocar para outro projeto/banco, altere apenas as variáveis de ambiente abaixo. Elas não devem ser commitadas.

O upload das imagens usa o bucket público `imagens`. No Railway, configure também:

```powershell
SUPABASE_URL=https://gyervoskufejkatveqbt.supabase.co
SUPABASE_SERVICE_ROLE_KEY=SUA_CHAVE_SERVICE_ROLE
```

O bucket deve existir com esse nome. A chave `service_role` é secreta e deve ficar somente nas variáveis do servidor.

No painel do Supabase, abra **Connect** e copie a conexão PostgreSQL. Prefira a conexão via pooler para a aplicação:

```powershell
$env:SUPABASE_DB_URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.gyervoskufejkatveqbt"
$env:SUPABASE_DB_PASSWORD = "SUA_SENHA_DO_BANCO"
$env:JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run
```

No Railway, cadastre essas variáveis em **Variables**. Não coloque a senha diretamente em arquivos versionados. O Railway fornece a porta pela variável `PORT`, e a aplicação a utiliza automaticamente.

<!-- $env:SUPABASE_DB_URL = "jdbc:postgresql://HOST:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.gyervoskufejkatveqbt"
$env:SUPABASE_DB_PASSWORD = "MM5hdibLpqsyAUJ9"
$env:JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run -->

$env:SUPABASE_DB_URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.gyervoskufejkatveqbt"
$env:SUPABASE_DB_PASSWORD = "MM5hdibLpqsyAUJ9"