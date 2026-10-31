# Configuração do Supabase

O projeto usa PostgreSQL do Supabase. Para trocar para outro projeto/banco, altere apenas as variáveis de ambiente abaixo. Elas não devem ser commitadas.

No painel do Supabase, abra **Connect** e copie a conexão PostgreSQL. Prefira a conexão via pooler para a aplicação:

```powershell
$env:SUPABASE_DB_URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.gyervoskufejkatveqbt"
$env:SUPABASE_DB_PASSWORD = "MM5hdibLpqsyAUJ9"
$env:JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run
```

Substitua `HOST`, `SEU_PROJECT_REF` e `SUA_SENHA` pelos dados do novo projeto exibidos em **Connect > Connection string > JDBC**. A porta `6543` é a conexão pooler; para conexão direta, use a porta indicada pelo Supabase. Não coloque a senha diretamente em arquivos versionados.

