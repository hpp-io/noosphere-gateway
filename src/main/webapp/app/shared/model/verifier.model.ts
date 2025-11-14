import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export interface IVerifier {
  id?: string;
  name?: string | null;
  walletAddress?: string;
  verifierAddress?: string;
  imageName?: string;
  port?: number;
  command?: string;
  environmentVariables?: string;
  volumes?: string;
  payments?: string;
  statusCode?: StatusCode;
}

export const defaultValue: Readonly<IVerifier> = {
};
