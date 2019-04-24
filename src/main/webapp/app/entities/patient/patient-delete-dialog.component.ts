import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPatient } from 'app/shared/model/patient.model';
import { PatientService } from './patient.service';

@Component({
    selector: 'jhi-patient-delete-dialog',
    templateUrl: './patient-delete-dialog.component.html'
})
export class PatientDeleteDialogComponent {
    patient: IPatient;

    constructor(protected patientService: PatientService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.patientService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'patientListModification',
                content: 'Deleted an patient'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-patient-delete-popup',
    template: ''
})
export class PatientDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ patient }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(PatientDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.patient = patient;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/patient', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/patient', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
