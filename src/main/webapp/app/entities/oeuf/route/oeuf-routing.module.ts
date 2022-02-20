import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OeufComponent } from '../list/oeuf.component';
import { OeufDetailComponent } from '../detail/oeuf-detail.component';
import { OeufUpdateComponent } from '../update/oeuf-update.component';
import { OeufRoutingResolveService } from './oeuf-routing-resolve.service';

const oeufRoute: Routes = [
  {
    path: '',
    component: OeufComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OeufDetailComponent,
    resolve: {
      oeuf: OeufRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OeufUpdateComponent,
    resolve: {
      oeuf: OeufRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OeufUpdateComponent,
    resolve: {
      oeuf: OeufRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(oeufRoute)],
  exports: [RouterModule],
})
export class OeufRoutingModule {}
