import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FieldError } from '../form-error-handler/FieldError';
import { PersonData } from '../../../../../core/model/Person';
import { SafeStorageService } from '../../../../../core/services/localStorageService/storage-service';
import { Seat } from '../../../../../core/model/Seat';
import { GridDTO } from '../../../../../core/model/fetch/grid-dto';
import { GridService_Observable } from '../../../../services/grid-state';
import { ApiService } from '../../../../../core/services/api-service';
import { CommonModule } from '@angular/common';
import { SeatsColection } from '../seatsSelectedComponent/seats-colection/seats-colection';

@Component({
  selector: 'app-form-component',
  standalone: true,
  imports: [CommonModule, SeatsColection],
  templateUrl: './form-component.html',
  styleUrl: './form-component.scss'
})
export class FormComponent {

  constructor(
    private gridObservable: GridService_Observable,
    private safeStorage: SafeStorageService,
    private api: ApiService,
  ) {}


  // @Input()
  public doNotShould_click : boolean = true;

  @Input()
  public personData : PersonData = { name: '', cpf: '' };

  @Input()
  public selectedSeatList : Array<Seat> = [];

  @Input()
  public is_visibleHandleSelection : boolean = true;

  @Input()
  public grid: Array<Seat[]> = [];



  @Output()
  public doNotShould_clickChange = new EventEmitter<boolean>();

  @Output()
  public selectedSeatListChange = new EventEmitter<Array<Seat>>();

  @Output()
  public is_visibleHandleSelectionChange = new EventEmitter<boolean>();


  public errorsMessage: FieldError[] = [];



  private emitSelectedSeatList(value: Array<Seat>): void {
    this.selectedSeatList = value;
    this.selectedSeatListChange.emit(value);
  }

