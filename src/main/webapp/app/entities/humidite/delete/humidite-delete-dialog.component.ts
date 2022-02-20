import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHumidite } from '../humidite.model';
import { HumiditeService } from '../service/humidite.service';

@Component({
  templateUrl: './humidite-delete-dialog.component.html',
})
export class HumiditeDeleteDialogComponent {
  humidite?: IHumidite;

  constructor(protected humiditeService: HumiditeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.humiditeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
