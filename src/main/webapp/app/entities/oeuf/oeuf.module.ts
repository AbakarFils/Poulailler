import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OeufComponent } from './list/oeuf.component';
import { OeufDetailComponent } from './detail/oeuf-detail.component';
import { OeufUpdateComponent } from './update/oeuf-update.component';
import { OeufDeleteDialogComponent } from './delete/oeuf-delete-dialog.component';
import { OeufRoutingModule } from './route/oeuf-routing.module';

@NgModule({
  imports: [SharedModule, OeufRoutingModule],
  declarations: [OeufComponent, OeufDetailComponent, OeufUpdateComponent, OeufDeleteDialogComponent],
  entryComponents: [OeufDeleteDialogComponent],
})
export class OeufModule {}