  private emitIsVisibleHandleSelection(value: boolean): void {
    this.is_visibleHandleSelection = value;
    this.is_visibleHandleSelectionChange.emit(value);
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
   * Verifica se a lista responsável por armazenar as "Seat's" seleciondas está vazia ou não
   * vazia. E caso vazia defini o valor da propriedade is_visibleHandleSelection para true
   *
   * */
  public checkList() : boolean | void {
    if (this.selectedSeatList.length !== 0) {
      this.emitIsVisibleHandleSelection(false);
      return this.is_visibleHandleSelection = false;

    } else if (this.selectedSeatList.length === 0) {
      this.emitIsVisibleHandleSelection(true);
      return this.is_visibleHandleSelection = true;
    }
  }



  /*
   *
   * Muda o status de cada elemento 'Seat' presente na lista 'this.selectedSeatList'
   * que fazem parte do 'this.grid', atualiza o DTO do localStorage com as mudança
   * feitas em 'this.grid' e envia uma cópia desse DTO para a API
   *
   * */
  public confirm(): void {
    const seat : Seat | undefined = this.selectedSeatList.at(0);
    if (seat) {
      if (seat.status === 'SELECTED') {
        seat.status = 'OCCUPIED';
        seat.free = false;
        seat.person = {
          name: this.personData.name,
          cpf: this.personData.cpf?.replace(/\D/g, '')
        }
        this.selectedSeatList.shift();
      }
    }

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
    // this.selectedSeatList = [];
    this.checkList();
    this.clearInputElements();
    // this.emitSelectedSeatList( [] );
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
    this.emitSelectedSeatList( [] );

    console.log(this.selectedSeatList);

    // this.emitIsVisibleHandleSelection(true);
    this.checkList();
  }


  private clearAllErrorStyles(): void {
    const inputs = document.querySelectorAll('input[placeholder="First and Last name"], input[placeholder="CPF"]');
    inputs.forEach(input => {
      input.classList.remove('input-error');
      (input as HTMLInputElement).style.borderLeft = '';
    });
  }

  private clearAllErrors(): void {
    this.errorsMessage = [];
    this.clearAllErrorStyles();
  }

  public isFormValid(): void {
    setTimeout(() => {
      const nameInput = document.querySelector('input[placeholder="First and Last name"]') as HTMLInputElement;
      const cpfInput = document.querySelector('input[placeholder="CPF"]') as HTMLInputElement;

      const errors = this.inputValidate(nameInput, cpfInput);

      // Limpa erros anteriores
      this.clearAllErrors();

      if (errors.length === 0) {
        this.doNotShould_click = false;
      } else {
        this.doNotShould_click = true;
        this.errorsMessage = errors;

        // Aplica estilos visuais nos inputs com erro
        this.applyErrorStyles(errors);
      }
    }, 0);
  }

  // Métodos para usar no template
  hasError(field: 'name' | 'cpf'): boolean {
    return this.errorsMessage?.some(error => error.field === field) || false;
  }

  getErrorMessage(field: 'name' | 'cpf'): string {
    const error = this.errorsMessage?.find(e => e.field === field);
    return error?.message || '';
  }

  getErrorsForField(field: 'name' | 'cpf'): FieldError[] {
    return this.errorsMessage?.filter(error => error.field === field) || [];
  }


  private inputValidate(nameInput: HTMLInputElement, cpfInput: HTMLInputElement): FieldError[] {
    const nameValue = nameInput.value.trim();
    const cpfValue = cpfInput.value.replace(/\D/g, '');

    const errors: FieldError[] = [];

    // Validação do NOME
    if (nameValue.length === 0) {
      errors.push({
        field: 'name',
        message: "Nome é obrigatório",
        inputElement: nameInput
      });
    } else if (nameValue.length > 50) {
      errors.push({
        field: 'name',
        message: "Nome deve ter no máximo 50 caracteres",
        inputElement: nameInput
      });
    }

    // Validação do CPF
    if (cpfValue.length === 0) {
      errors.push({
        field: 'cpf',
        message: "CPF é obrigatório",
        inputElement: cpfInput
      });
    } else if (cpfValue.length !== 11) {
      errors.push({
        field: 'cpf',
        message: "CPF deve ter 11 dígitos",
        inputElement: cpfInput
      });
    } else if (/^(\d)\1{10}$/.test(cpfValue)) {
      errors.push({
        field: 'cpf',
        message: "CPF não pode ter todos dígitos iguais",
        inputElement: cpfInput
      });
    }

    return errors;
  }


  onNameInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value;

    value = value.replace(/[^a-zA-ZÀ-ÿ\s\-']/g, '');
    if (value.length > 50) {
      value = value.substring(0, 50);
    }

    this.personData.name = value;
    input.value = value;

    // 🔥 CORREÇÃO: Apenas valida, não limpa erros manualmente
    this.isFormValid();
  }


  private clearFieldErrors(field: 'name' | 'cpf'): void {
    if (this.errorsMessage) {
      this.errorsMessage = this.errorsMessage.filter(error => error.field !== field);
    }

    // Atualiza estilos visuais
    const input = document.querySelector(
      field === 'name'
        ? 'input[placeholder="First and Last name"]'
        : 'input[placeholder="CPF"]'
    ) as HTMLInputElement;

    if (input) {
      input.classList.remove('input-error', 'has-error');
      input.style.borderLeft = '';
    }
  }

  onCPFInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const lastChar = input.value.slice(-1);

    // Se o último caractere não é número e não é backspace, ignora
    if (!/\d/.test(lastChar) && lastChar !== '') {
      input.value = input.value.slice(0, -1);
      return;
    }

    // Limpa e formata
    let cleanValue = input.value.replace(/\D/g, '');
    if (cleanValue.length > 11) {
      cleanValue = cleanValue.substring(0, 11);
    }

    let formattedValue = cleanValue;
    if (cleanValue.length <= 3) {
      formattedValue = cleanValue;
    } else if (cleanValue.length <= 6) {
      formattedValue = cleanValue.replace(/(\d{3})(\d{0,3})/, '$1.$2');
    } else if (cleanValue.length <= 9) {
      formattedValue = cleanValue.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
    } else {
      formattedValue = cleanValue.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
    }

    // Atualiza o modelo
    this.personData.cpf = formattedValue;

    // Não limpa os erros imediatamente, apenas valida
    // O estilo será mantido/atualizado pela validação sem piscar
    this.isFormValid();
  }



  private clearInputElements(): void {
    // Usando setTimeout para garantir que a atualização do DOM já aconteceu
    setTimeout(() => {
      const nameInput = document.querySelector('input[placeholder="First and Last name"]') as HTMLInputElement;
      const cpfInput = document.querySelector('input[placeholder="CPF"]') as HTMLInputElement;

      if (nameInput) nameInput.value = '';
      if (cpfInput) cpfInput.value = '';
      // this.isFormValid();
      this.doNotShould_click = true;
    }, 0);
  }


  private applyErrorStyles(errors: FieldError[]): void {
    // Primeiro remove todos os estilos de erro
    this.clearAllErrorStyles();

    // Aplica estilos apenas nos inputs com erro
    errors.forEach(error => {
      if (error.inputElement) {
        error.inputElement.classList.add('input-error');

        // Adiciona um indicador visual específico para cada campo
        if (error.field === 'name') {
          error.inputElement.style.borderLeft = '4px solid #e74c3c';
        } else if (error.field === 'cpf') {
          error.inputElement.style.borderLeft = '4px solid #e74c3c';
        }
      }
    });
  }
}
