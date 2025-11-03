import { RowOccupacy } from "./rowOccupacyDTO";

export interface Chart {
  percentOccupied : number,
  seatsUnoccupied : number,
  rowOccupacyDTO : Array<RowOccupacy>
}
