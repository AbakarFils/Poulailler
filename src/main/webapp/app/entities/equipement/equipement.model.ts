import dayjs from 'dayjs/esm';
import { IEmploye } from 'app/entities/employe/employe.model';
import { IDirecteur } from 'app/entities/directeur/directeur.model';
import { TypeEquipement } from 'app/entities/enumerations/type-equipement.model';

export interface IEquipement {
  id?: number;
  statut?: boolean | null;
  refArduino?: string;
  dateCrea?: dayjs.Dayjs | null;
  libelle?: string;
  marque?: string | null;
  type?: TypeEquipement | null;
  employes?: IEmploye[] | null;
  gestionnaires?: IDirecteur[] | null;
}

export class Equipement implements IEquipement {
  constructor(
    public id?: number,
    public statut?: boolean | null,
    public refArduino?: string,
    public dateCrea?: dayjs.Dayjs | null,
    public libelle?: string,
    public marque?: string | null,
    public type?: TypeEquipement | null,
    public employes?: IEmploye[] | null,
    public gestionnaires?: IDirecteur[] | null
  ) {
    this.statut = this.statut ?? false;
  }
}

export function getEquipementIdentifier(equipement: IEquipement): number | undefined {
  return equipement.id;
}
