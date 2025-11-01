import { Component } from '@angular/core';
import { ChartRoomsCapacity } from '../../../../../shared/components/chats/chart-rooms-capacity/chart-rooms-capacity';



@Component({
  selector: 'app-layout-bottom-left',
  standalone: true,
  imports: [ChartRoomsCapacity],
  templateUrl: './layout-bottom-left.html',
  styleUrl: './layout-bottom-left.scss'
})
export class LayoutBottomLeft {

}
