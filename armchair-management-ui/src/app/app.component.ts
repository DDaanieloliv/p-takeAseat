import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
//import { SeatListComponent } from './components/seat-list/seat-list.component';
import { DecorativeBarrComponent } from './components/decorative-barr/decorative-barr.component';
import { DecorativePretendBackgroundComponent } from './components/decorative-pretend-background/decorative-pretend-background.component';
import { LayoutMainComponent } from './components/layout-main/layout-main.component';
import { LayoutSidebarComponent } from './components/layout-sidebar/layout-sidebar.component';
import { LayoutBottomLeftComponent } from './components/layout-bottom-left/layout-bottom-left.component';
import { LayoutTopRightComponent } from './components/layout-top-right/layout-top-right.component';
import { LayoutBottomRightComponent } from './components/layout-bottom-right/layout-bottom-right.component';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, DecorativeBarrComponent, DecorativePretendBackgroundComponent, LayoutMainComponent,
    LayoutSidebarComponent, LayoutBottomLeftComponent, LayoutTopRightComponent, LayoutBottomRightComponent ], // Importa o SeatListComponent
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'armchair-management-ui';

}
