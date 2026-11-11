# Frontend BaskStats

Consulte o [README principal](../README.md) para execução, rotas, autenticação, demonstração e publicação.

```bash
npm ci --ignore-scripts
npm start
```

O seletor “Explorar demonstração” permite revisar as telas sem backend. Para login real, inicie também a API Spring Boot. O proxy local encaminha `/api` para a porta 8080.

```bash
npm run build
npx playwright install chromium
npm run test:e2e
```
