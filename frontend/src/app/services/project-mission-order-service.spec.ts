import { TestBed } from '@angular/core/testing';

import { ProjectMissionOrderService } from './project-mission-order-service';

describe('ProjectMissionOrderService', () => {
  let service: ProjectMissionOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectMissionOrderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
