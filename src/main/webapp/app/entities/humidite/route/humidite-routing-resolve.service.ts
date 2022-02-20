import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHumidite, Humidite } from '../humidite.model';
import { HumiditeService } from '../service/humidite.service';

@Injectable({ providedIn: 'root' })
export class HumiditeRoutingResolveService implements Resolve<IHumidite> {
  constructor(protected service: HumiditeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHumidite> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((humidite: HttpResponse<Humidite>) => {
          if (humidite.body) {
            return of(humidite.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Humidite());
  }
}
