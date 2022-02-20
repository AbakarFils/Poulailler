import { IVariable } from 'app/entities/variable/variable.model';

export interface IHumidite {
  id?: number;
  niveau?: number | null;
  variable?: IVariable;
}

export class Humidite implements IHumidite {
  constructor(public id?: number, public niveau?: number | null, public variable?: IVariable) {}
}

export function getHumiditeIdentifier(humidite: IHumidite): number | undefined {
  return humidite.id;
}
