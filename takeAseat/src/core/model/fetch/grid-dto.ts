export interface GridDTO {
  grid: Seat[][];
}


export interface CurrentGrid {
  grid: string;
  rowNumber: number;
  columnNumber: number;
  isInitialGrid: boolean;
  is_currentGrid: boolean;
}


export interface Seat {
  seatID: string;
  position: number;
  row: number;
  column: number;
  status: 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'MAINTENANCE' | 'DISABLED';
  free: boolean;
  person: string | null;
  currentGrid: CurrentGrid;
}
