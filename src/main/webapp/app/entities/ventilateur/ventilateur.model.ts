export interface IVentilateur {
  id?: number;
  libelle?: string | null;
  vitesse?: number | null;
}

export class Ventilateur implements IVentilateur {
  constructor(public id?: number, public libelle?: string | null, public vitesse?: number | null) {}
}

export function getVentilateurIdentifier(ventilateur: IVentilateur): number | undefined {
  return ventilateur.id;
}
