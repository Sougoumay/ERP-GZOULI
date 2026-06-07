export interface ProjectMissionOrder {
  id?: number;
  type: 'DEMARRAGE' | 'ARRET' | 'REPRISE';
  effectiveDate: string | Date;
  createdAt?: string | Date;
  uploadedBy?: string;
  fileKey?: string;
  fileUrl?: string; // Le lien S3 généré par le backend pour la lecture
}
