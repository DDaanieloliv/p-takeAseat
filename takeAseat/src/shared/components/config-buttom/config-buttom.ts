import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Adicione esta linha
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-config-buttom',
  imports: [CommonModule, FaIconComponent],
  templateUrl: './config-buttom.html',
  styleUrl: './config-buttom.scss'
})
export class ConfigButtom {

  protected faGear = faGear;

  protected shouldShowMenu : boolean = false;

  protected canAnimate : boolean = false;

  protected isHovering : boolean = false;



  protected onHover(isHoveringEvent : boolean) {
    this.isHovering = isHoveringEvent;
  }

  protected shouldShowInfoBox() : boolean {
    if (this.isHovering && !this.shouldShowMenu) return true;

    return false;
  }

  protected toggleMenu() {
    this.shouldShowMenu = !this.shouldShowMenu
    this.canAnimate = !this.canAnimate;
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement; // Type assertion
    if (!target.closest('.config-container')) {
      this.shouldShowMenu = false;
    }
  }
}
