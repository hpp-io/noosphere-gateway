import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export interface IContainer {
  id?: string;
  name?: string | null;
  walletAddress?: string;
  imageName?: string;
  port?: number;
  command?: string;
  environmentVariables?: string;
  volumes?: string;
  payments?: string;
  statusCode?: StatusCode;
}

export const defaultValue: Readonly<IContainer> = {
};
