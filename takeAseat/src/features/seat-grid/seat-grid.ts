import { Component, Input, Output, EventEmitter, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { GridService_Observable } from '../../shared/services/grid-state';
import { Subscription } from 'rxjs';
// import { FaIconComponent } from '@fortawesome/angular-fontawesome';
// import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { filter } from 'rxjs/operators';
import { ApiService } from '../../core/services/api-service';
import { GridDTO } from '../../core/model/fetch/grid-dto';
import { SafeStorageService } from '../../core/services/localStorageService/storage-service';



export interface Seat {
  id: string;
  row: number;
  column: number;
  selected: boolean;
  reserved: boolean;
  status: 'available' | 'reserved' | 'unavailable' | 'maintenance' | 'disabled' | 'selected';
}
@Component({
  selector: 'app-seat-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seat-grid.html',
  styleUrls: ['./seat-grid.scss']
})
export class SeatGridComponent {

  faXmark = faXmark;

  constructor(
    private gridObservable: GridService_Observable,
    private api: ApiService,
    private safeStorage: SafeStorageService,
    @Inject(PLATFORM_ID) private platformId: any
  ) { }

  private subscription: Subscription = new Subscription();



  @Input()
  public rows: number = 8;

  @Input()
  public columns: number = 22;

  @Input()
  public newGrid: Array<Seat[]> = [];

  @Output()
  public grid: Array<Seat[]> = [];

  @Output()
  public seatSelected = new EventEmitter<Seat>();


  public somethingSelected = false;


  async ngOnInit() {
    // Verificação adicional de segurança
    if (!isPlatformBrowser(this.platformId)) {
      // Se estiver no servidor, gera grid padrão e retorna
      this.generateGrid();
      return;
    }

    try {
      // Obtem o json da api
      const grid: GridDTO = await this.api.fetchAPI();

      if (grid == null) {
        this.generateGrid();
      } else {
        // USA O SERVIÇO SEGURO
        this.safeStorage.setItem('currentGrid', grid);

        this.generateGrid(
          grid.entity.rowNumber,
          grid.entity.columnNumber
        );
      }

      // Configura a subscription
      this.setupGridSubscription();

      // Recupera do storage para verificação
      const storedGrid = this.safeStorage.getItem<GridDTO>('currentGrid');
      console.log('Grid from storage:', storedGrid);

    } catch (error) {
      console.error('Error in ngOnInit:', error);
      this.generateGrid(); // Fallback
    }
  }


  ngOnDestroy() {
    this.subscription.unsubscribe();
  }


  private setupGridSubscription() :void {
    // Envia grid inicial para o serviço. (Grid inicial é o grid que é gerado no
    // componente 'SeatGridComponent' pelos seus valores definidos em seu atributos).
    // this.gridObservable.setInitialGrid(this.grid);
    this.subscription.add(
      this.gridObservable.currentGrid$
        .pipe(
          filter(newGrid => newGrid && newGrid.length > 0) // ← Só emite se não for vazio
        )
        .subscribe((newGrid: Array<Seat[]>) => {
          this.grid = newGrid;
          // Atualiza dimensões
          this.updateDimensions(newGrid);
        })
    );

  }


  // Método para atualizar rows/columns baseado no grid
  private updateDimensions(grid: Array<Seat[]>) {
    if (grid && grid.length > 0) {
      this.rows = grid.length;
      this.columns = grid[0]?.length || 0;
    }
  }



  /*
   *
   * Cuidado!! Qualquer modificação nos elementos dentro dessa estrutura irá refletir
   * nos elementos Seat em memória, visto que estamos passando referências para o ARRAY
   *
   * */
  public checkSelections(seat : Seat) : void {

    const existingIndex = this.selectedSeatList.findIndex(s => s.id === seat.id);

    if (existingIndex !== -1) {
      if (!seat.selected) {
        this.selectedSeatList.splice(existingIndex, 1);
      } else {
        this.selectedSeatList[existingIndex] = seat;
      }
    } else {
      if (seat.selected) {
        this.selectedSeatList.push(seat);
      }
    }

  }


  /*
   *
   * When confirm, update tha backend with a new grid structure
   *
   * */
  public confirm(): void {
    // this.api.updateGrid();
    console.log(this.selectedSeatList);
  }


  public selectedSeatList : Array<Seat> = [];

  public toggleSeat(seat: Seat) {
  if (seat.reserved) return;

  seat.selected = !seat.selected;
  // If 'seat.selected' == true, 'seat.status' = 'selected' other wise 'seat.status' = 'available'
  seat.status = seat.selected ? 'selected' : 'available';
  this.seatSelected.emit(seat);

  this.checkSelections(seat);

  /*
   * Send the grid modified to a service that shares it with edit-grid
   *
   * */
  // this.gridObservable.setInitialGrid(this.grid);
}


  private generateGrid(rowDto ? : number, columnDto ? : number) {
  this.grid = [];

  if (rowDto && columnDto) {
    for (let rowCount = 0; rowCount < rowDto; rowCount++) {
      const rowArray: Seat[] = [];

      this.generateColums(rowArray, rowCount, columnDto);
      this.grid.push(rowArray);
    }
  }
  else {
    for (let rowCount = 0; rowCount < this.rows; rowCount++) {
      const rowArray: Seat[] = [];

      this.generateColums(rowArray, rowCount);
      this.grid.push(rowArray);
    }
  }
}


  private generateColums(row: Seat[], rowCount: number, columnDto ? : number) {
  if (columnDto) {
    for (let c = 0; c < columnDto; c++) {
      row.push({
        id: `seat-${rowCount + 1}-${c + 1}`,
        row: rowCount + 1,
        column: c + 1,
        selected: false,
        reserved: false,
        status: 'available'
      });
    }
  }
  else {
    for (let c = 0; c < this.columns; c++) {
      row.push({
        id: `seat-${rowCount + 1}-${c + 1}`,
        row: rowCount + 1,
        column: c + 1,
        selected: false,
        reserved: false,
        status: 'available'
      });
    }
  }
}

}
