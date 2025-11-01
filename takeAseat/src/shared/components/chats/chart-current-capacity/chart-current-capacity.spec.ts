import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartComponentCurrentCapacity } from './chart-component-current-capacity';

describe('ChartComponentCurrentCapacity', () => {
  let component: ChartComponentCurrentCapacity;
  let fixture: ComponentFixture<ChartComponentCurrentCapacity>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChartComponentCurrentCapacity]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartComponentCurrentCapacity);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
