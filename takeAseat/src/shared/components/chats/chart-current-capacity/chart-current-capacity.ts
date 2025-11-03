import { Component, ElementRef, ViewChild, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { ApiService } from '../../../../core/services/api-service';
import { ChartDTO } from '../../../../core/model/chartModel/chartDTO';




let chartJsRegistered
  = false;

@Component({
  selector: 'app-chart-current-capacity',
  standalone: true,
  imports: [],
  templateUrl: './chart-current-capacity.html',
  styleUrl: './chart-current-capacity.scss'
})
export class ChartCurrentCapacity {

  constructor(
    @Inject(PLATFORM_ID)
    private platformId: any,

    private api: ApiService
  ) {

    this.isBrowser = isPlatformBrowser(this.platformId);
    // Registrar Chart.js apenas uma vez
    if (this.isBrowser && !chartJsRegistered) {
      Chart.register(...registerables);
      chartJsRegistered = true;
    }
  }

  @ViewChild('chart_current_room_capacity_Canvas')
  chartCurrentRoomCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_current_room_capacity: Chart<'doughnut'> | null = null;

  private isBrowser: boolean;

  private chartsData: ChartDTO | null = null;

  public freeSeats : number = 0;


  ngAfterViewInit(): void {
    if (this.isBrowser) {
      // Pequeno delay para garantir que o DOM esteja pronto
      setTimeout(() => {
        this.createChart();
      }, 100);
    }
  }


  async ngOnInit(): Promise<void> {

    const chartsData = await this.api.charts();
    this.chartsData = chartsData;
    this.freeSeats = chartsData.seatsUnoccupied;

    console.log("Dados do gráfico: ");
    console.log(chartsData);

    this.createChart();
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

    if (!this.chartsData) return

    const ctx = this.chartCurrentRoomCanvas.nativeElement.getContext('2d');
    if (!ctx) {
      console.warn('Canvas context not available');
      return;
    }

    try {
      // Porcentagem de ocupação lançada pela API
      const occupancy = this.chartsData?.percentOccupied;
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
            cutout: '0%',
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
