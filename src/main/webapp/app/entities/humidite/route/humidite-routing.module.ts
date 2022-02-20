import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HumiditeComponent } from '../list/humidite.component';
import { HumiditeDetailComponent } from '../detail/humidite-detail.component';
import { HumiditeUpdateComponent } from '../update/humidite-update.component';
import { HumiditeRoutingResolveService } from './humidite-routing-resolve.service';

const humiditeRoute: Routes = [
  {
    path: '',
    component: HumiditeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HumiditeDetailComponent,
    resolve: {
      humidite: HumiditeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HumiditeUpdateComponent,
    resolve: {
      humidite: HumiditeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HumiditeUpdateComponent,
    resolve: {
      humidite: HumiditeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(humiditeRoute)],
  exports: [RouterModule],
})
export class HumiditeRoutingModule {}
