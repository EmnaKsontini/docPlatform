import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IRequest } from 'app/shared/model/request.model';
import { RequestService } from './request.service';

@Component({
    selector: 'jhi-request-delete-dialog',
    templateUrl: './request-delete-dialog.component.html'
})
export class RequestDeleteDialogComponent {
    request: IRequest;

    constructor(protected requestService: RequestService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.requestService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'requestListModification',
                content: 'Deleted an request'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-request-delete-popup',
    template: ''
})
export class RequestDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ request }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(RequestDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.request = request;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/request', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/request', { outlets: { popup: null } }]);
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
