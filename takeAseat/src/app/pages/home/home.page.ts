import { Component } from "@angular/core";
import { LayoutMain } from "../../../features/layout/components/grid-areas/layout-main/layout-main";
import { LayoutSidebar } from "../../../features/layout/components/grid-areas/layout-sidebar/layout-sidebar";
import { LayoutTopRight } from "../../../features/layout/components/grid-areas/layout-top-right/layout-top-right";
import { LayoutBottomLeft } from "../../../features/layout/components/grid-areas/layout-bottom-left/layout-bottom-left";
import { LayoutBottomRight } from "../../../features/layout/components/grid-areas/layout-bottom-right/layout-bottom-right";
import { BottomLeftCorner } from "../../../features/layout/components/grid-areas/bottom-left-corner/bottom-left-corner";
// import { SeatGridComponent } from "../../../features/seat-grid/seat-grid";


@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    LayoutSidebar,
    LayoutMain,
    LayoutBottomLeft,
    LayoutTopRight,
    LayoutBottomRight,
    BottomLeftCorner,
    // SeatGridComponent
  ],
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage {
  // Lógica específica da página home
}
