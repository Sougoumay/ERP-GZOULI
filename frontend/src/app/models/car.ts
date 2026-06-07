export interface Car {
  id: number;
  brand: string;             // Ex: Dacia
  model: string;             // Ex: Logan
  registrationNumber: string; // Ex: 12345-A-6
  monthlyCost: number;       // Charge Fixe (Crédit/Leasing)
  state: string;             // EN_SERVICE, EN_PANNE...

  // Infos conducteur (calculées par le back)
  assigned: boolean;
  currentDriverId?: number;
  currentDriverName?: string;
}
