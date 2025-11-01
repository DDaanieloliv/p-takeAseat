import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartRowsCapacity } from './chart-rows-capacity';

describe('ChartRowsCapacity', () => {
  let component: ChartRowsCapacity;
  let fixture: ComponentFixture<ChartRowsCapacity>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChartRowsCapacity]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartRowsCapacity);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
