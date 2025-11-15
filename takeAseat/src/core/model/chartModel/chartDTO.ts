import { RowOccupacy } from "./rowOccupacyDTO";

export interface ChartDTO {
  percentOccupied: number,
  seatsUnoccupied: number,
  rowOccupacyDTO: Array<RowOccupacy>
  roomOccupacyDTOs: { getGridID: string , getRoomOccupacy: number }[]
}
