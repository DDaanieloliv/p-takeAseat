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
import { GridUpdatedDTO } from '../../core/model/fetch/seatsUpdated-dto';
import { Seat } from '../../core/model/Seat';
import { Console } from 'node:console';
import { GridState } from '../../core/model/fetch/GridState';



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

  public selectedSeatList : Array<Seat> = [];

  public is_visibleHandleSelection : boolean = true;


  // public selectedSeatList_isEmpty = true;
  //
  // public shouldShowButtom() : boolean {
  //   return this.selectedSeatList_isEmpty;
  // }
  //
  // public checkList() : boolean {
  //   if (this.selectedSeatList.length < 1) {
  //    return false;
  //   }
  //   return true;
  // }





  ngOnDestroy() {
    this.subscription.unsubscribe();
  }


  // async ngOnInit() {
  //   // this.is_visibleHandleSelection = true;
  //
  //   // Configura a subscription
  //   this.setupGridSubscription();
  //   console.log('Esse é o grid armazenado no localStorege...');
  //   console.log(this.safeStorage.getItem<GridDTO>('gridState'));
  //
  //
  //   const savedState = this.safeStorage.getItem<GridDTO>('gridState');
  //   let grid: GridDTO | null = null;
  //
  //   try {
  //       grid = await this.api.fetchAPI();
  //       console.log('Grid da API:', grid);
  //   } catch (error) {
  //       console.warn('API não disponível, usando fallback:', error);
  //       grid = null;
  //   }
  //
  //
  //   if (savedState && grid && this.isSameGridWithStates(savedState, grid)) {
  //     console.log('Carregando grid salvo do localStorage');
  //     this.safeStorage.setItem('gridState', grid);
  //     this.grid = savedState.grid;
  //     this.rows = savedState.entity.rowNumber;
  //     this.columns = savedState.entity.columnNumber;
  //     this.gridObservable.updateGrid(this.grid);
  //     console.log("Number column: " + this.columns + "\nNumber row: " + this.rows)
  //     return;
  //
  //   }else if (!savedState || !grid) {
  //     console.log("Gerando grid sem api...")
  //     try {
  //       if (grid == null) {
  //         this.generateGrid();
  //         const new_grid : GridDTO = {
  //           entity : {
  //             grid : "",
  //             rowNumber : this.rows,
  //             columnNumber : this.columns,
  //             is_currentGrid : true
  //           },
  //           grid : this.grid
  //         }
  //         console.log("Aaaaaa!")
  //         this.grid = new_grid.grid;
  //         console.log(this.grid)
  //         this.safeStorage.setItem('gridState', new_grid);
  //       } else {
  //         // USA O SERVIÇO SEGURO
  //
  //         this.generateGrid(
  //           grid.entity.rowNumber,
  //           grid.entity.columnNumber
  //         );
  //         this.rows = grid.entity.rowNumber;
  //         this.columns = grid.entity.columnNumber;
  //         console.log("column number from api: " + this.columns + "\nrow number from api: " + this.rows)
  //
  //         this.safeStorage.setItem('gridState', grid);
  //       }
  //       // Verificação adicional de segurança
  //       if (!isPlatformBrowser(this.platformId)) {
  //         // Se estiver no servidor, gera grid padrão e retorna
  //         this.generateGrid();
  //         return;
  //       }
  //       // Recupera do storage para verificação
  //       const storedGrid = this.safeStorage.getItem<GridDTO>('gridState');
  //       console.log('Grid from storage:', storedGrid);
  //
  //     } catch (error) {
  //       console.error('Error in ngOnInit:', error);
  //       this.generateGrid(); // Fallback
  //     }
  //   }
  //
  // }

  async ngOnInit() {
    this.setupGridSubscription();

    // Use GridState para o localStorage
    const savedState = this.safeStorage.getItem<GridState>('gridState');
    console.log('Esse é o grid armazenado no localStorage...', savedState);

    let grid: GridDTO | null = null;

    try {
      grid = await this.api.fetchAPI();
      console.log('Grid da API:', grid);
    } catch (error) {
      console.warn('API não disponível, usando fallback:', error);
      grid = null;
    }

    // PRIMEIRO: Se tem estado salvo, usa ele
    if (savedState && this.isValidGridState(savedState)) {
      console.log('Carregando grid salvo do localStorage');
      this.grid = savedState.grid;
      this.rows = savedState.dimensions.rows;
      this.columns = savedState.dimensions.columns;
      this.gridObservable.updateGrid(this.grid);
      console.log("Number column: " + this.columns + "\nNumber row: " + this.rows);
      return;
    }

    // SEGUNDO: Se não tem estado salvo, usa API ou gera padrão
    console.log("Gerando grid sem estado salvo...");

    if (grid) {
      // Tem API
      console.log("Usando dados da API");
      this.safeStorage.setItem('currentGrid', grid); // Salva GridDTO completo

      this.generateGrid(grid.entity.rowNumber, grid.entity.columnNumber);
      this.rows = grid.entity.rowNumber;
      this.columns = grid.entity.columnNumber;
    } else {
      // Sem API - gera padrão
      console.log("Gerando grid padrão");
      this.generateGrid();
    }

    // Salva o estado inicial
    const gridState: GridState = {
      grid: this.grid,
      dimensions: { rows: this.rows, columns: this.columns },
      timestamp: new Date().toISOString()
    };
    this.safeStorage.setItem('gridState', gridState);

    console.log('Grid inicial salvo:', gridState);
  }

  private isValidGridState(state: any): state is GridState {
    return state &&
      state.grid &&
      Array.isArray(state.grid) &&
      state.dimensions &&
      typeof state.dimensions.rows === 'number' &&
      typeof state.dimensions.columns === 'number';
  }



  // /*
  //  *
  //  * Deve atualizar o backend com uma nova estrutura grid.
  //  *
  //  * */
  // public confirm(): void {
  //   // this.api.updateGrid();
  //   this.is_visibleHandleSelection = true;
  //   console.log(this.selectedSeatList);
  //   /*
  //    * Send the grid modified to a service that shares it with edit-grid
  //    *
  //    * */
  //   this.gridObservable.updateGrid(this.grid);
  //
  //   const saved_dto : GridDTO | null = this.safeStorage.getItem<GridDTO>('gridState');
  //
  //
  //   this.selectedSeatList.forEach
  //     ((seat : Seat) => {
  //       if (seat.status === 'SELECTED') {
  //         seat.status = 'OCCUPIED';
  //         seat.free = false;
  //       }
  //     } );
  //
  //   // this.safeStorage.setItem('gridState', this.grid);
  //   const gridState = {
  //     grid: this.grid,
  //     dimensions: {
  //       rows: this.rows,
  //       columns: this.columns
  //     },
  //     timestamp: new Date().toISOString()
  //   };
  //
  //   this.safeStorage.setItem('gridState', gridState);
  //
  //   if (saved_dto) {
  //
  //     const dto : GridDTO = {
  //       entity: {
  //         grid : saved_dto.entity.grid,
  //         rowNumber : saved_dto.entity.rowNumber,
  //         columnNumber : saved_dto.entity.columnNumber,
  //         is_currentGrid : true
  //       },
  //       grid: this.grid
  //     }
  //     this.api.updateGrid(dto);
  //     console.log(dto);
  //   }
  //
  //   console.log('GridState salvo:', gridState);
  //   this.selectedSeatList = [];
  // }

  public confirm(): void {
    this.is_visibleHandleSelection = true;

    // Atualiza os assentos selecionados
    this.selectedSeatList.forEach((seat: Seat) => {
      if (seat.status === 'SELECTED') {
        seat.status = 'OCCUPIED';
        seat.free = false;
      }
    });

    // Atualiza o observable
    this.gridObservable.updateGrid(this.grid);

    // Salva o ESTADO LOCAL
    const gridState: GridState = {
      grid: this.grid,
      dimensions: { rows: this.rows, columns: this.columns },
      timestamp: new Date().toISOString()
    };
    this.safeStorage.setItem('gridState', gridState);

    // Envia para API (se disponível)
    const saved_dto: GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');
    if (saved_dto) {
      const dto: GridUpdatedDTO = {
        entity: {
          grid: saved_dto.entity.grid,
          rowNumber: saved_dto.entity.rowNumber,
          columnNumber: saved_dto.entity.columnNumber,
          is_currentGrid: true
        },
        grid: this.selectedSeatList // Apenas os modificados
      };
      this.api.updateGrid(dto);
      console.log('Enviado para API:', dto);
    }

    console.log('GridState salvo localmente:', gridState);
    this.selectedSeatList = [];
  }


  /*
   *
   * Deve retirar a selection de cada um dos elementos LocalSeat em 'selectedSeatList'.
   *
   * */
  public cancel(): void {
    /*
     * Se o array muda de tamanho durante a iteração > comportamento imprevisível
     * Se possível evite alterações na maioria estruturas de dados durante iterações,
     * adote a programação funcional.
     * */
    this.selectedSeatList.forEach
      ((seat : Seat) => {
        seat.selected = !seat.selected;
        seat.status = seat.selected ? 'SELECTED' : 'AVAILABLE';
      } );

    this.is_visibleHandleSelection = true;

    this.selectedSeatList = [];
    console.log(this.selectedSeatList);
  }

  private isSameGridWithStates(savedState: GridDTO, grid: GridDTO): boolean {
    const sameEntity = savedState &&
      savedState.entity &&
      savedState.entity.grid === grid.entity.grid;

    if (!sameEntity) return false;

    const compatibleDimensions =
      savedState.entity.rowNumber === grid.entity.rowNumber &&
        savedState.entity.columnNumber === grid.entity.columnNumber;

    return compatibleDimensions;
  }



  private setupGridSubscription() :void {
    // Envia grid inicial para o serviço. (Grid inicial é o grid que é gerado no
    // componente 'SeatGridComponent' pelos seus valores definidos em seu atributos).
    // this.gridObservable.setInitialGrid(this.grid);
    this.subscription.add(
      this.gridObservable.current_grid$
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
   * Verifica se o parâmetro Seat existe em 'selectedSeatList', e o substitui pelo seu
   * elemento cópia mais recente ou faz um fresh insert.
   *
   * Qualquer modificação nos elementos dentro de selectedSeatList repercutirá nos
   * elementos instanciados.
   *
   * */
  public checkSelections(seat : Seat) : void {

    const existingIndex = this.selectedSeatList.findIndex(s => s.position === seat.position);

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
   * Toggle de visualização do menu de handle-confirmation.
   *
   * */
  public handleSelection() : void {
    if (this.selectedSeatList.length > 0) {
      this.is_visibleHandleSelection = false;
    }
  }



  public toggleSeat(seat: Seat) {
    if (!seat.free) return;

    // this.checkList()
    seat.selected = !seat.selected;
    // If 'seat.selected' == true, 'seat.status' = 'selected' other wise 'seat.status' = 'available'
    seat.status = seat.selected ? 'SELECTED' : 'AVAILABLE';
    // this.seatSelected.emit(seat);

    this.checkSelections(seat);
    this.handleSelection();
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
          position: `seat-${rowCount + 1}-${c + 1}`,
          row: rowCount + 1,
          column: c + 1,
          selected: false,
          free: true,
          status: 'AVAILABLE'
        });
      }
    }
    else {
      for (let c = 0; c < this.columns; c++) {
        row.push({
          position: `seat-${rowCount + 1}-${c + 1}`,
          row: rowCount + 1,
          column: c + 1,
          selected: false,
          free: true,
          status: 'AVAILABLE'
        });
      }
    }
  }

}
