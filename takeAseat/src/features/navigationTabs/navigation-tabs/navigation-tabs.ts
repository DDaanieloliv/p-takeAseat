import { Component } from '@angular/core';
import { ApiService } from '../../../core/services/api-service';
import { CurrentGrid } from '../../../core/model/fetch/grid-entity-dto';
import { CommonModule } from '@angular/common';
import { Seat } from '../../../core/model/Seat';
import { GridService_Observable } from '../../../shared/services/grid-state';
import { SeatDto } from '../../../core/model/fetch/SeatDTO';

@Component({
  selector: 'app-navigation-tabs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navigation-tabs.html',
  styleUrl: './navigation-tabs.scss'
})
export class NavigationTabs {

  constructor(
    private api: ApiService,
    private gridObservable: GridService_Observable,
  ) { }

  public grid: Array<Seat[]> = [];
  public gridList: CurrentGrid[] = [];
  public tabSelected: CurrentGrid | null = null;

  async ngOnInit(): Promise<void> {
    try {
      const grids = await this.api.fetchListGrid();

      // Ordena os grids para manter consistência visual
      this.gridList = this.sortGrids(grids);

      // Encontra o grid atual
      this.tabSelected = this.gridList.find(grid => grid.is_currentGrid) || null;

      console.log("GridList carregado:", this.gridList);
      console.log("Tab selecionada:", this.tabSelected);
    } catch (error) {
      console.error('Erro ao carregar grids:', error);
      this.gridList = [];
    }
  }

  // Ordena os grids para manter ordem consistente
  private sortGrids(grids: CurrentGrid[]): CurrentGrid[] {
    return [...grids].sort((a, b) => {
      // Ordena por grid ID ou por algum critério consistente: comparação
      // lexicográfica baseada na ordem dos caracteres na tabela Unicode
      return a.grid.localeCompare(b.grid);
      // Ou se tiver um número de ordem: return a.order - b.order;
    });
  }

  // TrackBy para melhor performance e consistência
  public trackByGridId(index: number, entity: CurrentGrid): string {
    return entity.grid; // ou entity.id se tiver
  }

  // Número de exibição baseado na posição ordenada
  public getRoomDisplayNumber(entity: CurrentGrid): number {
    const index = this.gridList.findIndex(grid => grid.grid === entity.grid);
    return index + 1; // Sempre retorna a posição atual na lista ordenada
  }


  // Método puro para verificação - não altera estado
  public isTabSelected(entity: CurrentGrid): boolean {
    return this.tabSelected?.grid === entity.grid;
  }

  /*
   *
   * Compartilha o Grid atualizado com o Observable para o compartilhar com outro subscriber
   *
   * */
  private shareGridWithSubscribers(grid: Array<Seat[]>) {
    this.gridObservable.updateGrid(grid);
  }



  /*
   * Converte SeatDto[] para Array<Seat[]> (formato usado pelo grid)
   */
  private convertSeatsDtoToGrid(seatsDto: SeatDto[]): Array<Seat[]> {
    if (!seatsDto || seatsDto.length === 0) {
      return [];
    }

    const maxRow = Math.max(...seatsDto.map(seat => seat.row));
    const maxCol = Math.max(...seatsDto.map(seat => seat.column));

    const grid: Array<Seat[]> = [];

    for (let row = 1; row <= maxRow; row++) {
      const rowArray: Seat[] = [];

      for (let col = 1; col <= maxCol; col++) {
        const seatDto = seatsDto.find(s => s.row === row && s.column === col);

        if (seatDto) {
          rowArray.push(this.convertSeatDtoToSeat(seatDto));
        } else {
          rowArray.push(this.createEmptySeat(row, col));
        }
      }

      grid.push(rowArray);
    }

    return grid;
  }

  /*
   * Converte um SeatDto para Seat
   */
  private convertSeatDtoToSeat(seatDto: SeatDto): Seat {
    return {
      gridId: seatDto.grid.grid,
      position: seatDto.position.toString(),
      row: seatDto.row,
      column: seatDto.column,
      selected: false,
      free: seatDto.free,
      status: seatDto.type,
      // Falta adicionar a pessao.
      person: { name: "", cpf: "" }
    };
  }

  /*
   * Cria um seat vazio para posições sem dados
   */
  private createEmptySeat(row: number, col: number): Seat {
    return {
      gridId: this.tabSelected?.grid || '',
      position: `seat-${row}-${col}`,
      row: row,
      column: col,
      selected: false,
      free: true,
      status: 'AVAILABLE',
      person: { name: "", cpf: "" }
    };
  }



  public async selectTab(grid: CurrentGrid): Promise<void> {
    try {
      // Se já é a tab selecionada, não faz nada
      if (this.isTabSelected(grid)) {
        return;
      }

      // Atualiza no backend
      await this.api.curretnGridSwitch(grid.grid);
      const allSeats: SeatDto[] = await this.api.fetchAllSeats();

      // Atualiza estado local de cada CurrentGrid.is_currentGrid
      // caso não seja equivalente ao recebido como parâmetro
      this.gridList.forEach(grid_parsed => {
        grid_parsed.is_currentGrid = grid_parsed.grid === grid.grid;
      });

      this.tabSelected = grid;

      const seatsById: SeatDto[] = [];

      allSeats.forEach(element => {
        if (element.grid.grid === grid.grid) {
          seatsById.push(element)
        }
      });

      // Converte e atualiza o grid
      this.grid = this.convertSeatsDtoToGrid(seatsById);
      this.shareGridWithSubscribers(this.grid);


      console.log('Tab selecionada:', grid.grid);

    } catch (error) {
      console.error('Erro ao alternar grid:', error);
      // Aqui você pode mostrar uma mensagem de erro para o usuário
    }
  }
}
