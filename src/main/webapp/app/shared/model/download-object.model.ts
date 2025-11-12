export interface IDownloadContainer {
  id?: any;
  image?: string;
  port?: number;
  command?: string;
  env?: Record<string, any>;
  volumes?: string[];
  acceptedPayments?: Record<string, any>;
}

export const defaultValueContainer: Readonly<IDownloadContainer> = {
};

export interface IDownloadValidator extends IDownloadContainer{
  verifierAddress?: string;
}

export const defaultValueValidator: Readonly<IDownloadValidator> = {
};
