import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Patient } from 'app/shared/model/patient.model';
import { PatientService } from './patient.service';
import { PatientComponent } from './patient.component';
import { PatientDetailComponent } from './patient-detail.component';
import { PatientUpdateComponent } from './patient-update.component';
import { PatientDeletePopupComponent } from './patient-delete-dialog.component';
import { IPatient } from 'app/shared/model/patient.model';

@Injectable({ providedIn: 'root' })
export class PatientResolve implements Resolve<IPatient> {
    constructor(private service: PatientService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IPatient> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Patient>) => response.ok),
                map((patient: HttpResponse<Patient>) => patient.body)
            );
        }
        return of(new Patient());
    }
}

export const patientRoute: Routes = [
    {
        path: '',
        component: PatientComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'doctorsPlatformApp.patient.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: PatientDetailComponent,
        resolve: {
            patient: PatientResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.patient.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: PatientUpdateComponent,
        resolve: {
            patient: PatientResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.patient.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: PatientUpdateComponent,
        resolve: {
            patient: PatientResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.patient.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const patientPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: PatientDeletePopupComponent,
        resolve: {
            patient: PatientResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.patient.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
