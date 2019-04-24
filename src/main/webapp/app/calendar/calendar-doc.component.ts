import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CalendarEvent } from 'angular-calendar';
import { colors } from '../calendar-util/colors';

@Component({
    selector: 'jhi-calendar',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './calendar-doc.component.html',
    styles: []
})
export class CalendarDocComponent {
    view: String = 'month';

    viewDate: Date = new Date();

    events: CalendarEvent[] = [
        {
            title: 'Editable event',
            color: colors.yellow,
            start: new Date(),
            actions: [
                {
                    label: '<i class="fa fa-fw fa-pencil"></i>',
                    onClick: ({ event }: { event: CalendarEvent }): void => {
                        console.log('Edit event', event);
                    }
                }
            ]
        },
        {
            title: 'Deletable event',
            color: colors.blue,
            start: new Date(),
            actions: [
                {
                    label: '<i class="fa fa-fw fa-times"></i>',
                    onClick: ({ event }: { event: CalendarEvent }): void => {
                        this.events = this.events.filter(iEvent => iEvent !== event);
                        console.log('Event deleted', event);
                    }
                }
            ]
        },
        {
            title: 'Non editable and deletable event',
            color: colors.red,
            start: new Date()
        }
    ];
}
