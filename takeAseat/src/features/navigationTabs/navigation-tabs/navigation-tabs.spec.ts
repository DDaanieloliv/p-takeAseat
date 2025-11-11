import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigationTabs } from './navigation-tabs';

describe('NavigationTabs', () => {
  let component: NavigationTabs;
  let fixture: ComponentFixture<NavigationTabs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationTabs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavigationTabs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
