import { Seat } from "../Seat";

export interface GridDTO {
  entity: CurrentGrid;
  grid: Seat[][];
}


export interface CurrentGrid {
  grid: string;
  rowNumber: number;
  columnNumber: number;
  is_currentGrid: boolean;
}
