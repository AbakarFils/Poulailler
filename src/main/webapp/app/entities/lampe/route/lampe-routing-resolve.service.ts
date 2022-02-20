import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILampe, Lampe } from '../lampe.model';
import { LampeService } from '../service/lampe.service';

@Injectable({ providedIn: 'root' })
export class LampeRoutingResolveService implements Resolve<ILampe> {
  constructor(protected service: LampeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILampe> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((lampe: HttpResponse<Lampe>) => {
          if (lampe.body) {
            return of(lampe.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Lampe());
  }
}
