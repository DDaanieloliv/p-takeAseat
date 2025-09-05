import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarningWindow } from './warning-window';

describe('WarningWindow', () => {
  let component: WarningWindow;
  let fixture: ComponentFixture<WarningWindow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WarningWindow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WarningWindow);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
