import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutBottomRight } from './layout-bottom-right';

describe('LayoutBottomRight', () => {
  let component: LayoutBottomRight;
  let fixture: ComponentFixture<LayoutBottomRight>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutBottomRight]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutBottomRight);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
