import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Tip } from 'app/shared/model/tip.model';
import { TipService } from './tip.service';
import { TipComponent } from './tip.component';
import { TipDetailComponent } from './tip-detail.component';
import { TipUpdateComponent } from './tip-update.component';
import { TipDeletePopupComponent } from './tip-delete-dialog.component';
import { ITip } from 'app/shared/model/tip.model';

@Injectable({ providedIn: 'root' })
export class TipResolve implements Resolve<ITip> {
    constructor(private service: TipService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITip> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Tip>) => response.ok),
                map((tip: HttpResponse<Tip>) => tip.body)
            );
        }
        return of(new Tip());
    }
}

export const tipRoute: Routes = [
    {
        path: '',
        component: TipComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'doctorsPlatformApp.tip.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: TipDetailComponent,
        resolve: {
            tip: TipResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.tip.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: TipUpdateComponent,
        resolve: {
            tip: TipResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.tip.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: TipUpdateComponent,
        resolve: {
            tip: TipResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.tip.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const tipPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: TipDeletePopupComponent,
        resolve: {
            tip: TipResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'doctorsPlatformApp.tip.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
