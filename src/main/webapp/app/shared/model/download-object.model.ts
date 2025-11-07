
export interface IAcceptedPayment {
  address?: string;
  price?: number;
}

export interface IEnv {
  address?: string;
  price?: number;
}

export interface IDownloadContainer {
  id?: any;
  image?: string;
  port?: number;
  command?: string;
  env?: object;
  volumes?: string[];
  acceptedPayments?: IAcceptedPayment[];
}

export const defaultValueContainer: Readonly<IDownloadContainer> = {
};

export interface IDownloadValidator extends IDownloadContainer{
  verifierAddress?: string;
}

export const defaultValueValidator: Readonly<IDownloadValidator> = {
};
