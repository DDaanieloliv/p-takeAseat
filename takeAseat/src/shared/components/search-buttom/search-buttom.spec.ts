import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchButtom } from './search-buttom';

describe('SearchButtom', () => {
  let component: SearchButtom;
  let fixture: ComponentFixture<SearchButtom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchButtom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchButtom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
