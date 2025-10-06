import { Seat } from "../Seat";

export interface GridDTO {
  entity: CurrentGrid;
  grid: Seat[][];
}


export interface CurrentGrid {
  grid: string;
  rowNumber: number;
  columnNumber: number;
  // isInitialGrid: boolean;
  is_currentGrid: boolean;
}
