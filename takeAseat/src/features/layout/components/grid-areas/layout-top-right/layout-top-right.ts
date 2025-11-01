import { Component } from '@angular/core';
import { ChartCurrentCapacity } from '../../../../../shared/components/chats/chart-current-capacity/chart-current-capacity';



@Component({
  selector: 'app-layout-top-right',
  standalone: true,
  imports: [ChartCurrentCapacity],
  templateUrl: './layout-top-right.html',
  styleUrl: './layout-top-right.scss'
})
export class LayoutTopRight {

}
