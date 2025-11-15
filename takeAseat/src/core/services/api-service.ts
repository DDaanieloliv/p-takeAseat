import { Injectable } from '@angular/core';
import { GridDTO } from '../model/fetch/grid-dto';
import { CurrentGrid } from '../model/fetch/grid-entity-dto';
import { ChartDTO } from '../model/chartModel/chartDTO';
import { SeatDto } from '../model/fetch/SeatDTO';

@Injectable({
  providedIn: 'root'
})
export class ApiService {



  public async fetchAllSeats(): Promise<any> {
    const res = await fetch('http://localhost:8080/seats');
    const data = await res.json();
    return data as SeatDto[];
  }

  public async fetchListGrid(): Promise<CurrentGrid[]> {
    const res = await fetch('http://localhost:8080/seats/grid/list');
    const data = await res.json();
    return data as CurrentGrid[];
  }


  public async curretnGridSwitch(ID: string): Promise<void> {
    await fetch(`http://localhost:8080/seats/grid/currentGridSwitch/${ID}`,
      {
        method: 'PUT'
      });
  }


  public async fetchAPI(): Promise<GridDTO> {
    const res = await fetch('http://localhost:8080/seats/grid');
    const data = await res.json();
    return data;
  }


  public async fetchCurrentGridEntity(): Promise<CurrentGrid> {
    const res = await fetch('http://localhost:8080/seats/gridEntity')
    const data = await res.json();
    return data as CurrentGrid;
  }


  public async updateGrid(dto: GridDTO): Promise<GridDTO> {
    try {
      const response = await fetch('http://localhost:8080/seats/grid/update', {
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

  public async eraseGrid(entityGrid: CurrentGrid): Promise<CurrentGrid> {
    try {
      const response = await fetch('http://localhost:8080/seats/grid/erase', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(entityGrid)
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


  public async charts(gridId: string): Promise<ChartDTO> {
    try {
      const response = await fetch(`http://localhost:8080/seats/charts/${gridId}`);

      if (!response.ok) {
        let errorMessage = `HTTP error! status: ${response.status}`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch {
          // Se não conseguir parsear o JSON de erro
        }
        throw new Error(errorMessage);
      }

      const data = await response.json();
      return data as ChartDTO;
    } catch (error) {
      console.error('Error fetching charts:', error);
      throw error;
    }
  }

  public async chartsNoArgs(): Promise<ChartDTO> {
    try {
      const response = await fetch(`http://localhost:8080/seats/charts`);

      if (!response.ok) {
        let errorMessage = `HTTP error! status: ${response.status}`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch {
          // Se não conseguir parsear o JSON de erro
        }
        throw new Error(errorMessage);
      }

      const data = await response.json();
      return data as ChartDTO;
    } catch (error) {
      console.error('Error fetching charts:', error);
      throw error;
    }
  }


}
