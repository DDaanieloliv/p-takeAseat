import { Component } from '@angular/core';
import { HamburgerMenu } from '../../../../../shared/components/ui/hamburger-menu/hamburger-menu';
import { SearchButtom } from '../../../../../shared/components/ui/search-buttom/search-buttom';
import { OptionsMenu } from '../../../../../shared/components/ui/options-menu/options-menu';
import { TicketStore } from '../../../../../shared/components/ui/ticket-store/ticket-store';
import { EditGrid } from '../../../../../shared/components/ui/edit-grid/edit-grid';

@Component({
  selector: 'app-layout-sidebar',
  standalone: true,
  imports: [EditGrid, TicketStore, OptionsMenu, HamburgerMenu, SearchButtom],
  templateUrl: './layout-sidebar.html',
  styleUrl: './layout-sidebar.scss'
})
export class LayoutSidebar {

}
