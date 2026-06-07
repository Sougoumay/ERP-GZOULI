import {ProjectSummary} from './project-summary';
import {TeamMember} from './team-member';

export interface ProjectDetail extends ProjectSummary {
  teamMembers: TeamMember[];
  totalInvoicesCertified: number;
  totalInvoicesSubmitted: number;
  totalExpenses: number;
  currentMargin: number;
}
