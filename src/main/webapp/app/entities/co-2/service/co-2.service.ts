import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICo2, getCo2Identifier } from '../co-2.model';

export type EntityResponseType = HttpResponse<ICo2>;
export type EntityArrayResponseType = HttpResponse<ICo2[]>;

@Injectable({ providedIn: 'root' })
export class Co2Service {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/co-2-s');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(co2: ICo2): Observable<EntityResponseType> {
    return this.http.post<ICo2>(this.resourceUrl, co2, { observe: 'response' });
  }

  update(co2: ICo2): Observable<EntityResponseType> {
    return this.http.put<ICo2>(`${this.resourceUrl}/${getCo2Identifier(co2) as number}`, co2, { observe: 'response' });
  }

  partialUpdate(co2: ICo2): Observable<EntityResponseType> {
    return this.http.patch<ICo2>(`${this.resourceUrl}/${getCo2Identifier(co2) as number}`, co2, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICo2>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICo2[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCo2ToCollectionIfMissing(co2Collection: ICo2[], ...co2sToCheck: (ICo2 | null | undefined)[]): ICo2[] {
    const co2s: ICo2[] = co2sToCheck.filter(isPresent);
    if (co2s.length > 0) {
      const co2CollectionIdentifiers = co2Collection.map(co2Item => getCo2Identifier(co2Item)!);
      const co2sToAdd = co2s.filter(co2Item => {
        const co2Identifier = getCo2Identifier(co2Item);
        if (co2Identifier == null || co2CollectionIdentifiers.includes(co2Identifier)) {
          return false;
        }
        co2CollectionIdentifiers.push(co2Identifier);
        return true;
      });
      return [...co2sToAdd, ...co2Collection];
    }
    return co2Collection;
  }
}
