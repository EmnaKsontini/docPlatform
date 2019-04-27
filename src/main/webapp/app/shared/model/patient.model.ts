import { IRequest } from 'app/shared/model/request.model';

export interface IPatient {
    id?: number;
    cin?: number;
    name?: string;
    email?: string;
    phoneNumber?: number;
    requests?: IRequest[];
}

export class Patient implements IPatient {
    constructor(
        public id?: number,
        public cin?: number,
        public name?: string,
        public email?: string,
        public phoneNumber?: number,
        public requests?: IRequest[]
    ) {}
}
