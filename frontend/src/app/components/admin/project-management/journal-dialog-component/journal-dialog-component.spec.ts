import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JournalDialogComponent } from './journal-dialog-component';

describe('JournalDialogComponent', () => {
  let component: JournalDialogComponent;
  let fixture: ComponentFixture<JournalDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JournalDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JournalDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
