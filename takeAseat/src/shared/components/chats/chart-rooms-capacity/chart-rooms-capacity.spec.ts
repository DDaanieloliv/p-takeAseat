import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartRoomsCapacity } from './chart-rooms-capacity';

describe('ChartRoomsCapacity', () => {
  let component: ChartRoomsCapacity;
  let fixture: ComponentFixture<ChartRoomsCapacity>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChartRoomsCapacity]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartRoomsCapacity);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
