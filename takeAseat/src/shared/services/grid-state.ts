import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Seat } from '../../core/model/Seat';

@Injectable({
  providedIn: 'root'
})
export class GridService_Observable {

  public current_grid$ = new BehaviorSubject<Array<Seat[]>>([]);

  public updateGrid(grid: Array<Seat[]>) {
    this.current_grid$.next(grid);
  }
}
