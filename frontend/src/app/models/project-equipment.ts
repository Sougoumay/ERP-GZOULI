export interface ProjectEquipment {
  assignmentId: number;
  equipmentId: number;
  label: string;
  reference: string;
  startDate: string; // YYYY-MM-DD
  endDate?: string;  // null si encore actif
  status: 'ACTIF' | 'LIBÉRÉ';
}
