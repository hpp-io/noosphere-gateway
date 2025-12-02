export interface IRateLimit {
  id?: any;
  apiKey?: string;
  capacity?: number;
  refillAmount?: number;
  refillTimeInSeconds?: number;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
}

export const defaultValue: Readonly<IRateLimit> = {
  id: '',
  apiKey: '',
  capacity: 0,
  refillAmount: 0,
  refillTimeInSeconds: 0,
  lastModifiedBy: '',
  lastModifiedDate: null,
};
