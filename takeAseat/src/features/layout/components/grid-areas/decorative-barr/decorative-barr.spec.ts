import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecorativeBarr } from './decorative-barr';

describe('DecorativeBarr', () => {
  let component: DecorativeBarr;
  let fixture: ComponentFixture<DecorativeBarr>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecorativeBarr]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecorativeBarr);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
