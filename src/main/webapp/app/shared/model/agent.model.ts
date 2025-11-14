import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export interface IAgent {
  id?: string;
  name?: string;
  walletAddress?: string;
  description?: string;
  statusCode?: StatusCode;
}

export const defaultValue: Readonly<IAgent> = {
};
