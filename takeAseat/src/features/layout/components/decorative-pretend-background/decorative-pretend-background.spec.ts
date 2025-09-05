import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecorativePretendBackground } from './decorative-pretend-background';

describe('DecorativePretendBackground', () => {
  let component: DecorativePretendBackground;
  let fixture: ComponentFixture<DecorativePretendBackground>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecorativePretendBackground]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecorativePretendBackground);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
