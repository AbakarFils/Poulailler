import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { Co2Component } from './list/co-2.component';
import { Co2DetailComponent } from './detail/co-2-detail.component';
import { Co2UpdateComponent } from './update/co-2-update.component';
import { Co2DeleteDialogComponent } from './delete/co-2-delete-dialog.component';
import { Co2RoutingModule } from './route/co-2-routing.module';

@NgModule({
  imports: [SharedModule, Co2RoutingModule],
  declarations: [Co2Component, Co2DetailComponent, Co2UpdateComponent, Co2DeleteDialogComponent],
  entryComponents: [Co2DeleteDialogComponent],
})
export class Co2Module {}
