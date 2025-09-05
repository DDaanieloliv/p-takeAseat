import { Component } from '@angular/core';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-buttom',
  imports: [ CommonModule, FaIconComponent, FormsModule ],
  templateUrl: './search-buttom.html',
  styleUrl: './search-buttom.scss'
})
export class SearchButtom {

  protected faMagnifyingGlass = faMagnifyingGlass;

  protected faXmark = faXmark;


  public isHovering : boolean = false;
  public showWindow : boolean = false


  public onHover(isHoveringEvent : boolean) {
    this.isHovering = isHoveringEvent;
  }


  public shouldShowInfoBox() : boolean {
    if (this.isHovering && !this.showWindow) return true;

    return false;
  }



  public showSearchWindow() {
    this.showWindow = !this.showWindow;
    console.log("fuck");
  }

  public searchQuery : string = '';
  public showResults : boolean = false;

  // JSON embutido diretamente no componente
  public jsonData = [
    { id: 1, name: 'Notebook Dell', category: 'Eletrônicos' },
    { id: 2, name: 'Smartphone Samsung', category: 'Eletrônicos' },
    { id: 3, name: 'Mesa de Escritório', category: 'Móveis' },
    { id: 4, name: 'Cadeira Gamer', category: 'Móveis' },
    { id: 5, name: 'Teclado Mecânico', category: 'Acessórios' },
    { id: 6, name: 'Monitor LG', category: 'Eletrônicos' },
    { id: 7, name: 'Mouse Sem Fio', category: 'Acessórios' }
  ];


  public filteredResults: any[] = [];

  public onSearchInput() {
    if (!this.searchQuery.trim()) {
      this.filteredResults = [];
      this.showResults = false;
      return;
    }

    const query = this.searchQuery.toLowerCase();

    this.filteredResults = this.jsonData.filter(item =>
      item.name.toLowerCase().includes(query) ||
      item.category.toLowerCase().includes(query)
    );

    this.showResults = this.filteredResults.length > 0;
  }



}
