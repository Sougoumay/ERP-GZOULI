import { TestBed } from '@angular/core/testing';

import { SiteJournalService } from './site-journal-service';

describe('SiteJournalService', () => {
  let service: SiteJournalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SiteJournalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
