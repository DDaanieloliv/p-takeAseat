import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Seat } from '../../../../../../core/model/Seat';

@Component({
  selector: 'app-seats-colection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seats-colection.html',
  styleUrl: './seats-colection.scss'
})
export class SeatsColection {

  @Input()
  public selectedSeatList : Array<Seat> = [];

}
