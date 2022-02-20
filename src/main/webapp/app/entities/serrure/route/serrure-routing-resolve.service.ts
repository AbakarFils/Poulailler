import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISerrure, Serrure } from '../serrure.model';
import { SerrureService } from '../service/serrure.service';

@Injectable({ providedIn: 'root' })
export class SerrureRoutingResolveService implements Resolve<ISerrure> {
  constructor(protected service: SerrureService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISerrure> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((serrure: HttpResponse<Serrure>) => {
          if (serrure.body) {
            return of(serrure.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Serrure());
  }
}
