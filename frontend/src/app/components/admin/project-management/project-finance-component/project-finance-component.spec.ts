import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectFinanceComponent } from './project-finance-component';

describe('ProjectFinanceComponent', () => {
  let component: ProjectFinanceComponent;
  let fixture: ComponentFixture<ProjectFinanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectFinanceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectFinanceComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
