import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHumidite, getHumiditeIdentifier } from '../humidite.model';

export type EntityResponseType = HttpResponse<IHumidite>;
export type EntityArrayResponseType = HttpResponse<IHumidite[]>;

@Injectable({ providedIn: 'root' })
export class HumiditeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/humidites');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(humidite: IHumidite): Observable<EntityResponseType> {
    return this.http.post<IHumidite>(this.resourceUrl, humidite, { observe: 'response' });
  }

  update(humidite: IHumidite): Observable<EntityResponseType> {
    return this.http.put<IHumidite>(`${this.resourceUrl}/${getHumiditeIdentifier(humidite) as number}`, humidite, { observe: 'response' });
  }

  partialUpdate(humidite: IHumidite): Observable<EntityResponseType> {
    return this.http.patch<IHumidite>(`${this.resourceUrl}/${getHumiditeIdentifier(humidite) as number}`, humidite, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHumidite>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHumidite[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHumiditeToCollectionIfMissing(humiditeCollection: IHumidite[], ...humiditesToCheck: (IHumidite | null | undefined)[]): IHumidite[] {
    const humidites: IHumidite[] = humiditesToCheck.filter(isPresent);
    if (humidites.length > 0) {
      const humiditeCollectionIdentifiers = humiditeCollection.map(humiditeItem => getHumiditeIdentifier(humiditeItem)!);
      const humiditesToAdd = humidites.filter(humiditeItem => {
        const humiditeIdentifier = getHumiditeIdentifier(humiditeItem);
        if (humiditeIdentifier == null || humiditeCollectionIdentifiers.includes(humiditeIdentifier)) {
          return false;
        }
        humiditeCollectionIdentifiers.push(humiditeIdentifier);
        return true;
      });
      return [...humiditesToAdd, ...humiditeCollection];
    }
    return humiditeCollection;
  }
}
