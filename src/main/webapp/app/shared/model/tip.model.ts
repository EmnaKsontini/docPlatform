export interface ITip {
    id?: number;
    title?: string;
    content?: any;
    imageContentType?: string;
    image?: any;
}

export class Tip implements ITip {
    constructor(public id?: number, public title?: string, public content?: any, public imageContentType?: string, public image?: any) {}
}
