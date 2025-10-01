import { Component } from '@angular/core';
// import { DecorativePretendBackground } from '../decorative-pretend-background/decorative-pretend-background';
import { DecorativeBarr } from '../decorative-barr/decorative-barr';
import { SeatGridComponent } from '../../../../seat-grid/seat-grid';

@Component({
  selector: 'app-layout-main',
  standalone: true,
  imports: [/* DecorativePretendBackground, */ DecorativeBarr, SeatGridComponent ],
  templateUrl: './layout-main.html',
  styleUrl: './layout-main.scss'
})
export class LayoutMain {

}
