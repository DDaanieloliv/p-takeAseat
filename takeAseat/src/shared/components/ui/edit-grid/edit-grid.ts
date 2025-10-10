import { Component, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faPencil } from '@fortawesome/free-solid-svg-icons';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { faFloppyDisk } from '@fortawesome/free-solid-svg-icons';
import { faReply } from '@fortawesome/free-solid-svg-icons';
import { WarningPopupComponent } from '../warning-window/warning-window';
import { GridService_Observable } from '../../../services/grid-state';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { take } from 'rxjs/operators';
import { Seat } from '../../../../core/model/Seat';
import { SafeStorageService } from '../../../../core/services/localStorageService/storage-service';
import { GridDTO } from '../../../../core/model/fetch/grid-dto';
import { GridUpdatedDTO } from '../../../../core/model/fetch/seatsUpdated-dto';
import { ApiService } from '../../../../core/services/api-service';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { CurrentGrid } from '../../../../core/model/fetch/grid-entity-dto';
// import { SeatGridComponent } from '../../../features/seat-grid/seat-grid';
// import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';



@Component({
  selector: 'app-edit-grid',
  standalone: true,
  imports: [ CommonModule, FaIconComponent, WarningPopupComponent /* , SeatGridComponent  */],
  templateUrl: './edit-grid.html',
  styleUrl: './edit-grid.scss'
})
export class EditGrid {

  constructor(
    private gridObservable: GridService_Observable,
    private safeStorage: SafeStorageService,
    private api: ApiService
  ) {}

  private subscription: Subscription = new Subscription();



  // Estabelecendo cominicação Imperativa, ou seja sem ser apenas pelo template.
  // Permitindo acessar métodos, propriedades publicas do filho e ter o controle programático
  // e que o pai reaja a eventos do filho devido ao EventEmiter.emit() desencadeando um evento resposta do pai.
  // Ou seja um PONTEIRO para a instância do WarningPopupComponent
  @ViewChild('warningPopup')
  private warningPopup! : WarningPopupComponent;

  @Input()
  public rows : number = 12;

  @Input()
  public columns : number = 36;

  @Input() grid: Array<Seat[]> = [];

  @Output()
  public seatSelected = new EventEmitter<Seat>();

  @Output()
  public gridUpdated = new EventEmitter<Array<Seat[]>>();


  public showWindow : boolean = false

  public flagConfirmation : boolean = true;

  private should_erase_seat_state: boolean = false;

  public faPencil = faPencil;
  public faXmark = faXmark;
  public faFloppyDisk = faFloppyDisk;
  public faReply = faReply;
  public faTrash = faTrash;

  public selectedSeatList : Array<Seat> = [];




  ngOnDestroy() {
    this.subscription.unsubscribe();
  }


  public ngOnInit() : void {
    // this.generateGrid();
    // Escuta o grid inicial do SeatGrid
    this.subscription.add(
      this.gridObservable.current_grid$
        .pipe(
          filter(current_grid => current_grid && current_grid.length > 0)
        )
        .subscribe(initialGrid => {
          setTimeout(() => {
            this.grid = initialGrid;
            this.rows = initialGrid.length;
            this.columns = initialGrid[0]?.length || 0;

            this.updateGrid();

          });
        })
    );
    // Se não receber grid inicial, gera um
    setTimeout(() => {
      if (!this.grid || this.grid.length === 0) {
        this.generateGrid();
      }
    }, 100);

  }


  public toggleSeat(seat: Seat) : void {
    // if (!seat.free) return;
    console.log("Toggle Seat to UNAVAILABLE or AVAILABLE...")
    // seat.free = seat.free ? false : true;
    seat.selected = !seat.selected;
    seat.status = seat.selected ? 'UNAVAILABLE' : 'AVAILABLE';
    // this.seatSelected.emit(seat);
    this.checkSelections(seat);
    console.log(seat);
  }


  public handleConfirm() {
    if (this.should_erase_seat_state) {
      this.erase_seat_state();
    }
    else if (this.should_erase_seat_state === false) {
      console.log("Should Emmit the new Grid format.");

      this.selectedSeatList.forEach
        ((seat : Seat) => {
            console.log("Iterando sobre cada assento selecionado...")
            seat.free = !seat.free;
            // seat.status = seat.status === 'UNAVAILABLE' ? 'AVAILABLE' : 'UNAVAILABLE';
        } );
      console.log("Assentos modificados pela iteração...");
      console.log(this.selectedSeatList);


      this.gridObservable.updateGrid(this.grid);
      // this.gridUpdated.emit(this.grid);

      const gridState = {
        grid: this.grid,
        dimensions: {
          rows: this.rows,
          columns: this.columns
        },
        timestamp: new Date().toISOString()
      };

      this.safeStorage.setItem('gridState', gridState);

      const saved_dto : GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');

      if (saved_dto) {
        const dto : GridDTO = {
          entity: {
            grid : saved_dto.entity.grid,
            rowNumber : saved_dto.entity.rowNumber,
            columnNumber : saved_dto.entity.columnNumber,
            is_currentGrid : true
          },
          grid: this.grid
        }
        console.log("Enviando assentos modificados no componente de edição para a API...");
        console.log(dto);
        this.api.updateGrid(dto);
      }
      this.selectedSeatList = [];
      this.showBlurWindow();
    }
  }

  public handleCancel() {
    if (this.should_erase_seat_state) {
      this.warningPopup.hide();
      // console.log("ok")
    }
    else if (this.should_erase_seat_state === false) {
      this.showBlurWindow();
    }
  }


