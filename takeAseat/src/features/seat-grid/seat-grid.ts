import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GridService_Observable } from '../../shared/services/grid-state';
import { Subscription } from 'rxjs';
// import { FaIconComponent } from '@fortawesome/angular-fontawesome';
// import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';
import { filter } from 'rxjs/operators';
import { ApiService } from '../../core/services/api-service';



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
  imports: [CommonModule/* , FaIconComponent */],
  templateUrl: './seat-grid.html',
  styleUrls: ['./seat-grid.scss']
})
export class SeatGridComponent {

  constructor(
    private gridObservable: GridService_Observable,
    private api : ApiService ) {}

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




  async ngOnInit() {
    const grid = await this.api.fetchAPI();
    if (grid == null) {
      this.generateGrid();
    }
    else {
      this.generateGrid(
        grid.entity.rowNumber,
        grid.entity.columnNumber
      );
    }

    // Envia grid inicial para o serviço. (Grid inicial é o grid que é gerado no
    // componente 'SeatGridComponent' pelos seus valores dfinidos em seu atributos).
    this.gridObservable.setInitialGrid(this.grid);

    // Escuta atualizações, mas ignora arrays vazios
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

    console.log(grid);
  }


  ngOnDestroy() {
    this.subscription.unsubscribe();
  }



  // Método para atualizar rows/columns baseado no grid
  private updateDimensions(grid: Array<Seat[]>) {
    if (grid && grid.length > 0) {
      this.rows = grid.length;
      this.columns = grid[0]?.length || 0;
    }
  }








  public toggleSeat(seat: Seat) {
    if (seat.reserved) return;

    seat.selected = !seat.selected;
    seat.status = seat.selected ? 'selected' : 'available';
    this.seatSelected.emit(seat);
    this.gridObservable.setInitialGrid(this.grid);
  }


  private generateGrid(rowDto? : number, columnDto? : number) {
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


  private generateColums(row: Seat[], rowCount: number, columnDto? : number) {
    if (columnDto) {
      for (let c = 0; c < columnDto; c++) {
        row.push({
          id: `seat-${rowCount}-${c}`,
          row: rowCount,
          column: c,
          selected: false,
          reserved: false,
          status: 'available'
        });
      }
    }
    else {
      for (let c = 0; c < this.columns; c++) {
        row.push({
          id: `seat-${rowCount}-${c}`,
          row: rowCount,
          column: c,
          selected: false,
          reserved: false,
          status: 'available'
        });
      }
    }
  }

}
