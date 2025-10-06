export interface Seat {
  position: string
  row: number;
  column: number;
  selected: boolean;
  free: boolean;
  status: 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'MAINTENANCE' | 'DISABLED' | 'SELECTED' | 'UNAVAILABLE';
}
