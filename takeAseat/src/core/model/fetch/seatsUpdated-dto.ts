import { Seat } from "../Seat";
import { CurrentGrid } from "./grid-entity-dto";
//
// export interface Seat {
//   position: string
//   row: number;
//   column: number;
//   selected: boolean;
//   free: boolean;
//   status: 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'MAINTENANCE' | 'DISABLED' | 'SELECTED' | 'UNAVAILABLE';
// }

export interface GridUpdatedDTO {
  entity: CurrentGrid;
  grid: Seat[];
}


// export interface CurrentGrid {
//   grid: string;
//   rowNumber: number;
//   columnNumber: number;
//   is_currentGrid: boolean;
// }
