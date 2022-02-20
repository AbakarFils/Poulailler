import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEquipement, getEquipementIdentifier } from '../equipement.model';

export type EntityResponseType = HttpResponse<IEquipement>;
export type EntityArrayResponseType = HttpResponse<IEquipement[]>;

@Injectable({ providedIn: 'root' })
export class EquipementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/equipements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(equipement: IEquipement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(equipement);
    return this.http
      .post<IEquipement>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(equipement: IEquipement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(equipement);
    return this.http
      .put<IEquipement>(`${this.resourceUrl}/${getEquipementIdentifier(equipement) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(equipement: IEquipement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(equipement);
    return this.http
      .patch<IEquipement>(`${this.resourceUrl}/${getEquipementIdentifier(equipement) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEquipement>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEquipement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEquipementToCollectionIfMissing(
    equipementCollection: IEquipement[],
    ...equipementsToCheck: (IEquipement | null | undefined)[]
  ): IEquipement[] {
    const equipements: IEquipement[] = equipementsToCheck.filter(isPresent);
    if (equipements.length > 0) {
      const equipementCollectionIdentifiers = equipementCollection.map(equipementItem => getEquipementIdentifier(equipementItem)!);
      const equipementsToAdd = equipements.filter(equipementItem => {
        const equipementIdentifier = getEquipementIdentifier(equipementItem);
        if (equipementIdentifier == null || equipementCollectionIdentifiers.includes(equipementIdentifier)) {
          return false;
        }
        equipementCollectionIdentifiers.push(equipementIdentifier);
        return true;
      });
      return [...equipementsToAdd, ...equipementCollection];
    }
    return equipementCollection;
  }

  protected convertDateFromClient(equipement: IEquipement): IEquipement {
    return Object.assign({}, equipement, {
      dateCrea: equipement.dateCrea?.isValid() ? equipement.dateCrea.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateCrea = res.body.dateCrea ? dayjs(res.body.dateCrea) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((equipement: IEquipement) => {
        equipement.dateCrea = equipement.dateCrea ? dayjs(equipement.dateCrea) : undefined;
      });
    }
    return res;
  }
}
