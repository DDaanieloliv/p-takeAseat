import { Component, OnDestroy, ElementRef, ViewChild, Inject, PLATFORM_ID, AfterViewInit, Input } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { ChartType } from '../charts-enum/ChartType';




// Registrar todos os componentes do Chart.js apenas uma vez
let chartJsRegistered = false;

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


  @ViewChild('chart_rooms_capacity_Canvas')
  chartRoomsCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_rooms_capacity: Chart<'bar'> | null = null;


  @ViewChild('chart_current_room_capacity_Canvas')
  chartCurrentRoomCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_current_room_capacity: Chart<'doughnut'> | null = null;


  @ViewChild('chart_rows_capacity_Canvas')
  chartRowsCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_rows_capacity: Chart<'bar'> | null = null;




  public isChartRooms(): boolean {
    return this.chartType === ChartType.Rooms_capacity;
  }

  public isChartCurrentRoomCapacity(): boolean {
    return this.chartType === ChartType.Current_room_capacity;
  }

  public isChartRowCapacity(): boolean {
    return this.chartType === ChartType.Rows_capacity;
  }


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

    let canvas: ElementRef<HTMLCanvasElement> | null = null;
    let chartVar: 'bar' | 'doughnut' = 'bar';

    if (this.chartType === ChartType.Rooms_capacity) {
      canvas = this.chartRoomsCanvas;
      chartVar = 'bar';
    } else if (this.chartType === ChartType.Current_room_capacity) {
      canvas = this.chartCurrentRoomCanvas;
      chartVar = 'doughnut';
    } else if (this.chartType === ChartType.Rows_capacity) {
      canvas = this.chartRowsCanvas;
      chartVar = 'bar';
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

    if (this.chartType === ChartType.Rooms_capacity) {
      try {
        // Dados dos rooms com cores baseadas na ocupação
        const roomData = [
          { name: 'Room 1', occupancy: 65 },
          { name: 'Room 2', occupancy: 59 },
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
    else if (this.chartType === ChartType.Current_room_capacity) {

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
    else if (this.chartType === ChartType.Rows_capacity) {
      const ctx = this.chartRowsCanvas.nativeElement.getContext('2d');
      if (!ctx) return;

      try {
        // Dados de exemplo para muitas fileiras
        // Gera dados para 15 fileiras
        const rowData = this.generateRowData(15);

        this.chart_rows_capacity = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: rowData.labels,
            datasets: [{
              data: rowData.values,
              backgroundColor: rowData.colors,
              borderColor: 'transparent',
              borderWidth: 1,
              borderRadius: 2,
              borderSkipped: false,
            }]
          },
          options: {
            // Barras horizontais
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,

            // Configurações para muitas barras
            datasets: {
              bar: {
                // Barras mais finas para muitas fileiras
                barPercentage: 0.6,
                categoryPercentage: 0.8,
              }
            },

            plugins: {
              legend: {
                display: false
              },
              tooltip: {
                enabled: true,
                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                titleFont: {
                  size: 8
                },
                bodyFont: {
                  size: 5
                },
                padding: 3,
                callbacks: {
                  label: function(context) {
                    return `Ocupação: ${context.parsed.x}%`;
                  }
                }
              }
            },

            scales: {
              x: {
                beginAtZero: true,
                max: 100,
                border: {
                  display: false
                },
                grid: {
                  color: 'rgba(200, 200, 200, 0.2)',
                  lineWidth: 1,
                },
                ticks: {
                  font: {
                    size: 7,
                    family: "'Roboto', 'Helvetica Neue', sans-serif",
                    weight: 500
                  },
                  color: '#fff',
                  callback: function(value) {
                    return value + '%';
                  },
                  // Menos ticks no eixo X para mais espaço
                  maxTicksLimit: 6
                },
                title: {
                  display: true,
                  text: 'Percentual de Ocupação',
                  font: {
                    size: 10,
                    weight: 600
                  },
                  color: '#6d6d6d',
                  padding: { top: 1, bottom: 1 }
                }
              },
              y: {
                border: {
                  display: false
                },
                grid: {
                  color: 'rgba(200, 200, 200, 0.1)',
                  lineWidth: 1,
                  // drawBorder: false
                },
                ticks: {
                  font: {
                    size: 5, // Fonte menor para muitos labels
                    family: "'Roboto', 'Helvetica Neue', sans-serif",
                    weight: 600
                  },
                  color: '#fff',
                  padding: 1,
                  // Configurações para muitos labels
                  autoSkip: true,
                  maxTicksLimit: 30 // Limite máximo de labels mostrados
                }
              }
            },

            // Animação otimizada para muitos dados
            animation: {
              duration: 1000,
              easing: 'easeOutQuart'
            }
          }
        });
      } catch (error) {
        console.error('Error creating rows chart:', error);
      }
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

  // Método para gerar dados de exemplo para fileiras
  private generateRowData(count: number): { labels: string[], values: number[], colors: string[] } {
    const labels: string[] = [];
    const values: number[] = [];
    const colors: string[] = [];

    for (let i = 1; i <= count; i++) {
      labels.push(`Fileira ${i}`);
      const occupancy = Math.floor(Math.random() * 100);
      values.push(occupancy);

      // Cores baseadas na ocupação
      if (occupancy < 30) {
        colors.push('rgba(76, 175, 80, 0.8)');    // Verde (baixa ocupação)
      } else if (occupancy < 70) {
        colors.push('rgba(255, 193, 7, 0.8)');    // Amarelo (média ocupação)
      } else {
        colors.push('rgba(244, 67, 54, 0.8)');    // Vermelho (alta ocupação)
      }
    }

    return { labels, values, colors };
  }

  private destroyChart(): void {
    if (this.chart_rooms_capacity) {
      this.chart_rooms_capacity.destroy();
      this.chart_rooms_capacity = null;
    }


    if (this.chart_current_room_capacity) {
      this.chart_current_room_capacity.destroy();
      this.chart_current_room_capacity = null;
    }

  }
}
