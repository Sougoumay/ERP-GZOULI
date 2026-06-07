export interface Task extends TaskCreation{
  id: number;
  // label: string;
  completed: boolean;
  completionDate?: string; // Date si terminée
  // assigneeId?: number;     // ID de l'employé (optionnel)
  assigneeName?: string;   // Nom complet pour l'affichage
}

export interface TaskCreation {
  label: string;
  taskWeight: number;
  startDate: string;
  scheduledEndDate: string;
  assigneeId?: number | null;
}
