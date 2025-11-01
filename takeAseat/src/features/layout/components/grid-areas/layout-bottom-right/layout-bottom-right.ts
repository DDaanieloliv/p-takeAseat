import { Component } from '@angular/core';
import { ChartRowsCapacity } from '../../../../../shared/components/chats/chart-rows-capacity/chart-rows-capacity';


@Component({
  selector: 'app-layout-bottom-right',
  standalone: true,
  imports: [ChartRowsCapacity],
  templateUrl: './layout-bottom-right.html',
  styleUrl: './layout-bottom-right.scss'
})
export class LayoutBottomRight {

}
