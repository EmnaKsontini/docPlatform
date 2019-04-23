import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BoxModule } from 'angular-admin-lte';
import { SidebarComponent } from 'app/layouts/sidebar/sidebar.component';

@NgModule({
    imports: [CommonModule, BoxModule],
    declarations: [SidebarComponent]
})
export class SidebarModule {}
