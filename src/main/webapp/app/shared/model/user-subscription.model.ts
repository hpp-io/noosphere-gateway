import { StatusCode } from "app/shared/model/enumerations/status-code.model";
import { PeriodType } from "app/shared/model/enumerations/period-type.model";
import { IContainer } from "app/shared/model/container.model";
import { IUser } from "app/shared/model/user.model";

export interface IUserSubscription {
  id?: string;
  amount?: number;
  periodType?: PeriodType;
  periodValue?: number;
  statusCode?: StatusCode;
  owner?: IUser;
  container?: IContainer;

}

export const defaultValue: Readonly<IUserSubscription> = {
};
