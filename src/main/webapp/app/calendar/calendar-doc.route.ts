import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core';
import { CalendarDocComponent } from './calendar-doc.component';

export const calendarRoute: Route = {
    path: 'calendar',
    component: CalendarDocComponent,
    data: {
        pageTitle: 'global.menu.account.history'
    },
    canActivate: [UserRouteAccessService]
};
