import { CurrentGrid } from "./grid-entity-dto";

export interface SeatDto {
  position: number;
  row: number;
  column: number;
  free: boolean;
  type: 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'MAINTENANCE' | 'DISABLED' | 'SELECTED' | 'UNAVAILABLE';
  grid: CurrentGrid;
}

// {
//   "position": "1",
//   "row": 1,
//   "column": 1,
//   "type": "OCCUPIED",
//   "gridEntity": {
//     "grid": "78f1047a-8098-4651-be0b-204ce9064c95",
//     "rowNumber": 10,
//     "columnNumber": 24,
//     "is_currentGrid": true
//   }
// }

