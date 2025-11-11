import { Component } from '@angular/core';
import { NavigationTabs } from '../../../../navigationTabs/navigation-tabs/navigation-tabs';
import { LayoutMain } from '../layout-main/layout-main';

@Component({
  selector: 'app-wrap-grid-navigation',
  standalone: true,
  imports: [
    NavigationTabs,
    LayoutMain
  ],
  templateUrl: './wrap-grid-navigation.html',
  styleUrl: './wrap-grid-navigation.scss'
})
export class WrapGridNavigation {





}
