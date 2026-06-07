export interface SalaryAdvance {
  id: number;
  employeeId: number;
  employeeName: string;
  amount: number;
  date: string; // YYYY-MM-DD
  note: string;
}
