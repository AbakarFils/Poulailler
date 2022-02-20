import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HumiditeComponent } from './list/humidite.component';
import { HumiditeDetailComponent } from './detail/humidite-detail.component';
import { HumiditeUpdateComponent } from './update/humidite-update.component';
import { HumiditeDeleteDialogComponent } from './delete/humidite-delete-dialog.component';
import { HumiditeRoutingModule } from './route/humidite-routing.module';

@NgModule({
  imports: [SharedModule, HumiditeRoutingModule],
  declarations: [HumiditeComponent, HumiditeDetailComponent, HumiditeUpdateComponent, HumiditeDeleteDialogComponent],
  entryComponents: [HumiditeDeleteDialogComponent],
})
export class HumiditeModule {}
