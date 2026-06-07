import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectTeamComponent } from './project-team-component';

describe('ProjectTeamComponent', () => {
  let component: ProjectTeamComponent;
  let fixture: ComponentFixture<ProjectTeamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectTeamComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectTeamComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
