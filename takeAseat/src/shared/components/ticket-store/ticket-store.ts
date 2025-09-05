import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faTicket } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-ticket-store',
  imports: [ CommonModule, FaIconComponent ],
  templateUrl: './ticket-store.html',
  styleUrl: './ticket-store.scss'
})
export class TicketStore {

  protected faTicket = faTicket;

}
