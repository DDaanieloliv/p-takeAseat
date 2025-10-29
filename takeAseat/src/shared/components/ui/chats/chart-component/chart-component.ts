import { Component, OnDestroy, ElementRef, ViewChild, Inject, PLATFORM_ID, AfterViewInit, Input } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';

// Registrar todos os componentes do Chart.js apenas uma vez
let chartJsRegistered = false;

export enum ChartType {
  Rooms_capacity = 'Rooms_capacity',
  Curren_rooms_capacity = 'Curren_rooms_capacity',
  Row_capacity = 'Row_capacity'
}


@Component({
  selector: 'app-chart-component',
  standalone: true,
  imports: [],
  templateUrl: './chart-component.html',
  styleUrl: './chart-component.scss'
})
export class ChartComponent implements AfterViewInit, OnDestroy {

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

  @Input()
  public chartType: string = '';


  @ViewChild('chart_roonCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  public chart_room: Chart<'bar'> | null = null;
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

    if (!this.chartCanvas?.nativeElement) {
      console.warn('Canvas element not available');
      return;
    }

    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) {
      console.warn('Canvas context not available');
      return;
    }

    if (this.chartType === ChartType.Rooms_capacity) {
      try {
        this.chart_room = new Chart(ctx, {
          type: 'bar',
          // Configurando dataset structure
          data: {
            labels: ['Room 1', 'Room 2', 'Room 3', 'Room 4', 'Room 5'],
            datasets: [{
              data: [65, 59, 80, 81, 56],
              backgroundColor: 'grey',
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
                barPercentage: 0.4,      // Largura das barras (40% da categoria)
                categoryPercentage: 0.8, // Espaço entre categorias
              }
            },

            // configurações de plugins
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
                padding: 8
              }
            },

            // configurações das escalas (grid e fontes)
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

                // configuração dos números no eixo y
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
                // CONFIGURAÇÃO DOS LABELS DAS FILEIRAS
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
  }


  private destroyChart(): void {
    if (this.chart_room) {
      this.chart_room.destroy();
      this.chart_room = null;
    }
  }
}
