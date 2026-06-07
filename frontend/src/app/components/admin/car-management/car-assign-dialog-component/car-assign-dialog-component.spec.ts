import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarAssignDialogComponent } from './car-assign-dialog-component';

describe('CarAssignDialogComponent', () => {
  let component: CarAssignDialogComponent;
  let fixture: ComponentFixture<CarAssignDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarAssignDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarAssignDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
