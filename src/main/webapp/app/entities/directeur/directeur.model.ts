import { IUser } from 'app/entities/user/user.model';

export interface IDirecteur {
  id?: number;
  adresse?: string | null;
  user?: IUser;
}

export class Directeur implements IDirecteur {
  constructor(public id?: number, public adresse?: string | null, public user?: IUser) {}
}

export function getDirecteurIdentifier(directeur: IDirecteur): number | undefined {
  return directeur.id;
}
