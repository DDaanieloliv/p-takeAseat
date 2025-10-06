import { Injectable } from '@angular/core';
import { GridDTO } from '../model/fetch/grid-dto';

@Injectable({
  providedIn: 'root'
})
export class ApiService {


  public async fetchAPI() : Promise<GridDTO> {
    const res = await fetch('http://localhost:8080/seats/grid');
    const data = await res.json();
    return data;
  }

  public async updateGrid(dto: GridDTO): Promise<GridDTO> {
    try {
      const response = await fetch('https://localhost:8080/seats/grid/update', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(dto)
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error creating user:', error);
      throw error;
    }
  }
}
