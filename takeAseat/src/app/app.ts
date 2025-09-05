import { Component, signal } from '@angular/core';
// import { RouterOutlet } from '@angular/router';
import { LayoutMain } from '../features/layout/components/layout-main/layout-main';
import { LayoutSidebar } from '../features/layout/components/layout-sidebar/layout-sidebar';
import { LayoutBottomLeft } from '../features/layout/components/layout-bottom-left/layout-bottom-left';
import { LayoutBottomRight } from '../features/layout/components/layout-bottom-right/layout-bottom-right';
import { LayoutTopRight } from '../features/layout/components/layout-top-right/layout-top-right';
import { BottomLeftCorner } from '../features/layout/components/bottom-left-corner/bottom-left-corner';

@Component({
  selector: 'app-root',
  imports: [/* RouterOutlet, */ LayoutMain, LayoutSidebar, LayoutBottomLeft, LayoutBottomRight, LayoutTopRight, BottomLeftCorner],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('takeAseat');
}
