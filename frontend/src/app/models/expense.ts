export interface Expense {
  id: number;
  expenseDate: string; // YYYY-MM-DD
  type: ExpenseType;
  label: string;
  amount: number;

  fileName?: string;
  downloadUrl?: string; // Lien S3 temporaire

  performedById?: number;
  performedByName?: string;
}

export enum ExpenseType {
  GASOIL = 'GASOIL',
  MAINTENANCE = 'MAINTENANCE',
  FOURNITURE = 'FOURNITURE',
  AUTRE = 'AUTRE'
}
