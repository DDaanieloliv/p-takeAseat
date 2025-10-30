import { Component } from '@angular/core';
import { ChartComponent } from '../../../../../shared/components/ui/chats/chart-component/chart-component';

@Component({
  selector: 'app-layout-bottom-right',
  standalone: true,
  imports: [ChartComponent],
  templateUrl: './layout-bottom-right.html',
  styleUrl: './layout-bottom-right.scss'
})
export class LayoutBottomRight {

}
