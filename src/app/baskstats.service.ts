import { Injectable } from '@angular/core';
import { AtletaDetalhada, DashData, Jogadora, PosicaoStat } from './baskstats.model';

@Injectable({
  providedIn: 'root'
})
export class BaskstatsService {
  private readonly PRIMEIRO_NOMES = ['Ana','Beatriz','Camila','Débora','Emanuelle','Fernanda','Gabriela','Helena','Isabela','Juliana','Kamila','Larissa'];
  private readonly SOBRENOMES = ['Silva','Santos','Oliveira','Souza','Costa','Pereira','Almeida','Rodrigues','Ferreira','Lima'];
  private readonly POSICOES = ['Armadora','Ala','Ala-Pivô','Pivô'];

  rand(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  nomeAleatorio(): string {
    const p = this.PRIMEIRO_NOMES[this.rand(0, this.PRIMEIRO_NOMES.length - 1)];
    const s = this.SOBRENOMES[this.rand(0, this.SOBRENOMES.length - 1)];
    return `${p} ${s}`;
  }

  gerarDados(): DashData {
    const rodadas = Array.from({ length: 10 }, (_, i) => `R${i + 1}`);
    const pontosPorRodada = rodadas.map(() => this.rand(58, 92));
    const vitorias = this.rand(10, 18);
    const derrotas = this.rand(4, 12);

    const jogadoras: Jogadora[] = Array.from({ length: 5 }, () => ({
      nome: this.nomeAleatorio(),
      pos: this.POSICOES[this.rand(0, this.POSICOES.length - 1)],
      ppj: this.rand(9, 22),
      reb: this.rand(2, 11),
      ast: this.rand(1, 8)
    })).sort((a, b) => b.ppj - a.ppj);

    const arremesso2 = this.rand(42, 58);
    const arremesso3 = this.rand(28, 40);
    const lanceLivre = this.rand(65, 85);
    const roubosBola = this.rand(5, 12);
    const tocos = this.rand(2, 8);

    const pontosPorPosicao: PosicaoStat[] = this.POSICOES.map(pos => ({
      pos,
      media: this.rand(6, 16)
    }));

    const sequenciaJogos: ('V' | 'D')[] = Array.from({ length: 10 }, () => (Math.random() < 0.6 ? 'V' : 'D'));

    return {
      rodadas, pontosPorRodada, vitorias, derrotas, jogadoras,
      arremesso2, arremesso3, lanceLivre,
      roubosBola, tocos, pontosPorPosicao, sequenciaJogos
    };
  }

  gerarAtletas(qtd: number): AtletaDetalhada[] {
    const numerosUsados = new Set<number>();
    return Array.from({ length: qtd }, (_, i) => {
      let numero = this.rand(0, 99);
      while (numerosUsados.has(numero)) numero = this.rand(0, 99);
      numerosUsados.add(numero);

      const jogos = this.rand(12, 22);
      const ppj = this.rand(4, 22);
      const reb = this.rand(1, 11);
      const ast = this.rand(0, 8);
      const roubos = this.rand(0, 4);
      const tocos = this.rand(0, 3);
      const turnovers = this.rand(1, 5);

      return {
        id: i + 1,
        nome: this.nomeAleatorio(),
        pos: this.POSICOES[this.rand(0, this.POSICOES.length - 1)],
        numero,
        jogos,
        minutos: this.rand(14, 34),
        ppj, reb, ast, roubos, tocos, turnovers,
        arremesso2: this.rand(38, 60),
        arremesso3: this.rand(20, 42),
        lanceLivre: this.rand(60, 90),
        eficiencia: Math.round(ppj + reb + ast + roubos + tocos - turnovers)
      };
    });
  }
}