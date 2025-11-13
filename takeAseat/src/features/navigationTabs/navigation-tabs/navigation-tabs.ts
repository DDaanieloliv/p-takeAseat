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

  constructor(private api: ApiService) {}

  public gridList: CurrentGrid[] = [];

  async ngOnInit(): Promise<void> {
    try {
      this.gridList = await this.api.fetchListGrid();
      console.log("GridList carregado:", this.gridList);
    } catch (error) {
      console.error('Erro ao carregar grids:', error);
      this.gridList = [];
    }
  }

  public selectTab(ID: string) : void {
    this.api.curretnGridSwitch(ID)
    console.log(ID);
  }

}
