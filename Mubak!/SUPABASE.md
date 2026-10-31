<!-- # Configuração do Supabase

O projeto usa PostgreSQL do Supabase. Para trocar para outro projeto/banco, altere apenas as variáveis de ambiente abaixo. Elas não devem ser commitadas.

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