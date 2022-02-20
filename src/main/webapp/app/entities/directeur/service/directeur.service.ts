import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDirecteur, getDirecteurIdentifier } from '../directeur.model';

export type EntityResponseType = HttpResponse<IDirecteur>;
export type EntityArrayResponseType = HttpResponse<IDirecteur[]>;

@Injectable({ providedIn: 'root' })
export class DirecteurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/directeurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(directeur: IDirecteur): Observable<EntityResponseType> {
    return this.http.post<IDirecteur>(this.resourceUrl, directeur, { observe: 'response' });
  }

  update(directeur: IDirecteur): Observable<EntityResponseType> {
    return this.http.put<IDirecteur>(`${this.resourceUrl}/${getDirecteurIdentifier(directeur) as number}`, directeur, {
      observe: 'response',
    });
  }

  partialUpdate(directeur: IDirecteur): Observable<EntityResponseType> {
    return this.http.patch<IDirecteur>(`${this.resourceUrl}/${getDirecteurIdentifier(directeur) as number}`, directeur, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDirecteur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDirecteur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDirecteurToCollectionIfMissing(
    directeurCollection: IDirecteur[],
    ...directeursToCheck: (IDirecteur | null | undefined)[]
  ): IDirecteur[] {
    const directeurs: IDirecteur[] = directeursToCheck.filter(isPresent);
    if (directeurs.length > 0) {
      const directeurCollectionIdentifiers = directeurCollection.map(directeurItem => getDirecteurIdentifier(directeurItem)!);
      const directeursToAdd = directeurs.filter(directeurItem => {
        const directeurIdentifier = getDirecteurIdentifier(directeurItem);
        if (directeurIdentifier == null || directeurCollectionIdentifiers.includes(directeurIdentifier)) {
          return false;
        }
        directeurCollectionIdentifiers.push(directeurIdentifier);
        return true;
      });
      return [...directeursToAdd, ...directeurCollection];
    }
    return directeurCollection;
  }
}
