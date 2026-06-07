export interface ProjectSummary {
  id: number;
  projectOwner: string;
  name: string;
  description: string;
  generalObjectives?: string;
  specificObjectives?: string;
  // amountIncTax?: number;
  amountExTax: number;
  durationMonths: number;
  projectWinDate: string;
  startDate: string;
  active: boolean;
}
