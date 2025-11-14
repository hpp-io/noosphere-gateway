import { StatusCode } from "app/shared/model/enumerations/status-code.model";
import { IAgent } from "app/shared/model/agent.model";
import { IContainer } from "app/shared/model/container.model";
import { IUserSubscription } from "app/shared/model/user-subscription.model";

export interface IAgentRequest {
  id?: string;
  statusCode?: StatusCode;
  agent?: IAgent;
  container?: IContainer;
  userSubscription?: IUserSubscription;
}

export const defaultValue: Readonly<IAgentRequest> = {
};
