import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatsColection } from './seats-colection';

describe('SeatsColection', () => {
  let component: SeatsColection;
  let fixture: ComponentFixture<SeatsColection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeatsColection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatsColection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
