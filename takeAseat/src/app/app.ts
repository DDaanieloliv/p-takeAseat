import { Component, signal } from '@angular/core';
// import { LayoutMain } from '../features/layout/components/grid-areas/layout-main/layout-main';
// import { LayoutSidebar } from '../features/layout/components/grid-areas/layout-sidebar/layout-sidebar';
// import { LayoutBottomLeft } from '../features/layout/components/grid-areas/layout-bottom-left/layout-bottom-left';
// import { LayoutBottomRight } from '../features/layout/components/grid-areas/layout-bottom-right/layout-bottom-right';
// import { LayoutTopRight } from '../features/layout/components/grid-areas/layout-top-right/layout-top-right';
// import { BottomLeftCorner } from '../features/layout/components/grid-areas/bottom-left-corner/bottom-left-corner';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ RouterOutlet/* , LayoutMain, LayoutSidebar, LayoutBottomLeft, LayoutBottomRight, LayoutTopRight, BottomLeftCorner */],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('takeAseat');
}
