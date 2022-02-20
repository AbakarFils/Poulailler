import { IEquipement } from 'app/entities/equipement/equipement.model';

export interface ISerrure {
  id?: number;
  libelle?: string | null;
  dimension?: number | null;
  equipement?: IEquipement | null;
}

export class Serrure implements ISerrure {
  constructor(
    public id?: number,
    public libelle?: string | null,
    public dimension?: number | null,
    public equipement?: IEquipement | null
  ) {}
}

export function getSerrureIdentifier(serrure: ISerrure): number | undefined {
  return serrure.id;
}
