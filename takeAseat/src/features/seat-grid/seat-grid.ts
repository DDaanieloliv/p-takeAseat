import { Component, Input, Output, EventEmitter, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { GridService_Observable } from '../../shared/services/grid-state';
import { async, Subscription } from 'rxjs';
// import { FaIconComponent } from '@fortawesome/angular-fontawesome';
// import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';
import { faXmark, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { filter } from 'rxjs/operators';
import { ApiService } from '../../core/services/api-service';
import { GridDTO } from '../../core/model/fetch/grid-dto';
import { SafeStorageService } from '../../core/services/localStorageService/storage-service';
import { Seat } from '../../core/model/Seat';
import { strict } from 'node:assert';



@Component({
  selector: 'app-seat-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seat-grid.html',
  styleUrls: ['./seat-grid.scss']
})
export class SeatGridComponent {

  faXmark : IconDefinition = faXmark;

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
  public seatSelected : EventEmitter<Seat> = new EventEmitter<Seat>();





  public selectedSeatList : Array<Seat> = [];

  public is_visibleHandleSelection : boolean = true;





  ngOnDestroy() : void{
    this.subscription.unsubscribe();
  }

  private async make_aGridDTO_Request() : Promise<GridDTO | null> {
    let  dto : GridDTO | null = null;

    try {
      dto = await this.api.fetchAPI();
      console.log('Grid da API:', dto);
    } catch (error) {
      console.warn('API não disponível, usando fallback:', error);
      dto = null;
    }
    return dto;
  }

  async ngOnInit() : Promise<void> {

    this.setupGridSubscription();
    console.log('Esse é o grid armazenado no localStorage...');

    const savedState = this.safeStorage.getItem<GridDTO>('currentGrid');
    console.log('Saved: ', savedState);

    // let apiGrid : Promise<GridDTO | null> = this.make_aGridDTO_Request();
    let apiGrid : GridDTO | null = await this.make_aGridDTO_Request();

    // try {
    //     apiGrid = await this.api.fetchAPI();
    //     console.log('Grid da API:', apiGrid);
    // } catch (error) {
    //     console.warn('API não disponível, usando fallback:', error);
    //     apiGrid = null;
    // }

    // LÓGICA DE DECISÃO:
    if (savedState && apiGrid && this.isSameGridWithStates(savedState, apiGrid)) {
        // 1. Temos savedState E API respondeu E são compatíveis
        console.log('Carregando grid salvo do localStorage (compatível com API)');
        this.loadGridFromDTO(savedState);
        return;
    }
    else if (savedState && !apiGrid) {
        // 2. Temos savedState mas API não respondeu
        console.log('API indisponível, carregando grid do localStorage');
        this.loadGridFromDTO(savedState);
        return;
    }
    else if (apiGrid) {
        // 3. API respondeu (com ou sem savedState incompatível)
        console.log('Carregando grid da API');
        this.safeStorage.setItem('currentGrid', apiGrid);
        this.loadGridFromDTO(apiGrid);

        // Se tinha savedState mas era incompatível, substitui
        if (savedState) {
            console.log('Substituindo grid salvo incompatível');
            this.safeStorage.setItem('currentGrid', apiGrid);
        }
        return;
    }
    else {
        // 4. Nem savedState nem API - gera padrão
        console.log('Gerando grid padrão (sem savedState e sem API)');
        this.generateGrid();

        // Cria um GridDTO com o grid padrão
        const defaultGridDTO: GridDTO = {
            entity: {
                grid: "default-grid",
                rowNumber: this.rows,
                columnNumber: this.columns,
                is_currentGrid: true
            },
            grid: this.grid
        };
        // this.safeStorage.setItem('gridState', defaultGridDTO);
        this.safeStorage.setItem('currentGrid', defaultGridDTO);
    }
}

  private loadGridFromDTO(dto: GridDTO) : void {
    this.grid = dto.grid;
    this.rows = dto.entity.rowNumber;
    this.columns = dto.entity.columnNumber;
    this.shareGridWithSubscribers(this.grid);
    console.log("Number column: " + this.columns + "\nNumber row: " + this.rows);
  }


  private isSameGridWithStates(savedState: GridDTO, apiGrid: GridDTO): boolean {
    // Verifica se são a mesma entidade (mesmo UUID)
    const sameEntity = savedState.entity.grid === apiGrid.entity.grid;

    // Verifica se têm as mesmas dimensões
    const sameDimensions =
      savedState.entity.rowNumber === apiGrid.entity.rowNumber &&
        savedState.entity.columnNumber === apiGrid.entity.columnNumber;

    return sameEntity && sameDimensions;
  }


  /*
   *
   * Verifica se a lista responsável por armazenar as "Seat's" seleciondas está vazia ou não
   * vazia. E caso vazia defini o valor da propriedade is_visibleHandleSelection para true
   *
   * */
  public checkList() : boolean | void {
    if (this.selectedSeatList.length !== 0) {
      return this.is_visibleHandleSelection = false;

    } else if (this.selectedSeatList.length === 0) {
      return this.is_visibleHandleSelection = true;
    }
  }


  /*
   *
   * Alterna a propriedade 'status' do respectivo elemento 'Seat'
   *
   * */
  public toggleSeat(seat: Seat) :void {
    if (!seat.free) return;
    seat.selected = !seat.selected;
    seat.status = seat.selected ? 'SELECTED' : 'AVAILABLE';

    this.checkSelections(seat);
    this.checkList();
  }

  /*
   *
   * Compartilha o Grid atualizado com o Observable para o compartilhar com outro subscriber
   *
   * */
  private shareGridWithSubscribers(grid : Array<Seat[]>) {
    this.gridObservable.updateGrid(grid);
  }

  /*
   *
   * Muda o status de cada elemento 'Seat' presente na lista 'this.selectedSeatList'
   * que fazem parte do 'this.grid', atualiza o DTO do localStorage com as mudança
   * feitas em 'this.grid' e envia uma cópia desse DTO para a API
   *
   * */
  public confirm(): void {
    // Atualiza os assentos selecionados
    this.selectedSeatList.forEach((seat: Seat) => {
      if (seat.status === 'SELECTED') {
        seat.status = 'OCCUPIED';
        seat.free = false;
      }
    });

    // Atualiza o observable
    this.shareGridWithSubscribers(this.grid);

    const currentGrid: GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');

    if (currentGrid) {
      // Atualiza APENAS o grid (assentos) mantendo a entity
      const updatedGridDTO: GridDTO = {
        entity: currentGrid.entity, // Mantém a entity original
        grid: this.grid // Atualiza com as modificações
      };

      // Salva de volta na MESMA chave
      this.safeStorage.setItem('currentGrid', updatedGridDTO);

      // Envia para API (apenas os assentos modificados)
      const dto: GridDTO = {
        entity: currentGrid.entity,
        // grid: this.selectedSeatList
        grid: this.grid
      };
      this.api.updateGrid(dto);
      console.log('Enviado para API:', dto);
    } else {
      console.warn('currentGrid não encontrado');
    }

    const savedGrid = this.safeStorage.getItem<GridDTO>('currentGrid');
    console.log('Grid salvo localmente:', savedGrid);
    this.selectedSeatList = [];
    this.checkList();
  }


  /*
   *
   * Deve retirar a selection de cada um dos elementos 'Seat' em 'selectedSeatList'.
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

    this.selectedSeatList = [];
    console.log(this.selectedSeatList);
    this.checkList();
  }



  /*
   *
   * Defini o subscriber que para receber/obter o Grid
   * enviado pelo 'EditGridComponent' para o observable
   *
   * */
  private setupGridSubscription() :void {
    // Envia grid inicial para o serviço. (Grid inicial é o grid que é gerado no
    // componente 'SeatGridComponent' pelos seus valores definidos em seu atributos).
    // this.gridObservable.setInitialGrid(this.grid);
    this.subscription.add(
      this.gridObservable.current_grid$
        .pipe(
          filter(newGrid => newGrid && newGrid.length > 0) // Só emite se não for vazio
        )
        .subscribe((newGrid: Array<Seat[]>) => {
          this.grid = newGrid;
          // Atualiza dimensões
          this.updateDimensions(newGrid);
        })
    );

  }


  /*
   *
   * Atualiza os atributos rows/columns baseado no grid possuido
   *
   * */
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
   *
   * Gera um Grid de elementos 'Seat'(Array<Seat[]>) e o salva no atributo 'this.grid'
   *
   * */
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


  /*
   *
   * Função auxiliar para gerar as colunas de cada
   * indice da lista de seats(Seat[]) em Array<Seat[]>
   *
   * */
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
