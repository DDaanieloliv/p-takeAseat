import { Seat } from "../Seat";
import { CurrentGrid } from "./grid-entity-dto";

export interface GridDTO {
  entity: CurrentGrid;
  grid: Seat[][];
}
