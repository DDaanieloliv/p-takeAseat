import { Seat } from "../Seat";

export interface GridState {
  grid: Seat[][];
  dimensions: {
    rows: number;
    columns: number;
  };
  timestamp: string;
}
