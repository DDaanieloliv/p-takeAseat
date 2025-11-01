import { Component, OnDestroy, ElementRef, ViewChild, Inject, PLATFORM_ID, AfterViewInit } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';



// Registrar todos os componentes do Chart.js apenas uma vez
let chartJsRegistered = false;

@Component({
  selector: 'app-chart-rooms-capacity',
  standalone: true,
  imports: [],
  templateUrl: './chart-rooms-capacity.html',
  styleUrl: './chart-rooms-capacity.scss'
})
export class ChartRoomsCapacity implements AfterViewInit, OnDestroy {

  constructor(
    @Inject(PLATFORM_ID) private platformId: any,
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    // Registrar Chart.js apenas uma vez
    if (this.isBrowser && !chartJsRegistered) {
      Chart.register(...registerables);
      chartJsRegistered = true;
    }
  }

  @ViewChild('chart_rooms_capacity_Canvas')
  chartRoomsCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_rooms_capacity: Chart<'bar'> | null = null;

  private isBrowser: boolean;


  ngAfterViewInit(): void {
    if (this.isBrowser) {
      // Pequeno delay para garantir que o DOM esteja pronto
      setTimeout(() => {
        this.createChart();
      }, 0);
    }
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }


  private createChart(): void {
    // Destruir chart existente antes de criar um novo
    this.destroyChart();


    const ctx = this.chartRoomsCanvas.nativeElement.getContext('2d');
    if (!ctx) {
      console.warn('Canvas context not available');
      return;
    }

      try {

        // Dados dos rooms persistidos e sua respectiva taxa de ocupação
        const roomData = [
          { name: 'Room 1', occupancy: 21 },
          { name: 'Room 2', occupancy: 29 },
          { name: 'Room 3', occupancy: 80 },
          { name: 'Room 4', occupancy: 81 },
          { name: 'Room 5', occupancy: 56 }
        ];

        this.chart_rooms_capacity = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: roomData.map(room => room.name),
            datasets: [{
              data: roomData.map(room => room.occupancy),
              backgroundColor: roomData.map(room => this.getOccupancyColor(room.occupancy)),
              borderColor: 'transparent',
              borderWidth: 1,
              borderRadius: 3,
              borderSkipped: false,
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            datasets: {
              bar: {
                barPercentage: 0.4,
                categoryPercentage: 0.8,
              }
            },
            plugins: {
              legend: {
                display: false
              },
              tooltip: {
                enabled: true,
                backgroundColor: 'rgba(0, 0, 0, 0.7)',
                titleFont: {
                  size: 12
                },
                bodyFont: {
                  size: 11
                },
                padding: 8,
                callbacks: {
                  label: function(context) {
                    return `Ocupação: ${context.parsed.y}%`;
                  }
                }
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                max: 100,
                border: {
                  display: false
                },
                grid: {
                  color: 'rgba(200, 200, 200, 0.3)',
                  lineWidth: 1,
                },
                ticks: {
                  font: {
                    size: 8,
                    family: "'Roboto', 'Helvetica Neue', sans-serif",
                    weight: 500
                  },
                  color: '#fff',
                  callback: function(value) {
                    return value + '%';
                  }
                }
              },
              x: {
                border: {
                  display: false
                },
                grid: {
                  color: 'rgba(200, 200, 200, 0.2)',
                  lineWidth: 1,
                },
                ticks: {
                  font: {
                    size: 6.5,
                    family: "'Roboto', 'Helvetica Neue', sans-serif",
                    weight: 800
                  },
                  color: '#fff',
                  padding: 4,
                  maxRotation: 60,
                  minRotation: 50,
                }
              }
            }
          }
        });
      } catch (error) {
        console.error('Error creating chart:', error);
      }
  }

  // Método para determinar a cor baseada na ocupação
  private getOccupancyColor(occupancy: number): string {
    if (occupancy < 30) {
      return 'rgba(76, 175, 80, 0.8)';    // Verde (baixa ocupação)
    } else if (occupancy < 70) {
      return 'rgba(255, 193, 7, 0.8)';    // Amarelo (média ocupação)
    } else {
      return 'rgba(244, 67, 54, 0.8)';    // Vermelho (alta ocupação)
    }
  }


  private destroyChart(): void {
    if (this.chart_rooms_capacity) {
      this.chart_rooms_capacity.destroy();
      this.chart_rooms_capacity = null;
    }
  }
}
