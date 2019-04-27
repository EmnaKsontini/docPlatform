import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, navbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Chat_ROUTE } from 'app/chat/chat-dialog/chat-dialog.route';
import { calendarRoute } from 'app/calendar/calendar-doc.route';

const LAYOUT_ROUTES = [navbarRoute, calendarRoute, Chat_ROUTE, ...errorRoute];

@NgModule({
    imports: [
        RouterModule.forRoot(
            [
                {
                    path: 'admin',
                    loadChildren: './admin/admin.module#DoctorsPlatformAdminModule'
                },
                ...LAYOUT_ROUTES
            ],
            { useHash: true, enableTracing: DEBUG_INFO_ENABLED }
        )
    ],
    exports: [RouterModule]
})
export class DoctorsPlatformAppRoutingModule {}
