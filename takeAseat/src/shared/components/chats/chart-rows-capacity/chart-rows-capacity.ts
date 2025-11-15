import { isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, Inject, PLATFORM_ID, ViewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { ChartDTO } from '../../../../core/model/chartModel/chartDTO';
import { ApiService } from '../../../../core/services/api-service';
import { SafeStorageService } from '../../../../core/services/localStorageService/storage-service';
import { GridDTO } from '../../../../core/model/fetch/grid-dto';
import { CurrentGrid } from '../../../../core/model/fetch/grid-entity-dto';


// Registrar todos os componentes do Chart.js apenas uma vez
let chartJsRegistered
  = false;

@Component({
  selector: 'app-chart-rows-capacity',
  standalone: true,
  imports: [],
  templateUrl: './chart-rows-capacity.html',
  styleUrl: './chart-rows-capacity.scss'
})
export class ChartRowsCapacity {

  private isBrowser: boolean;

  constructor(
    @Inject(PLATFORM_ID)
    private platformId: any,
    private api: ApiService,
    private safeStorage: SafeStorageService

  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    if (this.isBrowser && !chartJsRegistered) {
      Chart.register(...registerables);
      chartJsRegistered = true;
    }
  }

  private chartsData: ChartDTO | null = null;

  @ViewChild('chart_rows_capacity_Canvas')
  chartRowsCanvas!: ElementRef<HTMLCanvasElement>;
  public chart_rows_capacity: Chart<'bar'> | null = null;

  private createChartWithDefaultData(): void {
    // Cria gráfico com dados padrão quando a API falha
    this.chartsData = {
      percentOccupied: 0,
      seatsUnoccupied: 0,
      rowOccupacyDTO: [],
      roomOccupacyDTOs: []
    };
    this.createChart();
  }


  async ngOnInit(): Promise<void> {
    try {
      const gridEntity: CurrentGrid = await this.api.fetchCurrentGridEntity();
      const chartsData: ChartDTO = await this.api.charts(gridEntity.grid);
      this.chartsData = chartsData;

      // console.log("Dados do gráfico: ", chartsData);

      if (this.isBrowser) {
        setTimeout(() => {
          this.createChart();
        }, 0);
      }
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
      if (this.isBrowser) {
        this.createChartWithDefaultData();
      }
    }
  }


  ngAfterViewInit(): void {
    // if (this.isBrowser) {
    //   // Pequeno delay para garantir que o DOM esteja pronto
    //   setTimeout(() => {
    //     this.createChart();
    //   }, 0);
    // }
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }





  private destroyChart(): void {
    if (this.chart_rows_capacity) {
      this.chart_rows_capacity.destroy();
      this.chart_rows_capacity = null;
    }
  }

  private createChart(): void {
    // Destruir chart existente antes de criar um novo
    this.destroyChart();

    if (!this.isBrowser) {
      return;
    }

    const ctx = this.chartRowsCanvas.nativeElement.getContext('2d');

    if (!ctx) {
      console.warn('Canvas context not available');
      return;
    }

    try {
      // Dados obtidos por meio de uma função que irá pegar a
      // taxa de ocupação de cada fileira e a quantidade de fileiras
      const rowData = this.generateRowData();
      // console.log("Objeto rowData: ")
      // console.log(rowData);

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



  private generateRowData(): { labels: string[], values: number[], colors: string[] } {
    const data = {
      labels: [] as string[],
      values: [] as number[],
      colors: [] as string[]
    };

    this.chartsData?.rowOccupacyDTO.forEach(element => {
      data.labels.push(`Fileira ${element.fileira}`)
      data.values.push(element.taxaOcupacaoPercentual);

    });

    for (let index = 0; index < data.values.length; index++) {
      const occupancy = data.values[index];

      if (occupancy < 30) {
        data.colors.push('rgba(76, 175, 80, 0.8)');    // Verde (baixa ocupação)
      } else if (occupancy < 70) {
        data.colors.push('rgba(255, 193, 7, 0.8)');    // Amarelo (média ocupação)
      } else {
        data.colors.push('rgba(244, 67, 54, 0.8)');    // Vermelho (alta ocupação)
      }
    }

    return data;
  }

}




