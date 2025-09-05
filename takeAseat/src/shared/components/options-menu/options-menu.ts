import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Adicione esta linha
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faTicket , faCirclePlus, faPencil } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-options-menu',
  imports: [ CommonModule, FaIconComponent ],
  templateUrl: './options-menu.html',
  styleUrl: './options-menu.scss'
})
export class OptionsMenu {

  protected faTicket = faTicket;

  protected faPlus = faCirclePlus;

  protected faPencil = faPencil;

}
