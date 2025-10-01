import { Component, HostListener} from '@angular/core';

@Component({
  selector: 'app-hamburger-menu',
  standalone: true,
  imports: [ ],
  templateUrl: './hamburger-menu.html',
  styleUrls: ['./hamburger-menu.scss']
})
export class HamburgerMenu {

  public showUpWindow = false;
  public isActive = false;
  public isHovering = false;

  public toggle() {
    this.showUpWindow = !this.showUpWindow;
    this.isActive = !this.isActive;
  }

  public onHover(isHoveringEvent: boolean) {
    this.isHovering = isHoveringEvent;
  }

  public shouldShowInfoBox(): boolean {
    if (this.isHovering && !this.showUpWindow) return true;

    return false;
  }

  @HostListener('document:click', ['$event'])
  public onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement; // Type assertion
    if (!target.closest('.fuckDOM')) {
      this.showUpWindow = false;
      this.isActive = false;
    }
  }

}
