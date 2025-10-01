import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutSidebar } from './layout-sidebar';

describe('LayoutSidebar', () => {
  let component: LayoutSidebar;
  let fixture: ComponentFixture<LayoutSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutSidebar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutSidebar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
