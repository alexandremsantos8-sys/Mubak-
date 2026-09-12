# Configuração do Supabase

O projeto usa PostgreSQL do Supabase. As variáveis de ambiente são obrigatórias para iniciar a aplicação.

No painel do Supabase, abra **Connect** e copie a conexão PostgreSQL. Prefira a conexão via pooler para a aplicação:

```powershell
$env:SUPABASE_DB_URL = "jdbc:postgresql://HOST:6543/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.SEU_PROJECT_REF"
$env:SUPABASE_DB_PASSWORD = "SUA_SENHA"
$env:JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run
```

Substitua `HOST`, `SEU_PROJECT_REF` e `SUA_SENHA` pelos dados exibidos no painel. Não committe a senha nem coloque essas variáveis diretamente em arquivos versionados.

