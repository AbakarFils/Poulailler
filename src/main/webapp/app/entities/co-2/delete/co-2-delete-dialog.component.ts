import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICo2 } from '../co-2.model';
import { Co2Service } from '../service/co-2.service';

@Component({
  templateUrl: './co-2-delete-dialog.component.html',
})
export class Co2DeleteDialogComponent {
  co2?: ICo2;

  constructor(protected co2Service: Co2Service, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.co2Service.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
