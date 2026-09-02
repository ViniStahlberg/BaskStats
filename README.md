# BaskStats — versão Angular

Este pacote contém o mesmo login → dashboard (dados e gráficos gerados,
sem validação real) que a versão em HTML puro, portado para um
componente Angular standalone (Angular 17+).

## Arquivos

```
src/
  index.html
  main.ts
  app/
    app.component.ts     -> lógica: login fake, geração de dados, geometria dos gráficos
    app.component.html   -> template com as telas de login e dashboard
    app.component.css    -> estilos (mesma identidade visual: navy + laranja)
    app.config.ts         -> configuração standalone da aplicação
```

Os gráficos (linha, rosca, barras) são desenhados com SVG puro dentro
do próprio template, sem nenhuma biblioteca externa de gráficos.

## Como usar

Agora o zip já contém a estrutura completa do projeto (`angular.json`,
`package.json`, `tsconfig.json`) — não precisa mais rodar `ng new`.

```bash
# 1. extraia o zip e entre na pasta
cd baskstats-angular

# 2. instale as dependências (baixa o Angular, cria node_modules/)
npm install

# 3. rode o servidor de desenvolvimento
npx ng serve
```

Acesse `http://localhost:4200` no navegador.

Se preferir instalar o Angular CLI globalmente para usar só `ng serve`
(sem o `npx`): `npm install -g @angular/cli`.

## Observações
- O login não valida credenciais reais — qualquer e-mail em formato
  válido e senha com 6+ caracteres entra no dashboard.
- Os dados (times, atletas, jogos, pontuadoras, aproveitamento de
  arremessos, pontos por posição, sequência de resultados) são
  gerados aleatoriamente a cada login, só para fins de teste visual.
