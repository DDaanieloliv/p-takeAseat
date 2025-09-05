import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigButtom } from './config-buttom';

describe('ConfigButtom', () => {
  let component: ConfigButtom;
  let fixture: ComponentFixture<ConfigButtom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfigButtom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfigButtom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
