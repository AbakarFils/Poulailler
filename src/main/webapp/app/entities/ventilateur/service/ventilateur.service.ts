import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVentilateur, getVentilateurIdentifier } from '../ventilateur.model';

export type EntityResponseType = HttpResponse<IVentilateur>;
export type EntityArrayResponseType = HttpResponse<IVentilateur[]>;

@Injectable({ providedIn: 'root' })
export class VentilateurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ventilateurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ventilateur: IVentilateur): Observable<EntityResponseType> {
    return this.http.post<IVentilateur>(this.resourceUrl, ventilateur, { observe: 'response' });
  }

  update(ventilateur: IVentilateur): Observable<EntityResponseType> {
    return this.http.put<IVentilateur>(`${this.resourceUrl}/${getVentilateurIdentifier(ventilateur) as number}`, ventilateur, {
      observe: 'response',
    });
  }

  partialUpdate(ventilateur: IVentilateur): Observable<EntityResponseType> {
    return this.http.patch<IVentilateur>(`${this.resourceUrl}/${getVentilateurIdentifier(ventilateur) as number}`, ventilateur, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVentilateur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVentilateur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVentilateurToCollectionIfMissing(
    ventilateurCollection: IVentilateur[],
    ...ventilateursToCheck: (IVentilateur | null | undefined)[]
  ): IVentilateur[] {
    const ventilateurs: IVentilateur[] = ventilateursToCheck.filter(isPresent);
    if (ventilateurs.length > 0) {
      const ventilateurCollectionIdentifiers = ventilateurCollection.map(ventilateurItem => getVentilateurIdentifier(ventilateurItem)!);
      const ventilateursToAdd = ventilateurs.filter(ventilateurItem => {
        const ventilateurIdentifier = getVentilateurIdentifier(ventilateurItem);
        if (ventilateurIdentifier == null || ventilateurCollectionIdentifiers.includes(ventilateurIdentifier)) {
          return false;
        }
        ventilateurCollectionIdentifiers.push(ventilateurIdentifier);
        return true;
      });
      return [...ventilateursToAdd, ...ventilateurCollection];
    }
    return ventilateurCollection;
  }
}
