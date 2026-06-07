import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseDialogComponent } from './expense-dialog-component';

describe('ExpenseDialogComponent', () => {
  let component: ExpenseDialogComponent;
  let fixture: ComponentFixture<ExpenseDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
