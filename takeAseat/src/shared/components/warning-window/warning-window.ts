import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faTriangleExclamation } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-warning-popup',
  standalone: true,
  imports: [CommonModule, FaIconComponent],
  templateUrl: './warning-window.html',
  styleUrls: ['./warning-window.scss']
})
export class WarningPopupComponent {

  // Property Binding
  @Input()
  public title: string = 'Atenção!';

  @Input()
  public message: string = 'Tem certeza que deseja executar esta ação?';

  @Input()
  public cancelText: string = 'Cancelar';

  @Input()
  public confirmText: string = 'Continuar';



  // Event Binding
  @Output()
  public cancel = new EventEmitter<void>();

  @Output()
  public confirm = new EventEmitter<void>();


  public isVisible: boolean = false;

  public faTriangleExclamation = faTriangleExclamation;



  public show() {
    this.isVisible = true;
    document.body.style.overflow = 'hidden'; // Previne scroll
  }

  public hide() {
    this.isVisible = false;
    document.body.style.overflow = ''; // Restaura scroll
  }

  public onConfirm() {
    this.confirm.emit();
    this.hide();
  }

  public onCancel() {
    this.cancel.emit();
    this.hide();
  }
}
