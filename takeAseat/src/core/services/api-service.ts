import { Injectable } from '@angular/core';
import { GridDTO } from '../model/fetch/grid-dto';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  public async fetchAPI() : Promise<GridDTO> {
    const res = await fetch('http://localhost:8080/seats/grid');
    const data = await res.json();
    return data as GridDTO;
  }
}
