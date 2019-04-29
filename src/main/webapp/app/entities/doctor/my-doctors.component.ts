import { Component, OnInit } from '@angular/core';
import { IDoctor } from 'app/shared/model/doctor.model';
import { Subscription } from 'rxjs';
import { DoctorService } from 'app/entities/doctor/doctor.service';
import { JhiAlertService, JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { AccountService } from 'app/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ITEMS_PER_PAGE } from 'app/shared';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';

@Component({
    selector: 'jhi-my-doctors',
    templateUrl: './my-doctors.component.html',
    styles: []
})
export class MyDoctorsComponent implements OnInit {
    currentAccount: any;
    doctors: IDoctor[];
    error: any;
    success: any;
    eventSubscriber: Subscription;
    currentSearch: string;
    routeData: any;
    links: any;
    totalItems: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;
    Mydoctors: IDoctor[];

    constructor(
        protected doctorService: DoctorService,
        protected parseLinks: JhiParseLinks,
        protected jhiAlertService: JhiAlertService,
        protected accountService: AccountService,
        protected activatedRoute: ActivatedRoute,
        protected router: Router,
        protected eventManager: JhiEventManager
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe(data => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    ngOnInit() {
        this.doctorService.getMyDoctors().subscribe(AllDoctors => {
            this.Mydoctors = AllDoctors;
        });
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
