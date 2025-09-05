import { TestBed } from '@angular/core/testing';

import { GridState } from './grid-state';

describe('GridState', () => {
  let service: GridState;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GridState);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
