import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export interface ISearchAgentRequest {
  agentName?: string | null;
  containerId?: string | null;
  agentId?: string | null;
  statusCode?: StatusCode | null;
  page: number,
  size: number,
  sort: string

}

export const defaultValue: Readonly<ISearchAgentRequest> = {
  page: 0,
  size: 10,
  sort: '',
};
