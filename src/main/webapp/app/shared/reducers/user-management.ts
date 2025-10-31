import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, createSlice } from '@reduxjs/toolkit';

import { IUser } from 'app/shared/model/user.model';
import { IQueryParams } from 'app/shared/reducers/reducer.utils';
import { IUpdateWallet } from "app/shared/model/update-wallet.model";

const initialState = {
  errorMessage: null,
  users: [] as ReadonlyArray<IUser>,
  walletAddress: null,
};

const apiUrl = 'api/users';

// Async Actions

export const getUsers = createAsyncThunk('userManagement/fetch_users', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return axios.get<IUser[]>(requestUrl);
});

export const getMyWalletAddress = createAsyncThunk('userManagement/fetch_user_wallet', async () => {
  const requestUrl = `${apiUrl}/mine/wallet`;
  return axios.get<string>(requestUrl);
});

export const createMyWalletAddress = createAsyncThunk('userManagement/fetch_user_wallet', async (entity: IUpdateWallet, thunkAPI) => {
  const requestUrl = `${apiUrl}/mine/wallet`;
  return axios.post<string>(requestUrl, entity);
});

export const updateMyWalletAddress = createAsyncThunk('userManagement/fetch_user_wallet', async (entity: IUpdateWallet, thunkAPI) => {
  const requestUrl = `${apiUrl}/mine/wallet`;
  return axios.put<string>(requestUrl, entity);
});

export type UserManagementState = Readonly<typeof initialState>;

export const UserManagementSlice = createSlice({
  name: 'userManagement',
  initialState: initialState as UserManagementState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getUsers.pending, state => state)
      .addCase(getUsers.rejected, (state, action) => {
        state.errorMessage = action.error.message;
      })
      .addCase(getUsers.fulfilled, (state, action) => {
        state.users = action.payload.data;
      })
    .addMatcher(isFulfilled(getMyWalletAddress), (state, action) => {
      return {
        ...state,
        loading: false,
        walletAddress: action.payload.data,
      };
    })
    .addMatcher(isFulfilled(createMyWalletAddress, updateMyWalletAddress), (state, action) => {
      return {
        ...state,
        loading: false,
        updateSuccess: true,
        walletAddress: action.payload.data,
      };
    })
    .addMatcher(isPending(getMyWalletAddress), (state, action) => {
      return {
        ...state,
        updateSuccess: false,
        loading: true,
        updating: false,
        errorMessage: null,
      };
    })
    .addMatcher(isPending(createMyWalletAddress, updateMyWalletAddress), (state, action) => {
      return {
        ...state,
        updateSuccess: false,
        loading: true,
        updating: true,
        errorMessage: null,
      };
    })
    ;
  },
});

export const { reset } = UserManagementSlice.actions;

// Reducer
export default UserManagementSlice.reducer;
