import { Component, ElementRef, ViewChild, Inject, PLATFORM_ID, Input } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { ChartType } from '../charts-enum/ChartType';

let chartJsRegistered = false;


@Component({
  selector: 'app-chart-component-current-capacity',
  standalone: true,
  imports: [],
  templateUrl: './chart-component-current-capacity.html',
  styleUrl: './chart-component-current-capacity.scss'
})
export class ChartComponentCurrentCapacity {

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

  private isBrowser: boolean;

  @Input()
  public chartType: string = '';


  @ViewChild('chart_current_room_capacity_Canvas')
  chartCurrentRoomCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_current_room_capacity: Chart<'doughnut'> | null = null;



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

  private destroyChart(): void {
    if (this.chart_current_room_capacity) {
      this.chart_current_room_capacity.destroy();
      this.chart_current_room_capacity = null;
    }

  }

  private createChart(): void {
    // Destruir chart existente antes de criar um novo
    this.destroyChart();

    let canvas: ElementRef<HTMLCanvasElement> | null = null;
    let chartVar: 'bar' | 'doughnut' = 'bar';

    if (this.chartType === ChartType.Current_room_capacity) {
      canvas = this.chartCurrentRoomCanvas;
      chartVar = 'doughnut';
    }

    if (!canvas?.nativeElement) {
      console.warn('Canvas element not available for chart type:', this.chartType);
      return;
    }

    const ctx = canvas.nativeElement.getContext('2d');
    if (!ctx) {
      console.warn('Canvas context not available');
      return;
    }

    if (this.chartType === ChartType.Current_room_capacity) {

      try {
        const occupancy = 75;
        this.chart_current_room_capacity = new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: ['Ocupado', 'Livre'],
            datasets: [{
              data: [occupancy, 100 - occupancy],
              backgroundColor: [
                '#FFD600',
                'rgba(200, 200, 200, 0.3)'
              ],
              borderColor: [
                '#FFD600',
                'rgba(200, 200, 200, 0.5)'
              ],
              borderWidth: 2,
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
              legend: {
                position: 'bottom',
                labels: {
                  padding: 15,
                  usePointStyle: true,
                  font: {
                    size: 11
                  }
                }
              },
              tooltip: {
                callbacks: {
                  label: function(context) {
                    return `${context.label}: ${context.parsed}%`;
                  }
                }
              },
            }
          }
        });
      } catch (error) {
        console.error('Error creating chart:', error);
      }
    }
  }


}
