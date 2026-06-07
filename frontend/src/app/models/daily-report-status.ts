export interface DailyReportStatus {
  employeeName: string;
  role: string;
  projectName: string;
  reportSubmitted: boolean;
  submissionTime?: string; // Ex: "18:45"
}

export interface DashboardStats {
  // 1. Vue Macro (Le Carnet de commande)
  totalMarketValueHT: number;
  globalCompletionRate: number;

  // 2. Vue Trésorerie (Le Nerf de la guerre)
  certifiedTurnover: number; // Factures Validées
  pendingTurnover: number;   // Factures Déposées (En attente)

  // 3. Vue Résultat
  totalExpenses: number;     // Salaires + Matériel + Charges
  realNetMargin: number;     // Marge Réelle à date

  // 4. Météo (Liste)
  dailyReports: DailyReportStatus[];
}
