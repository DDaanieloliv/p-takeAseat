import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BottomLeftCorner } from './bottom-left-corner';

describe('BottomLeftCorner', () => {
  let component: BottomLeftCorner;
  let fixture: ComponentFixture<BottomLeftCorner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BottomLeftCorner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BottomLeftCorner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
