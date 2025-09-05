import { Component } from '@angular/core';
import { HamburgerMenu } from '../../../../shared/components/hamburger-menu/hamburger-menu';
import { SearchButtom } from '../../../../shared/components/search-buttom/search-buttom';
import { OptionsMenu } from '../../../../shared/components/options-menu/options-menu';
import { TicketStore } from '../../../../shared/components/ticket-store/ticket-store';
import { EditGrid } from '../../../../shared/components/edit-grid/edit-grid';

@Component({
  selector: 'app-layout-sidebar',
  imports: [ EditGrid, TicketStore, OptionsMenu, HamburgerMenu, SearchButtom ],
  templateUrl: './layout-sidebar.html',
  styleUrl: './layout-sidebar.scss'
})
export class LayoutSidebar {

}
