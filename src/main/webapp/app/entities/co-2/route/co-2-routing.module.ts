import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Co2Component } from '../list/co-2.component';
import { Co2DetailComponent } from '../detail/co-2-detail.component';
import { Co2UpdateComponent } from '../update/co-2-update.component';
import { Co2RoutingResolveService } from './co-2-routing-resolve.service';

const co2Route: Routes = [
  {
    path: '',
    component: Co2Component,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: Co2DetailComponent,
    resolve: {
      co2: Co2RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: Co2UpdateComponent,
    resolve: {
      co2: Co2RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: Co2UpdateComponent,
    resolve: {
      co2: Co2RoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(co2Route)],
  exports: [RouterModule],
})
export class Co2RoutingModule {}
