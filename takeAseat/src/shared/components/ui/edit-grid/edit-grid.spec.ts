import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditGrid } from './edit-grid';

describe('EditGrid', () => {
  let component: EditGrid;
  let fixture: ComponentFixture<EditGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditGrid]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditGrid);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
