import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectJournalComponent } from './project-journal-component';

describe('ProjectJournalComponent', () => {
  let component: ProjectJournalComponent;
  let fixture: ComponentFixture<ProjectJournalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectJournalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectJournalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
