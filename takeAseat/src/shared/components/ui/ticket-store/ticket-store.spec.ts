import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketStore } from './ticket-store';

describe('TicketStore', () => {
  let component: TicketStore;
  let fixture: ComponentFixture<TicketStore>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketStore]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketStore);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
