export interface ProjectRegistration {
  name: string;
  description: string;
  generalObjectives?: string;
  specificObjectives?: string;
  amountIncTax: number;
  amountExTax: number;
  durationMonths: number;
  startDate: string;
}
