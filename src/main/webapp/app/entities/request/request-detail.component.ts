import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRequest } from 'app/shared/model/request.model';

@Component({
    selector: 'jhi-request-detail',
    templateUrl: './request-detail.component.html'
})
export class RequestDetailComponent implements OnInit {
    request: IRequest;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ request }) => {
            this.request = request;
        });
    }

    previousState() {
        window.history.back();
    }
}
