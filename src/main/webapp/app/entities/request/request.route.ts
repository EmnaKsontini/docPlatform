import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Request } from 'app/shared/model/request.model';
import { RequestService } from './request.service';
import { RequestComponent } from './request.component';
import { RequestDetailComponent } from './request-detail.component';
import { RequestUpdateComponent } from './request-update.component';
import { RequestDeletePopupComponent } from './request-delete-dialog.component';
import { IRequest } from 'app/shared/model/request.model';

@Injectable({ providedIn: 'root' })
export class RequestResolve implements Resolve<IRequest> {
    constructor(private service: RequestService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IRequest> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Request>) => response.ok),
                map((request: HttpResponse<Request>) => request.body)
            );
        }
        return of(new Request());
    }
}

export const requestRoute: Routes = [
    {
        path: '',
        component: RequestComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'doctorsPlatformApp.request.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: RequestDetailComponent,
        resolve: {
            request: RequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.request.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: RequestUpdateComponent,
        resolve: {
            request: RequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.request.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: RequestUpdateComponent,
        resolve: {
            request: RequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.request.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const requestPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: RequestDeletePopupComponent,
        resolve: {
            request: RequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.request.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
