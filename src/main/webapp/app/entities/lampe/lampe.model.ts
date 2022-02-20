import { IEquipement } from 'app/entities/equipement/equipement.model';

export interface ILampe {
  id?: number;
  libelle?: string | null;
  equipement?: IEquipement | null;
}

export class Lampe implements ILampe {
  constructor(public id?: number, public libelle?: string | null, public equipement?: IEquipement | null) {}
}

export function getLampeIdentifier(lampe: ILampe): number | undefined {
  return lampe.id;
}
