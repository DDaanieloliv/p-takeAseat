import { Component } from '@angular/core';
import { ChartComponent } from '../../../../../shared/components/ui/chats/chart-component/chart-component';

@Component({
  selector: 'app-layout-top-right',
  standalone: true,
  imports: [ChartComponent],
  templateUrl: './layout-top-right.html',
  styleUrl: './layout-top-right.scss'
})
export class LayoutTopRight {

}