  public openWarning( flagConfirmation : boolean ) {
    this.warningPopup.message = this.pickMessageToConfirmation(flagConfirmation);
    this.warningPopup.confirmText = this.pickContentTextToConfirmation();

    this.should_erase_seat_state = flagConfirmation;

    this.warningPopup.show();
  }



  public erase_seat_state() : void {
    this.safeStorage.removeItem('gridState');
    // this.safeStorage.removeItem('currentGrid');

    console.log("Apagando essa porra");

    this.grid.forEach((seatList : Array<Seat>) => {
      seatList.forEach(
        (seat : Seat) => {
          seat.selected = false;
          seat.status = 'AVAILABLE';
          seat.free = true;
        }
      )
    });
    // const gridState = {
    //   grid: this.grid,
    //   dimensions: {
    //     rows: this.rows,
    //     columns: this.columns
    //   },
    //   timestamp: new Date().toISOString()
    // };


    const saved_dto : GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');

    if (saved_dto) {
      const dto : CurrentGrid = {
          grid : saved_dto.entity.grid,
          rowNumber : saved_dto.entity.rowNumber,
          columnNumber : saved_dto.entity.columnNumber,
          is_currentGrid : true
      }
      console.log("Enviando assentos modificados no componente de edição para a API...");
      console.log(dto);
      this.api.eraseGrid(dto);
    }
  }


  public checkSelections(seat : Seat) : void {

    const existingIndex = this.selectedSeatList.findIndex(s => s.position === seat.position);

    if (existingIndex !== -1) {
        this.selectedSeatList[existingIndex] = seat;
    } else {
        this.selectedSeatList.push(seat);
    }

  }


  public showBlurWindow() : void {
    this.showWindow = !this.showWindow;
    console.log("fuck");

    if (this.showWindow) {
      this.subscription.add(
        this.gridObservable.current_grid$
          .pipe(
            filter(current_grid => current_grid && current_grid.length > 0),
            take(1)
          )
          .subscribe(current_grid => {
            setTimeout(() => {
              this.grid = current_grid;
              this.rows = current_grid.length;
              this.columns = current_grid[0]?.length || 0;

              this.updateGrid();

            });
          })
      );

    }

  }



  public increaseColumns() : void {
    this.columns = this.columns + 1;
    this.updateGrid();
  }

  public decreaseColumns() : void {
    this.columns = this.columns - 1;
    this.updateGrid();
  }


  public increaseRows() : void {
    this.rows = this.rows + 1;
    this.updateGrid();
  }

  public decreaseRows() : void {
    this.rows = this.rows - 1;
    this.updateGrid();
  }







  public updateGrid() : void {
    const oldGrid = this.grid;
    // Generate a new grid based on increased or decreased rows or columns.
    this.generateGrid();
    // Add the status and others properties that the oldGrid had to the new generated Grid.
    this.preserveSeatStates(oldGrid);
  }

  // // Método para deep copy de todo o grid
  // private deepCopyGrid(grid: Array<Seat[]>): Array<Seat[]> {
  //   return grid.map(row =>
  //     row.map(seat => ({
  //       ...seat // Spread operator cria cópias
  //     }))
  //   );
  // }


  private preserveSeatStates(oldGrid : Array<Seat[]>) : void  {

    for (let row = 0; row < this.MIN_SEARCHES_TO_ROWS(this.rows, oldGrid); row++) {

      for (let column = 0; column < this.MIN_SEARCHES_TO_COLUMNS(this.columns, oldGrid, row); column++) {
        // Equivale a um .isPresent no JAVA.
        if (oldGrid[row] && oldGrid[row][column]) {

          // Preserva o estado do assento
          this.grid[row][column].selected = oldGrid[row][column].selected;
          this.grid[row][column].free = oldGrid[row][column].free;
          this.grid[row][column].status = oldGrid[row][column].status;
        }
      }

    }
  }

  public MIN_SEARCHES_TO_ROWS( rows: number, oldGrid: Array<Seat[]> ) : number {
    // If rows value greater than oldGrid.length that means on edit
    // the rows number was icreased otherwise it was decrease.
    return Math.min(rows, oldGrid.length);
  }

  public MIN_SEARCHES_TO_COLUMNS( column: number, oldGrid : Array<Seat[]>, row: number ) : number {
    // If column value greater than oldGrid[index].length that means on edit
    // the column number was icreased otherwise it was decrease.
    return Math.min(column, oldGrid[row]?.length || 0 );
  }


  private generateGrid() {
    this.grid = [];

    for ( let rowCount = 0; rowCount < this.rows; rowCount++ ) {
      const rowArray : Seat[] = [];

      this.generateColums(rowArray, rowCount);
      this.grid.push(rowArray);
    }
  }

  private generateColums(row : Seat[], rowCount : number) {
    for (let c = 0; c < this.columns; c++) {
      row.push({
        position : `seat-${rowCount + 1}-${c + 1}`,
        row : rowCount + 1,
        column : c + 1,
        selected : false,
        free : false,
        status : 'AVAILABLE'
      });
    }
  }




  private pickMessageToConfirmation( IsSavedAction : boolean ) : string {
    let message = '';

    if (IsSavedAction) return message = 'Do you want to save thoses changes? <br> This Changes will be applied to the current template!'

    return message = 'Do you want leave thoses changes? <br> Every changes that you made will be discard!'
  }


  private pickContentTextToConfirmation() : string {
    let message = '';

    return message = 'Save';
  }





}
