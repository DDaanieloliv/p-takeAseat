import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Seat } from '../components/ui/edit-grid/edit-grid';

@Injectable({
  providedIn: 'root'
})
export class GridService_Observable {

  private gridSource = new BehaviorSubject<Array<Seat[]>>([]);

  // Subscribers podem escutar essa variável, cujo
  // valor é definido pelo valor que consta em gridSource.
  public currentGrid$ = this.gridSource.asObservable();

  public getCurrentGrid(): Array<Seat[]> {
    return this.gridSource.getValue();
  }

  // Método que será usado para atualizar o grid principal, ou seja o grid
  // que é gerado em SeatGridComponent. SeatGridComponent ao longo do tempos
  // não usará esse método pois apenas poderá ser incrementado ou decrementado
  // por meio do componente EditGrid, porem modificações no principal deverão
  // repercutir no grid espelho em EditGrid.
  public updateGrid(grid: Array<Seat[]>) {
    this.gridSource.next(grid);
  }






  private initialGridSource = new BehaviorSubject<Array<Seat[]>>([]);

  // Subscribers podem escutar essa variável, cujo valor
  // é definido pelo valor que consta em initialGridSource.
  public initialGrid$ = this.initialGridSource.asObservable();

  public setInitialGrid(grid: Array<Seat[]>) {
    this.initialGridSource.next(grid);
  }





  // Eis o seguinte pattern.
  //
  // Definimos um Serviço, que possuirá os Observables,
  // esse Observables 'initialGridSource' e 'gridSource' serão as nossas fontes,
  // que guardarão do determinado tipo.
  //
  // 'initialGridSource' será usado para
  // sincronizar o estado de ambos os grid ao iniciarmos, o valor desse tipo
  // poderá ser defindo atravez do método public, 'setInitialGrid', e obtido por
  // meio da variável public 'initialGrid$' pelos subscribers.
  //
  // Utilizamos esses atributos e método public da fonte 'initialGridSource'
  // para que o subscriber do componente 'EditGrid' possa receber o estado do grid
  // do componente 'SeatGridComponent', cujo estado dos elementos que compoem o
  // grid poderão ser mudados, exceto as dimensões do Grid, e para que o componente
  // 'SeatGridComponent', possa definir o valor do tipo private, 'initialGridSource'
  // respectivamente.
  //
  //
  // 'gridSource', esse Observable usaremos para guardar o estado das ediçõe
  // feitas no grid espelho, ou seja o grid do componente 'EditGrid', tornando
  // possível sincronizarmos o estado do grid do componente do EditGrid como grid
  // do componente 'SeatGridComponent'.
  //
  // Utilizamos os respectivos métodos public (updateGrid) e
  // atributos, para atualizar o estado e formato do tipo da fonte 'gridSource'
  // quando terminarmos a edição em 'EditGrid', e o atributo para que o
  // subscriber do componente 'SeatGridComponent', possa obter o estado da fonte
  // 'gridSource', grid de edição.
}
