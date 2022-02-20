import { IVariable } from 'app/entities/variable/variable.model';

export interface ITemperature {
  id?: number;
  dregree?: number | null;
  variable?: IVariable;
}

export class Temperature implements ITemperature {
  constructor(public id?: number, public dregree?: number | null, public variable?: IVariable) {}
}

export function getTemperatureIdentifier(temperature: ITemperature): number | undefined {
  return temperature.id;
}
