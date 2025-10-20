import { Component, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faPencil, IconDefinition } from '@fortawesome/free-solid-svg-icons';
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

  public faPencil : IconDefinition = faPencil;
  public faXmark : IconDefinition  = faXmark;
  public faFloppyDisk : IconDefinition  = faFloppyDisk;
  public faReply : IconDefinition  = faReply;
  public faTrash : IconDefinition  = faTrash;


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
  public seatSelected : EventEmitter<Seat> = new EventEmitter<Seat>();

  @Output()
  public gridUpdated : EventEmitter<Array<Seat[]>> = new EventEmitter<Array<Seat[]>>();




  public showWindow : boolean = false

  public flagConfirmation : boolean = true;

  private should_erase_seat_state: boolean = false;

  public selectedSeatList : Array<Seat> = [];




  ngOnDestroy() : void {
    this.subscription.unsubscribe();
  }

  public ngOnInit() : void {
    // this.generateGrid();
    // Escuta o grid inicial do SeatGrid

    this.setupGridSubscription();

  }

  /*
   *
   * Defini o subscriber que para receber/obter o Grid
   * enviado pelo 'SeatGridComponent' para o observable
   *
   * */
  private setupGridSubscription() : void {
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


  /*
   *
   * Alterna o status do elemento 'Seat' no Grid
   *
   * */
  public toggleSeat(seat: Seat) : void {
    // if (!seat.free) return;
    console.log("Toggle Seat to UNAVAILABLE or AVAILABLE...")
    seat.selected = !seat.selected;
    seat.status = seat.selected ? 'UNAVAILABLE' : 'AVAILABLE';
    this.checkSelections(seat);
    console.log(seat);
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
   * Se a propriedade 'this.should_erase_seat_state' estiver como true
   * restaura o status inicial de cada elemento 'Seat' do grid, do contrário
   * define para falso o atributo 'free' de cada elemento 'Seat' na lista
   * 'this.selectedSeatList', envia o 'this.grid' atualizado para o observable,
   * para o localStorage e para a API
   *
   * */
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

      this.shareGridWithSubscribers(this.grid);

      // this.gridObservable.updateGrid(this.grid);
      // this.gridUpdated.emit(this.grid);

      const currentGrid : GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');

      if (currentGrid) {
        const gridState : GridDTO = {
          entity : {
            grid : currentGrid?.entity.grid,
            rowNumber : this.rows,
            columnNumber : this.columns,
            is_currentGrid : true
          },
          grid: this.grid
        };
        this.safeStorage.setItem('currentGrid', gridState);
      }



      if (currentGrid) {
        const dto : GridDTO = {
          entity: {
            grid : currentGrid.entity.grid,
            rowNumber : this.rows,
            columnNumber : this.columns,
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


  /*
   *
   * Caso o atributo 'this.should_erase_seat_state' seja true fechamos
   * o popup de Warning e caso contrário chamamos a função 'this.showBlurWindow()'
   *
   * */
  public handleCancel() {
    if (this.should_erase_seat_state) {
      this.warningPopup.hide();
      // console.log("ok")
    }
    else if (this.should_erase_seat_state === false) {
      this.showBlurWindow();
    }
  }


  /*
   *
   * Definimos a menssagem e o texto de confirmação que o popup
   * terá e sua respectiva flag para para indicar o texto e ação de confirmação que ele terá
   * e por fim abrindo o PopUp
   *
   * */
  public openWarning( flagConfirmation : boolean ) {
    this.warningPopup.message = this.pickMessageToConfirmation(flagConfirmation);
    this.warningPopup.confirmText = this.pickContentTextToConfirmation();

    this.should_erase_seat_state = flagConfirmation;

    this.warningPopup.show();
  }


  /*
   *
   * Restauramos ao estado padrão cada elemento 'Seat' do Grid atual 'this.grid' e
   * o removemos do localStorage e obtido os valores da entidade Grid que guardavamos
   * no localStorage fazemos uma requisição para API passando o Grid ao qual iremos
   * restaurar o status padrão
   *
   * */
  public erase_seat_state() : void {
    const currentGrid : GridDTO | null = this.safeStorage.getItem<GridDTO>('currentGrid');

    this.safeStorage.removeItem('currentGrid');
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

    if (currentGrid) {
      const dto : CurrentGrid = {
          grid : currentGrid.entity.grid,
          rowNumber : currentGrid.entity.rowNumber,
          columnNumber : currentGrid.entity.columnNumber,
          is_currentGrid : true
      }
      console.log("Enviando assentos modificados no componente de edição para a API...");
      console.log(dto);
      this.api.eraseGrid(dto);
    }
  }


  /*
   *
   * Lida a inserção de elementos 'Seat' para ser
   * inseridos na lista 'this.selectedSeatList'
   *
   * */
  public checkSelections(seat : Seat) : void {
    const existingIndex = this.selectedSeatList.findIndex(s => s.position === seat.position);

    if (existingIndex !== -1) {
        this.selectedSeatList[existingIndex] = seat;
    } else {
        this.selectedSeatList.push(seat);
    }
  }



  /*
   *
   * Modifica o atributo 'this.showWindow' que é usado para ativar uma classe scss
   * e apoz alternar o valor booleano desse atributo obtemos o Grid compartilhado pelo
   * component SeatGridComponent por meio do subscriber que definimos aqui
   *
   * */
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


  /*
   *
   * Modifica o tamanho das colunas do Grid ou linhas
   *
   * */
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


  /*
   *
   * Gera um grid novo baseado nas colunas e linhas acresentadas ou
   * decrescidas e preservando o estado de cada elemento 'Seat'
   *
   * */
  public updateGrid() : void {
    const oldGrid = this.grid;
    this.generateGrid();
    // Add the status and others properties that the oldGrid had to the new generated Grid.
    this.preserveSeatStates(oldGrid);
  }


  /*
   *
   * Função auxiliar para iterar sob cada um dos elementos 'Seat' do 'this.grid'
   * e obter o seu estado e ao por no seu respectivo elemento no no Grid
   *
   * */
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


  /*
   *
   * Função auxiliar para sabermos o máximo de vezes que iremos iterar sob
   * as colunas ou linhas do respectivo grid que seria o velho Grid
   *
   * */
  private MIN_SEARCHES_TO_ROWS( rows: number, oldGrid: Array<Seat[]> ) : number {
    // If rows value greater than oldGrid.length that means on edit
    // the rows number was icreased otherwise it was decrease.
    return Math.min(rows, oldGrid.length);
  }
  private MIN_SEARCHES_TO_COLUMNS( column: number, oldGrid : Array<Seat[]>, row: number ) : number {
    // If column value greater than oldGrid[index].length that means on edit
    // the column number was icreased otherwise it was decrease.
    return Math.min(column, oldGrid[row]?.length || 0 );
  }

  /*
   *
   * Função que é responsável por gerar o grid baseado no valores
   * dos atributos 'this.column' e 'this.row'
   *
   * */
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
        status : 'AVAILABLE',
        person: {
          name: "ok",
          cpf: "111"
        }
      });
    }
  }

  /*
   *
   * Retorna uma string que serviá de menssagem para o PopUp de warning
   *
   * */
  private pickMessageToConfirmation( IsSavedAction : boolean ) : string {
    let message = '';
    if (IsSavedAction) return message = 'Do you want to save thoses changes? <br> This Changes will be applied to the current template!'
    return message = 'Do you want leave thoses changes? <br> Every changes that you made will be discard!'
  }

  /*
   *
   * Apenas retorna a string 'Save'
   *
   * */
  private pickContentTextToConfirmation() : string {
    let message = '';
    return message = 'Save';
  }





}
