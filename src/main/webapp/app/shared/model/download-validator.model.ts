export interface IDownloadValidator {
  id?: any;
  name?: string;
  image?: string;
  external?: boolean;
  port?: number;
  command?: string;
  parameters?: string;
  generatesProofs?: boolean;
  price?: number;
}

export const defaultValue: Readonly<IDownloadValidator> = {
  external: true,
  // command: '',
  generatesProofs: true,
  // price: null,
};
