import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarDialogComponent } from './car-dialog-component';

describe('CarDialogComponent', () => {
  let component: CarDialogComponent;
  let fixture: ComponentFixture<CarDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
