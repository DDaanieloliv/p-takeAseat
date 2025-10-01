import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutTopRight } from './layout-top-right';

describe('LayoutTopRight', () => {
  let component: LayoutTopRight;
  let fixture: ComponentFixture<LayoutTopRight>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutTopRight]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutTopRight);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
