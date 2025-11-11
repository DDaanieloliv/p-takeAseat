import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WrapGridNavigation } from './wrap-grid-navigation';

describe('WrapGridNavigation', () => {
  let component: WrapGridNavigation;
  let fixture: ComponentFixture<WrapGridNavigation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WrapGridNavigation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WrapGridNavigation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
