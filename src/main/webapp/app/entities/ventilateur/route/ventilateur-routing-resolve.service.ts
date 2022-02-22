import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVentilateur, Ventilateur } from '../ventilateur.model';
import { VentilateurService } from '../service/ventilateur.service';

@Injectable({ providedIn: 'root' })
export class VentilateurRoutingResolveService implements Resolve<IVentilateur> {
  constructor(protected service: VentilateurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVentilateur> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ventilateur: HttpResponse<Ventilateur>) => {
          if (ventilateur.body) {
            return of(ventilateur.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ventilateur());
  }
}
