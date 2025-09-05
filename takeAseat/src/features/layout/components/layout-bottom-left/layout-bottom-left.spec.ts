import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutBottomLeft } from './layout-bottom-left';

describe('LayoutBottomLeft', () => {
  let component: LayoutBottomLeft;
  let fixture: ComponentFixture<LayoutBottomLeft>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutBottomLeft]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutBottomLeft);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
