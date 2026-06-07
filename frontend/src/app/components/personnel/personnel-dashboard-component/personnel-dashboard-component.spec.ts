import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonnelDashboardComponent } from './personnel-dashboard-component';

describe('PersonnelDashboardComponent', () => {
  let component: PersonnelDashboardComponent;
  let fixture: ComponentFixture<PersonnelDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonnelDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonnelDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
