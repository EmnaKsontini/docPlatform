import { Route } from '@angular/router';
import { ChatDialogComponent } from 'app/chat/chat-dialog/chat-dialog.component';

export const Chat_ROUTE: Route = {
    path: '',
    component: ChatDialogComponent,
    data: {
        authorities: [],
        pageTitle: 'chatbot'
    }
};
