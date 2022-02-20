import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDirecteur, Directeur } from '../directeur.model';
import { DirecteurService } from '../service/directeur.service';

@Injectable({ providedIn: 'root' })
export class DirecteurRoutingResolveService implements Resolve<IDirecteur> {
  constructor(protected service: DirecteurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDirecteur> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((directeur: HttpResponse<Directeur>) => {
          if (directeur.body) {
            return of(directeur.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Directeur());
  }
}
