import { Component } from '@angular/core';
import { ApiService } from '../../../core/services/api-service';
import { CurrentGrid } from '../../../core/model/fetch/grid-entity-dto';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navigation-tabs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navigation-tabs.html',
  styleUrl: './navigation-tabs.scss'
})
export class NavigationTabs {

  constructor(private api: ApiService) { }

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
      // Ordena por grid ID ou por algum critério consistente
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

  public async selectTab(grid: CurrentGrid): Promise<void> {
    try {
      // Se já é a tab selecionada, não faz nada
      if (this.isTabSelected(grid)) {
        return;
      }

      // Atualiza no backend
      await this.api.curretnGridSwitch(grid.grid);

      // Atualiza estado local
      this.gridList.forEach(g => {
        g.is_currentGrid = g.grid === grid.grid;
      });

      this.tabSelected = grid;

      console.log('Tab selecionada:', grid.grid);

    } catch (error) {
      console.error('Erro ao alternar grid:', error);
      // Aqui você pode mostrar uma mensagem de erro para o usuário
    }
  }
}
